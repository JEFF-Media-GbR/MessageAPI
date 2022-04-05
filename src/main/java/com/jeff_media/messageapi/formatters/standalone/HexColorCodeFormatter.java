package com.jeff_media.messageapi.formatters.standalone;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class HexColorCodeFormatter extends ColorCodeFormatter {

    @Override
    public String format(final String string, final CommandSender player) {
        final Map<Integer, char[]> foundColorCodes = new HashMap<>();
        final char[] arr = string.toCharArray();
        debug(arr);
        int currentOffset = 0;
        outer:
        for (int index = 0; index < arr.length - HEX_COLOR_CODE_LENGTH + 1; index++) {
            /*index = */
            index += currentOffset;
            currentOffset = 0;
            //System.out.println("Index " + index + ", char " + arr[index]);
            if (!isColorCodeChar(arr[index])) {
                System.out.println(index + " is no &§");
                continue;
            }
            currentOffset = 1;
            if (!isHexColorCodeChar(arr[index + currentOffset])) {
                System.out.println(index + " is not xX");
            }
            currentOffset = 2;
            if (!isColorCodeChar(arr[index + currentOffset])) {
                System.out.println(index + " is no &§");
                continue;
            }
            final char[] hex = new char[6];
            int hexPos = 0;
            for (int offset = 3; offset <= HEX_COLOR_CODE_LENGTH - 1; offset++) {
                currentOffset = offset;
                if (offset % 2 == 0) {
                    if (!isColorCodeChar(arr[index + offset])) {
                        System.out.println(index + offset + " is not &§");
                        continue outer;
                    }
                } else {
                    if (!isPartOf(arr[index + offset], LEGACY_HEX_COLOR_CODES)) {
                        System.out.println(index + offset + " is no valid hex code");
                        continue outer;
                    }
                    System.out.println(index + offset + " is hex char " + arr[index + offset]);
                    hex[hexPos++] = arr[index + offset];
                }
                offset++;
            }
            System.out.println("Found Hex Color: " + new String(hex) + " at starting index " + index);
            foundColorCodes.put(index, hex);
        }
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
