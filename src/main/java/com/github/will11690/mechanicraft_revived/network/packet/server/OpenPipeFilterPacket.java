package com.github.will11690.mechanicraft_revived.network.packet.server;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.gui.PipeFilterContainer;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.network.IFilterablePipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenPipeFilterPacket {

    private final BlockPos pos;
    private final byte sideId;

    public OpenPipeFilterPacket(BlockPos pos, byte sideId) {
        this.pos = pos;
        this.sideId = sideId;
    }

    public static void encode(OpenPipeFilterPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeByte(msg.sideId);
    }

    public static OpenPipeFilterPacket decode(FriendlyByteBuf buf) {
        BlockPos pos  = buf.readBlockPos();
        byte sideId   = buf.readByte();
        return new OpenPipeFilterPacket(pos, sideId);
    }

    public static void handle(OpenPipeFilterPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Level level = player.level();
            if (!level.isLoaded(msg.pos)) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (!(be instanceof BasePipeBlockEntity pipe)) return;
            if (!(pipe instanceof IFilterablePipe)) return;

            final var side = net.minecraft.core.Direction.from3DDataValue(msg.sideId);

            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.mechanicraft.pipe_filter");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                    return new PipeFilterContainer(windowId, inv, be, side);
                }
            };

            NetworkHooks.openScreen(player, provider, buf -> {
                buf.writeBlockPos(msg.pos);
                buf.writeByte(side.get3DDataValue());
            });
        });
        ctx.setPacketHandled(true);
    }
}