package com.jeff_media.oyster.utils;

import com.jeff_media.oyster.Msg;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LanguageFileUtils {

    public static File getFile(final String name) {
        final File folder = Msg.getLanguageFolderFile();
        final File[] files = folder.listFiles((dir, test) -> test.equalsIgnoreCase(name) || test.equalsIgnoreCase(name + ".yml") || test.equalsIgnoreCase(name + ".yaml"));
        if (files != null && files.length > 0) return files[0];
        return null;
    }

    public static void saveLanguageFiles() {
        final URL url = Msg.class.getResource("/" + Msg.getLanguageFolderName() + "/");
        if (url == null) {
            throw new IllegalStateException(".jar file does not contain languages/ folder");
        }

        try (final InputStream inputStream = Msg.class.getResourceAsStream("/" + Msg.getLanguageFolderName() + "/english.yml"); final InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8)) {
            final YamlConfiguration includedEnglishTranslation = YamlConfiguration.loadConfiguration(inputStreamReader);
            for (final Path includedFile : getAllIncludedTranslations()) {
                final File alreadySavedFile = new File(Msg.getLanguageFolderFile(), includedFile.getFileName().toString());
                final String resourceFileName = "/" + Msg.getLanguageFolderName() + "/" + includedFile.getFileName().toString();
                if (!alreadySavedFile.exists()) {
                    try (final InputStream includedFileAsStream = getFileFromResourceAsStream(resourceFileName)) {

                        copyFileToFile(includedFileAsStream, alreadySavedFile);
                        Msg.getLogger().info("Saved default translation file \"" + includedFile.getFileName().toString() + "\"");

                    } catch (final IOException exception) {
                        exception.printStackTrace();
                    }
                }

                try (final InputStream includedFileAsStream = getFileFromResourceAsStream(resourceFileName); final InputStreamReader includedFileAsReader = new InputStreamReader(includedFileAsStream, StandardCharsets.UTF_8)) {


                    final FileConfiguration includedTranslation = YamlConfiguration.loadConfiguration(includedFileAsReader);
                    final YamlConfiguration alreadySavedTranslation = new YamlConfiguration();
                    boolean isValid = false;
                    try {
                        alreadySavedTranslation.load(alreadySavedFile);
                        isValid = true;
                    } catch (final InvalidConfigurationException exception) {
                        Msg.getLogger().severe(" ");
                        Msg.getLogger().severe("Could not load translation file \"" + alreadySavedFile.getName() + "\" because it's broken.");
                        Msg.getLogger().severe("Paste that file on http://www.yamllint.com/ to find out where the problem is. ");
                        Msg.getLogger().severe("The file will not be loaded nor updated until you have fixed the problem.");
                        Msg.getLogger().severe("You can also delete the file to get a fresh copy if you don't mind losing your changes.");
                        Msg.getLogger().severe(" ");
                    }
                    if (!isValid) continue;
                    if (merge(alreadySavedTranslation, includedTranslation, includedEnglishTranslation)) {
                        Msg.getLogger().info("Updated translation \"" + includedFile.getFileName().toString() + "\"");
                        //alreadySavedTranslation.save(alreadySavedFile);
                        final String yamlDump = alreadySavedTranslation.saveToString();
                        try (final InputStream target = new ByteArrayInputStream(yamlDump.getBytes(StandardCharsets.UTF_8))) {
                            copyFileToFile(target, alreadySavedFile);
                        }
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Path> getAllIncludedTranslations() {
        try {
            return getPathsFromResourceJar(Msg.getLanguageFolderName() + "/");
        } catch (final URISyntaxException | IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static InputStream getFileFromResourceAsStream(String fileName) {
        final ClassLoader classLoader = Msg.class.getClassLoader();
        if (fileName.startsWith("/")) fileName = fileName.substring(1);
        final InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("Included file not found: " + fileName);
        } else {
            return inputStream;
        }
    }

    public static void copyFileToFile(final InputStream stream, final File output) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)); final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8); final BufferedWriter bufferedWriter = new BufferedWriter(writer/*, StandardCharsets.UTF_8*/)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replaceFirst("^(\\s*)\"(.+?)\":", "$1$2:");
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean merge(final YamlConfiguration savedFile, final ConfigurationSection includedTranslation, final ConfigurationSection includedEnglishTranslation) {
        boolean changed = false;
        hackDumperOptions(savedFile);
        for (final String key : includedEnglishTranslation.getKeys(true)) {
            final Object includedEnglish = includedEnglishTranslation.get(key);
            final Object included = includedTranslation.get(key, includedEnglish);
            if (savedFile.isSet(key) && Objects.equals(savedFile.get(key), includedEnglish) && !Objects.equals(included, includedEnglish)) {
                changed = true;
                savedFile.set(key, included);
            }
            if (!savedFile.isSet(key)) {
                changed = true;
                savedFile.set(key, included);
            }
            final List<String> defaultComments = includedEnglishTranslation.getComments(key);
            final List<String> existingComments = savedFile.getComments(key);
            if (!defaultComments.equals(existingComments)) {
                changed = true;
                savedFile.setComments(key, defaultComments);
            }
        }
        return changed;
    }

    public static List<Path> getPathsFromResourceJar(final String folder) throws URISyntaxException, IOException {
        final List<Path> result;
        final String jarPath = Msg.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        final URI uri = URI.create("jar:file:" + jarPath);
        try (final FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            result = Files.walk(fs.getPath(folder)).filter(Files::isReadable).filter(file -> file.toString().endsWith(".yml")).collect(Collectors.toList());
        }
        return result;
    }

    private static void hackDumperOptions(final YamlConfiguration yamlConfiguration) {
        try {
            final Field yamlField = YamlConfiguration.class.getDeclaredField("yaml");
            yamlField.setAccessible(true);
            final Yaml innerYaml = (Yaml) yamlField.get(yamlConfiguration);
            final Field representerField = Yaml.class.getDeclaredField("representer");
            representerField.setAccessible(true);

            final Representer representer = (Representer) representerField.get(innerYaml);
            representer.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
