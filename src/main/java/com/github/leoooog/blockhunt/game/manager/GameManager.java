package com.github.leoooog.blockhunt.game.manager;

import com.github.leoooog.blockhunt.BlockHuntPlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class GameManager {

    enum Type {
        SINGLE_GAME, MULTI_GAME
    }

    private final Type type;

    protected final BlockHuntPlugin plugin = BlockHuntPlugin.getInstance();


    public GameManager(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public abstract void handleNewPlayer(Player player);

    public abstract void handlePlayerLeave(Player player);

    public abstract void handleNewSpectator(Player player);

    public abstract void handleSpectatorLeave(Player player);

    public abstract void handleNewPlayer(Player player, UUID gameID);


    public abstract void handleNewSpectator(Player player, UUID gameID);


    public abstract int getGamesRunning();

    public abstract int getGamesPlaying();


}
