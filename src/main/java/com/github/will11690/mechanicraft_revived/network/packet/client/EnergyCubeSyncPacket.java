package com.github.will11690.mechanicraft_revived.network.packet.client;

import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.BaseEnergyCubeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnergyCubeSyncPacket {

    private final BlockPos pos;
    private final int energyStored;
    private final int energyCapacity;
    private final int[] ioModes;   // length 6
    private final int[] rsModes;   // length 6
    private final int[] limits;    // length 6

    public EnergyCubeSyncPacket(BlockPos pos, int energyStored, int energyCapacity, int[] ioModes, int[] rsModes, int[] limits) {
        this.pos = pos;
        this.energyStored = energyStored;
        this.energyCapacity = energyCapacity;
        this.ioModes = ioModes;
        this.rsModes = rsModes;
        this.limits = limits;
    }

    public static void encode(EnergyCubeSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.energyStored);
        buf.writeVarInt(msg.energyCapacity);

        // Always 6 (Direction.values().length)
        for (int i = 0; i < Direction.values().length; i++) {
            buf.writeByte(msg.ioModes[i]);
        }
        for (int i = 0; i < Direction.values().length; i++) {
            buf.writeByte(msg.rsModes[i]);
        }
        for (int i = 0; i < Direction.values().length; i++) {
            buf.writeVarInt(msg.limits[i]);
        }
    }

    public static EnergyCubeSyncPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int stored = buf.readVarInt();
        int cap = buf.readVarInt();

        int n = Direction.values().length;

        int[] io = new int[n];
        int[] rs = new int[n];
        int[] lim = new int[n];

        for (int i = 0; i < n; i++) io[i] = buf.readByte();
        for (int i = 0; i < n; i++) rs[i] = buf.readByte();
        for (int i = 0; i < n; i++) lim[i] = buf.readVarInt();

        return new EnergyCubeSyncPacket(pos, stored, cap, io, rs, lim);
    }

    public static void handle(EnergyCubeSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            if (level.getBlockEntity(msg.pos) instanceof BaseEnergyCubeBlockEntity cube) {
                cube.applyClientSync(msg.energyStored, msg.energyCapacity, msg.ioModes, msg.rsModes, msg.limits);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}