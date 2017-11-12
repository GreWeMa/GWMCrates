package ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues;

import ninja.leaping.configurate.ConfigurationNode;
import ua.gwm.sponge_plugin.crates.gui.AdvancedComboBox;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.Utils;

import javax.swing.*;

public abstract class OpenManagerConfigurationDialog extends ConfigurationDialog {

    private JLabel open_sound_label;
    private AdvancedComboBox open_sound_combo_box;

    public OpenManagerConfigurationDialog(String type, ConfigurationNode node) {
        super(SuperObjectType.OPEN_MANAGER, type, node);
        open_sound_label = new JLabel("Open Sound");
        open_sound_label.setLocation(8, 60);
        open_sound_label.setSize(190, 20);
        add(open_sound_label);
        open_sound_combo_box = new AdvancedComboBox(Utils.getSoundTypes(true));
        open_sound_combo_box.setLocation(8, 80);
        open_sound_combo_box.setSize(190, 20);
        add(open_sound_combo_box);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode open_sound_node = node.getNode("OPEN_SOUND");
            if (!open_sound_node.isVirtual()) {
                open_sound_combo_box.setSelectedItem(open_sound_node.getString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Open Manager Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode open_sound_node = node.getNode("OPEN_SOUND");
            String open_sound = open_sound_combo_box.getSelectedText();
            if (!open_sound.equals("NO SOUND")) {
                open_sound_node.setValue(open_sound);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Open Manager Configuration Dialog!", e);
        }
    }
}
