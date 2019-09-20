package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public class ForceCommand implements CommandExecutor {

    private final Language language;

    public ForceCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean self = source.equals(player);
        if (self) {
            if (!source.hasPermission("gwm_crates.command.force." + managerId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        } else {
            if (!source.hasPermission("gwm_crates.command.force_others." + managerId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        }
        OpenManager openManager = manager.getOpenManager();
        if (!openManager.canOpen(player, manager)) {
            if (self) {
                GWMCratesUtils.sendCannotOpenMessage(source, manager);
                return CommandResult.empty();
            } else {
                source.sendMessages(language.getTranslation("PLAYER_CANNOT_OPEN_MANAGER", Arrays.asList(
                        new Pair<>("MANAGER_NAME", manager.getName()),
                        new Pair<>("MANAGER_ID", manager.id()),
                        new Pair<>("PLAYER_NAME", player.getName()),
                        new Pair<>("PLAYER_UUID", player.getUniqueId())
                ), source));
                return CommandResult.empty();
            }
        }
        openManager.open(player, manager);
        if (self) {
            source.sendMessages(language.getTranslation("CRATE_FORCE_OPENED", Arrays.asList(
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.id())
            ), source));
        } else  {
            source.sendMessages(language.getTranslation("CRATE_FORCE_OPENED_FOR_PLAYER", Arrays.asList(
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.id()),
                    new Pair<>("PLAYER_NAME", player.getName()),
                    new Pair<>("PLAYER_UUID", player.getUniqueId())
            ), source));
        }
        return CommandResult.success();
    }
}
