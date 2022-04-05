package com.jeff_media.messageapi;

import com.jeff_media.messageapi.message.Message;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language {

    //private final String name;
    final Map<String, Message> messages = new HashMap<>();

    Language(File file) throws InvalidConfigurationException, IOException {
        //this.name = file.getName().replaceFirst("[.][^.]+$", "");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(file);
        for (String key : yaml.getKeys(true)) {
            if(yaml.isConfigurationSection(key)) continue;
            final List<String> list;
            if(yaml.isString(key)) {
                list = Collections.singletonList(yaml.getString(key));
            } else {
                list = yaml.getStringList(key);
            }
            Message message = new Message(list);
            System.out.println("Loaded message: " + key + " -> " + message);
            messages.put(key, message);
        }
    }

}
