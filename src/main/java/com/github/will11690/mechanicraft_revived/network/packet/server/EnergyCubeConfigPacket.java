package com.github.will11690.mechanicraft_revived.network.packet.server;

import com.github.will11690.mechanicraft_revived.blocks.storages.base.energycube.BaseEnergyCubeBlockEntity;
import com.github.will11690.mechanicraft_revived.util.block.IOMode;
import com.github.will11690.mechanicraft_revived.util.block.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnergyCubeConfigPacket {

    public enum ChangeType {
        IO, RS, LIMIT
    }

    private final BlockPos pos;
    private final int sideOrdinal;
    private final ChangeType type;
    private final int value; // ordinal for IO/RS, integer for LIMIT

    public EnergyCubeConfigPacket(BlockPos pos, Direction side, ChangeType type, int value) {
        this.pos = pos;
        this.sideOrdinal = side.ordinal();
        this.type = type;
        this.value = value;
    }

    public static void encode(EnergyCubeConfigPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeByte(msg.sideOrdinal);
        buf.writeByte(msg.type.ordinal());
        buf.writeVarInt(msg.value);
    }

    public static EnergyCubeConfigPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int sideOrd = buf.readByte();
        int typeOrd = buf.readByte();
        int value = buf.readVarInt();

        Direction side = Direction.values()[Math.max(0, Math.min(Direction.values().length - 1, sideOrd))];
        ChangeType type = ChangeType.values()[Math.max(0, Math.min(ChangeType.values().length - 1, typeOrd))];

        return new EnergyCubeConfigPacket(pos, side, type, value);
    }

    public static void handle(EnergyCubeConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            BlockEntity be = player.level().getBlockEntity(msg.pos);
            if (!(be instanceof BaseEnergyCubeBlockEntity cube)) return;

            Direction side = Direction.values()[msg.sideOrdinal];

            switch (msg.type) {
                case IO -> {
                    IOMode[] v = IOMode.values();
                    int ord = Math.max(0, Math.min(v.length - 1, msg.value));
                    cube.setSideIOMode(side, v[ord]);
                }
                case RS -> {
                    RedstoneMode[] v = RedstoneMode.values();
                    int ord = Math.max(0, Math.min(v.length - 1, msg.value));
                    cube.setSideRedstoneMode(side, v[ord]); // global apply
                }
                case LIMIT -> cube.setSideTransferLimit(side, msg.value);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}