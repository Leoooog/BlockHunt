package com.github.leoooog.blockhunt;

import com.github.leoooog.blockhunt.files.ConfigManager;
import com.github.leoooog.blockhunt.files.MessagesManager;
import com.github.leoooog.blockhunt.game.manager.GameManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BlockHuntPlugin extends JavaPlugin {

    private static BlockHuntPlugin instance;
    private MessagesManager messages;
    private ConfigManager config;
    private GameManager gameManager;
    private boolean isBungee;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BlockHuntPlugin getInstance() {
        return instance;
    }

    public MessagesManager getMessages() {
        return messages;
    }

    @NotNull
    public ConfigManager getConfiguration() {
        return config;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public boolean isBungee() {
        return isBungee;
    }
}
