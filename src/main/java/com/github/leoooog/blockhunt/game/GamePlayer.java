package com.github.leoooog.blockhunt.game;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;


public class GamePlayer {
    private final Player player;
    private final UUID playerID;
    private final Game game;
    private PlayerRole role;
    private boolean inGame;

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.playerID = player.getUniqueId();
        this.game = game;
        this.role = PlayerRole.NONE;
        this.inGame = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public PlayerRole getRole() {
        return role;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GamePlayer that = (GamePlayer) o;
        return Objects.equals(playerID, that.playerID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerID);
    }
}
