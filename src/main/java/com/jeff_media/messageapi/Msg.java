package com.jeff_media.messageapi;

import com.jeff_media.messageapi.formatters.MessageFormatter;
import com.jeff_media.messageapi.formatters.PluginMessageFormatter;
import com.jeff_media.messageapi.formatters.plugin.ItemsAdderFormatter;
import com.jeff_media.messageapi.formatters.plugin.PlaceholderAPIFormatter;
import com.jeff_media.messageapi.formatters.standalone.HexColorCodeFormatter;
import com.jeff_media.messageapi.formatters.standalone.SingleColorCodeFormatter;
import com.jeff_media.messageapi.message.Message;
import com.jeff_media.messageapi.message.TitleMessage;
import com.jeff_media.messageapi.utils.LanguageFileUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Msg {

    private static final String LANGUAGE_FOLDER_NAME = "languages";
    private static final List<MessageFormatter> MESSAGE_FORMATTERS = new ArrayList<>();
    static File languageFolderFile;
    private static BukkitAudiences audience;
    private static Plugin plugin;
    private static Logger logger;
    private static Language language;

    public static File getLanguageFolderFile() {
        return languageFolderFile;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getLanguageFolderName() {
        return LANGUAGE_FOLDER_NAME;
    }

    public static void init(final Plugin plugin, final String language) {
        Msg.plugin = plugin;
        logger = plugin.getLogger();
        audience = BukkitAudiences.create(plugin);
        languageFolderFile = new File(plugin.getDataFolder(), LANGUAGE_FOLDER_NAME);
        languageFolderFile.mkdirs();
        registerMessageFormatters();
        LanguageFileUtils.saveLanguageFiles();
        loadLanguageFile(language);
    }

    private static void registerMessageFormatters() {
        registerFormatter(HexColorCodeFormatter::new);
        registerPluginFormatter("PlaceholderAPI", PlaceholderAPIFormatter::new);
        registerPluginFormatter("ItemsAdder", ItemsAdderFormatter::new);
        registerFormatter(SingleColorCodeFormatter::new);
    }

    private static void loadLanguageFile(final String language) {
        File languageFile = LanguageFileUtils.getFile(language);
        if (languageFile == null) {
            plugin.getLogger().severe("Could not find language file \"" + language + "\". Falling back to included default translation.");
            languageFile = LanguageFileUtils.getFile("english.yml");
        }
        try {
            Msg.language = Language.fromFile(languageFile);
        } catch (final InvalidConfigurationException e) {
            plugin.getLogger().severe("Could not read language file \"" + Objects.requireNonNull(languageFile).getName() + "\", please check if it's valid YAML. Falling back to included default translation.");
            try {
                Msg.language = Language.fromInputStream(plugin.getResource(LANGUAGE_FOLDER_NAME + "/english.yml"));
            } catch (final IOException | InvalidConfigurationException ex) {
                throw new IllegalStateException("Could neither load the given language file, nor the saved default language file, nor the included default language file!");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerFormatter(final Supplier<? extends MessageFormatter> supplier) {
        MESSAGE_FORMATTERS.add(supplier.get());
    }

    private static void registerPluginFormatter(final String pluginName, final PluginMessageFormatter.Constructor constructor) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            MESSAGE_FORMATTERS.add(constructor.create(plugin));
        }
    }

    public static Map<String, Message> getMessages() {
        return language.messages;
    }

    public static Message get(final String name) {
        return language.messages.get(name);
    }

    public static TitleMessage getTitle(final String titleName, final String subTitleName) {
        final Message title = titleName == null ? Message.EMPTY : language.messages.get(titleName);
        final Message subTitle = subTitleName == null ? Message.EMPTY : language.messages.get(subTitleName);
        return new TitleMessage(title, subTitle);
    }

    public static List<MessageFormatter> getMessageFormatters() {
        return MESSAGE_FORMATTERS;
    }

    public static BukkitAudiences audience() {
        return audience;
    }

}
