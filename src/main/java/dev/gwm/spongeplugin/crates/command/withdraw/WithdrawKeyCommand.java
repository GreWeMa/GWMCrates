package dev.gwm.spongeplugin.crates.command.withdraw;

import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public final class WithdrawKeyCommand implements CommandExecutor {

    private final Language language;

    public WithdrawKeyCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.hasAny("f");
        boolean self = source.equals(player);
        if (self) {
            if (!source.hasPermission("gwm_crates.command.withdraw." + managerId + ".key")) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        } else {
            if (!source.hasPermission("gwm_crates.command.withdraw_others." + managerId + ".key")) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        }
        Key key = manager.getKey();
        int keyAmount = key.get(player);
        key.withdraw(player, Math.min(amount, keyAmount), force);
        if (self) {
            source.sendMessages(language.getTranslation("SUCCESSFULLY_WITHDREW_KEY", Arrays.asList(
                    new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                    new ImmutablePair<>("MANAGER_ID", manager.id())
            ), source));
        } else {
            source.sendMessages(language.getTranslation("SUCCESSFULLY_WITHDREW_OTHERS_KEY", Arrays.asList(
                    new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                    new ImmutablePair<>("MANAGER_ID", manager.id()),
                    new ImmutablePair<>("PLAYER_NAME", player.getName()),
                    new ImmutablePair<>("PLAYER_UUID", player.getUniqueId())
            ), source));
        }
        return CommandResult.success();
    }
}
