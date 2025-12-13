package com.github.will11690.mechanicraft_revived.blocks.transport.base.network;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipe;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeLogicMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

/**
 * Per-level cached pipe networks.
 *
 * - Networks are per TYPE + TIER.
 * - BFS traverses only same-type AND same-tier pipes.
 * - Same-type different-tier neighbors become BRIDGE endpoints.
 * - Channels (0–255) and logic modes are handled at distribution time.
 */
public class PipeNetworkManager {

    private static final WeakHashMap<Level, PipeNetworkManager> BY_LEVEL = new WeakHashMap<>();

    public static PipeNetworkManager get(Level level) {

        synchronized (BY_LEVEL) {

            return BY_LEVEL.computeIfAbsent(level, PipeNetworkManager::new);
        }
    }

    private final Level level;

    private long energyTopologyVersion = 0L;
    private long itemTopologyVersion   = 0L;
    private long fluidTopologyVersion  = 0L;
    private long gasTopologyVersion    = 0L;

    private final Map<Long, EnergyNetwork> energyCache = new HashMap<>();
    private final Map<Long, ItemNetwork>   itemCache   = new HashMap<>();
    private final Map<Long, FluidNetwork>  fluidCache  = new HashMap<>();
    private final Map<Long, GasNetwork>    gasCache    = new HashMap<>();

    private PipeNetworkManager(Level level) {

        this.level = level;
    }

    public void markDirty(PipeType type) {

        switch (type) {

            case ENERGY -> {
                energyTopologyVersion++;
                energyCache.clear();
            }
            case ITEM -> {
                itemTopologyVersion++;
                itemCache.clear();
            }
            case FLUID -> {
                fluidTopologyVersion++;
                fluidCache.clear();
            }
            case GAS -> {
                gasTopologyVersion++;
                gasCache.clear();
            }
        }
    }

    public EnergyNetwork getEnergyNetwork(BlockPos startPos) {

        long key = startPos.asLong();
        EnergyNetwork cached = energyCache.get(key);
        if (cached != null && cached.version == energyTopologyVersion) return cached;

        EnergyNetwork rebuilt = EnergyNetwork.build(level, startPos, energyTopologyVersion);
        if (rebuilt != null) energyCache.put(key, rebuilt);
        return rebuilt;
    }

    public ItemNetwork getItemNetwork(BlockPos startPos) {

        long key = startPos.asLong();
        ItemNetwork cached = itemCache.get(key);
        if (cached != null && cached.version == itemTopologyVersion) return cached;

        ItemNetwork rebuilt = ItemNetwork.build(level, startPos, itemTopologyVersion);
        if (rebuilt != null) itemCache.put(key, rebuilt);
        return rebuilt;
    }

    public FluidNetwork getFluidNetwork(BlockPos startPos) {

        long key = startPos.asLong();
        FluidNetwork cached = fluidCache.get(key);
        if (cached != null && cached.version == fluidTopologyVersion) return cached;

        FluidNetwork rebuilt = FluidNetwork.build(level, startPos, fluidTopologyVersion);
        if (rebuilt != null) fluidCache.put(key, rebuilt);
        return rebuilt;
    }

    public GasNetwork getGasNetwork(BlockPos startPos) {

        long key = startPos.asLong();
        GasNetwork cached = gasCache.get(key);
        if (cached != null && cached.version == gasTopologyVersion) return cached;

        GasNetwork rebuilt = GasNetwork.build(level, startPos, gasTopologyVersion);
        if (rebuilt != null) gasCache.put(key, rebuilt);
        return rebuilt;
    }

    /* --------------------------------------------------------------------- */
    /* Small re-entry guard to avoid cross-tier ping-pong in a single push   */
    /* --------------------------------------------------------------------- */

    private static final ThreadLocal<Set<Long>> BRIDGE_GUARD =
            ThreadLocal.withInitial(HashSet::new);

    private static long bridgeKey(PipeType type, int tier, BlockPos pos) {

        long k = pos.asLong();
        k ^= ((long) type.ordinal() & 0xF) << 52;
        k ^= ((long) tier & 0xFF) << 56;
        return k;
    }

