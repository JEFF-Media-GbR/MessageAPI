package com.jeff_media.messageapi.formatters.plugin;

import com.jeff_media.messageapi.formatters.PluginMessageFormatter;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ItemsAdderFormatter extends PluginMessageFormatter {

    public ItemsAdderFormatter(final Plugin plugin) {
        super(plugin);
    }

    @Override
    public String format(final String string, final CommandSender player) {
        return FontImageWrapper.replaceFontImages(string);
    }
}
