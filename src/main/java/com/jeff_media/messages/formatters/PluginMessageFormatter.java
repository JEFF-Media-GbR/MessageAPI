package com.jeff_media.messages.formatters;

import org.bukkit.plugin.Plugin;

public abstract class PluginMessageFormatter extends MessageFormatter {

    private final Plugin plugin;

    public PluginMessageFormatter(Plugin plugin) {
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
