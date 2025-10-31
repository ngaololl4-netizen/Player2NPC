package com.goodbird.player2npc.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Identifier;

import java.io.File;

public class SkinManager {

    private static final Identifier STEVE_SKIN_ID = new Identifier("textures/entity/player/wide/steve.png");

    public static Identifier getSkinIdentifier(String skinUrl) {
        if (skinUrl == null || skinUrl.isEmpty()) {
            return STEVE_SKIN_ID;
        }

        Identifier location = ResourceDownloader.getUrlResourceLocation(skinUrl, true);

        if (MinecraftClient.getInstance().getTextureManager().getOrDefault(location, null) != null) {
            return location;
        }

        File cacheFile = ResourceDownloader.getUrlFile(skinUrl, true);
        ImageDownloadAlt downloader = new ImageDownloadAlt(cacheFile, skinUrl, location, STEVE_SKIN_ID, true, () -> {
        });
        ResourceDownloader.load(downloader);

        return STEVE_SKIN_ID;
    }

    public static void renderSkinHead(GuiGraphics graphics, int x, int y, int size, Identifier skinIdentifier) {
        RenderSystem.setShaderTexture(0, skinIdentifier);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        int faceU = 8;
        int faceV = 8;
        int faceWidth = 8;
        int faceHeight = 8;
        int textureWidth = 64;
        int textureHeight = 64;

        graphics.drawTexture(skinIdentifier, x, y, size, size, faceU, faceV, faceWidth, faceHeight, textureWidth, textureHeight);

        int hatU = 40;
        int hatV = 8;
        int hatWidth = 8;
        int hatHeight = 8;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.drawTexture(skinIdentifier, x, y, size, size, hatU, hatV, hatWidth, hatHeight, textureWidth, textureHeight);
        RenderSystem.disableBlend();
    }
}