package ua.gwm.sponge_plugin.crates.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.first_gui_decorative_items_change_mode.FirstGuiDecorativeItemsChangeMode;
import ua.gwm.sponge_plugin.crates.preview.Preview;

import java.util.HashMap;

public class GWMCratesRegistrationEvent implements Event {

    private HashMap<String, Class<? extends Case>> cases = new HashMap<String, Class<? extends Case>>();
    private HashMap<String, Class<? extends Key>> keys = new HashMap<String, Class<? extends Key>>();
    private HashMap<String, Class<? extends Drop>> drops = new HashMap<String, Class<? extends Drop>>();
    private HashMap<String, Class<? extends OpenManager>> open_managers = new HashMap<String, Class<? extends OpenManager>>();
    private HashMap<String, Class<? extends Preview>> previews = new HashMap<String, Class<? extends Preview>>();
    private HashMap<String, Class<? extends FirstGuiDecorativeItemsChangeMode>> first_gui_decorative_items_change_modes = new HashMap<String, Class<? extends FirstGuiDecorativeItemsChangeMode>>();

    @Override
    public Cause getCause() {
        return GWMCrates.getInstance().getDefaultCause();
    }

    public HashMap<String, Class<? extends Case>> getCases() {
        return cases;
    }

    public HashMap<String, Class<? extends Key>> getKeys() {
        return keys;
    }

    public HashMap<String, Class<? extends Drop>> getDrops() {
        return drops;
    }

    public HashMap<String, Class<? extends OpenManager>> getOpenManagers() {
        return open_managers;
    }

    public HashMap<String, Class<? extends Preview>> getPreviews() {
        return previews;
    }

    public HashMap<String, Class<? extends FirstGuiDecorativeItemsChangeMode>> getFirstGuiDecorativeItemsChangeModes() {
        return first_gui_decorative_items_change_modes;
    }
}
