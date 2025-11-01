package com.goodbird.player2npc.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventLogger {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logCompanionSpawn(String companionName, String playerName, String worldName) {
        LOGGER.info("[{}] Companion '{}' spawned for player '{}' in world '{}'",
                LocalDateTime.now().format(FORMATTER), companionName, playerName, worldName);
    }

    public static void logCompanionDespawn(String companionName, String playerName, String reason) {
        LOGGER.info("[{}] Companion '{}' despawned for player '{}'. Reason: {}",
                LocalDateTime.now().format(FORMATTER), companionName, playerName, reason);
    }

    public static void logCompanionCommand(String companionName, String playerName, String command) {
        LOGGER.info("[{}] Player '{}' commanded companion '{}': {}",
                LocalDateTime.now().format(FORMATTER), playerName, companionName, command);
    }

    public static void logError(String context, Exception e) {
        LOGGER.error("[{}] Error in {}: {}", LocalDateTime.now().format(FORMATTER), context, e.getMessage(), e);
    }

    public static void logDebug(String message, Object... args) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[{}] {}", LocalDateTime.now().format(FORMATTER), String.format(message, args));
        }
    }

    public static void logWarning(String message, Object... args) {
        LOGGER.warn("[{}] {}", LocalDateTime.now().format(FORMATTER), String.format(message, args));
    }
}
