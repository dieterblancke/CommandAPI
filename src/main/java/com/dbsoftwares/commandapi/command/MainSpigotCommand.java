package com.dbsoftwares.commandapi.command;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class MainSpigotCommand extends SpigotCommand {

    protected final List<SubCommand> subCommands;

    public MainSpigotCommand(final String name) {
        this(name, Lists.newArrayList());
    }

    public MainSpigotCommand(final String name, final String... aliases) {
        this(name, Lists.newArrayList(aliases));
    }

    public MainSpigotCommand(final String name, final List<String> aliases) {
        this(name, aliases, null);
    }

    public MainSpigotCommand(final String name, final List<String> aliases, final String permission) {
        this(name, aliases, permission, Lists.newArrayList());
    }

    public MainSpigotCommand(final String name, final List<String> aliases, final String permission, final List<SubCommand> subCommands) {
        super(name, aliases, permission);

        this.subCommands = subCommands;
    }

    @Override
    public List<String> onTabComplete(final Player player, final String[] args) {
        return onTabComplete((CommandSender) player, args);
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        List<String> completions = null;

        if (args.length == 0) {
            completions = subCommands.stream().map(SubCommand::getName).collect(Collectors.toList());
        } else if (args.length == 1) {
            completions = subCommands.stream()
                    .filter(subCommand -> subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(SubCommand::getName)
                    .collect(Collectors.toList());
        } else {
            final SubCommand command = findSubCommand(args[0]);

            if (command != null) {
                return command.getCompletions(sender, args);
            }
        }
        return completions;
    }

    @Override
    public void onExecute(final Player player, final String[] args) {
        this.onExecute((CommandSender) player, args);
    }

    @Override
    public void onExecute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            sendHelpList(sender);
            return;
        }
        for (SubCommand subCommand : subCommands) {
            if (subCommand.execute(sender, args, getMessageConfig())) {
                return;
            }
        }
        sendHelpList(sender);
    }

    private void sendHelpList(final CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                getMessageConfig().getHelpHeaderMessage().replace("{commandName}", getName())
        ));
        subCommands.forEach(cmd -> sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                getMessageConfig().getHelpFormat().replace("{usage}", cmd.getUsage())
        )));
    }

    protected SubCommand findSubCommand(String name) {
        return subCommands.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}