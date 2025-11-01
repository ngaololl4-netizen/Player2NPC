package com.goodbird.player2npc.companion;

import adris.altoclef.player2api.Character;
import adris.altoclef.player2api.utils.CharacterUtils;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CompanionManager implements Component, ServerTickingComponent {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final ComponentKey<CompanionManager> KEY = ComponentRegistry
            .getOrCreate(new Identifier("automatone", "companion_manager"), CompanionManager.class);

    private final ServerPlayerEntity _player;

    private final Map<String, UUID> _companionMap = new ConcurrentHashMap<>();

    private List<Character> _assignedCharacters = new ArrayList<>();
    private boolean _needsToSummon = false;

    public CompanionManager(ServerPlayerEntity player) {
        this._player = player;
    }

    public void summonAllCompanionsAsync() {
        _needsToSummon = true;
        CompletableFuture
                .supplyAsync(() -> CharacterUtils.requestCharacters(_player, "player2-ai-npc-minecraft"))
                .thenAcceptAsync(characters -> this._assignedCharacters = new ArrayList<>(Arrays.asList(characters)),
                        _player.getServer());
    }

    private void summonCompanions() {
        if (_assignedCharacters.isEmpty()) {
            return;
        }
        List<String> assignedNames = _assignedCharacters.stream().map(c -> c.name()).toList();
        List<String> toDismiss = new ArrayList<>();
        _companionMap.forEach((name, uuid) -> {
            if (!assignedNames.contains(name)) {
                toDismiss.add(name);
            }
        });
        toDismiss.forEach(this::dismissCompanion);

        this._assignedCharacters.stream().filter(character -> character != null).forEach(character -> {
            LOGGER.info("summonCompanions for character={}", character);
            this.ensureCompanionExists(character);
        });

        _assignedCharacters.clear();
    }

    public void ensureCompanionExists(Character character) {
        if (character == null) {
            LOGGER.warn("ensureCompanionExists called with null character");
            return;
        }

        LOGGER.info("ensureCompanionExists for character={}", character.name());

        if (_player.getWorld() == null || _player.getServer() == null) {
            LOGGER.warn("Cannot summon companion: world or server is null for player {}", _player.getName().getString());
            return;
        }

        UUID companionUuid = _companionMap.get(character.name());
        ServerWorld world = _player.getServerWorld();
        Entity existingCompanion = (companionUuid != null) ? world.getEntity(companionUuid) : null;

        BlockPos spawnPos = _player.getBlockPos().add(
                _player.getRandom().nextInt(3) - 1,
                1,
                _player.getRandom().nextInt(3) - 1);

        if (existingCompanion instanceof AutomatoneEntity && existingCompanion.isAlive()) {
            existingCompanion.teleport(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            LOGGER.info("Teleported existing companion: {} for player {}", character.name(), _player.getName().getString());
        } else {
            try {
                AutomatoneEntity newCompanion = new AutomatoneEntity(_player.getWorld(), character, _player);
                newCompanion.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        _player.getYaw(), 0);

                world.spawnEntity(newCompanion);
                _companionMap.put(character.name(), newCompanion.getUuid());
                LOGGER.info("Summoned new companion: {} for player {}", character.name(), _player.getName().getString());
            } catch (Exception e) {
                LOGGER.error("Failed to summon companion {} for player {}", character.name(), _player.getName().getString(), e);
            }
        }
    }

    public void dismissCompanion(String characterName) {
        if (characterName == null) {
            LOGGER.warn("dismissCompanion called with null character name");
            return;
        }

        UUID companionUuid = _companionMap.remove(characterName);
        if (companionUuid != null && _player.getServer() != null) {
            try {
                for (ServerWorld world : _player.getServer().getWorlds()) {
                    Entity companion = world.getEntity(companionUuid);
                    if (companion instanceof AutomatoneEntity) {
                        companion.discard();
                        LOGGER.info("Dismissed companion: {} for player {}", characterName, _player.getName().getString());
                        return;
                    }
                }
                LOGGER.debug("Companion {} not found in any world", characterName);
            } catch (Exception e) {
                LOGGER.error("Failed to dismiss companion {}", characterName, e);
            }
        }
    }

    public void dismissAllCompanions() {
        List<String> names = new ArrayList<>(_companionMap.keySet());
        names.forEach(this::dismissCompanion);
        _companionMap.clear();
    }

    public List<AutomatoneEntity> getActiveCompanions() {
        List<AutomatoneEntity> companions = new ArrayList<>();

        if (_player.getServer() == null) {
            LOGGER.debug("Cannot get active companions: server is null");
            return companions;
        }

        try {
            for (UUID uuid : _companionMap.values()) {
                if (uuid == null) continue;

                for (ServerWorld world : _player.getServer().getWorlds()) {
                    Entity entity = world.getEntity(uuid);
                    if (entity instanceof AutomatoneEntity companion && companion.isAlive()) {
                        companions.add(companion);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error retrieving active companions", e);
        }

        return companions;
    }

    @Override
    public void serverTick() {
        if (_needsToSummon) {
            summonCompanions();
            _needsToSummon = false;
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        NbtCompound companionsTag = tag.getCompound("companions");
        for (String key : companionsTag.getKeys()) {
            _companionMap.put(key, companionsTag.getUuid(key));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound companionsTag = new NbtCompound();
        _companionMap.forEach(companionsTag::putUuid);
        tag.put("companions", companionsTag);
    }
}