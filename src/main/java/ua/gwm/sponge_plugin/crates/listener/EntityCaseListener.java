package ua.gwm.sponge_plugin.crates.listener;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.caze.cases.EntityCase;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.preview.Preview;
import ua.gwm.sponge_plugin.library.utils.Pair;

import java.util.Optional;

public class EntityCaseListener {

    @Listener(order = Order.LATE)
    public void openEntityCase(InteractEntityEvent event) {
        Entity entity = event.getTargetEntity();
        Optional<Player> optional_player = event.getCause().first(Player.class);
        if (!optional_player.isPresent()) return;
        Player player = optional_player.get();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            Case caze = manager.getCase();
            if (!(caze instanceof EntityCase)) {
                continue;
            }
            if (((EntityCase) caze).getEntityUuid().equals(entity.getUniqueId())) {
                event.setCancelled(true);
                if (event instanceof InteractEntityEvent.Secondary.MainHand) {
                    OpenManager open_manager = manager.getOpenManager();
                    if (!open_manager.canOpen(player, manager)) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CAN_NOT_OPEN_CASE"));
                        return;
                    }
                    Key key = manager.getKey();
                    if (key.get(player) < 1) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_KEY"));
                        return;
                    }
                    key.add(player, -1);
                    open_manager.open(player, manager);
                    return;
                } else if (event instanceof InteractEntityEvent.Primary.MainHand) {
                    Optional<Preview> optional_preview = manager.getPreview();
                    if (!optional_preview.isPresent()) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_NOT_AVAILABLE",
                                new Pair<String, String>("%MANAGER%", manager.getName())));
                        return;
                    }
                    Preview preview = optional_preview.get();
                    if (!player.hasPermission("gwm_crates.preview." + manager.getId())) {
                        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                        return;
                    }
                    preview.preview(player, manager);
                    player.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_STARTED",
                            new Pair<String, String>("%MANAGER%", manager.getName())));
                    return;
                }
            }
        }
    }
}
