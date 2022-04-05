package com.jeff_media.messageapi.formatters.vanilla;

import com.jeff_media.messageapi.formatters.MessageFormatter;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyHexColorFormatter extends MessageFormatter {

    private static boolean isHexColorCodeChar(char character) {
        return character == 'x' || character == 'X';
    }

    private static void debug(char[] arr) {
        for(int i = 0; i < arr.length; i++) {
            System.out.println(i + " -> " + arr[i]);
        }
    }

    @Override
    public String format(String string, CommandSender player) {
        char[] arr = string.toCharArray();
        debug(arr);
        outer:
        for(int index = 0; index < arr.length - 13; index ++) {
            //System.out.println("Index " + index + ", char " + arr[index]);
            if(!LegacyChatColorFormatter.isColorCodeChar(arr[index])) {
                System.out.println(index + " is no &§");
                continue;
            }
            if(!isHexColorCodeChar(arr[index++])) {
                System.out.println(index + " is not xX");
            }
            if(!LegacyChatColorFormatter.isColorCodeChar(arr[index++])) {
                System.out.println(index + " is no &§");
                continue;
            }
            char[] hex = new char[6];
            int hexPos = 0;
            for(int offset = 3; offset <= 13; offset++) {
                if(offset % 2 == 0) {
                    if(!LegacyChatColorFormatter.isColorCodeChar(arr[index+offset])) {
                        System.out.println(index+offset + " is not &§");
                        continue outer;
                    }
                } else {
                    System.out.println(index + offset + " is hex char " + arr[index + offset]);
                    hex[hexPos++] = arr[index + offset];
                }
                offset++;
            }
            System.out.println("Found Hex Color: " + new String(hex));
        }
        return new String(arr);
    }

}
