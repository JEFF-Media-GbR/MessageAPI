package com.jeff_media.oyster.formatters.plugin;

import com.jeff_media.oyster.formatters.PluginMessageFormatter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIFormatter extends PluginMessageFormatter {

    public PlaceholderAPIFormatter(final Plugin plugin) {
        super(plugin);
    }

    @Override
    public String format(final @NotNull String string, final CommandSender sender) {
        return PlaceholderAPI.setPlaceholders(sender instanceof OfflinePlayer ? (OfflinePlayer) sender : null, string);
    }
}
