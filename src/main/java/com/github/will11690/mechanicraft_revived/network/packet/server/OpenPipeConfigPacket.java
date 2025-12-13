package com.github.will11690.mechanicraft_revived.network.packet.server;

import com.github.will11690.mechanicraft_revived.blocks.transport.base.block.BasePipeBlockEntity;
import com.github.will11690.mechanicraft_revived.blocks.transport.base.gui.PipeConfigContainer;
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

public class OpenPipeConfigPacket {

    private final BlockPos pos;

    public OpenPipeConfigPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(OpenPipeConfigPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static OpenPipeConfigPacket decode(FriendlyByteBuf buf) {
        return new OpenPipeConfigPacket(buf.readBlockPos());
    }

    public static void handle(OpenPipeConfigPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Level level = player.level();
            if (!level.isLoaded(msg.pos)) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (!(be instanceof BasePipeBlockEntity pipe)) return;

            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    // reuse block name as title
                    return pipe.getBlockState().getBlock().getName();
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player p) {
                    return new PipeConfigContainer(windowId, inv, pipe);
                }
            };

            NetworkHooks.openScreen(player, provider, buf -> buf.writeBlockPos(msg.pos));
        });
        ctx.setPacketHandled(true);
    }
}