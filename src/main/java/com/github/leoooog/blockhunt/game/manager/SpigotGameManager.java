package com.github.leoooog.blockhunt.game.manager;

import com.github.leoooog.blockhunt.game.Game;
import com.github.leoooog.blockhunt.game.GameState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpigotGameManager extends GameManager {

    private final List<Game> games;

    public SpigotGameManager() {
        super(Type.MULTI_GAME);
        this.games = new ArrayList<>();
    }

    @Override
    public void handleNewPlayer(Player player) {
        Game next = nextAvailable();
        if(next == null) {
            Game game = new Game(UUID.randomUUID(), plugin.getConfiguration().getGameConfiguration());
            game.addPlayingPlayer(player);
            games.add(game);
        } else {
            next.addPlayingPlayer(player);
        }
    }

    @Override
    public void handlePlayerLeave(Player player) {
        games.forEach(game -> game.handlePlayerLeave(player));
    }

    @Override
    public void handleNewSpectator(Player player) {
        if(games.isEmpty()) return;
        games.get(0).addSpectator(player);
    }

    @Override
    public void handleSpectatorLeave(Player player) {
        games.forEach(game -> game.removeSpectator(player));
    }

    @Override
    public void handleNewPlayer(Player player, UUID gameID) {
        Optional<Game> game = games.stream().filter(g -> g.getId().equals(gameID)).findFirst();
        if(game.isPresent()) {
            game.get().handlePlayerJoin(player);
        }else {
            throw new IllegalArgumentException("Game with ID " + gameID + " not found");
        }
    }

    @Override
    public void handleNewSpectator(Player player, UUID gameID) {
        Optional<Game> game = games.stream().filter(g -> g.getId().equals(gameID)).findFirst();
        if(game.isPresent()) {
            game.get().addSpectator(player);
        }else {
            throw new IllegalArgumentException("Game with ID " + gameID + " not found");
        }
    }

    @Override
    public int getGamesRunning() {
        return games.size();
    }

    @Override
    public int getGamesPlaying() {
        return games.stream().filter(game -> game.getState() == GameState.INGAME).toArray().length;
    }

    private Game nextAvailable() {
        return games.stream().filter(game -> game.getState() == GameState.LOBBY).findFirst().orElse(null);
    }

    public List<Game> getGames() {
        return games;
    }
}
