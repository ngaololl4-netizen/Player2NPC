package com.goodbird.player2npc.client.gui;

import adris.altoclef.player2api.Character;
import adris.altoclef.player2api.utils.CharacterUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class CharacterSelectionScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();

    private Character[] characters = null;
    private boolean isLoading = true;
    private String errorMessage = null;
    private long loadStartTime = 0;
    private static final long LOAD_TIMEOUT = 10000;

    public CharacterSelectionScreen() {
        super(Text.of("Select a Character"));
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        isLoading = true;
        errorMessage = null;
        loadStartTime = System.currentTimeMillis();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            errorMessage = "Player not found";
            isLoading = false;
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            try {
                return CharacterUtils.requestCharacters(client.player, "player2-ai-npc-minecraft");
            } catch (Exception e) {
                LOGGER.error("Failed to request characters", e);
                throw new RuntimeException("Failed to load characters: " + e.getMessage(), e);
            }
        })
        .thenAcceptAsync(result -> {
            if (result == null || result.length == 0) {
                errorMessage = "No characters available";
            } else {
                this.characters = result;
                client.execute(this::createCharacterCards);
            }
            this.isLoading = false;
        }, client)
        .exceptionally(e -> {
            LOGGER.error("Error loading characters", e);
            errorMessage = "Failed to load characters: " + e.getMessage();
            isLoading = false;
            return null;
        });
    }

    private void createCharacterCards() {
        if (characters == null || characters.length == 0) return;

        int cardWidth = 100;
        int cardHeight = 130;
        int padding = 30;
        int cardsPerRow = Math.max(1, (this.width - padding) / (cardWidth + padding));

        int totalWidth = cardsPerRow * (cardWidth + padding) - padding;
        int startX = this.width / 2 - totalWidth / 2;
        int startY = 70;

        int currentX = startX;
        int currentY = startY;

        for (Character character : characters) {
            if (character != null) {
                this.addDrawableChild(new CharacterCardWidget(currentX, currentY, cardWidth, cardHeight, character, this::onCharacterClicked));

                currentX += cardWidth + padding;
                if (currentX + cardWidth > startX + totalWidth) {
                    currentX = startX;
                    currentY += cardHeight + padding;
                }
            }
        }
    }

    private void onCharacterClicked(Character character) {
        if (this.client != null && character != null) {
            this.client.setScreen(new CharacterDetailScreen(this, character));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);

        graphics.drawCenteredShadowedText(this.textRenderer, "Select a Character", this.width / 2, 20, 0xFFFFFF);

        if (isLoading) {
            long elapsed = System.currentTimeMillis() - loadStartTime;
            String dots = ".".repeat((int) (elapsed / 500) % 4);
            graphics.drawCenteredShadowedText(this.textRenderer, "Loading" + dots, this.width / 2, this.height / 2, 0xAAAAAA);

            if (elapsed > LOAD_TIMEOUT) {
                errorMessage = "Character loading timed out";
                isLoading = false;
            }
        } else if (errorMessage != null) {
            graphics.drawCenteredShadowedText(this.textRenderer, "Error: " + errorMessage, this.width / 2, this.height / 2, 0xFF5555);
        } else if (characters != null && characters.length == 0) {
            graphics.drawCenteredShadowedText(this.textRenderer, "No characters available", this.width / 2, this.height / 2, 0xFFAA00);
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}