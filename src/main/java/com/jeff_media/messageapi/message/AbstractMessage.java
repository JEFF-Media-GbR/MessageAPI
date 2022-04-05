package com.jeff_media.messageapi.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractMessage {

    public abstract AbstractMessage placeholders(CommandSender sender);

    public abstract AbstractMessage replace(String... placeholders);

    public abstract void sendTo(CommandSender sender);

    public void sendTo(Predicate<Player> predicate) {
        players(predicate).forEach(this::sendTo);
    }

    public void sendToAll() {
        allPlayers().forEach(this::sendTo);
    }

    protected static Stream<? extends Player> players(Predicate<Player> predicate) {
        return allPlayers().filter(predicate);
    }

    protected static Stream<? extends Player> allPlayers() {
        return Bukkit.getOnlinePlayers().stream();
    }

}