    /* --------------------------------------------------------------------- */
    /* ENERGY NETWORK (per-tier + bridges)                                   */
    /* --------------------------------------------------------------------- */

    public static class EnergyNetwork {

        final long version;
        final int tierIndex;
        final List<Endpoint> endpoints;
        final Map<Integer, List<Endpoint>> byPriority;
        final int maxPriority;

        private EnergyNetwork(long version,
                              int tierIndex,
                              List<Endpoint> endpoints,
                              Map<Integer, List<Endpoint>> byPriority,
                              int maxPriority) {

            this.version = version;
            this.tierIndex = tierIndex;
            this.endpoints = endpoints;
            this.byPriority = byPriority;
            this.maxPriority = maxPriority;
        }

        public int distributeEnergy(int amount, boolean simulate, int channel) {

            if (amount <= 0 || endpoints.isEmpty()) return 0;

            int remaining = amount;
            int acceptedTotal = 0;

            // Honor ALL priorities, including negative ones, highest first
            List<Integer> priorities = new ArrayList<>(byPriority.keySet());
            priorities.sort(Collections.reverseOrder());

            for (int pr : priorities) {

                if (remaining <= 0) break;

                List<Endpoint> group = byPriority.get(pr);
                if (group == null || group.isEmpty()) continue;

                // Filter by channel
                List<Endpoint> candidates = new ArrayList<>();
                for (Endpoint ep : group) {
                    if (ep.channel == channel) {
                        candidates.add(ep);
                    }
                }
                if (candidates.isEmpty()) continue;

                int count = candidates.size();
                int baseShare = remaining / count;
                int leftover  = remaining % count;
                if (baseShare <= 0 && leftover <= 0) break;

                for (Endpoint ep : candidates) {

                    if (remaining <= 0) break;

                    int toSend = baseShare + (leftover > 0 ? 1 : 0);
                    if (leftover > 0) leftover--;

                    toSend = Math.min(toSend, ep.transferLimit);
                    toSend = Math.min(toSend, remaining);
                    if (toSend <= 0) continue;

                    int accepted = ep.storage.receiveEnergy(toSend, simulate);
                    if (accepted > 0) {

                        acceptedTotal += accepted;
                        remaining     -= accepted;
                    }
                }
            }

            return acceptedTotal;
        }

        public static class Endpoint {

            public final IEnergyStorage storage;
            public final int priority;
            public final int transferLimit;
            public final int channel;
            public int distance; // set by builder

            public Endpoint(IEnergyStorage storage,
                            int priority,
                            int transferLimit,
                            int channel) {

                this.storage = storage;
                this.priority = priority;
                this.transferLimit = transferLimit;
                this.channel = channel;
            }
        }

