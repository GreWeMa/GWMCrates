package ua.gwm.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.key.Key;

import java.math.BigDecimal;
import java.util.Optional;

public class VirtualKey extends Key {

    private String virtual_name;

    public VirtualKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode virtual_name_node = node.getNode("VIRTUAL_NAME");
            if (virtual_name_node.isVirtual()) {
                throw new RuntimeException("VIRTUAL_NAME node does not exist!");
            }
            virtual_name = virtual_name_node.getString();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Virtual Key!", e);
        }
    }

    public VirtualKey(Optional<BigDecimal> price, Optional<String> id, String virtual_name) {
        super("VIRTUAL", id, price);
        this.virtual_name = virtual_name;
    }

    @Override
    public void add(Player player, int amount) {
        GWMCrates.getInstance().getVirtualKeysConfig().
                getNode(player.getUniqueId().toString(), virtual_name).setValue(get(player) + amount);
    }

    @Override
    public int get(Player player) {
        return GWMCrates.getInstance().getVirtualKeysConfig().
                getNode(player.getUniqueId().toString(), virtual_name).getInt(0);
    }

    public String getVirtualName() {
        return virtual_name;
    }

    public void setVirtualName(String virtual_name) {
        this.virtual_name = virtual_name;
    }
}
