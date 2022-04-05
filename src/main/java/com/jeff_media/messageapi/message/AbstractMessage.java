package com.jeff_media.messageapi.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractMessage {

    public abstract AbstractMessage placeholders(CommandSender sender);

    public abstract AbstractMessage replace(String... placeholders);

    public void sendTo(final Predicate<? super Player> predicate) {
        players(predicate).forEach(this::sendTo);
    }

    protected static Stream<? extends Player> players(final Predicate<? super Player> predicate) {
        return allPlayers().filter(predicate);
    }

    public abstract void sendTo(CommandSender sender);

    protected static Stream<? extends Player> allPlayers() {
        return Bukkit.getOnlinePlayers().stream();
    }

    public void sendToAll() {
        allPlayers().forEach(this::sendTo);
    }

}