        public static EnergyNetwork build(Level level, BlockPos startPos, long version) {

            if (level == null || level.isClientSide) return null;
            if (!level.isLoaded(startPos)) return null;

            BlockEntity startBE = level.getBlockEntity(startPos);
            if (!(startBE instanceof BasePipeBlockEntity startPipe) ||
                    startPipe.getPipeType() != PipeType.ENERGY) {

                return null;
            }

            final int networkTier = startPipe.getTierIndex();

            BasePipeBlockEntity.EndpointFinder<Endpoint> finder =
                    BasePipeBlockEntity.getEndpointFinder(PipeType.ENERGY);
            if (finder == null) return null;

            List<Endpoint> endpoints = new ArrayList<>();
            Map<Integer, List<Endpoint>> byPriority = new HashMap<>();
            int maxPriority = 0;

            Set<Long> visitedPipes = new HashSet<>();
            Deque<BlockPos> queue = new ArrayDeque<>();
            Map<Long, Integer> distanceMap = new HashMap<>();

            long startKey = startPos.asLong();
            visitedPipes.add(startKey);
            queue.add(startPos);
            distanceMap.put(startKey, 0);

            while (!queue.isEmpty()) {

                BlockPos current = queue.poll();
                long currentKey = current.asLong();
                int baseDist = distanceMap.getOrDefault(currentKey, 0);

                BlockEntity beAt = level.getBlockEntity(current);
                if (!(beAt instanceof BasePipeBlockEntity pipe)) continue;

                for (Direction dir : Direction.values()) {

                    if (!pipe.isSideNetworkOpen(dir)) continue;

                    BlockPos neighborPos = current.relative(dir);
                    if (!level.isLoaded(neighborPos)) continue;

                    BlockState neighborState = level.getBlockState(neighborPos);
                    BlockEntity neighborBE = level.getBlockEntity(neighborPos);

                    // Same-type pipe?
                    if (neighborState.getBlock() instanceof BasePipe &&
                            neighborBE instanceof BasePipeBlockEntity neighborPipe &&
                            neighborPipe.getPipeType() == PipeType.ENERGY) {

                        if (!neighborPipe.isSideNetworkOpen(dir.getOpposite())) continue;

                        // Same tier = traverse
                        if (neighborPipe.getTierIndex() == networkTier) {

                            long nKey = neighborPos.asLong();
                            if (visitedPipes.add(nKey)) {

                                queue.add(neighborPos);
                                distanceMap.put(nKey, baseDist + 1);
                            }
                            continue;
                        }

                        // Cross-tier bridge endpoint
                        Direction fromSide = dir.getOpposite();
                        LazyOptional<IEnergyStorage> cap =
                                neighborBE.getCapability(ForgeCapabilities.ENERGY, fromSide);

                        IEnergyStorage storage = cap.resolve().orElse(null);
                        if (storage != null && storage.canReceive()) {

                            if (!pipe.isSideActiveForInsert(dir)) continue;
                            if (!neighborPipe.isSideActiveForExtract(fromSide)) continue;

                            PipeSideConfig cfgHere =
                                    pipe.getSideConfig(dir);
                            PipeSideConfig cfgThere =
                                    neighborPipe.getSideConfig(fromSide);

                            int prHere = (cfgHere != null) ? cfgHere.insertPriority : 0;

                            int limHere = (cfgHere != null)
                                    ? pipe.clampTransferLimit(cfgHere.insertTransferLimit)
                                    : pipe.getTierMaxTransfer();

                            int limThere = (cfgThere != null)
                                    ? neighborPipe.clampTransferLimit(cfgThere.extractTransferLimit)
                                    : neighborPipe.getTierMaxTransfer();

                            int bridgeLimit = Math.min(limHere, limThere);
                            int channel = (cfgHere != null) ? cfgHere.insertChannel : 0;
                            int epDist = baseDist + 1;

                            IEnergyStorage guarded = new IEnergyStorage() {

                                @Override
                                public int receiveEnergy(int maxReceive, boolean simulate) {

                                    long bk = bridgeKey(PipeType.ENERGY,
                                            neighborPipe.getTierIndex(), neighborPos);
                                    Set<Long> guard = BRIDGE_GUARD.get();
                                    if (!guard.add(bk)) return 0;
                                    try {

                                        return storage.receiveEnergy(maxReceive, simulate);
                                    } finally {

                                        guard.remove(bk);
                                    }
                                }

                                @Override public int extractEnergy(int maxExtract, boolean simulate) {
                                    return 0;
                                }
                                @Override public int getEnergyStored() { return 0; }
                                @Override public int getMaxEnergyStored() { return 0; }
                                @Override public boolean canExtract() { return false; }
                                @Override public boolean canReceive() { return true; }
                            };

                            Endpoint ep = new Endpoint(guarded, prHere, bridgeLimit, channel);
                            ep.distance = epDist;

                            endpoints.add(ep);
                            byPriority.computeIfAbsent(ep.priority, k -> new ArrayList<>()).add(ep);
                            if (ep.priority > maxPriority) maxPriority = ep.priority;
                        }

                        continue;
                    }

                    // Real endpoint (non-pipe)
                    if (!pipe.isSideActiveForInsert(dir)) continue;

                    Endpoint ep = finder.findEndpoint(pipe, level, neighborPos, dir);
                    if (ep != null) {

                        ep.distance = baseDist + 1;
                        endpoints.add(ep);
                        byPriority.computeIfAbsent(ep.priority, k -> new ArrayList<>()).add(ep);
                        if (ep.priority > maxPriority) maxPriority = ep.priority;
                    }
                }
            }

            return new EnergyNetwork(version, networkTier, endpoints, byPriority, maxPriority);
        }
    }

