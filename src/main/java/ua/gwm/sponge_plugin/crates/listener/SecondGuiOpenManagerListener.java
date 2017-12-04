package ua.gwm.sponge_plugin.crates.listener;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.SecondOpenManager;
import ua.gwm.sponge_plugin.crates.util.Pair;
import ua.gwm.sponge_plugin.crates.util.UnsafeUtils;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SecondGuiOpenManagerListener {

    public static final HashSet<Container> SHOWN_GUI = new HashSet<Container>();

    @Listener
    public void controlClick(ClickInventoryEvent event) {
        Container container = event.getTargetInventory();
        if (SHOWN_GUI.contains(container)) {
            event.setCancelled(true);
            return;
        }
        if (SecondOpenManager.SECOND_GUI_INVENTORIES.containsKey(container)) {
            event.setCancelled(true);
            Optional<Player> optional_player = event.getCause().first(Player.class);
            if (!optional_player.isPresent()) return;
            Player player = optional_player.get();
            OrderedInventory ordered = (OrderedInventory) container.iterator().next();
            Pair<SecondOpenManager, Manager> pair = SecondOpenManager.SECOND_GUI_INVENTORIES.get(container);
            SecondOpenManager open_manager = pair.getKey();
            Manager manager = pair.getValue();
            for (SlotTransaction transaction : event.getTransactions()) {
                Slot slot = transaction.getSlot();
                if (Utils.isFirstInventory(container, slot)) {
                    SHOWN_GUI.add(container);
                    Drop drop = Utils.chooseDropByLevel(manager.getDrops(), player, false);
                    ItemStack drop_item = drop.getDropItem().orElse(ItemStack.of(ItemTypes.NONE, 1));
                    Sponge.getScheduler().createTaskBuilder().delayTicks(1).execute(() -> slot.set(drop_item)).
                            submit(GWMCrates.getInstance());
                    if (open_manager.isShowOtherDrops()) {
                        Sponge.getScheduler().createTaskBuilder().delayTicks(open_manager.getShowOtherDropsDelay()).execute(() -> {
                            for (Slot next : ordered.<Slot>slots()) {
                                if (!Objects.equals(next.getProperty(SlotIndex.class, "slotindex").get().getValue(),
                                        slot.getProperty(SlotIndex.class, "slotindex").get().getValue())) {
                                    next.set(Utils.chooseDropByLevel(manager.getDrops(), player, true).getDropItem().orElse(ItemStack.of(ItemTypes.NONE, 1)));
                                }
                            }
                        }).submit(GWMCrates.getInstance());
                    }
                    Sponge.getScheduler().createTaskBuilder().delay(1, TimeUnit.SECONDS).execute(() -> drop.apply(player)).submit(GWMCrates.getInstance());
                    open_manager.getClickSound().ifPresent(click_sound -> player.playSound(click_sound, player.getLocation().getPosition(), 1.));
                    PlayerOpenedCrateEvent opened_event = new PlayerOpenedCrateEvent(player, manager, drop);
                    Sponge.getEventManager().post(opened_event);
                    Sponge.getScheduler().createTaskBuilder().delayTicks(open_manager.getCloseDelay()).execute(() -> {
                        Optional<Container> optional_open_inventory = player.getOpenInventory();
                        if (optional_open_inventory.isPresent() && container.equals(optional_open_inventory.get())) {
                            UnsafeUtils.closeInventory(player);
                        }
                        SHOWN_GUI.remove(container);
                        SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
                    }).submit(GWMCrates.getInstance());
                    return;
                }
            }
        }
    }

    @Listener
    public void controlClose(InteractInventoryEvent.Close event) {
        Container container = event.getTargetInventory();
        Optional<Player> optional_player = event.getCause().first(Player.class);
        if (!optional_player.isPresent()) return;
        Player player = optional_player.get();
        if (SecondOpenManager.SECOND_GUI_INVENTORIES.containsKey(container) &&
                !SHOWN_GUI.contains(container)) {
            Pair<SecondOpenManager, Manager> pair = SecondOpenManager.SECOND_GUI_INVENTORIES.get(container);
            SecondOpenManager open_manager = pair.getKey();
            Manager manager = pair.getValue();
            if (open_manager.isForbidClose()) {
                event.setCancelled(true);
            } else if (open_manager.isGiveRandomOnClose()) {
                Drop drop = Utils.chooseDropByLevel(manager.getDrops(), player, false);
                drop.apply(player);
                PlayerOpenedCrateEvent opened_event = new PlayerOpenedCrateEvent(player, manager, drop);
                Sponge.getEventManager().post(opened_event);
                SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
            } else {
                PlayerOpenedCrateEvent opened_event = new PlayerOpenedCrateEvent(player, manager, null);
                Sponge.getEventManager().post(opened_event);
                SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
            }
        }
    }
}
