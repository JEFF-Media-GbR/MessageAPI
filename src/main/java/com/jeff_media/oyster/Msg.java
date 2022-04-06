package com.jeff_media.oyster;

import com.jeff_media.oyster.formatters.FormattingPhase;
import com.jeff_media.oyster.formatters.MessageFormatter;
import com.jeff_media.oyster.formatters.PluginMessageFormatter;
import com.jeff_media.oyster.formatters.plugin.ItemsAdderFormatter;
import com.jeff_media.oyster.formatters.plugin.PlaceholderAPIFormatter;
import com.jeff_media.oyster.formatters.standalone.HexColorCodeFormatter;
import com.jeff_media.oyster.formatters.standalone.SingleColorCodeFormatter;
import com.jeff_media.oyster.message.Message;
import com.jeff_media.oyster.message.TitleMessage;
import com.jeff_media.oyster.utils.LanguageFileUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Main OysterMessage class. This should be the entry point for everything you do using this API.
 */
public class Msg {

    private static final String LANGUAGE_FOLDER_NAME = "languages";
    private static final EnumMap<FormattingPhase,List<MessageFormatter>> MESSAGE_FORMATTERS = new EnumMap<>(FormattingPhase.class);
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

    /**
     * Initialize MessageAPI. Required before other methods can be used.
     * @param plugin Your plugin's instance
     * @param language Language file to use. The file extension can be omitted if it's .yml or .yaml. If it doesn't exist, or is invalid, it falls back to the saved or included "english.yml" file.
     */
    public static void init(@NotNull final Plugin plugin, @NotNull final String language) {
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
        registerFormatter(FormattingPhase.PARSE, HexColorCodeFormatter::new);
        registerFormatter(FormattingPhase.PARSE,SingleColorCodeFormatter::new);
        registerPluginFormatter(FormattingPhase.MANUAL,PlaceholderAPIFormatter::new, "PlaceholderAPI");
        registerPluginFormatter(FormattingPhase.MANUAL,ItemsAdderFormatter::new, "ItemsAdder");
        registerFormatter(FormattingPhase.BAKE, HexColorCodeFormatter::new);
        registerFormatter(FormattingPhase.BAKE,SingleColorCodeFormatter::new);
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

    private static void registerFormatter(final FormattingPhase phase, final Supplier<? extends MessageFormatter> supplier) {
        MESSAGE_FORMATTERS.computeIfAbsent(phase, __ -> new ArrayList<>()).add(supplier.get());
    }

    private static void registerPluginFormatter(final FormattingPhase phase, final PluginMessageFormatter.Constructor constructor, final String pluginName) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            registerFormatter(phase, () -> constructor.create(plugin));
        }
    }

    /**
     * Returns all {@link Message}s
     */
    @NotNull
    public static Map<String, Message> getMessages() {
        return language.messages;
    }

    /**
     * Gets a {@link Message} by its fully qualified name
     */
    @Nullable
    public static Message get(@NotNull final String name) {
        return language.messages.get(name);
    }

    /**
     * Gets a {@link TitleMessage}, consisting of two {@link Message}s. Either of those can be null.
     */
    @NotNull
    public static TitleMessage getTitle(@Nullable final String titleName, @Nullable final String subTitleName) {
        final Message title = titleName == null ? Message.EMPTY : language.messages.get(titleName);
        final Message subTitle = subTitleName == null ? Message.EMPTY : language.messages.get(subTitleName);
        return new TitleMessage(title, subTitle);
    }

    /**
     * Gets a {@link List} of all registered {@link MessageFormatter}s
     */
    @NotNull
    public static List<MessageFormatter> getMessageFormatters(@NotNull final FormattingPhase phase) {
        return MESSAGE_FORMATTERS.get(phase);
    }

    @NotNull
    public static BukkitAudiences audience() {
        return audience;
    }

}
