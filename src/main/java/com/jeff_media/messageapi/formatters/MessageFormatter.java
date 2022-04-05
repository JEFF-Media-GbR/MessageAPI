package com.jeff_media.messageapi.formatters;

import org.bukkit.command.CommandSender;

public abstract class MessageFormatter {
    public abstract String format(String string, CommandSender player);
}
