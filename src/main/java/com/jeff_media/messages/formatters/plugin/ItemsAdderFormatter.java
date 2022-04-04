package com.jeff_media.messages.formatters.plugin;

import com.jeff_media.messages.formatters.PluginMessageFormatter;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ItemsAdderFormatter extends PluginMessageFormatter {

    public ItemsAdderFormatter(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String format(String string, CommandSender player) {
        return FontImageWrapper.replaceFontImages(string);
    }
}
