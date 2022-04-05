package com.jeff_media.messageapi.formatters;

import org.bukkit.plugin.Plugin;

public abstract class PluginMessageFormatter extends MessageFormatter {

    private final Plugin plugin;

    public PluginMessageFormatter(final Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    protected Plugin getPlugin() {
        return plugin;
    }

    @FunctionalInterface
    public interface Constructor {
        PluginMessageFormatter create(Plugin plugin);
    }
}