    /* --------------------------------------------------------------------- */
    /* ITEM NETWORK (per-tier + bridges + channels + logic modes + filters)  */
    /* --------------------------------------------------------------------- */

    public static class ItemNetwork {

        final long version;
        final int tierIndex;
        final List<Endpoint> endpoints;
        final Map<Integer, List<Endpoint>> byPriority;
        final int maxPriority;

        private long roundRobinCounter = 0L;

        private ItemNetwork(long version,
                            int tierIndex,
                            List<Endpoint> endpoints,
                            Map<Integer, List<Endpoint>> byPriority,
                            int maxPriority) {

            this.version = version;
            this.tierIndex = tierIndex;
            this.endpoints = endpoints;
            this.byPriority = byPriority;
            this.maxPriority = maxPriority;
        }

        /**
         * Distribute items among endpoints.
         *
         * @param stack       stack to distribute
         * @param simulate    simulate only
         * @param logicMode   NEAREST/FURTHEST/ROUND_ROBIN
         * @param source      handler we are pulling from (never insert back into this)
         * @param channel     channel to use (0–255)
         */
        public ItemStack distributeItems(ItemStack stack,
                                         boolean simulate,
                                         PipeLogicMode logicMode,
                                         IItemHandler source,
                                         int channel) {

            if (stack.isEmpty() || endpoints.isEmpty()) return stack;

            ItemStack remaining = stack.copy();

            // Sort all known priorities in descending order so negative ones are honored
            List<Integer> priorities = new ArrayList<>(byPriority.keySet());
            priorities.sort(Collections.reverseOrder());

            for (int pr : priorities) {

                if (remaining.isEmpty()) break;

                List<Endpoint> group = byPriority.get(pr);
                if (group == null || group.isEmpty()) continue;

                // Filter by channel and exclude source
                List<Endpoint> candidates = new ArrayList<>();
                for (Endpoint ep : group) {

                    if (ep.channel != channel) continue;
                    if (ep.handler == source) continue;
                    candidates.add(ep);
                }
                if (candidates.isEmpty()) continue;

                // Order by logic mode
                List<Endpoint> ordered = orderByLogic(candidates, logicMode);

                for (Endpoint ep : ordered) {

                    if (remaining.isEmpty()) break;

                    int toTry = Math.min(remaining.getCount(), ep.transferLimit);
                    if (toTry <= 0) continue;

                    ItemStack probe = remaining.copy();
                    probe.setCount(toTry);

                    // Insert-side filter: respect whitelist/blacklist etc.
                    if (!ep.insertFilter.test(probe)) {
                        continue;
                    }

                    ItemStack leftover = insertIntoHandler(ep.handler, probe, simulate);

                    int inserted = toTry - leftover.getCount();
                    if (inserted > 0) {

                        remaining.shrink(inserted);
                    }
                }
            }

            return remaining;
        }

        private List<Endpoint> orderByLogic(List<Endpoint> extract,
                                            PipeLogicMode logicMode) {

            if (extract.size() <= 1) return extract;

            List<Endpoint> list = new ArrayList<>(extract);

            switch (logicMode) {

                case NEAREST_FIRST -> list.sort(Comparator.comparingInt(ep -> ep.distance));
                case FURTHEST_FIRST -> list.sort((a, b) -> Integer.compare(b.distance, a.distance));
                case ROUND_ROBIN -> {
                    long start = roundRobinCounter++;
                    int size = list.size();
                    List<Endpoint> rotated = new ArrayList<>(size);
                    int offset = (int) (start % size);
                    for (int i = 0; i < size; i++) {
                        rotated.add(list.get((i + offset) % size));
                    }
                    return rotated;
                }
            }

            return list;
        }

        private static ItemStack insertIntoHandler(IItemHandler handler,
                                                   ItemStack stack,
                                                   boolean simulate) {

            ItemStack ret = stack;
            for (int i = 0; i < handler.getSlots(); i++) {

                ret = handler.insertItem(i, ret, simulate);
                if (ret.isEmpty()) break;
            }
            return ret;
        }

        public static class Endpoint {

            public final IItemHandler handler;
            public final int priority;
            public final int transferLimit;
            public final int channel;
            public final java.util.function.Predicate<ItemStack> insertFilter;
            public int distance; // set by builder

