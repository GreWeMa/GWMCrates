package ua.gwm.sponge_plugin.crates.command;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.gui.GWMCratesGUI;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.preview.Preview;
import ua.gwm.sponge_plugin.crates.util.LanguageUtils;
import ua.gwm.sponge_plugin.crates.util.Pair;
import ua.gwm.sponge_plugin.crates.util.SuperObject;
import ua.gwm.sponge_plugin.crates.util.Utils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

//Someday I will rework it... Someday... I will...
public class GWMCratesCommand implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.split(" ");
        Optional<Player> optional_player = source instanceof Player ? Optional.of((Player) source) : Optional.empty();
        if (args.length == 0) {
            return CommandResult.empty();
        }
        switch (args[0].toLowerCase()) {
            case "help": {
                sendHelp(source);
                return CommandResult.success();
            }
            case "gui": {
                if (!(source instanceof ConsoleSource)) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                try {
                    GWMCratesGUI.initialize();
                } catch (Exception e) {
                    source.sendMessage(Text.builder("GWMCratesGUI already initialized (or some exception happened)!").color(TextColors.RED).build());
                    GWMCrates.getInstance().getLogger().warn("Exception when initializing GWMCratesGUI!", e);
                }
                return CommandResult.success();
            }
            case "save": {
                if (!source.hasPermission("gwm_crates.command.save")) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                GWMCrates.getInstance().save();
                source.sendMessage(LanguageUtils.getText("SUCCESSFULLY_SAVED"));
                return CommandResult.success();
            }
            case "reload": {
                if (!source.hasPermission("gwm_crates.command.reload")) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                GWMCrates.getInstance().reload();
                source.sendMessage(LanguageUtils.getText("SUCCESSFULLY_RELOADED"));
                return CommandResult.success();
            }
            case "open": {
                if (args.length != 2) {
                    sendHelp(source);
                    return CommandResult.empty();
                }
                String manager_id = args[1].toLowerCase();
                if (!optional_player.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("COMMAND_CAN_BE_EXECUTED_ONLY_BY_PLAYER"));
                    return CommandResult.success();
                }
                Player player = optional_player.get();
                Optional<Manager> optional_manager = Utils.getManager(manager_id);
                if (!optional_manager.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("MANAGER_NOT_EXIST",
                            new Pair<String, String>("%MANAGER_ID%", manager_id)));
                    return CommandResult.success();
                }
                Manager manager = optional_manager.get();
                if (!player.hasPermission("gwm_crates.open." + manager_id) ||
                        !player.hasPermission("gwm_crates.command_open." + manager_id)) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                OpenManager open_manager = manager.getOpenManager();
                if (!open_manager.canOpen(player, manager)) {
                    source.sendMessage(LanguageUtils.getText("CAN_NOT_OPEN_MANAGER"));
                    return CommandResult.success();
                }
                Case caze = manager.getCase();
                Key key = manager.getKey();
                if (caze.get(player) < 1) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_CASE"));
                    return CommandResult.success();
                }
                if (key.get(player) < 1) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_KEY"));
                    return CommandResult.success();
                }
                caze.add(player, -1);
                key.add(player, -1);
                open_manager.open(player, manager);
                return CommandResult.success();
            }
            case "force": {
                if (args.length < 2 || args.length > 3) {
                    sendHelp(source);
                    return CommandResult.empty();
                }
                Optional<Player> optional_target = Optional.empty();
                if (args.length == 3) {
                    String target_name = args[2];
                    optional_target = Sponge.getServer().getPlayer(target_name);
                    if (!optional_target.isPresent()) {
                        source.sendMessage(LanguageUtils.getText("PLAYER_NOT_EXIST",
                                new Pair<String, String>("%PLAYER%", target_name)));
                        return CommandResult.success();
                    }
                }
                String manager_id = args[1].toLowerCase();
                if (!optional_target.isPresent() && !optional_player.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("COMMAND_CAN_BE_EXECUTED_ONLY_BY_PLAYER"));
                    return CommandResult.success();
                }
                Optional<Manager> optional_manager = Utils.getManager(manager_id);
                if (!optional_manager.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("MANAGER_NOT_EXIST",
                            new Pair<String, String>("%MANAGER_ID%", manager_id)));
                    return CommandResult.success();
                }
                Manager manager = optional_manager.get();
                if ((!optional_target.isPresent() && !source.hasPermission("gwm_crates.open." + manager_id) ||
                        !source.hasPermission("gwm_crates.command_open." + manager_id) ||
                        !source.hasPermission("gwm_crates.force_open." + manager_id)) ||
                        optional_target.isPresent() && !source.hasPermission("gwm_crates.force_open_other." + manager_id)) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                OpenManager open_manager = manager.getOpenManager();
                if (!optional_target.isPresent() && optional_player.isPresent() &&
                        !open_manager.canOpen(optional_player.get(), manager)) {
                    source.sendMessage(LanguageUtils.getText("CAN_NOT_OPEN_MANAGER"));
                    return CommandResult.success();
                } else if (optional_target.isPresent() && !open_manager.canOpen(optional_target.get(), manager)) {
                    source.sendMessage(LanguageUtils.getText("PLAYER_CAN_NOT_OPEN_MANAGER",
                            new Pair<String, String>("%PLAYER%", optional_target.get().getName())));
                    return CommandResult.success();
                }
                if (!optional_target.isPresent()) {
                    open_manager.open(optional_player.get(), manager);
                    source.sendMessage(LanguageUtils.getText("CRATE_FORCE_OPENED",
                            new Pair<String, String>("%MANAGER%", manager.getName())));
                } else {
                    Player target = optional_target.get();
                    open_manager.open(target, manager);
                    source.sendMessage(LanguageUtils.getText("CRATE_FORCE_OPENED_FOR_PLAYER",
                            new Pair<String, String>("%MANAGER%", manager.getName()),
                            new Pair<String, String>("%PLAYER%", target.getName())));
                    if (GWMCrates.getInstance().getConfig().getNode("TELL_FORCE_CRATE_OPEN_INFO").getBoolean(true)) {
                        target.sendMessage(LanguageUtils.getText("CRATE_FORCE_OPENED_BY_PLAYER",
                                new Pair<String, String>("%PLAYER%", optional_player.get().getName()),
                                new Pair<String, String>("%MANAGER%", manager.getName())));
                    }
                }
                return CommandResult.success();
            }
            case "preview": {
                if (args.length != 2) {
                    sendHelp(source);
                    return CommandResult.empty();
                }
                String manager_id = args[1].toLowerCase();
                if (!optional_player.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("COMMAND_CAN_BE_EXECUTED_ONLY_BY_PLAYER"));
                    return CommandResult.success();
                }
                Player player = optional_player.get();
                Optional<Manager> optional_manager = Utils.getManager(manager_id);
                if (!optional_manager.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("MANAGER_NOT_EXIST",
                            new Pair<String, String>("%MANAGER_ID%", manager_id)));
                    return CommandResult.success();
                }
                Manager manager = optional_manager.get();
                Optional<Preview> optional_preview = manager.getPreview();
                if (!optional_preview.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("PREVIEW_NOT_AVAILABLE",
                            new Pair<String, String>("%MANAGER%", manager.getName())));
                    return CommandResult.success();
                }
                Preview preview = optional_preview.get();
                if (!player.hasPermission("gwm_crates.preview." + manager_id)) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                preview.preview(player, manager);
                player.sendMessage(LanguageUtils.getText("PREVIEW_STARTED",
                        new Pair<String, String>("%MANAGER%", manager.getName())));
                return CommandResult.success();
            }
            case "buy": {
                if (args.length < 3 || args.length > 5) {
                    sendHelp(source);
                    return CommandResult.empty();
                }
                if (!optional_player.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("COMMAND_CAN_BE_EXECUTED_ONLY_BY_PLAYER"));
                    return CommandResult.success();
                }
                Player player = optional_player.get();
                UUID uuid = player.getUniqueId();
                Optional<EconomyService> optional_economy_service = GWMCrates.getInstance().getEconomyService();
                if (!optional_economy_service.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("ECONOMY_SERVICE_NOT_FOUND"));
                    return CommandResult.success();
                }
                EconomyService economy_service = optional_economy_service.get();
                Optional<UniqueAccount> optional_player_account = economy_service.getOrCreateAccount(uuid);
                if (!optional_player_account.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("ECONOMY_ACCOUNT_NOT_FOUND"));
                    return CommandResult.success();
                }
                UniqueAccount player_account = optional_player_account.get();
                Currency currency = economy_service.getDefaultCurrency();
                BigDecimal money = player_account.getBalance(currency);
                String manager_id = args[2].toLowerCase();
                Optional<Manager> optional_manager = Utils.getManager(manager_id);
                if (!optional_manager.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("MANAGER_NOT_EXIST",
                            new Pair<String, String>("%MANAGER_ID%", manager_id)));
                    return CommandResult.success();
                }
                Manager manager = optional_manager.get();
                switch (args[1].toLowerCase()) {
                    case "case": {
                        if (args.length > 4) {
                            sendHelp(source);
                            return CommandResult.empty();
                        }
                        BigDecimal amount = new BigDecimal("1");
                        if (args.length == 4) {
                            try {
                                amount = new BigDecimal(args[3]);
                            } catch (Exception e) {
                                sendHelp(source);
                                return CommandResult.empty();
                            }
                        }
                        Case caze = manager.getCase();
                        Optional<BigDecimal> optional_price = caze.getPrice();
                        if (!optional_price.isPresent()) {
                            source.sendMessage(LanguageUtils.getText("CASE_NOT_FOR_SALE"));
                            return CommandResult.success();
                        }
                        if (!source.hasPermission("gwm_crates.buy.manager." + manager_id + ".case")) {
                            source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                            return CommandResult.success();
                        }
                        BigDecimal price = optional_price.get().multiply(amount);
                        if (price.compareTo(money) > 0) {
                            source.sendMessage(LanguageUtils.getText("NOT_ENOUGH_MONEY"));
                            return CommandResult.success();
                        }
                        player_account.withdraw(currency, price, GWMCrates.getInstance().getDefaultCause());
                        caze.add(player, amount.intValue());
                        player.sendMessage(LanguageUtils.getText("SUCCESSFULLY_BOUGHT_CASE",
                                new Pair<String, String>("%MANAGER%", manager.getName())));
                        return CommandResult.success();
                    }
                    case "key": {
                        if (args.length > 4) {
                            sendHelp(source);
                            return CommandResult.empty();
                        }
                        BigDecimal amount = new BigDecimal("1");
                        if (args.length == 4) {
                            try {
                                amount = new BigDecimal(args[3]);
                            } catch (Exception e) {
                                sendHelp(source);
                                return CommandResult.empty();
                            }
                        }
                        Key key = manager.getKey();
                        Optional<BigDecimal> optional_price = key.getPrice();
                        if (!optional_price.isPresent()) {
                            source.sendMessage(LanguageUtils.getText("KEY_NOT_FOR_SALE"));
                            return CommandResult.success();
                        }
                        if (!source.hasPermission("gwm_crates.buy.manager." + manager_id + ".key")) {
                            source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                            return CommandResult.success();
                        }
                        BigDecimal price = optional_price.get().multiply(amount);
                        if (price.compareTo(money) > 0) {
                            source.sendMessage(LanguageUtils.getText("NOT_ENOUGH_MONEY"));
                            return CommandResult.success();
                        }
                        player_account.withdraw(currency, price, GWMCrates.getInstance().getDefaultCause());
                        key.add(player, amount.intValue());
                        player.sendMessage(LanguageUtils.getText("SUCCESSFULLY_BOUGHT_KEY",
                                new Pair<String, String>("%MANAGER%", manager.getName())));
                        return CommandResult.success();
                    }
                    case "drop": {
                        if (args.length > 5) {
                            sendHelp(source);
                            return CommandResult.empty();
                        }
                        BigDecimal amount = new BigDecimal("1");
                        if (args.length == 5) {
                            try {
                                amount = new BigDecimal(args[4]);
                            } catch (Exception e) {
                                sendHelp(source);
                                return CommandResult.empty();
                            }
                        }
                        String drop_id = args[3].toLowerCase();
                        Optional<Drop> optional_drop = manager.getDropById(drop_id);
                        if (!optional_drop.isPresent()) {
                            source.sendMessage(LanguageUtils.getText("DROP_NOT_EXIST",
                                    new Pair<String, String>("%DROP_ID%", drop_id)));
                            return CommandResult.success();
                        }
                        if (!source.hasPermission("gwm_crates.buy.manager." + manager_id + ".drop." + drop_id)) {
                            source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                            return CommandResult.success();
                        }
                        Drop drop = optional_drop.get();
                        Optional<BigDecimal> optional_price = drop.getPrice();
                        if (!optional_price.isPresent()) {
                            source.sendMessage(LanguageUtils.getText("DROP_NOT_FOR_SALE"));
                            return CommandResult.success();
                        }
                        BigDecimal price = optional_price.get().multiply(amount);
                        if (price.compareTo(money) > 0) {
                            source.sendMessage(LanguageUtils.getText("NOT_ENOUGH_MONEY"));
                            return CommandResult.success();
                        }
                        player_account.withdraw(currency, price, GWMCrates.getInstance().getDefaultCause());
                        for (int i = 0; i < amount.intValue(); i++) {
                            drop.apply(player);
                        }
                        player.sendMessage(LanguageUtils.getText("SUCCESSFULLY_BOUGHT_DROP",
                                new Pair<String, String>("%DROP_ID%", drop_id),
                                new Pair<String, String>("%MANAGER%", manager.getName())));
                        return CommandResult.success();
                    }
                    default: {
                        sendHelp(source);
                        return CommandResult.empty();
                    }
                }
            }
            case "give": {
                if (args.length < 4 || args.length > 6) {
                    sendHelp(source);
                    return CommandResult.empty();
                }
                String manager_id = args[3].toLowerCase();
                Optional<Manager> optional_manager = Utils.getManager(manager_id);
                if (!optional_manager.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("MANAGER_NOT_EXIST",
                            new Pair<String, String>("%MANAGER_ID%", manager_id)));
                    return CommandResult.success();
                }
                Manager manager = optional_manager.get();
                String target_name = args[1];
                Optional<Player> optional_target = Sponge.getServer().getPlayer(target_name);
                if (!optional_target.isPresent()) {
                    source.sendMessage(LanguageUtils.getText("PLAYER_NOT_EXIST",
                            new Pair<String, String>("%PLAYER%", target_name)));
                    return CommandResult.success();
                }
                Player target = optional_target.get();
                switch (args[2].toLowerCase()) {
                    case "case": {
                        if (args.length > 5) {
                            sendHelp(source);
                            return CommandResult.empty();
                        }
                        BigDecimal amount = new BigDecimal("1");
                        if (args.length == 5) {
                            try {
                                amount = new BigDecimal(args[4]);
                            } catch (Exception e) {
                                sendHelp(source);
                                return CommandResult.empty();
                            }
                        }
                        Case caze = manager.getCase();
                        if (!source.hasPermission("gwm_crates.give." + manager_id + ".case")) {
                            source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                            return CommandResult.success();
                        }
                        caze.add(target, amount.intValue());
                        source.sendMessage(LanguageUtils.getText("SUCCESSFULLY_GIVE_CASE",
                                new Pair<String, String>("%MANAGER%", manager.getName()),
                                new Pair<String, String>("%PLAYER%", target_name)));
                        if (GWMCrates.getInstance().getConfig().getNode("TELL_GIVE_INFO").getBoolean(true)) {
                            source.sendMessage(LanguageUtils.getText("GET_CASE_BY_PLAYER",
                                    new Pair<String, String>("%MANAGER%", manager.getName()),
                                    new Pair<String, String>("%PLAYER%", source.getName())));
                        }
                        return CommandResult.success();
                    }
                    case "key": {
                        if (args.length > 5) {
                            sendHelp(source);
                            return CommandResult.empty();
                        }
                        BigDecimal amount = new BigDecimal("1");
                        if (args.length == 5) {
                            try {
                                amount = new BigDecimal(args[4]);
                            } catch (Exception e) {
                                sendHelp(source);
                                return CommandResult.empty();
                            }
                        }
                        Key key = manager.getKey();
                        if (!source.hasPermission("gwm_crates.give." + manager_id + ".key")) {
                            source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                            return CommandResult.success();
                        }
                        key.add(target, amount.intValue());
                        source.sendMessage(LanguageUtils.getText("SUCCESSFULLY_GIVE_KEY",
                                new Pair<String, String>("%MANAGER%", manager.getName()),
                                new Pair<String, String>("%PLAYER%", target_name)));
                        if (GWMCrates.getInstance().getConfig().getNode("TELL_GIVE_INFO").getBoolean(true)) {
                            source.sendMessage(LanguageUtils.getText("GET_KEY_BY_PLAYER",
                                    new Pair<String, String>("%MANAGER%", manager.getName()),
                                    new Pair<String, String>("%PLAYER%", source.getName())));
                        }
                        return CommandResult.success();
                    }
                    case "drop": {
                        if (args.length > 6) {
                            sendHelp(source);
                            return CommandResult.empty();
                        }
                        BigDecimal amount = new BigDecimal("1");
                        if (args.length == 6) {
                            try {
                                amount = new BigDecimal(args[5]);
                            } catch (Exception e) {
                                sendHelp(source);
                                return CommandResult.empty();
                            }
                        }
                        String drop_id = args[4].toLowerCase();
                        Optional<Drop> optional_drop = manager.getDropById(drop_id);
                        if (!optional_drop.isPresent()) {
                            source.sendMessage(LanguageUtils.getText("DROP_NOT_EXIST",
                                    new Pair<String, String>("%DROP_ID%", drop_id)));
                            return CommandResult.success();
                        }
                        Drop drop = optional_drop.get();
                        if (!source.hasPermission("gwm_crates.give." + manager_id + ".drop." + drop_id)) {
                            source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                            return CommandResult.success();
                        }
                        for (int i = 0; i < amount.intValue(); i++) {
                            drop.apply(target);
                        }
                        source.sendMessage(LanguageUtils.getText("SUCCESSFULLY_GIVE_DROP",
                                new Pair<String, String>("%DROP_ID%", drop_id),
                                new Pair<String, String>("%MANAGER%", manager.getName()),
                                new Pair<String, String>("%PLAYER%", target_name)));
                        if (GWMCrates.getInstance().getConfig().getNode("TELL_GIVE_INFO").getBoolean(true)) {
                            source.sendMessage(LanguageUtils.getText("GET_DROP_BY_PLAYER",
                                    new Pair<String, String>("%DROP_ID%", drop_id),
                                    new Pair<String, String>("%MANAGER%", manager.getName()),
                                    new Pair<String, String>("%PLAYER%", source.getName())));
                        }
                        return CommandResult.success();
                    }
                    default: {
                        sendHelp(source);
                        return CommandResult.empty();
                    }
                }
            }
            case "list": {
                if (!source.hasPermission("gwm_crates.command.list")) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                Iterator<Manager> manager_iterator = GWMCrates.getInstance().getCreatedManagers().iterator();
                Text.Builder message_builder = Text.builder();
                boolean has_next = manager_iterator.hasNext();
                while (has_next) {
                    Manager next = manager_iterator.next();
                    if (has_next = manager_iterator.hasNext()) {
                        message_builder.append(LanguageUtils.getText("MANAGER_LIST_FORMAT",
                                new Pair<String, String>("%MANAGER_ID%", next.getId()),
                                new Pair<String, String>("%MANAGER_NAME%", next.getName())));
                    } else {
                        message_builder.append(LanguageUtils.getText("LAST_MANAGER_LIST_FORMAT",
                                new Pair<String, String>("%MANAGER_ID%", next.getId()),
                                new Pair<String, String>("%MANAGER_NAME%", next.getName())));
                    }
                }
                if (LanguageUtils.exists("MANAGER_LIST_HEADER")) {
                    source.sendMessage(LanguageUtils.getText("MANAGER_LIST_HEADER"));
                }
                source.sendMessage(message_builder.build());
                if (LanguageUtils.exists("MANAGER_LIST_FOOTER")) {
                    source.sendMessage(LanguageUtils.getText("MANAGER_LIST_FOOTER"));
                }
                return CommandResult.success();
            }
            case "info": {
                if (args.length != 2) {
                    sendHelp(source);
                    return CommandResult.empty();
                }
                String manager_id = args[1].toLowerCase();
                Optional<Manager> optional_manager = Utils.getManager(manager_id);
                if (!optional_manager.isPresent()) {
                    source.sendMessages(LanguageUtils.getText("MANAGER_NOT_EXIST",
                            new Pair<String, String>("%MANAGER_ID%", manager_id)));
                    return CommandResult.success();
                }
                Manager manager = optional_manager.get();
                if (!source.hasPermission("gwm_crates.command.info." + manager_id)) {
                    source.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                Optional<Text> optional_custom_info = manager.getCustomInfo();
                if (optional_custom_info.isPresent()) {
                    source.sendMessage(optional_custom_info.get());
                    return CommandResult.success();
                }
                StringBuilder drops_builder = new StringBuilder();
                List<Drop> drop_list = manager.getDrops();
                for (int i = 0; i < drop_list.size(); i++) {
                    Drop drop = drop_list.get(i);
                    if (i != drop_list.size() - 1) {
                        drops_builder.append(LanguageUtils.getPhrase("DROP_LIST_FORMAT",
                                new Pair<>("%ID%", drop.getId().orElse("Unknown ID"))));
                    } else {
                        drops_builder.append(LanguageUtils.getPhrase("LAST_DROP_LIST_FORMAT",
                                new Pair<>("%ID%", drop.getId().orElse("Unknown ID"))));
                    }
                }
                source.sendMessages(LanguageUtils.getTextList("MANAGER_INFO_MESSAGE",
                        new Pair<>("%MANAGER_ID%", manager.getId()),
                        new Pair<>("%MANAGER_NAME%", manager.getName()),
                        new Pair<>("%CASE_TYPE%", manager.getCase().getType()),
                        new Pair<>("%KEY_TYPE%", manager.getKey().getType()),
                        new Pair<>("%OPEN_MANAGER_TYPE%", manager.getOpenManager().getType()),
                        new Pair<>("%PREVIEW_TYPE%", manager.getPreview().
                                map(SuperObject::getType).orElse("No preview")),
                        new Pair<>("%SEND_OPEN_MESSAGE%", manager.isSendOpenMessage()),
                        new Pair<>("%CUSTOM_OPEN_MESSAGE%", manager.getCustomOpenMessage().
                                orElse("No custom open message")),
                        new Pair<>("%DROPS%", drops_builder.toString())));
                return CommandResult.success();
            }
            default: {
                sendHelp(source);
                return CommandResult.empty();
            }
        }
    }

    private void sendHelp(CommandSource source) {
        LanguageUtils.getTextList("HELP_MESSAGE",
                new Pair<>("%VERSION%", GWMCrates.VERSION.toString())).
                forEach(source::sendMessage);
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> location) throws CommandException {
        return Collections.emptyList();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of();
    }
}
