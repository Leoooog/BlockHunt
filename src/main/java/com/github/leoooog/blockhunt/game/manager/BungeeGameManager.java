package com.github.leoooog.blockhunt.game.manager;

import com.github.leoooog.blockhunt.game.Game;
import com.github.leoooog.blockhunt.game.GameState;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BungeeGameManager extends GameManager{

    //TODO: Implement BungeeCord game manager

    private final Game game;
    private final UUID gameID;

    public BungeeGameManager() {
        super(Type.SINGLE_GAME);
        this.gameID = UUID.randomUUID();
        this.game = new Game(gameID, plugin.getConfiguration().getGameConfiguration());
    }
    @Override
    public void handleNewPlayer(Player player) {
        game.handlePlayerJoin(player);
    }

    @Override
    public void handleNewPlayer(Player player, UUID gameID) {
        if(gameID.equals(this.gameID))
            handleNewPlayer(player);
    }

    @Override
    public void handlePlayerLeave(Player player) {
        game.handlePlayerLeave(player);
    }

    @Override
    public void handleNewSpectator(Player player) {
        game.addSpectator(player);
    }

    @Override
    public void handleNewSpectator(Player player, UUID gameID) {
        handleNewSpectator(player);
    }

    @Override
    public void handleSpectatorLeave(Player player) {
        game.removeSpectator(player);
    }

    @Override
    public int getGamesRunning() {
        return 1;
    }

    @Override
    public int getGamesPlaying() {
        return game.getState() == GameState.INGAME ? 1 : 0;
    }

}