            public Endpoint(IItemHandler handler,
                            int priority,
                            int transferLimit,
                            int channel,
                            java.util.function.Predicate<ItemStack> insertFilter) {

                this.handler = handler;
                this.priority = priority;
                this.transferLimit = transferLimit;
                this.channel = channel;
                this.insertFilter = (insertFilter != null) ? insertFilter : stack -> true;
            }
        }

        public static ItemNetwork build(Level level, BlockPos startPos, long version) {

            if (level == null || level.isClientSide) return null;
            if (!level.isLoaded(startPos)) return null;

            BlockEntity startBE = level.getBlockEntity(startPos);
            if (!(startBE instanceof BasePipeBlockEntity startPipe) ||
                    startPipe.getPipeType() != PipeType.ITEM) {

                return null;
            }

            final int networkTier = startPipe.getTierIndex();

            BasePipeBlockEntity.EndpointFinder<Endpoint> finder =
                    BasePipeBlockEntity.getEndpointFinder(PipeType.ITEM);
            if (finder == null) return null;

            List<Endpoint> endpoints = new ArrayList<>();
            Map<Integer, List<Endpoint>> byPriority = new HashMap<>();
            int maxPriority = 0;

            Set<Long> visitedPipes = new HashSet<>();
            Deque<BlockPos> queue = new ArrayDeque<>();
            Map<Long, Integer> distanceMap = new HashMap<>();

            long startKey = startPos.asLong();
            visitedPipes.add(startKey);
            queue.add(startPos);
            distanceMap.put(startKey, 0);

            while (!queue.isEmpty()) {

                BlockPos current = queue.poll();
                long currentKey = current.asLong();
                int baseDist = distanceMap.getOrDefault(currentKey, 0);

                BlockEntity beAt = level.getBlockEntity(current);
                if (!(beAt instanceof BasePipeBlockEntity pipe)) continue;

                for (Direction dir : Direction.values()) {

                    if (!pipe.isSideNetworkOpen(dir)) continue;

                    BlockPos neighborPos = current.relative(dir);
                    if (!level.isLoaded(neighborPos)) continue;

                    BlockState neighborState = level.getBlockState(neighborPos);
                    BlockEntity neighborBE = level.getBlockEntity(neighborPos);

                    if (neighborState.getBlock() instanceof BasePipe &&
                            neighborBE instanceof BasePipeBlockEntity neighborPipe &&
                            neighborPipe.getPipeType() == PipeType.ITEM) {

                        if (!neighborPipe.isSideNetworkOpen(dir.getOpposite())) continue;

                        if (neighborPipe.getTierIndex() == networkTier) {

                            long nKey = neighborPos.asLong();
                            if (visitedPipes.add(nKey)) {

                                queue.add(neighborPos);
                                distanceMap.put(nKey, baseDist + 1);
                            }
                            continue;
                        }

                        // cross-tier bridge endpoint
                        Direction fromSide = dir.getOpposite();
                        LazyOptional<IItemHandler> cap =
                                neighborBE.getCapability(ForgeCapabilities.ITEM_HANDLER, fromSide);

                        IItemHandler handler = cap.resolve().orElse(null);
                        if (handler != null) {

                            if (!pipe.isSideActiveForInsert(dir)) continue;
                            if (!neighborPipe.isSideActiveForExtract(fromSide)) continue;

                            PipeSideConfig cfgHere =
                                    pipe.getSideConfig(dir);
                            PipeSideConfig cfgThere =
                                    neighborPipe.getSideConfig(fromSide);

                            int prHere = (cfgHere != null) ? cfgHere.insertPriority : 0;

                            int limHere = (cfgHere != null)
                                    ? pipe.clampTransferLimit(cfgHere.insertTransferLimit)
                                    : pipe.getTierMaxTransfer();

                            int limThere = (cfgThere != null)
                                    ? neighborPipe.clampTransferLimit(cfgThere.extractTransferLimit)
                                    : neighborPipe.getTierMaxTransfer();

                            int bridgeLimit = Math.min(limHere, limThere);
                            int channel = (cfgHere != null) ? cfgHere.insertChannel : 0;
                            int epDist = baseDist + 1;

                            java.util.function.Predicate<ItemStack> insertFilter =
                                    pipe.getInsertItemFilter(dir);

                            Endpoint ep = new Endpoint(handler, prHere, bridgeLimit, channel, insertFilter);
                            ep.distance = epDist;

                            endpoints.add(ep);
                            byPriority.computeIfAbsent(ep.priority, k -> new ArrayList<>()).add(ep);
                            if (ep.priority > maxPriority) maxPriority = ep.priority;
                        }

                        continue;
                    }

                    if (!pipe.isSideActiveForInsert(dir)) continue;

                    Endpoint ep = finder.findEndpoint(pipe, level, neighborPos, dir);
                    if (ep != null) {

                        ep.distance = baseDist + 1;
                        endpoints.add(ep);
                        byPriority.computeIfAbsent(ep.priority, k -> new ArrayList<>()).add(ep);
                        if (ep.priority > maxPriority) maxPriority = ep.priority;
                    }
                }
            }

            return new ItemNetwork(version, networkTier, endpoints, byPriority, maxPriority);
        }
    }

