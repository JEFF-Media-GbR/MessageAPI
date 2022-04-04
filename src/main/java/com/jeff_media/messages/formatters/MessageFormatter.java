package com.jeff_media.messages.formatters;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public abstract class MessageFormatter {
    public abstract String format(String string, CommandSender player);
}
