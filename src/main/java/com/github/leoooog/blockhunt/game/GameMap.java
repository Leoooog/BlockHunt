package com.github.leoooog.blockhunt.game;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class GameMap {

    private final World world;
    private final Location spectatorLocation;
    private final Location lobbyLocation;
    private final List<Location> hunterSpawns;
    private final List<Location> hiderSpawns;
    private int hunterSpawnIndex = 0;
    private int hiderSpawnIndex = 0;

    public GameMap(World world, Location spectatorLocation, Location lobbyLocation, List<Location> hunterSpawns, List<Location> hiderSpawns) {
        this.world = world;
        this.spectatorLocation = spectatorLocation;
        this.lobbyLocation = lobbyLocation;
        this.hunterSpawns = hunterSpawns;
        this.hiderSpawns = hiderSpawns;
    }

    public World getWorld() {
        return world;
    }

    public Location getSpectatorLocation() {
        return spectatorLocation;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public List<Location> getHunterSpawns() {
        return hunterSpawns;
    }

    public List<Location> getHiderSpawns() {
        return hiderSpawns;
    }

    public void teleportGamePlayer(GamePlayer gamePlayer) {
        Location nextLocation;
        if(gamePlayer.getRole() == PlayerRole.HUNTER) {
            nextLocation = hunterSpawns.get(hunterSpawnIndex);
            hunterSpawnIndex++;
        }else if(gamePlayer.getRole() == PlayerRole.HIDER) {
            nextLocation = hiderSpawns.get(hiderSpawnIndex);
            hiderSpawnIndex++;
        }else {
            nextLocation = lobbyLocation;
        }
        gamePlayer.getPlayer().teleport(nextLocation);
    }
}
