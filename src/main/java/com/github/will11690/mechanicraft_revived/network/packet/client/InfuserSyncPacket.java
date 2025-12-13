package com.github.will11690.mechanicraft_revived.network.packet.client;

import com.github.will11690.mechanicraft_revived.util.block.interfac.IInfuserSync;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InfuserSyncPacket {

    private final BlockPos pos;
    private final int progress;
    private final int maxProgress;
    private final int energyStored;
    private final int energyCapacity;

    public InfuserSyncPacket(IInfuserSync infuser) {
        this.pos = infuser.getBlockPos();
        this.progress = infuser.getGuiProgress();
        this.maxProgress = infuser.getGuiMaxProgress();
        this.energyStored = infuser.getGuiEnergyStored();
        this.energyCapacity = infuser.getGuiEnergyCapacity();
    }

    public InfuserSyncPacket(BlockPos pos, int progress, int maxProgress,
                             int energyStored, int energyCapacity) {
        this.pos = pos;
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.energyStored = energyStored;
        this.energyCapacity = energyCapacity;
    }

    public static void encode(InfuserSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.progress);
        buf.writeInt(msg.maxProgress);
        buf.writeInt(msg.energyStored);
        buf.writeInt(msg.energyCapacity);
    }

    public static InfuserSyncPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int progress = buf.readInt();
        int maxProgress = buf.readInt();
        int energyStored = buf.readInt();
        int energyCapacity = buf.readInt();
        return new InfuserSyncPacket(pos, progress, maxProgress, energyStored, energyCapacity);
    }

    public static void handle(InfuserSyncPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (!(be instanceof IInfuserSync sync)) return;

            sync.applyInfuserSync(
                    msg.progress,
                    msg.maxProgress,
                    msg.energyStored,
                    msg.energyCapacity
            );
        });
        ctx.setPacketHandled(true);
    }
}