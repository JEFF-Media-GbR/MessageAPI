package com.jeff_media.messages.formatters.vanilla;

import com.jeff_media.messages.formatters.MessageFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class LegacyChatColorFormatter extends MessageFormatter {

    private static final char COLOR_CODE_CHAR = '&';
    private static final char LEGACY_COLOR_CODE_CHAR = '§';
    private static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

    @Override
    public String format(String string, CommandSender player) {
        return translateLegacyColorCodes(string);
    }

    private static boolean isColorCodeChar(char character) {
        // Yes, both are needed.
        return character == COLOR_CODE_CHAR || character == LEGACY_COLOR_CODE_CHAR;
    }

    private static String translateLegacyColorCodes(String text) {
        char[] b = text.toCharArray();
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < b.length - 1; ++i) {
            // TODO: Legacy Hex Colors
            if (isColorCodeChar(b[i]) && COLOR_CODES.indexOf(b[i + 1]) > -1) {
                LegacyColor color = LegacyColor.of(b[i+1]);
                buffer.append(color.toAdventureTag());
                i++;
            } else {
                buffer.append(b[i]);
            }
        }
        return buffer.toString();
    }

    static class LegacyColor {

        private static final Map<Character,LegacyColor> BY_CHAR = new HashMap<>();

        static final LegacyColor BLACK = new LegacyColor('0',0x000000);
        static final LegacyColor DARK_BLUE = new LegacyColor('1',0x0000AA);
        static final LegacyColor DARK_GREEN = new LegacyColor('2',0x00AA00);
        static final LegacyColor DARK_AQUA = new LegacyColor('3',0x00AAAA);
        static final LegacyColor DARK_RED = new LegacyColor('4',0xAA0000);
        static final LegacyColor DARK_PURPLE = new LegacyColor('5',0xAA00AA);
        static final LegacyColor GOLD = new LegacyColor('6',0xFFAA00);
        static final LegacyColor GRAY = new LegacyColor('7',0xAAAAAA);
        static final LegacyColor DARK_GRAY = new LegacyColor('8',0x555555);
        static final LegacyColor BLUE = new LegacyColor('9',0x5555FF);
        static final LegacyColor GREEN = new LegacyColor('a',0x55FF55);
        static final LegacyColor AQUA = new LegacyColor('b',0x55FFFF);
        static final LegacyColor RED = new LegacyColor('c',0xFF5555);
        static final LegacyColor LIGHT_PURPLE = new LegacyColor('d',0xFF55FF);
        static final LegacyColor YELLOW = new LegacyColor('e',0xFFFF55);
        static final LegacyColor WHITE = new LegacyColor('f',0xFFFFFF);

        static final LegacyColor OBFUSCATED = new LegacyColor('k',"obf");
        static final LegacyColor BOLD = new LegacyColor('l',"b");
        static final LegacyColor STRIKETHROUGH = new LegacyColor('m',"st");
        static final LegacyColor UNDERLINE = new LegacyColor('n',"u");
        static final LegacyColor ITALIC = new LegacyColor('o',"i");
        static final LegacyColor RESET = new LegacyColor('r',"r");

        private final char character;
        private final String adventureTag;

        static LegacyColor of(char character) {
            return BY_CHAR.get(character);
        }

        LegacyColor(char character, int color) {
            this.character = character;
            this.adventureTag = "<color:#" + String.format("%06X",color) + ">";
            BY_CHAR.put(character, this);
        }

        LegacyColor(char character, String code) {
            this.character = character;
            this.adventureTag = "<" + code + ">";
            BY_CHAR.put(character, this);
        }

        String toAdventureTag() {
            return adventureTag;
        }
    }
}
