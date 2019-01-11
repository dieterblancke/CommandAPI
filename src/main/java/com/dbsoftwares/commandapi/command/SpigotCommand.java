package com.dbsoftwares.commandapi.command;

import com.dbsoftwares.commandapi.event.CommandCreateEvent;
import com.dbsoftwares.commandapi.utils.MessageConfig;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.List;

@Getter
public abstract class SpigotCommand extends Command {

    protected String name;
    protected List<String> aliases;
    protected String permission;

    @Setter
    private MessageConfig messageConfig = new MessageConfig();

    public SpigotCommand(final String name) {
        this(name, Lists.newArrayList());
    }

    public SpigotCommand(final String name, final String... aliases) {
        this(name, Lists.newArrayList(aliases));
    }

    public SpigotCommand(final String name, final List<String> aliases) {
        this(name, aliases, null);
    }

    public SpigotCommand(final String name, final List<String> aliases, final String permission) {
        super(name);
        setAliases(aliases);

        this.name = name;
        this.aliases = aliases;
        this.permission = permission;

        Bukkit.getPluginManager().callEvent(new CommandCreateEvent(this));
    }

    @Override
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',
                    messageConfig.getNoPermissionMessage().replace("{permission}", permission)
            ));
            return false;
        }

        try {
            if (sender instanceof Player) {
                onExecute((Player) sender, args);
            } else {
                onExecute(sender, args);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> tabCompletion;
        if (sender instanceof Player) {
            tabCompletion = onTabComplete((Player) sender, args);
        } else {
            tabCompletion = onTabComplete(sender, args);
        }

        if (tabCompletion == null) {
            if (args.length == 0) {
                final List<String> list = Lists.newArrayList();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
                return list;
            } else {
                final String lastWord = args[args.length - 1];
                final List<String> list = Lists.newArrayList();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (StringUtil.startsWithIgnoreCase(p.getName(), lastWord)) {
                        list.add(p.getName());
                    }
                }

                return list;
            }
        }
        return tabCompletion;
    }

    public abstract List<String> onTabComplete(Player sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    public abstract void onExecute(Player player, String[] args);

    public abstract void onExecute(CommandSender sender, String[] args);

}