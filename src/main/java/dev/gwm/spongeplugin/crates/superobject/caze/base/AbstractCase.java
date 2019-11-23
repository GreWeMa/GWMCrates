package dev.gwm.spongeplugin.crates.superobject.caze.base;

import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class AbstractCase extends AbstractSuperObject implements Case {

    private final boolean doNotWithdraw;

    public AbstractCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode doNotWithdrawNode = node.getNode("DO_NOT_WITHDRAW");
            doNotWithdraw = doNotWithdrawNode.getBoolean(false);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractCase(String id, boolean doNotWithdraw) {
        super(id);
        this.doNotWithdraw = doNotWithdraw;
    }

    @Override
    public final SuperObjectCategory<Case> category() {
        return GWMCratesSuperObjectCategories.CASE;
    }

    @Override
    public boolean isDoNotWithdraw() {
        return doNotWithdraw;
    }
}
