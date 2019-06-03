package org.gwmdevelopments.sponge_plugin.crates.command.commands.give;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class GiveEveryoneKeyCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.<Boolean>getOne(Text.of("force")).orElse(false);
        if (!src.hasPermission("gwm_crates.command.give_everyone.manager." + managerId + ".key")) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
            return CommandResult.success();
        }
        Key key = manager.getKey();
        if (!(key instanceof Giveable)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SSO_IS_NOT_GIVEABLE", src, null,
                    new Pair<>("%SUPER_OBJECT%", key)));
            return CommandResult.success();
        }
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            ((Giveable) key).give(player, amount, force);
            if (src.equals(player)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GOT_KEY", src, null,
                        new Pair<>("%MANAGER%", manager.getName())));
            } else {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GAVE_KEY", src, null,
                        new Pair<>("%MANAGER%", manager.getName()),
                        new Pair<>("%PLAYER%", player.getName())));
            }
        });
        return CommandResult.success();
    }
}