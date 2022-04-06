package com.jeff_media.oyster.formatters;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MessageFormatter {

    public abstract String format(@NotNull final String string, @Nullable final CommandSender player);

}
