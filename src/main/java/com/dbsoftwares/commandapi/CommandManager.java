package com.dbsoftwares.commandapi;

import com.dbsoftwares.commandapi.command.SpigotCommand;
import com.dbsoftwares.commandapi.exception.CommandException;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class CommandManager {

    // TODO: find a better way to handle messages ...

    private static CommandManager instance = new CommandManager();
    private Field commandMap;

    private CommandManager() {
        try {
            commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new CommandException("Could not find commandMap, please contact the Developer.");
        }
    }

    public static CommandManager getInstance() {
        return instance;
    }

    public void registerCommand(final Class<? extends SpigotCommand> clazz) {
        Validate.isTrue(clazz.getConstructors().length == 0, "No constructors available in class " + clazz.getSimpleName());

        final Constructor<? extends SpigotCommand> ctor;
        try {
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new CommandException("No default constructor (no parameters) has been found in class " + clazz.getSimpleName());
        }

        final SpigotCommand command;
        try {
            command = ctor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CommandException("Could not create new instance for class " + clazz.getSimpleName(), e);
        }

        registerCommand(command);
    }

    public void registerCommand(final SpigotCommand command) {
        try {
            final CommandMap map = (CommandMap) commandMap.get(Bukkit.getServer());
            unregisterCommands(map, command.getName(), command.getAliases());

            map.register(command.getName(), "centrixcore", command);

            Bukkit.getLogger().info("DBSoftwares - CommandAPI |-> Registered command: " + command.getName() + "!");
        } catch (Exception e) {
            throw new CommandException("Could not register command " + command.getName(), e);
        }
    }

    public void unregisterCommand(final SpigotCommand command) {
        try {
            final CommandMap map = (CommandMap) commandMap.get(Bukkit.getServer());

            command.unregister(map);
        } catch (Exception e) {
            throw new CommandException("Could not unregister command " + command.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void unregisterCommands(CommandMap map, String command, List<String> aliases) {
        try {
            Field field;
            try {
                field = map.getClass().getDeclaredField("knownCommands");
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                try {
                    field = map.getClass().getSuperclass().getDeclaredField("knownCommands");
                    field.setAccessible(true);
                } catch (NoSuchFieldException e2) {
                    throw new CommandException("Could not unregister commands for " + command, e2);
                }
            }

            final Map<String, Command> commands = (Map<String, Command>) field.get(map);

            commands.remove(command);
            aliases.forEach(commands::remove);
            field.set(map, commands);
        } catch (Exception e) {
            throw new CommandException("Could not unregister commands for " + command, e);
        }
    }
}
