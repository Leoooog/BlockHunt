package com.github.leoooog.blockhunt.game;

import com.github.leoooog.blockhunt.BlockHuntPlugin;
import com.github.leoooog.blockhunt.files.MessagesManager;
import com.github.leoooog.blockhunt.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {

    private final UUID id;
    private final List<GamePlayer> players;
    private final List<GamePlayer> spectators;
    private final List<GamePlayer> hunters;
    private final List<GamePlayer> hiders;
    private final int maxPlayers;
    private final int minPlayers;
    private final GameMap map;
    private GameState state;

    private final BlockHuntPlugin plugin = BlockHuntPlugin.getInstance();
    private final MessagesManager messages = plugin.getMessages();

    private BukkitTask countDownTask;


    public Game(UUID id, int maxPlayers, int minPlayers, GameMap map) {
        this.id = id;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.hunters = new ArrayList<>();
        this.hiders = new ArrayList<>();
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.map = map;
        this.state = GameState.LOBBY;
    }

    public void addPlayingPlayer(Player player) {
        if(state != GameState.LOBBY) {
            throw new IllegalStateException("Cannot add player to game in state " + state);
        }
        if(players.size() >= maxPlayers) {
            throw new IllegalStateException("Cannot add player to full game");
        }
        GamePlayer gamePlayer = new GamePlayer(player, this);
        if(players.contains(gamePlayer)) {
            throw new IllegalArgumentException("Player is already in game");
        }
        players.add(gamePlayer);
    }

    public void addSpectator(Player player) {
        GamePlayer gamePlayer = new GamePlayer(player, this);
        if(spectators.contains(gamePlayer)) {
            throw new IllegalArgumentException("Player is already a spectator");
        }
        gamePlayer.setRole(PlayerRole.SPECTATOR);
        spectators.add(gamePlayer);
    }

    public void removePlayer(Player player ) {
        players.removeIf(gamePlayer -> gamePlayer.getPlayer().equals(player));
    }

    public void removeSpectator(Player player) {
        spectators.removeIf(gamePlayer -> gamePlayer.getPlayer().equals(player));
    }

    public void setupGame() {
        if(state != GameState.LOBBY) {
            throw new IllegalStateException("Cannot start game in state " + state);
        }
        if(players.size() < minPlayers) {
            throw new IllegalStateException("Cannot start game with less than " + minPlayers + " players");
        }
        state = GameState.STARTING;
        // Assign roles
        // Shuffle players
        Collections.shuffle(players);
        int hidersCount = players.size() / 2;
        for(int i = 0; i < players.size(); i++) {
            GamePlayer gamePlayer = players.get(i);
            if(i < hidersCount) {
                gamePlayer.setRole(PlayerRole.HIDER);
                hiders.add(gamePlayer);
            } else {
                gamePlayer.setRole(PlayerRole.HUNTER);
                hunters.add(gamePlayer);
            }
        }
        // Start countdown
        countDownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for(int i = 10; i > 0; i--) {
                broadcast(messages.getCountdown(i));
            }
            countDownTask.cancel();
            broadcast(messages.getGameStart());
            startGame();
        }, 0, 20L);
    }

    private void startGame() {
        // Teleport players
        for(GamePlayer gamePlayer : players) {
            map.teleportGamePlayer(gamePlayer);
            Utils.sendStartTitle(gamePlayer);
            //TODO: player inventories, hiders blocks
        }
        for(GamePlayer gamePlayer : spectators) {
            gamePlayer.getPlayer().teleport(map.getSpectatorLocation());
            //TODO: spectator inventories
        }
        state = GameState.INGAME;
        //TODO: enable events check
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public UUID getId() {
        return id;
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public List<GamePlayer> getSpectators() {
        return spectators;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public List<GamePlayer> getHunters() {
        return hunters;
    }

    public List<GamePlayer> getHiders() {
        return hiders;
    }

    public GameMap getMap() {
        return map;
    }

    private void broadcast(String message) {
        for(GamePlayer gamePlayer : players) {
            gamePlayer.getPlayer().sendMessage(message);
        }
    }

    //TODO: handle events
}
