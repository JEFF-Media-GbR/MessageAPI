package com.jeff_media.messages;

import org.bukkit.configuration.ConfigurationSection;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LanguageFileUtils {

    public static boolean merge(ConfigurationSection savedFile, ConfigurationSection includedTranslation, ConfigurationSection includedEnglishTranslation) {
        boolean changed = false;
        for (String key : includedEnglishTranslation.getKeys(true)) {
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

    public static List<Path> getAllIncludedTranslations() {
        try {
            return getPathsFromResourceJar(Msg.LANGUAGE_FOLDER_NAME + "/");
        } catch (URISyntaxException | IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static List<Path> getPathsFromResourceJar(String folder) throws URISyntaxException, IOException {
        List<Path> result;
        String jarPath = Msg.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        URI uri = URI.create("jar:file:" + jarPath);
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            result = Files.walk(fs.getPath(folder)).filter(Files::isReadable).filter(file -> file.toString().endsWith(".yml")).collect(Collectors.toList());
        }
        return result;
    }

    public static InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = Msg.class.getClassLoader();
        if (fileName.startsWith("/")) fileName = fileName.substring(1);
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new IllegalArgumentException("Included file not found: " + fileName);
            } else {
                return inputStream;
            }
    }

    public static File getFile(String name) {
        File folder = Msg.languageFolderFile;
        File[] files = folder.listFiles((dir, test) -> test.equalsIgnoreCase(name) || test.equalsIgnoreCase(name + ".yml") || test.equalsIgnoreCase(name + ".yaml"));
        if (files != null && files.length > 0) return files[0];
        return null;
    }

    public static void copyFileToFile(InputStream stream, File output) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream)); FileWriter writer = new FileWriter(output); BufferedWriter bufferedWriter = new BufferedWriter(writer);) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
