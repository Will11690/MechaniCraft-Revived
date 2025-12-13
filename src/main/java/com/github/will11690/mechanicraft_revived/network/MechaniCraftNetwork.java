package com.github.will11690.mechanicraft_revived.network;

import com.github.will11690.mechanicraft_revived.MechaniCraftMain;
import com.github.will11690.mechanicraft_revived.network.packet.client.InfuserSyncPacket;
import com.github.will11690.mechanicraft_revived.network.packet.server.OpenPipeConfigPacket;
import com.github.will11690.mechanicraft_revived.network.packet.server.OpenPipeFilterPacket;
import com.github.will11690.mechanicraft_revived.network.packet.server.UpdatePipeSideConfigPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class MechaniCraftNetwork {

    private static final String PROTOCOL_VERSION = "1";
    private static int packetId = 0;

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MechaniCraftMain.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int nextId() { return packetId++; }

    public static void register() {

        CHANNEL.registerMessage(nextId(), OpenPipeFilterPacket.class,
                OpenPipeFilterPacket::encode,
                OpenPipeFilterPacket::decode,
                OpenPipeFilterPacket::handle);

        CHANNEL.registerMessage(nextId(), OpenPipeConfigPacket.class,
                OpenPipeConfigPacket::encode,
                OpenPipeConfigPacket::decode,
                OpenPipeConfigPacket::handle);

        CHANNEL.registerMessage(nextId(), UpdatePipeSideConfigPacket.class,
                UpdatePipeSideConfigPacket::encode,
                UpdatePipeSideConfigPacket::decode,
                UpdatePipeSideConfigPacket::handle);

        // Client-bound GUI sync for all infuser tiers that implement IInfuserSync
        CHANNEL.registerMessage(nextId(), InfuserSyncPacket.class,
                InfuserSyncPacket::encode,
                InfuserSyncPacket::decode,
                InfuserSyncPacket::handle);
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }

    /**
     * Send a message to all players tracking the chunk containing {@code pos}.
     * Used for BE -> GUI sync (e.g. InfuserSyncPacket).
     */
    public static void sendToTracking(ServerLevel level, BlockPos pos, Object msg) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), msg);
    }
}