package com.jeff_media.oyster.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractMessage {

    /**
     * Returns a new Message with all plugin-provided placeholders applied (PlaceholderAPI, ItemsAdder emojis, ...).
     *
     * @param sender The CommandSender for which the placeholder should be replaced. For example, %player_name% will be
     *               replaced with the name of this CommandSender.
     * @return A new instance of this message, with replaced placeholders
     */
    public abstract AbstractMessage placeholders(CommandSender sender);

    /**
     * Returns a new Message with the given replacements applied. Every even-indexed String in the given array
     * is a String to replace, every odd-indexed String is the corresponding replacement.
     * <p>
     * Example:
     * <pre>
     * Message message = Msg.get("join-message").replace(
     *     "{player}", event.getPlayer().getName(),
     *     "{world}", event.getPlayer().getWorld().getName()
     * );
     * message.sendToAll();
     * </pre>
     */
    public abstract AbstractMessage replace(String... placeholders);

    public void sendTo(final Predicate<? super Player> predicate) {
        players(predicate).forEach(this::sendTo);
    }

    protected static Stream<? extends Player> players(final Predicate<? super Player> predicate) {
        return allPlayers().filter(predicate);
    }

    /**
     * Sends this message to the given {@link CommandSender}. Supports all kinds of features such as Click and Hover events.
     */
    public abstract void sendTo(CommandSender sender);

    protected static Stream<? extends Player> allPlayers() {
        return Bukkit.getOnlinePlayers().stream();
    }

    public void sendToAll() {
        allPlayers().forEach(this::sendTo);
    }

}
