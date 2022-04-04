package com.jeff_media.messages;

import com.jeff_media.messages.formatters.MessageFormatter;
import com.jeff_media.messages.formatters.PluginMessageFormatter;
import com.jeff_media.messages.formatters.plugin.ItemsAdderFormatter;
import com.jeff_media.messages.formatters.plugin.PlaceholderAPIFormatter;
import com.jeff_media.messages.formatters.vanilla.LegacyChatColorFormatter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Msg {

    static final String LANGUAGE_FOLDER_NAME = "languages";
    private static final List<MessageFormatter> MESSAGE_FORMATTERS = new ArrayList<>();
    static File languageFolderFile;
    private static BukkitAudiences audience;
    private static Plugin plugin;
    private static Language language;

    public static void init(Plugin plugin, String language) {
        Msg.plugin = plugin;
        Msg.audience = BukkitAudiences.create(plugin);
        Msg.languageFolderFile = new File(plugin.getDataFolder(), LANGUAGE_FOLDER_NAME);
        Msg.languageFolderFile.mkdirs();
        registerMessageFormatters();
        saveLanguageFiles();
        File languageFile = LanguageFileUtils.getFile(language);
        if (languageFile == null) {
            plugin.getLogger().severe("Could not find language file \"" + language + "\". Falling back to included default translation.");
            languageFile = LanguageFileUtils.getFile("english.yml");
        }
        try {
            Msg.language = new Language(languageFile);
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().severe("Could not read language file \"" + languageFile.getName() + "\", please check if it's valid YAML.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String,Message> getMessages() {
        return language.messages;
    }

    private static void registerMessageFormatters() {
        registerPluginFormatter("PlaceholderAPI", PlaceholderAPIFormatter::new);
        registerPluginFormatter("ItemsAdder", ItemsAdderFormatter::new);
        registerFormatter(LegacyChatColorFormatter::new);
    }

    private static void saveLanguageFiles() {
        URL url = Msg.class.getResource("/" + LANGUAGE_FOLDER_NAME + "/");
        if (url == null) {
            throw new IllegalStateException(".jar file does not contain languages/ folder");
        }

        try (final InputStream inputStream = Msg.class.getResourceAsStream("/" + LANGUAGE_FOLDER_NAME + "/english.yml");
             final InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream))
        ) {
            YamlConfiguration includedEnglishTranslation = YamlConfiguration.loadConfiguration(inputStreamReader);
            for (Path includedFile : LanguageFileUtils.getAllIncludedTranslations()) {
                File alreadySavedFile = new File(languageFolderFile, includedFile.getFileName().toString());
                String resourceFileName = "/" + LANGUAGE_FOLDER_NAME + "/" + includedFile.getFileName().toString();
                try (InputStream includedFileAsStream = LanguageFileUtils.getFileFromResourceAsStream(resourceFileName);
                     InputStreamReader includedFileAsReader = new InputStreamReader(includedFileAsStream)
                ) {
                    final FileConfiguration includedTranslation = YamlConfiguration.loadConfiguration(includedFileAsReader);
                    if (!alreadySavedFile.exists()) {
                        LanguageFileUtils.copyFileToFile(includedFileAsStream, alreadySavedFile);
                        plugin.getLogger().info("Saved default translation file \"" + includedFile.getFileName().toString() + "\"");
                    }
                    final FileConfiguration alreadySavedTranslation = YamlConfiguration.loadConfiguration(alreadySavedFile);
                    if (LanguageFileUtils.merge(alreadySavedTranslation, includedTranslation, includedEnglishTranslation)) {
                        plugin.getLogger().info("Updated translation \"" + includedFile.getFileName().toString() + "\"");
                        alreadySavedTranslation.save(alreadySavedFile);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerFormatter(Supplier<MessageFormatter> supplier) {
        MESSAGE_FORMATTERS.add(supplier.get());
    }

    private static void registerPluginFormatter(String pluginName, PluginMessageFormatter.Constructor constructor) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            MESSAGE_FORMATTERS.add(constructor.create(plugin));
        }
    }

    public static Message get(String name) {
        return language.messages.get(name);
    }

    static List<MessageFormatter> getMessageFormatters() {
        return MESSAGE_FORMATTERS;
    }

    static BukkitAudiences getAudience() {
        return audience;
    }

}