    /* --------------------------------------------------------------------- */
    /* FLUID NETWORK (per-tier + bridges + channels + logic + filters)       */
    /* --------------------------------------------------------------------- */

    public static class FluidNetwork {

        final long version;
        final int tierIndex;
        final List<Endpoint> endpoints;
        final Map<Integer, List<Endpoint>> byPriority;
        final int maxPriority;

        private long roundRobinCounter = 0L;

        private FluidNetwork(long version,
                             int tierIndex,
                             List<Endpoint> endpoints,
                             Map<Integer, List<Endpoint>> byPriority,
                             int maxPriority) {

            this.version = version;
            this.tierIndex = tierIndex;
            this.endpoints = endpoints;
            this.byPriority = byPriority;
            this.maxPriority = maxPriority;
        }

        /**
         * Distribute fluid among endpoints.
         *
         * @param stack       fluid to distribute
         * @param simulate    simulate only
         * @param logicMode   NEAREST/FURTHEST/ROUND_ROBIN
         * @param source      handler we are draining from (never fill back into this)
         * @param channel     channel to use
         */
        public int distributeFluid(FluidStack stack,
                                   boolean simulate,
                                   PipeLogicMode logicMode,
                                   IFluidHandler source,
                                   int channel) {

            if (stack.isEmpty() || endpoints.isEmpty()) return 0;

            int remaining     = stack.getAmount();
            int acceptedTotal = 0;

            // Use actual priority keys, highest → lowest, so negatives are honored
            List<Integer> priorities = new ArrayList<>(byPriority.keySet());
            priorities.sort(Collections.reverseOrder());

            for (int pr : priorities) {

                if (remaining <= 0) break;

                List<Endpoint> group = byPriority.get(pr);
                if (group == null || group.isEmpty()) continue;

                // Filter by channel and exclude source
                List<Endpoint> candidates = new ArrayList<>();
                for (Endpoint ep : group) {
                    if (ep.channel != channel) continue;
                    if (ep.handler == source) continue;
                    candidates.add(ep);
                }
                if (candidates.isEmpty()) continue;

                List<Endpoint> ordered = orderByLogic(candidates, logicMode);

                for (Endpoint ep : ordered) {

                    if (remaining <= 0) break;

                    int toSend = Math.min(remaining, ep.transferLimit);
                    if (toSend <= 0) continue;

                    FluidStack probe = stack.copy();
                    probe.setAmount(toSend);

                    // Insert-side filter
                    if (!ep.insertFilter.test(probe)) {
                        continue;
                    }

                    int accepted = ep.handler.fill(
                            probe,
                            simulate ? IFluidHandler.FluidAction.SIMULATE
                                    : IFluidHandler.FluidAction.EXECUTE
                    );

                    if (accepted > 0) {

                        acceptedTotal += accepted;
                        remaining     -= accepted;
                    }
                }
            }

            return acceptedTotal;
        }

