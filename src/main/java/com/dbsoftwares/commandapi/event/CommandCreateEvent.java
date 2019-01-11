package com.dbsoftwares.commandapi.event;

import com.dbsoftwares.commandapi.command.SpigotCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private SpigotCommand command;

    public CommandCreateEvent(final SpigotCommand command) {
        this.command = command;
    }

    public SpigotCommand getCommand() {
        return command;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
