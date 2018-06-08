package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class SaveCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        GWMCrates.getInstance().save();
        src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_SAVED"));
        return CommandResult.success();
    }
}
