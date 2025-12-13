package com.github.will11690.mechanicraft_revived.network.packet.server;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.FilterMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeLogicMode;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.PipeSideConfig;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import com.github.will11690.mechanicraft_revived.util.block.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdatePipeSideConfigPacket {

    private final BlockPos pos;
    private final Direction side;

    private final IOMode ioMode;
    private final PipeLogicMode logicMode;
    private final RedstoneMode redstoneMode;

    private final int extractChannel;
    private final int extractPriority;
    private final int extractTransferLimit;

    private final int insertChannel;
    private final int insertPriority;
    private final int insertTransferLimit;

    private final FilterMode extractFilterMode;
    private final FilterMode insertFilterMode;

    public UpdatePipeSideConfigPacket(BlockPos pos,
                                      Direction side,
                                      IOMode ioMode,
                                      PipeLogicMode logicMode,
                                      RedstoneMode redstoneMode,
                                      int extractChannel,
                                      int extractPriority,
                                      int extractTransferLimit,
                                      int insertChannel,
                                      int insertPriority,
                                      int insertTransferLimit,
                                      FilterMode extractFilterMode,
                                      FilterMode insertFilterMode) {

        this.pos = pos;
        this.side = side;
        this.ioMode = ioMode;
        this.logicMode = logicMode;
        this.redstoneMode = redstoneMode;
        this.extractChannel = extractChannel;
        this.extractPriority = extractPriority;
        this.extractTransferLimit = extractTransferLimit;
        this.insertChannel = insertChannel;
        this.insertPriority = insertPriority;
        this.insertTransferLimit = insertTransferLimit;
        this.extractFilterMode = extractFilterMode;
        this.insertFilterMode = insertFilterMode;
    }

    public static void encode(UpdatePipeSideConfigPacket msg, FriendlyByteBuf buf) {

        buf.writeBlockPos(msg.pos);
        buf.writeByte(msg.side.get3DDataValue());

        buf.writeVarInt(msg.ioMode.ordinal());
        buf.writeVarInt(msg.logicMode.ordinal());
        buf.writeVarInt(msg.redstoneMode.ordinal());

        buf.writeVarInt(msg.extractChannel);
        buf.writeVarInt(msg.extractPriority);
        buf.writeVarInt(msg.extractTransferLimit);

        buf.writeVarInt(msg.insertChannel);
        buf.writeVarInt(msg.insertPriority);
        buf.writeVarInt(msg.insertTransferLimit);

        buf.writeVarInt(msg.extractFilterMode.ordinal());
        buf.writeVarInt(msg.insertFilterMode.ordinal());
    }

    public static UpdatePipeSideConfigPacket decode(FriendlyByteBuf buf) {

        BlockPos pos = buf.readBlockPos();
        Direction side = Direction.from3DDataValue(buf.readByte());

        IOMode ioMode = IOMode.values()[buf.readVarInt()];
        PipeLogicMode logicMode = PipeLogicMode.values()[buf.readVarInt()];
        RedstoneMode redstoneMode = RedstoneMode.values()[buf.readVarInt()];

        int exChannel = buf.readVarInt();
        int exPriority = buf.readVarInt();
        int exLimit = buf.readVarInt();

        int inChannel = buf.readVarInt();
        int inPriority = buf.readVarInt();
        int inLimit = buf.readVarInt();

        FilterMode exFilterMode = FilterMode.fromOrdinal(buf.readVarInt());
        FilterMode inFilterMode = FilterMode.fromOrdinal(buf.readVarInt());

        return new UpdatePipeSideConfigPacket(
                pos, side,
                ioMode, logicMode, redstoneMode,
                exChannel, exPriority, exLimit,
                inChannel, inPriority, inLimit,
                exFilterMode, inFilterMode
        );
    }

    public static void handle(UpdatePipeSideConfigPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {

        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();
            if (!level.isLoaded(msg.pos)) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (!(be instanceof BasePipeBlockEntity pipe)) return;

            // --- NEW: redstoneMode is global for the entire pipe ---
            RedstoneMode newRedstone = msg.redstoneMode;

            for (Direction dir : Direction.values()) {

                PipeSideConfig cfg = pipe.getSideConfig(dir);
                if (cfg == null) {
                    cfg = new PipeSideConfig();
                }

                // Redstone mode is shared across ALL faces
                cfg.redstoneMode = newRedstone;

                // All other fields remain per-side and only update for the selected face
                if (dir == msg.side) {

                    cfg.ioMode    = msg.ioMode;
                    cfg.logicMode = msg.logicMode;

                    cfg.extractChannel       = msg.extractChannel;
                    cfg.extractPriority      = msg.extractPriority;
                    cfg.extractTransferLimit = msg.extractTransferLimit;

                    cfg.insertChannel        = msg.insertChannel;
                    cfg.insertPriority       = msg.insertPriority;
                    cfg.insertTransferLimit  = msg.insertTransferLimit;

                    cfg.extractFilterMode    = msg.extractFilterMode;
                    cfg.insertFilterMode     = msg.insertFilterMode;
                }

                pipe.setSideConfig(dir, cfg);
            }

            // Force a block update even if the blockstate didn't structurally change
            BlockState state = level.getBlockState(msg.pos);
            level.sendBlockUpdated(msg.pos, state, state, Block.UPDATE_ALL);
        });
        ctx.setPacketHandled(true);
    }
}