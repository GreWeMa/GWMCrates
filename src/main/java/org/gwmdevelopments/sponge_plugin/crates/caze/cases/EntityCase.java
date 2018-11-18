package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.AbstractCase;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public class EntityCase extends AbstractCase {

    private UUID entityUuid;
    private boolean startPreviewOnLeftClick;

    public EntityCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode entityUuidNode = node.getNode("ENTITY_UUID");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (entityUuidNode.isVirtual()) {
                throw new RuntimeException("ENTITY_UUID node does not exist!");
            }
            entityUuid = entityUuidNode.getValue(TypeToken.of(UUID.class));
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Entity Case!", e);
        }
    }

    public EntityCase(Optional<String> id,
                      UUID entityUuid, boolean startPreviewOnLeftClick) {
        super("ENTITY", id, true);
        this.entityUuid = entityUuid;
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        return 1;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }

    public void setStartPreviewOnLeftClick(boolean startPreviewOnLeftClick) {
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
    }
}
