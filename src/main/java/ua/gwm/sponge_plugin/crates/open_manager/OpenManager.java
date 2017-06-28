package ua.gwm.sponge_plugin.crates.open_manager;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.NamedCause;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.manager.Manager;

import java.util.Optional;

public abstract class OpenManager {

    private Optional<SoundType> open_sound = Optional.empty();

    protected OpenManager(Optional<SoundType> open_sound) {
        this.open_sound = open_sound;
    }

    public OpenManager(ConfigurationNode node) {
        ConfigurationNode open_sound_node = node.getNode("OPEN_SOUND");
        try {
            if (!open_sound_node.isVirtual()) {
                open_sound = Optional.of(open_sound_node.getValue(TypeToken.of(SoundType.class)));
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Exception creating Open Manager!", e);
        }
    }

    public boolean canOpen(Player player, Manager manager) {
        return player.hasPermission("gwm_crates.open." + manager.getId().toLowerCase());
    }

    public abstract void open(Player player, Manager manager);

    public Optional<SoundType> getOpenSound() {
        return open_sound;
    }

    public void setOpenSound(Optional<SoundType> open_sound) {
        this.open_sound = open_sound;
    }
}
