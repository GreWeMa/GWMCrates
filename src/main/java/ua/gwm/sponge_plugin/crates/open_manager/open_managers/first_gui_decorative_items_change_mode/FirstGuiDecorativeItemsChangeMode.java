package ua.gwm.sponge_plugin.crates.open_manager.open_managers.first_gui_decorative_items_change_mode;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class FirstGuiDecorativeItemsChangeMode {

    private int change_delay = 10;
    private List<Integer> ignored_indices;

    public FirstGuiDecorativeItemsChangeMode(ConfigurationNode node) {
        try {
            ConfigurationNode change_delay_node = node.getNode("CHANGE_DELAY");
            ConfigurationNode ignored_indices_node = node.getNode("IGNORED_INDICES");
            change_delay = change_delay_node.getInt(10);
            ignored_indices = ignored_indices_node.getList(TypeToken.of(Integer.class), new ArrayList<Integer>());
        } catch (Exception e) {
            throw new RuntimeException("Exception creating First Gui Decorative Items Change Mode!", e);
        }
    }

    public FirstGuiDecorativeItemsChangeMode(int change_delay, List<Integer> ignored_indices) {
        this.change_delay = change_delay;
        this.ignored_indices = ignored_indices;
    }

    public abstract List<ItemStack> shuffle(List<ItemStack> decorative_items);

    public int getChangeDelay() {
        return change_delay;
    }

    public List<Integer> getIgnoredIndices() {
        return ignored_indices;
    }
}