package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.AbstractCase;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class EmptyCase extends AbstractCase {

    public EmptyCase(ConfigurationNode node) {
        super(node);
    }

    public EmptyCase(Optional<String> id) {
        super("EMPTY", id, true);
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        return 1;
    }
}
