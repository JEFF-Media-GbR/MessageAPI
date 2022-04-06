package com.jeff_media.oyster.formatters.plugin;

import com.jeff_media.oyster.formatters.PluginMessageFormatter;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ItemsAdderFormatter extends PluginMessageFormatter {

    public ItemsAdderFormatter(final Plugin plugin) {
        super(plugin);
    }

    @Override
    public String format(final @NotNull String string, final CommandSender player) {
        return FontImageWrapper.replaceFontImages(string);
    }
}
