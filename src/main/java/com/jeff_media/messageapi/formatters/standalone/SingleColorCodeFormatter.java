package com.jeff_media.messageapi.formatters.standalone;

import org.bukkit.command.CommandSender;

public class SingleColorCodeFormatter extends ColorCodeFormatter {

    @Override
    public String format(final String string, final CommandSender player) {
        return translateLegacyColorCodes(string);
    }


    private static String translateLegacyColorCodes(final String text) {
        final char[] arr = text.toCharArray();
        final StringBuilder buffer = new StringBuilder();
        applyRegularColors(arr, buffer);
        return buffer.toString();
    }


    private static void applyRegularColors(final char[] arr, final StringBuilder buffer) {
        for (int i = 0; i < arr.length/* - COLOR_CODE_LENGTH + 1*/; ++i) {
            System.out.println("Input: " + arr[i]);
            if (i + 1 < arr.length && isColorCodeChar(arr[i]) && isPartOf(arr[i + 1], LEGACY_COLOR_AND_FORMATTING_CODES)) {
                final LegacyColor color = LegacyColor.of(arr[i + 1]);
                buffer.append(color.toAdventureTag());
                i++;
            } else {
                buffer.append(arr[i]);
            }
        }
    }
}
