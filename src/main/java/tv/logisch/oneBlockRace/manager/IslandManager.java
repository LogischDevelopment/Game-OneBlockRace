package tv.logisch.oneBlockRace.manager;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;

public class IslandManager {

    private int x1 = 0;
    private int x2 = 0;
    @Getter @Accessors(fluent = true)
    private int y = 30;
    private int z = 0;

    private List<Location> availableIslands;

    public IslandManager(int width) {
        this.availableIslands = new ArrayList<>();
        World  world = Bukkit.createWorld(new WorldCreator("world"));
        Location loc = new Location(world, x1, y, z);
        availableIslands.addLast(loc);
        this.x1 = (int) width/2 + 8;
        this.x2 = (int) -width/2 - 8;
    }

    public Location createIsland() {
        if(!availableIslands.isEmpty()) {
            Location loc = availableIslands.removeFirst();
            loc.getBlock().setType(Material.BEDROCK);
            return loc;
        }
        Location loc;
        if(x1 < x2*(-1)) {
            loc = new Location(GameManager.get().gameWorld(), x1, y, z);
            x1 += GameManager.get().islandWidth()*2 + 5;
        } else {
            loc = new Location(GameManager.get().gameWorld(), x2, y, z);
            x2 -= GameManager.get().islandWidth()*2 + 5;
        }
        loc.getBlock().setType(Material.BEDROCK);
        return loc;
    }

}
