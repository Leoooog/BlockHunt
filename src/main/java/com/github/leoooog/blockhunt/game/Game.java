package com.github.leoooog.blockhunt.game;

import com.github.leoooog.blockhunt.BlockHuntPlugin;
import com.github.leoooog.blockhunt.files.MessagesManager;
import com.github.leoooog.blockhunt.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Game {

    private final UUID id;
    private final List<GamePlayer> players;
    private final List<GamePlayer> spectators;
    private final List<GamePlayer> hunters;
    private final List<GamePlayer> hiders;
    private final GameConfiguration configuration;
    private final BlockHuntPlugin plugin = BlockHuntPlugin.getInstance();
    private final MessagesManager messages = plugin.getMessages();
    private GameState state;
    private BukkitTask countDownTask;
    private boolean eventsEnabled;
    private long startTime;
    private long endTime;


    public Game(UUID id, GameConfiguration configuration) {
        this.id = id;
        this.configuration = configuration;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.hunters = new ArrayList<>();
        this.hiders = new ArrayList<>();
        this.state = GameState.LOBBY;
        this.eventsEnabled = false;
    }

    public void addPlayingPlayer(Player player) {
        if (state != GameState.LOBBY) {
            throw new IllegalStateException("Cannot add player to game in state " + state);
        }
        if (players.size() >= configuration.maxPlayers()) {
            throw new IllegalStateException("Cannot add player to full game");
        }
        GamePlayer gamePlayer = new GamePlayer(player, this);
        if (players.contains(gamePlayer)) {
            throw new IllegalArgumentException("Player is already in game");
        }
        players.add(gamePlayer);
        player.teleport(configuration.gameMap().getLobbyLocation());

        if (players.size() == configuration.maxPlayers()) {
            setupGame();
        }
        else {
            broadcast(messages.getJoinMessage(player));
        }
    }

    public void addSpectator(Player player) {
        GamePlayer gamePlayer = new GamePlayer(player, this);
        if (spectators.contains(gamePlayer)) {
            throw new IllegalArgumentException("Player is already a spectator");
        }
        gamePlayer.setRole(PlayerRole.SPECTATOR);
        spectators.add(gamePlayer);
    }


    public void removeSpectator(Player player) {
        spectators.removeIf(gamePlayer -> gamePlayer.getPlayerID().equals(player.getUniqueId()));
    }

    public void setupGame() {
        if (state != GameState.LOBBY) {
            throw new IllegalStateException("Cannot start game in state " + state);
        }
        if (players.size() < configuration.minPlayers()) {
            throw new IllegalStateException(
                    "Cannot start game with less than " + configuration.minPlayers() + " players");
        }
        state = GameState.STARTING;
        // Assign roles
        // Shuffle players
        Collections.shuffle(players);

        hiders.clear();
        hunters.clear();

        int hidersCount = players.size() / 2;
        for (int i = 0; i < players.size(); i++) {
            GamePlayer gamePlayer = players.get(i);
            if (i < hidersCount) {
                gamePlayer.setRole(PlayerRole.HIDER);
                hiders.add(gamePlayer);
            }
            else {
                gamePlayer.setRole(PlayerRole.HUNTER);
                hunters.add(gamePlayer);
            }
        }
        // Start countdown
        countDownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (int i = 10; i > 0; i--) {
                broadcast(messages.getCountdown(i));
            }
            countDownTask.cancel();
            broadcast(messages.getGameStart());
            startGame();
        }, 0, 20L);
    }

    private void startGame() {
        GameMap map = configuration.gameMap();
        // Teleport players
        for (GamePlayer gamePlayer : players) {
            map.teleportGamePlayer(gamePlayer);
            Utils.sendStartTitle(gamePlayer);
            gamePlayer.getPlayer().getInventory().clear();
            if(gamePlayer.getRole() == PlayerRole.HIDER) {
                gamePlayer.getPlayer().getInventory().setContents(configuration.hidersItems());
                //TODO: hiders blocks
            }else if(gamePlayer.getRole() == PlayerRole.HUNTER) {
                gamePlayer.getPlayer().getInventory().setContents(configuration.huntersItems());
            }
        }
        for (GamePlayer gamePlayer : spectators) {
            gamePlayer.getPlayer().teleport(map.getSpectatorLocation());
            //TODO: spectator inventories
        }
        state = GameState.INGAME;
        eventsEnabled = true;
        startTime = System.currentTimeMillis();
        endTime = startTime + configuration.gameDuration();

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

    public void setState(GameState state) {
        this.state = state;
    }

    public List<GamePlayer> getSpectators() {
        return spectators;
    }

    public List<GamePlayer> getHunters() {
        return hunters;
    }

    public List<GamePlayer> getHiders() {
        return hiders;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }

    private void broadcast(String message) {
        for (GamePlayer gamePlayer : players) {
            gamePlayer.getPlayer().sendMessage(message);
        }
    }

    //TODO: handle events

    public void handlePlayerLeave(Player player) {
        // Not removing from players list, as player can rejoin
        removeSpectator(player);
        hunters.removeIf(gamePlayer -> gamePlayer.getPlayerID().equals(player.getUniqueId()));
        hiders.removeIf(gamePlayer -> gamePlayer.getPlayerID().equals(player.getUniqueId()));
        if (state == GameState.INGAME) {
            if (hiders.isEmpty() || hunters.isEmpty()) {
                endGame();
            }
        }
        else if (state == GameState.LOBBY) {
            players.removeIf(gamePlayer -> gamePlayer.getPlayerID().equals(player.getUniqueId()));
        }
        if (state == GameState.STARTING && players.size() < configuration.minPlayers()) {
            countDownTask.cancel();
            state = GameState.LOBBY;
        }
    }

    public void handlePlayerJoin(Player player) {
        if (state == GameState.LOBBY) {
            addPlayingPlayer(player);
        }
        else if (state == GameState.INGAME || state == GameState.STARTING) {
            Optional<GamePlayer> optionalGamePlayer =
                    players.stream().filter(gp -> gp.getPlayerID().equals(player.getUniqueId())).findFirst();
            if (optionalGamePlayer.isPresent()) {
                GamePlayer gamePlayer = optionalGamePlayer.get();
                switch (gamePlayer.getRole()) {
                    case HIDER:
                        hiders.add(gamePlayer);
                        break;
                    case HUNTER:
                        hunters.add(gamePlayer);
                        break;
                    default:
                        break;
                }
            }
            else {
                addSpectator(player);
            }
        }
    }

    private void endGame() {
        state = GameState.ENDING;
        //TODO: end game
    }

}
