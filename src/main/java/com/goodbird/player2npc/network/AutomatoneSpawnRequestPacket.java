package com.goodbird.player2npc.network;

import adris.altoclef.player2api.Character;
import adris.altoclef.player2api.utils.CharacterUtils;
import com.goodbird.player2npc.Player2NPC;
import com.goodbird.player2npc.companion.CompanionManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




public class AutomatoneSpawnRequestPacket implements FabricPacket {
   private static final Logger LOGGER = LogManager.getLogger();
    public static final PacketType<AutomatonSpawnPacket> TYPE = PacketType.create(
            Player2NPC.SPAWN_REQUEST_PACKET_ID,
            AutomatonSpawnPacket::new
    );

    private final Character character;

    private AutomatoneSpawnRequestPacket(Character character) {
        this.character = character;
    }

    public AutomatoneSpawnRequestPacket(PacketByteBuf buf) {
        this.character = CharacterUtils.readFromBuf(buf);
    }

    public static Packet<ServerPlayPacketListener> create(Character character) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        new AutomatoneSpawnRequestPacket(character).write(buf);
        return ClientPlayNetworking.createC2SPacket(Player2NPC.SPAWN_REQUEST_PACKET_ID, buf);
    }

    @Override
    public void write(PacketByteBuf buf) {
        CharacterUtils.writeToBuf(buf, character);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        try {
            AutomatoneSpawnRequestPacket packet = new AutomatoneSpawnRequestPacket(buf);

            if (packet.character == null) {
                LOGGER.warn("Invalid spawn request from player {}: null character", player.getName().getString());
                return;
            }

            LOGGER.info("AutomatoneSpawnReqPacket from {}: character={}", player.getName().getString(), packet.character.name());

            server.execute(() -> {
                try {
                    CompanionManager manager = CompanionManager.KEY.get(player);
                    if (manager != null) {
                        manager.ensureCompanionExists(packet.character);
                    } else {
                        LOGGER.error("CompanionManager not found for player {}", player.getName().getString());
                    }
                } catch (Exception e) {
                    LOGGER.error("Error handling spawn request for player {}", player.getName().getString(), e);
                }
            });
        } catch (Exception e) {
            LOGGER.error("Error parsing spawn request packet", e);
        }
    }
}
