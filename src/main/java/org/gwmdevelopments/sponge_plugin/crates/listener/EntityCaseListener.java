package org.gwmdevelopments.sponge_plugin.crates.listener;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.EntityCase;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.Optional;
import java.util.UUID;

public class EntityCaseListener {

    @Listener(order = Order.LATE)
    public void openEntityCase(InteractEntityEvent event, @First Player player) {
        UUID uuid = player.getUniqueId();
        Entity entity = event.getTargetEntity();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            Case caze = manager.getCase();
            if (!(caze instanceof EntityCase)) {
                continue;
            }
            if (((EntityCase) caze).getEntityUuid().equals(entity.getUniqueId())) {
                event.setCancelled(true);
                if (event instanceof InteractEntityEvent.Secondary.MainHand) {
                    if (!player.hasPermission("gwm_crates.open." + manager.getId())) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                        return;
                    }
                    long delay = GWMCratesUtils.getCrateOpenDelay(uuid);
                    if (delay > 0L) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CRATE_OPEN_DELAY",
                                new Pair<>("%TIME%", GWMCratesUtils.millisToString(delay))));
                        return;
                    }
                    OpenManager openManager = manager.getOpenManager();
                    if (!openManager.canOpen(player, manager)) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CAN_NOT_OPEN_MANAGER"));
                        return;
                    }
                    Key key = manager.getKey();
                    if (key.get(player) < 1) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_KEY"));
                        return;
                    }
                    key.add(player, -1);
                    GWMCratesUtils.updateCrateOpenDelay(uuid);
                    openManager.open(player, manager);
                    return;
                } else if (event instanceof InteractEntityEvent.Primary.MainHand) {
                    Optional<Preview> optionalPreview = manager.getPreview();
                    if (!optionalPreview.isPresent()) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_NOT_AVAILABLE",
                                new Pair<>("%MANAGER%", manager.getName())));
                        return;
                    }
                    Preview preview = optionalPreview.get();
                    if (!player.hasPermission("gwm_crates.preview." + manager.getId())) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                        return;
                    }
                    preview.preview(player, manager);
                    player.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_STARTED",
                            new Pair<>("%MANAGER%", manager.getName())));
                    return;
                }
            }
        }
    }
}
