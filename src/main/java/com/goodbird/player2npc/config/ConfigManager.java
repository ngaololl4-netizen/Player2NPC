package com.goodbird.player2npc.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int SPAWN_RADIUS = 3;
    private static final int SPAWN_HEIGHT = 1;
    private static final int COMPANION_RENDER_RANGE = 64;
    private static final float COMPANION_SPEED = 0.4f;
    private static final float COMPANION_STEP_HEIGHT = 0.6f;
    private static final int ITEM_PICKUP_RANGE = 3;
    private static final boolean DO_HUNGER_TICKING = false;

    public static int getSpawnRadius() {
        return SPAWN_RADIUS;
    }

    public static int getSpawnHeight() {
        return SPAWN_HEIGHT;
    }

    public static int getCompanionRenderRange() {
        return COMPANION_RENDER_RANGE;
    }

    public static float getCompanionSpeed() {
        return COMPANION_SPEED;
    }

    public static float getCompanionStepHeight() {
        return COMPANION_STEP_HEIGHT;
    }

    public static int getItemPickupRange() {
        return ITEM_PICKUP_RANGE;
    }

    public static boolean isHungerTickingEnabled() {
        return DO_HUNGER_TICKING;
    }
}
