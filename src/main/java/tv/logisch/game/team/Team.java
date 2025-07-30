package tv.logisch.game.team;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class Team {

    private final List<Player> players;
    private final List<Block> placedBlocks;
    private Location island;
    private int coins;

    public Team() {
        this.players = new ArrayList<>();
        this.placedBlocks = new ArrayList<>();
        this.island = null;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }
    public void removePlayer(Player player) {
        players.remove(player);
    }
    public boolean isPlayerInTeam(Player player) {
        return players.contains(player);
    }

    public void addBlock(Block block) {
        if (!placedBlocks.contains(block)) {
            placedBlocks.add(block);
            placedBlocks.sort((b1, b2) -> Integer.compare(b2.getLocation().getBlockZ(), b1.getLocation().getBlockZ()));
        }
    }
    public void removeBlock(Block block) {
        placedBlocks.remove(block);
    }
    public boolean isPlacedBlock(Block block) {
        return placedBlocks.contains(block);
    }

    public void setIsland(Location island) {
        this.island = island;
    }
    public Location getIsland() {
        return island;
    }

    public void teleportToIsland() {
        this.players.forEach(this::teleportToIsland);
    }
    public void teleportToIsland(Player player) {
        player.teleport(island.clone().add(0.5, 1.25, 0.5));
    }

    public int getScore() {
        if(placedBlocks.isEmpty()) return 0;
        if(placedBlocks.getFirst() == null) return 0;
        return placedBlocks.getFirst().getLocation().getBlockZ();
    }

    public void coins(int coins) {
        this.coins = coins;
    }

}
