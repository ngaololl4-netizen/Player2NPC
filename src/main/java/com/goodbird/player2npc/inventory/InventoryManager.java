package com.goodbird.player2npc.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private List<ItemStack> items = new ArrayList<>();
    private int selectedSlot = 0;

    public InventoryManager() {
        for (int i = 0; i < 36; i++) {
            items.add(ItemStack.EMPTY);
        }
    }

    public void addItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        try {
            for (int i = 0; i < items.size(); i++) {
                ItemStack slot = items.get(i);
                if (slot.isEmpty()) {
                    items.set(i, stack.copy());
                    return;
                }

                if (slot.canCombine(stack) && slot.getCount() < slot.getMaxCount()) {
                    int added = Math.min(stack.getCount(), slot.getMaxCount() - slot.getCount());
                    slot.increment(added);
                    stack.decrement(added);

                    if (stack.isEmpty()) {
                        return;
                    }
                }
            }

            LOGGER.debug("Inventory full, cannot add item: {}", stack.getName().getString());
        } catch (Exception e) {
            LOGGER.error("Error adding item to inventory", e);
        }
    }

    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= items.size()) {
            LOGGER.warn("Invalid inventory slot: {}", slot);
            return ItemStack.EMPTY;
        }
        return items.get(slot);
    }

    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.size()) {
            LOGGER.warn("Invalid inventory slot: {}", slot);
            return;
        }

        if (stack == null) {
            items.set(slot, ItemStack.EMPTY);
        } else {
            items.set(slot, stack);
        }
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < items.size()) {
            selectedSlot = slot;
        }
    }

    public ItemStack getSelectedStack() {
        return getStack(selectedSlot);
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    public void clear() {
        items.clear();
        for (int i = 0; i < 36; i++) {
            items.add(ItemStack.EMPTY);
        }
        selectedSlot = 0;
    }

    public int getItemCount(String itemName) {
        int count = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty() && stack.getName().getString().contains(itemName)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public NbtCompound writeToNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("SelectedSlot", selectedSlot);
        tag.putInt("Size", items.size());

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                stack.writeNbt(itemTag);
                tag.put("Item" + i, itemTag);
            }
        }

        return tag;
    }

    public void readFromNbt(NbtCompound tag) {
        try {
            clear();
            selectedSlot = tag.getInt("SelectedSlot");
            int size = tag.getInt("Size");

            for (int i = 0; i < Math.min(size, 36); i++) {
                if (tag.contains("Item" + i)) {
                    ItemStack stack = ItemStack.fromNbt(tag.getCompound("Item" + i));
                    if (!stack.isEmpty()) {
                        items.set(i, stack);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error reading inventory from NBT", e);
        }
    }
}
