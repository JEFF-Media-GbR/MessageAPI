package com.jeff_media.messages;

import com.jeff_media.messages.formatters.MessageFormatter;
import com.jeff_media.messages.formatters.PluginMessageFormatter;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a message
 */
public class Message implements Cloneable {

    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();
    // Don't use System.lineSeparator(). It'd cause problems on Windows and macOS.
    private static final String NEW_LINE = "\n";

    private final List<String> lines;

    public Message(String line) {
        this(Collections.singletonList(line));
    }

    public Message(List<String> lines) {
        this.lines = lines;
    }

    /**
     * Returns a new Message with the given placeholders, and plugin-provided placeholders applied.
     * It will first apply the given placeholders, and then the plugin-provided placeholders.
     * @see #placeholders(String...)
     * @see #placeholders(CommandSender)
     */
    public Message placeholders(CommandSender sender, String... placeholders) {
        return placeholders(placeholders).placeholders(sender);
    }

    @Override
    public String toString() {
        return "Message{" + "lines=" + lines + '}';
    }

    /**
     * Returns a new Message with all plugin-provided placeholders applied (PlaceholderAPI, ItemsAdder emojis, ...).
     * @param sender The CommandSender for which the placeholder should be replaced. For example, %player_name% will be
     *               replaced with the name of this CommandSender.
     * @return A new instance of this message, with replaced placeholders
     */
    public Message placeholders(CommandSender sender) {
        return new Message(lines.stream().map(line ->
                applyPluginPlaceholders(line, sender)
        ).collect(Collectors.toList()));
    }

    /**
     * Returns a new Message with the given placeholders applies. Every even-indexed String in the given placeholder array
     * is a placeholder, every odd-index String is the corresponding replacement.
     * <p>
     * Example:
     * <pre>
     * Message message = Msg.get("join-message").placeholders(
     *     "{player}", event.getPlayer().getName(),
     *     "{world}", event.getPlayer().getWorld().getName()
     * );
     * message.sendToAll();
     * </pre>
     */
    public Message placeholders(String... placeholders) {
        if(placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Length of placeholder array must be even");
        }
        return new Message(lines.stream().map(line -> {
            for(int i = 0; i < placeholders.length; i+=2) {
                line = line.replace(placeholders[0], placeholders[1]);
            }
            return line;
        }).collect(Collectors.toList()));

    }

    private String applyPluginPlaceholders(String line, CommandSender sender) {
        for(MessageFormatter hook : Msg.getMessageFormatters()) {
            line = hook.format(line, sender);
        }
        return line;
    }

    /**
     * Converts this message into a list of line-separated {@link Component}s
     */
    public List<Component> toComponents() {
        return lines.stream().map(MINIMESSAGE::deserialize).collect(Collectors.toList());
    }

    private Component getFirstLineAsComponent() {
        return MINIMESSAGE.deserialize(getFirstLine());
    }

    /**
     * Sends a chat message to the given {@link CommandSender}. Supports all kinds of features such as Click and Hover events.
     */
    public void sendTo(CommandSender sender) {
        toComponents().forEach(component ->
            Msg.getAudience().sender(sender).sendMessage(component)
        );
    }

    /**
     * Sends a chat message to all online players matching the given {@link Predicate}
     * @see #sendTo(CommandSender) 
     */
    public void sendTo(Predicate<Player> predicate) {
        players(predicate).forEach(this::sendTo);
    }

    /**
     * Sends a chat message to all online players.
     * @see #sendTo(CommandSender) 
     */
    public void sendToAll() {
        allPlayers().forEach(this::sendTo);
    }

    /**
     * Sends an actionbar message to the given {@link CommandSender}. If this message contains of more than one line, only
     * the first line will be sent. Click and Hover events will be lost.
     */
    public void sendActionbarTo(CommandSender sender) {
        Msg.getAudience().sender(sender).sendActionBar(getFirstLineAsComponent());
    }

    /**
     * Sends an actionbar message to all online players matching the given {@link Predicate}.
     * @see #sendActionbarTo(CommandSender)
     */
    public void sendActionbarTo(Predicate<Player> predicate) {
        players(predicate).forEach(this::sendActionbarTo);
    }

    /**
     * Sends an actionbar message to all online players.
     * @see #sendActionbarTo(CommandSender)
     */
    public void sendActionbarToAll() {
        allPlayers().forEach(this::sendActionbarTo);
    }

    private static Stream<? extends Player> players(Predicate<Player> predicate) {
        return allPlayers().filter(predicate);
    }

    private static Stream<? extends Player> allPlayers() {
        return Bukkit.getOnlinePlayers().stream();
    }

    private String getFirstLine() {
        if(lines.size() == 0) return "";
        return lines.get(0);
    }

    /**
     * Converts this message into a line-separated list of legacy-formatted Strings. Click and Hover events will be lost.
     */
    public List<String> asStringList() {
        return toComponents().stream().map(component ->
                BukkitComponentSerializer.legacy().serialize(component)
        ).collect(Collectors.toList());
    }

    /**
     * Converts this message into a legacy-formatted String.
     * @see #asStringList()
     */
    public String asString() {
        return String.join(NEW_LINE, asStringList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return lines.equals(message.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lines);
    }
}
