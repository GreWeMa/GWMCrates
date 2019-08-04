package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.sql.SQLException;

public class ImportToMySQLCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        boolean async = args.hasAny("a");
        src.sendMessage(GWMCrates.getInstance().getLanguage().getText("STARTING_IMPORT_TO_MYSQL", src, null));
        if (async) {
            GWMCratesUtils.asyncImportToMySQL();
        } else {
            try {
                long time = GWMCratesUtils.importToMySQL();
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("IMPORT_TO_MYSQL_SUCCESSFUL", src, null,
                        new Pair<>("%TIME%", GWMCratesUtils.millisToString(time))));
            } catch (SQLException e) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("IMPORT_TO_MYSQL_FAILED", src, null));
                GWMCrates.getInstance().getLogger().warn("Async import to MySQL failed!", e);
            }
        }
        return CommandResult.success();
    }
}
