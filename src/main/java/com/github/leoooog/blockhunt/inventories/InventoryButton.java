package com.github.leoooog.blockhunt.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class InventoryButton {
    private Function<Player, ItemStack> iconCreator;
    private Consumer<InventoryClickEvent> eventConsumer;

    public static InventoryButton closeButton() {
        return new InventoryButton()
                .creator(p -> {
                    ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§cClose");
                    item.setItemMeta(meta);
                    return item;
                })
                .consumer(event -> {
                    event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                });
    }

    public static InventoryButton emptyButton(ItemStack item) {
        return new InventoryButton()
                .creator(p -> item)
                .consumer(event -> {
                });
    }

    public static InventoryButton booleanButton(String displayName, Supplier<Boolean> get,
            Consumer<Boolean> set) {
        return new InventoryButton()
                .creator(p -> booleanItemStack(displayName, get.get()))
                .consumer(event -> {
                    boolean value = get.get();
                    set.accept(!value);
                    event.getInventory().setItem(event.getSlot(), booleanItemStack(displayName, !value));
                });
    }

    private static ItemStack booleanItemStack(String displayName, boolean value) {
        ItemStack item = new ItemStack(
                value ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(value ? List.of("§aTrue") : List.of("§cFalse"));
        item.setItemMeta(meta);
        return item;
    }


    public InventoryButton creator(Function<Player, ItemStack> iconCreator) {
        this.iconCreator = iconCreator;
        return this;
    }

    public InventoryButton consumer(Consumer<InventoryClickEvent> eventConsumer) {
        this.eventConsumer = eventConsumer;
        return this;
    }

    public Function<Player, ItemStack> getIconCreator() {
        return iconCreator;
    }

    public Consumer<InventoryClickEvent> getEventConsumer() {
        return eventConsumer;
    }
}
