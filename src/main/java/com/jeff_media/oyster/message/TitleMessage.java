package com.jeff_media.oyster.message;

import com.jeff_media.oyster.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.function.Predicate;

public class TitleMessage extends AbstractMessage {

    private final Message title;
    private final Message subTitle;

    public TitleMessage(final Message title, final Message subTitle) {
        super();
        this.title = title;
        this.subTitle = subTitle;
    }

    @Override
    public TitleMessage placeholders(final CommandSender sender) {
        title.placeholders(sender);
        subTitle.placeholders(sender);
        return this;
    }

    @Override
    public TitleMessage replace(final String... placeholders) {
        title.replace(placeholders);
        subTitle.replace(placeholders);
        return this;
    }

    @Override
    public void sendTo(final CommandSender sender) {
        sendTo(sender, null);
    }

    public void sendTo(final CommandSender sender, final Title.Times times) {
        final Component firstLine = title.getFirstLineAsAdventureComponent();
        final Component secondLine = subTitle.getFirstLineAsAdventureComponent();
        final Title title = Title.title(firstLine, secondLine, times);
        Msg.audience().sender(sender).showTitle(title);
    }

    public void sendTo(final Predicate<? super Player> predicate, final Title.Times times) {
        players(predicate).forEach(player -> sendTo(player, times));
    }

    public void sendTo(final Predicate<? super Player> predicate, final double secondsFadeIn, final double secondsStay, final double secondsFadeOut) {
        players(predicate).forEach(player -> sendTo(player, secondsFadeIn, secondsStay, secondsFadeOut));
    }

    public void sendTo(final CommandSender sender, final double secondsFadeIn, final double secondsStay, final double secondsFadeOut) {
        sendTo(sender, Title.Times.times(Duration.ofMillis((long) (secondsFadeIn * 1000)), Duration.ofMillis((long) (secondsStay * 1000)), Duration.ofMillis((long) (secondsFadeOut * 1000))));
    }

    public void sendToAll(final Title.Times times) {
        allPlayers().forEach(player -> sendTo(player, times));
    }

    public void sendToAll(final double secondsFadeIn, final double secondsStay, final double secondsFadeOut) {
        allPlayers().forEach(player -> sendTo(player, secondsFadeIn, secondsStay, secondsFadeOut));
    }
}
