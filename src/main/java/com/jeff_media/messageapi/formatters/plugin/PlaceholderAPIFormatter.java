package com.jeff_media.messageapi.formatters.plugin;

import com.jeff_media.messageapi.formatters.PluginMessageFormatter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIFormatter extends PluginMessageFormatter {

    public PlaceholderAPIFormatter(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String format(String string, CommandSender sender) {
        return PlaceholderAPI.setPlaceholders(sender instanceof OfflinePlayer ? (OfflinePlayer) sender : null, string);
    }
}
