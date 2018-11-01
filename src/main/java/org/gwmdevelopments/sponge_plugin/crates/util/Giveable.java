package org.gwmdevelopments.sponge_plugin.crates.util;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public interface Giveable {

    void give(Player player, int amount);

    default void give(Player player) {
        give(player, 1);
    }

    Optional<BigDecimal> getPrice();

    Optional<Currency> getSellCurrency();
}
