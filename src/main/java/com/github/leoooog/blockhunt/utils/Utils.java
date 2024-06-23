package com.github.leoooog.blockhunt.utils;

import com.github.leoooog.blockhunt.BlockHuntPlugin;
import com.github.leoooog.blockhunt.files.MessagesManager;
import com.github.leoooog.blockhunt.game.GamePlayer;
import org.bukkit.ChatColor;

public class Utils {

    private static final MessagesManager messages = BlockHuntPlugin.getInstance().getMessages();
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void sendStartTitle(GamePlayer player) {
        player.getPlayer().sendTitle(messages.getStartTitle(player), messages.getStartSubtitle(player), 10, 70, 20);
    }

    public static void sendEndTitle(GamePlayer player) {
        player.getPlayer().sendTitle(messages.getEndTitle(player), messages.getEndSubtitle(player), 10, 70, 20);
    }

    public static void sendHiderWin(GamePlayer player) {
        player.getPlayer().sendTitle(messages.getHiderWin(player), "", 10, 70, 20);
    }

    public static void sendHunterWin(GamePlayer player) {
        player.getPlayer().sendTitle(messages.getHunterWin(player), "", 10, 70, 20);
    }
}
