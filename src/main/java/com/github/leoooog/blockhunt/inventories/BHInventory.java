package com.github.leoooog.blockhunt.inventories;

import com.github.leoooog.blockhunt.BlockHuntPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class BHInventory implements InventoryHandler {

    protected final BlockHuntPlugin plugin = BlockHuntPlugin.getInstance();


    private Inventory inventory;
    private final Map<Integer, InventoryButton> buttons = new HashMap<>();
    private final boolean cancelClicks;

    protected final Player player;

    private final InventoryHandler parent;

    public BHInventory(Player player, boolean cancelClicks, InventoryHandler parent) {
        this.cancelClicks = cancelClicks;
        this.player = player;
        this.parent = parent;
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null)
            inventory = createInventory();
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public InventoryHandler getParent() {
        return parent;
    }

    public void addButton(int slot, InventoryButton button) {
        buttons.put(slot, button);
    }

    public void removeButton(int slot) {
        buttons.remove(slot);
    }

    public InventoryButton getButton(int slot) {
        return buttons.get(slot);
    }

    public void decorate() {
        buttons.forEach((slot, button) -> inventory.setItem(slot, button.getIconCreator().apply(player)));
        player.updateInventory();
    }

    public void decorateSlot(int slot) {
        InventoryButton button = buttons.get(slot);
        if (button != null) {
            inventory.setItem(slot, button.getIconCreator().apply(player));
        }
        else {
            inventory.setItem(slot, null);
        }
        player.updateInventory();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(cancelClicks);
        int slot = event.getRawSlot();
        InventoryButton button = buttons.get(slot);
        if (button != null) {
            event.setCancelled(true);
            button.getEventConsumer().accept(event);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        decorate();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    protected abstract Inventory createInventory();

}
