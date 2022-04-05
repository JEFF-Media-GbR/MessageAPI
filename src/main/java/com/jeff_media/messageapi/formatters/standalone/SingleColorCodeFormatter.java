package com.jeff_media.messageapi.formatters.standalone;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class SingleColorCodeFormatter extends ColorCodeFormatter {

    @Override
    public String format(String string, CommandSender player) {
        return translateLegacyColorCodes(string);
    }

    public static boolean isColorCodeChar(char character) {
        // Yes, both are needed.
        return character == COLOR_CODE_CHAR || character == LEGACY_COLOR_CODE_CHAR;
    }

    private static String translateLegacyColorCodes(String text) {
        char[] b = text.toCharArray();
        StringBuilder buffer = new StringBuilder();
        applyRegularColors(b, buffer);
        return buffer.toString();
    }



    private static void applyRegularColors(char[] b, StringBuilder buffer) {
        for(int i = 0; i < b.length/* - COLOR_CODE_LENGTH + 1*/; ++i) {
            System.out.println("Input: " + b[i]);
            if (i+1 < b.length && isColorCodeChar(b[i]) && isPartOf(b[i+1],LEGACY_COLOR_AND_FORMATTING_CODES)) {
                LegacyColor color = LegacyColor.of(b[i+1]);
                buffer.append(color.toAdventureTag());
                i++;
            } else {
                buffer.append(b[i]);
            }
        }
    }
}