        private List<Endpoint> orderByLogic(List<Endpoint> extract,
                                            PipeLogicMode logicMode) {

            if (extract.size() <= 1) return extract;

            List<Endpoint> list = new ArrayList<>(extract);

            switch (logicMode) {

                case NEAREST_FIRST -> list.sort(Comparator.comparingInt(ep -> ep.distance));
                case FURTHEST_FIRST -> list.sort((a, b) -> Integer.compare(b.distance, a.distance));
                case ROUND_ROBIN -> {
                    long start = roundRobinCounter++;
                    int size = list.size();
                    List<Endpoint> rotated = new ArrayList<>(size);
                    int offset = (int) (start % size);
                    for (int i = 0; i < size; i++) {
                        rotated.add(list.get((i + offset) % size));
                    }
                    return rotated;
                }
            }

            return list;
        }

        public static class Endpoint {

            public final IFluidHandler handler;
            public final int priority;
            public final int transferLimit;
            public final int channel;
            public final java.util.function.Predicate<FluidStack> insertFilter;
            public int distance; // set by builder

            public Endpoint(IFluidHandler handler,
                            int priority,
                            int transferLimit,
                            int channel,
                            java.util.function.Predicate<FluidStack> insertFilter) {

                this.handler = handler;
                this.priority = priority;
                this.transferLimit = transferLimit;
                this.channel = channel;
                this.insertFilter = (insertFilter != null) ? insertFilter : fs -> true;
            }
        }

        public static FluidNetwork build(Level level, BlockPos startPos, long version) {

            if (level == null || level.isClientSide) return null;
            if (!level.isLoaded(startPos)) return null;

            BlockEntity startBE = level.getBlockEntity(startPos);
            if (!(startBE instanceof BasePipeBlockEntity startPipe) ||
                    startPipe.getPipeType() != PipeType.FLUID) {

                return null;
            }

            final int networkTier = startPipe.getTierIndex();

            BasePipeBlockEntity.EndpointFinder<Endpoint> finder =
                    BasePipeBlockEntity.getEndpointFinder(PipeType.FLUID);
            if (finder == null) return null;

            List<Endpoint> endpoints = new ArrayList<>();
            Map<Integer, List<Endpoint>> byPriority = new HashMap<>();
            int maxPriority = 0;

            Set<Long> visitedPipes = new HashSet<>();
            Deque<BlockPos> queue = new ArrayDeque<>();
            Map<Long, Integer> distanceMap = new HashMap<>();

            long startKey = startPos.asLong();
            visitedPipes.add(startKey);
            queue.add(startPos);
            distanceMap.put(startKey, 0);

            while (!queue.isEmpty()) {

                BlockPos current = queue.poll();
                long currentKey = current.asLong();
                int baseDist = distanceMap.getOrDefault(currentKey, 0);

                BlockEntity beAt = level.getBlockEntity(current);
                if (!(beAt instanceof BasePipeBlockEntity pipe)) continue;

                for (Direction dir : Direction.values()) {

                    if (!pipe.isSideNetworkOpen(dir)) continue;

                    BlockPos neighborPos = current.relative(dir);
                    if (!level.isLoaded(neighborPos)) continue;

                    BlockState neighborState = level.getBlockState(neighborPos);
                    BlockEntity neighborBE = level.getBlockEntity(neighborPos);

                    if (neighborState.getBlock() instanceof BasePipe &&
                            neighborBE instanceof BasePipeBlockEntity neighborPipe &&
                            neighborPipe.getPipeType() == PipeType.FLUID) {

                        if (!neighborPipe.isSideNetworkOpen(dir.getOpposite())) continue;

                        if (neighborPipe.getTierIndex() == networkTier) {

                            long nKey = neighborPos.asLong();
                            if (visitedPipes.add(nKey)) {

                                queue.add(neighborPos);
                                distanceMap.put(nKey, baseDist + 1);
                            }
                            continue;
                        }

                        // cross-tier bridge endpoint
                        Direction fromSide = dir.getOpposite();
                        LazyOptional<IFluidHandler> cap =
                                neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, fromSide);

                        IFluidHandler handler = cap.resolve().orElse(null);
                        if (handler != null) {

                            if (!pipe.isSideActiveForInsert(dir)) continue;
                            if (!neighborPipe.isSideActiveForExtract(fromSide)) continue;

                            PipeSideConfig cfgHere =
                                    pipe.getSideConfig(dir);
                            PipeSideConfig cfgThere =
                                    neighborPipe.getSideConfig(fromSide);

                            int prHere = (cfgHere != null) ? cfgHere.insertPriority : 0;

                            int limHere = (cfgHere != null)
                                    ? pipe.clampTransferLimit(cfgHere.insertTransferLimit)
                                    : pipe.getTierMaxTransfer();

                            int limThere = (cfgThere != null)
                                    ? neighborPipe.clampTransferLimit(cfgThere.extractTransferLimit)
                                    : neighborPipe.getTierMaxTransfer();

                            int bridgeLimit = Math.min(limHere, limThere);
                            int channel = (cfgHere != null) ? cfgHere.insertChannel : 0;
                            int epDist = baseDist + 1;

                            java.util.function.Predicate<FluidStack> insertFilter =
                                    pipe.getInsertFluidFilter(dir);

                            Endpoint ep = new Endpoint(handler, prHere, bridgeLimit, channel, insertFilter);
                            ep.distance = epDist;

                            endpoints.add(ep);
                            byPriority.computeIfAbsent(ep.priority, k -> new ArrayList<>()).add(ep);
                            if (ep.priority > maxPriority) maxPriority = ep.priority;
                        }

                        continue;
                    }

                    if (!pipe.isSideActiveForInsert(dir)) continue;

                    Endpoint ep = finder.findEndpoint(pipe, level, neighborPos, dir);
                    if (ep != null) {

                        ep.distance = baseDist + 1;
                        endpoints.add(ep);
                        byPriority.computeIfAbsent(ep.priority, k -> new ArrayList<>()).add(ep);
                        if (ep.priority > maxPriority) maxPriority = ep.priority;
                    }
                }
            }

