package dev.gwm.spongeplugin.crates.command.give;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Optional;

public class GiveDropCommand implements CommandExecutor {

    private final Language language;

    public GiveDropCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        String dropId = args.<String>getOne(Text.of("drop")).get();
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean self = source.equals(player);
        Optional<Drop> optionalDrop = manager.getDropById(dropId);
        if (!optionalDrop.isPresent()) {
            source.sendMessages(language.getTranslation("DROP_IS_NOT_FOUND", Arrays.asList(
                    new Pair<>("DROP_ID", dropId),
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.getId())
            ), source));
            return CommandResult.empty();
        }
        Drop drop = optionalDrop.get();
        String dropCustomName = drop.getCustomName().orElse(dropId);
        if (self) {
            if (!source.hasPermission("gwm_crates.command.give." + managerId + ".drop." + dropId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        } else {
            if (!source.hasPermission("gwm_crates.command.give_others." + managerId + ".drop." + dropId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        }
        drop.give(player, amount);
        if (self) {
            source.sendMessages(language.getTranslation("SUCCESSFULLY_GOT_DROP", Arrays.asList(
                    new Pair<>("DROP_ID", dropId),
                    new Pair<>("DROP_CUSTOM_NAME", dropCustomName),
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.getId())
            ), source));
        } else {
            source.sendMessages(language.getTranslation("SUCCESSFULLY_GAVE_DROP", Arrays.asList(
                    new Pair<>("DROP_ID", dropId),
                    new Pair<>("DROP_CUSTOM_NAME", dropCustomName),
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.getId()),
                    new Pair<>("PLAYER_NAME", player.getName()),
                    new Pair<>("PLAYER_UUID", player.getUniqueId())
            ), source));
        }
        return CommandResult.success();
    }
}