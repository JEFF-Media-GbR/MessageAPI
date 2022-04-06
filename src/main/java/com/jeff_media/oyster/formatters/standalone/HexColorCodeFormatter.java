package com.jeff_media.oyster.formatters.standalone;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HexColorCodeFormatter extends ColorCodeFormatter {

    @Override
    public String format(final @NotNull String string, final CommandSender player) {
        final Map<Integer, char[]> foundColorCodes = new HashMap<>();
        final char[] arr = string.toCharArray();
        int currentOffset = 0;
        outer:
        for (int index = 0; index < arr.length - HEX_COLOR_CODE_LENGTH + 1; index++) {
            /*index = */
            index += currentOffset;
            currentOffset = 0;
            //System.out.println("Index " + index + ", char " + arr[index]);
            if (!isColorCodeChar(arr[index])) {
                continue;
            }
            currentOffset = 1;
            if (!isHexColorCodeChar(arr[index + currentOffset])) {
                continue;
            }
            currentOffset = 2;
            if (!isColorCodeChar(arr[index + currentOffset])) {
                continue;
            }
            final char[] hex = new char[6];
            int hexPos = 0;
            for (int offset = 3; offset <= HEX_COLOR_CODE_LENGTH - 1; offset++) {
                currentOffset = offset;
                if (offset % 2 == 0) {
                    if (!isColorCodeChar(arr[index + offset])) {
                        continue outer;
                    }
                } else {
                    if (!isPartOf(arr[index + offset], LEGACY_HEX_COLOR_CODES)) {
                        continue outer;
                    }
                    hex[hexPos++] = arr[index + offset];
                }
                offset++;
            }
            foundColorCodes.put(index, hex);
        }

        // If there was no hex code, don't do anything
        if(foundColorCodes.isEmpty()) return string;

        // Replace all hex codes with an adventure tag
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < arr.length; index++) {
            if (!foundColorCodes.containsKey(index)) {
                builder.append(arr[index]);
                continue;
            }
            builder.append("<color:#").append(foundColorCodes.get(index)).append(">");
            index = index + HEX_COLOR_CODE_LENGTH - 1;
        }
        return builder.toString();
    }

}
