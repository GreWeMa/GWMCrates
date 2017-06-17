package ua.gwm.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

public class ItemCase extends Case {

    protected ItemStack item;
    protected boolean start_preview_on_left_click = false;

    public ItemCase(ConfigurationNode node) {
        super(node);
        ConfigurationNode item_node = node.getNode("ITEM");
        ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
        if (item_node.isVirtual()) {
            throw new RuntimeException("ITEM node does not exist!");
        }
        item = GWMCratesUtils.parseItem(item_node);
        start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
    }

    public ItemCase(Optional<BigDecimal> price, ItemStack item) {
        super(price);
        this.item = item;
    }

    @Override
    public void add(Player player, int amount) {
        if (amount > 0) {
            ItemStack copy = item.copy();
            copy.setQuantity(amount);
            player.getInventory().offer(copy);
        } else if (amount < 0) {
            amount = -amount;
            Inventory inventory = player.getInventory();
            Iterator<Slot> slot_iterator = inventory.<Slot>slots().iterator();
            while (amount > 0 && slot_iterator.hasNext()) {
                Slot slot = slot_iterator.next();
                Optional<ItemStack> optional_item = slot.peek();
                if (optional_item.isPresent()) {
                    ItemStack item = optional_item.get();
                    if (GWMCratesUtils.itemStacksEquals(this.item, item)) {
                        int item_quantity = item.getQuantity();
                        if (item_quantity > amount) {
                            item.setQuantity(item_quantity - amount);
                            slot.set(item);
                            amount = 0;
                        } else {
                            slot.set(ItemStack.empty());
                            amount -= item_quantity;
                        }
                    }
                }
            }
        }
    }

    @Override
    public int get(Player player) {
        int amout = 0;
        Inventory inventory = player.getInventory();
        Iterator<Slot> slot_iterator = inventory.<Slot>slots().iterator();
        while (slot_iterator.hasNext()) {
            Slot slot = slot_iterator.next();
            Optional<ItemStack> optional_item = slot.peek();
            if (optional_item.isPresent()) {
                ItemStack item = optional_item.get();
                if (GWMCratesUtils.itemStacksEquals(this.item, item)) {
                    amout += item.getQuantity();
                }
            }
        }
        return amout;
    }

    public ItemStack getItem() {
        return item.copy();
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean isStartPreviewOnLeftClick() {
        return start_preview_on_left_click;
    }

    public void setStartPreviewOnLeftClick(boolean start_preview_on_left_click) {
        this.start_preview_on_left_click = start_preview_on_left_click;
    }
}
