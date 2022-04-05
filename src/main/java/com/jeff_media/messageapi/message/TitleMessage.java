package com.jeff_media.messageapi.message;

import com.jeff_media.messageapi.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.function.Predicate;

public class TitleMessage extends AbstractMessage {

    private final Message title;
    private final Message subTitle;

    public TitleMessage(Message title, Message subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    @Override
    public TitleMessage placeholders(CommandSender sender) {
        title.placeholders(sender);
        subTitle.placeholders(sender);
        return this;
    }

    @Override
    public TitleMessage replace(String... placeholders) {
        title.replace(placeholders);
        subTitle.replace(placeholders);
        return this;
    }

    @Override
    public void sendTo(CommandSender sender) {
        sendTo(sender, null);
    }

    public void sendTo(CommandSender sender, double secondsFadeIn, double secondsStay, double secondsFadeOut) {
        sendTo(sender, Title.Times.times(
                Duration.ofMillis((long) (secondsFadeIn*1000)),
                Duration.ofMillis((long) (secondsStay*1000)),
                Duration.ofMillis((long) (secondsFadeOut*1000))
        ));
    }

    public void sendTo(CommandSender sender, Title.Times times) {
        Component firstLine = title.getFirstLineAsAdventureComponent();
        Component secondLine = subTitle.getFirstLineAsAdventureComponent();
        Title title = Title.title(firstLine, secondLine, times);
        Msg.audience().sender(sender).showTitle(title);
    }

    public void sendTo(Predicate<Player> predicate, Title.Times times) {
        players(predicate).forEach(player -> sendTo(player, times));
    }

    public void sendTo(Predicate<Player> predicate, double secondsFadeIn, double secondsStay, double secondsFadeOut) {
        players(predicate).forEach(player -> sendTo(player, secondsFadeIn, secondsStay, secondsFadeOut));
    }

    public void sendToAll(Title.Times times) {
        allPlayers().forEach(player -> sendTo(player, times));
    }

    public void sendToAll(double secondsFadeIn, double secondsStay, double secondsFadeOut) {
        allPlayers().forEach(player -> sendTo(player, secondsFadeIn, secondsStay, secondsFadeOut));
    }
}
