package com.jeff_media.messageapi;

import com.jeff_media.messageapi.message.Message;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language {

    final Map<String, Message> messages = new HashMap<>();

    Language(final ConfigurationSection config) {
        super();
        for (final String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) continue;
            final List<String> list;
            if (config.isString(key)) {
                list = Collections.singletonList(config.getString(key));
            } else {
                list = config.getStringList(key);
            }
            final Message message = new Message(list);
            System.out.println("Loaded message: " + key + " -> " + message);
            messages.put(key, message);
        }
    }

    static Language fromFile(final File file) throws InvalidConfigurationException, IOException {
        try (final FileInputStream inputStream = new FileInputStream(file)) {
            return fromInputStream(inputStream);
        }
    }

    static Language fromInputStream(final InputStream inputStream) throws IOException, InvalidConfigurationException {
        final YamlConfiguration yaml = new YamlConfiguration();
        try (final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            yaml.load(reader);
            return new Language(yaml);
        }
    }

}
