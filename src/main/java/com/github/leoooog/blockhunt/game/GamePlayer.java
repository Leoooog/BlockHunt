package com.github.leoooog.blockhunt.game;

import org.bukkit.entity.Player;

import java.util.Objects;


public class GamePlayer {
    private final Player player;
    private final Game game;
    private PlayerRole role;

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.role = PlayerRole.NONE;
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

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GamePlayer that = (GamePlayer) o;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player);
    }
}
