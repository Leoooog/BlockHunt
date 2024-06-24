package com.github.leoooog.blockhunt.game;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record GameConfiguration(int minPlayers, int maxPlayers, int maxSpectators, int gameDuration, int lobbyTime,
                                int countdownTime, GameMap gameMap, List<Material> hidersBlocks,
                                ItemStack[] hidersItems, ItemStack[]  huntersItems) {

}