            return new FluidNetwork(version, networkTier, endpoints, byPriority, maxPriority);
        }
    }

    /* --------------------------------------------------------------------- */
    /* GAS NETWORK (per-tier traversal only; no bridges yet)                 */
    /* --------------------------------------------------------------------- */

    public static class GasNetwork {

        final long version;
        final int tierIndex;

        private GasNetwork(long version, int tierIndex) {

            this.version = version;
            this.tierIndex = tierIndex;
        }

        public int distributeGas(Object gasStackLike, boolean simulate) {

            return 0; // stub until you pick a capability/API
        }

        public static GasNetwork build(Level level, BlockPos startPos, long version) {

            if (level == null || level.isClientSide) return null;
            if (!level.isLoaded(startPos)) return null;

            BlockEntity startBE = level.getBlockEntity(startPos);
            if (!(startBE instanceof BasePipeBlockEntity startPipe) ||
                    startPipe.getPipeType() != PipeType.GAS) {

                return null;
            }

            final int networkTier = startPipe.getTierIndex();

            Set<Long> visitedPipes = new HashSet<>();
            Deque<BlockPos> queue = new ArrayDeque<>();

            long startKey = startPos.asLong();
            visitedPipes.add(startKey);
            queue.add(startPos);

            while (!queue.isEmpty()) {

                BlockPos current = queue.poll();
                BlockEntity beAt = level.getBlockEntity(current);
                if (!(beAt instanceof BasePipeBlockEntity pipe)) continue;

                for (Direction dir : Direction.values()) {

                    if (!pipe.isSideNetworkOpen(dir)) continue;

                    BlockPos neighborPos = current.relative(dir);
                    if (!level.isLoaded(neighborPos)) continue;

                    BlockState neighborState = level.getBlockState(neighborPos);
                    BlockEntity neighborBE = level.getBlockEntity(neighborPos);

                    if (neighborState.getBlock() instanceof BasePipe &&
                            neighborBE instanceof BasePipeBlockEntity neighborPipe &&
                            neighborPipe.getPipeType() == PipeType.GAS &&
                            neighborPipe.getTierIndex() == networkTier) {

                        if (!neighborPipe.isSideNetworkOpen(dir.getOpposite())) continue;

                        long nKey = neighborPos.asLong();
                        if (visitedPipes.add(nKey)) {

                            queue.add(neighborPos);
                        }
                    }
                }
            }

            return new GasNetwork(version, networkTier);
        }
    }
}