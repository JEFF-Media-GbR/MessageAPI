package com.jeff_media.oyster.formatters.standalone;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SingleColorCodeFormatter extends ColorCodeFormatter {

    @Override
    public String format(final @NotNull String string, final CommandSender player) {
        final char[] arr = string.toCharArray();
        final StringBuilder builder = new StringBuilder();

        // When there's no color code, we don't need to evaluate the StringBuilder
        boolean foundColorCode = false;

        for (int i = 0; i < arr.length; ++i) {
            if (i + 1 < arr.length && isColorCodeChar(arr[i]) && isPartOf(arr[i + 1], LEGACY_COLOR_AND_FORMATTING_CODES)) {
                foundColorCode = true;
                final LegacyColor color = LegacyColor.of(arr[i + 1]);
                builder.append(color.toAdventureTag());
                i++;
            } else {
                builder.append(arr[i]);
            }
        }
        return foundColorCode ? builder.toString() : string;
    }
}
