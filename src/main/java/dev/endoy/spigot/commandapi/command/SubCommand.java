/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package dev.endoy.spigot.commandapi.command;

import dev.endoy.spigot.commandapi.utils.MessageConfig;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Data
public abstract class SubCommand {

    private String name;
    private int minimumArgs;
    private int maximumArgs;

    public SubCommand(String name) {
        this(name, 0, 0);
    }

    public SubCommand(String name, int minimumArgs) {
        this(name, minimumArgs, minimumArgs);
    }

    public SubCommand(String name, int minimumArgs, int maximumArgs) {
        this.name = name;
        this.minimumArgs = minimumArgs;
        this.maximumArgs = maximumArgs;
    }

    public abstract String getUsage();

    public abstract String getPermission();

    public abstract void onExecute(Player player, String[] args);

    public abstract void onExecute(CommandSender sender, String[] args);

    private ConditionResult checkConditions(CommandSender sender, String[] args) {
        if (!args[0].equalsIgnoreCase(name)) {
            return ConditionResult.FAILURE_WRONG_NAME;
        }
        final int length = args.length - 1;

        if (length < minimumArgs || length > maximumArgs) {
            return ConditionResult.FAILURE_WRONG_ARGS_LENGTH;
        }
        if (!getPermission().isEmpty() && !sender.hasPermission(getPermission()) && !sender.hasPermission("*")) {
            return ConditionResult.FAILURE_PERMISSION;
        }
        return ConditionResult.SUCCESS;
    }

    public boolean execute(final CommandSender sender, String[] args, final MessageConfig config) {
        final ConditionResult result = checkConditions(sender, args);

        if (result == ConditionResult.FAILURE_WRONG_ARGS_LENGTH) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',
                    config.getUsageMessage().replace("{usage}", getUsage())
            ));
            return true;
        } else if (result == ConditionResult.FAILURE_PERMISSION) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',
                    config.getNoPermissionMessage().replace("{permission}", getPermission())
            ));
            return true;
        } else if (result == ConditionResult.SUCCESS) {
            if (sender instanceof Player) {
                onExecute((Player) sender, Arrays.copyOfRange(args, 1, args.length));
            } else {
                onExecute(sender, Arrays.copyOfRange(args, 1, args.length));
            }
            return true;
        }

        return false;
    }

    public abstract List<String> getCompletions(CommandSender sender, String[] args);

    public abstract List<String> getCompletions(Player player, String[] args);

    public enum ConditionResult {

        FAILURE_PERMISSION, FAILURE_WRONG_NAME, FAILURE_WRONG_ARGS_LENGTH, SUCCESS

    }
}