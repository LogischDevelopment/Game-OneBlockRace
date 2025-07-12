package tv.logisch.oneBlockRace.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.team.TeamManager;
import tv.logisch.oneBlockRace.utils.Format;

import java.util.Random;

public class ItemManager {

    private final TeamManager teamManager;
    private final String text;
    private BossBar bossBar;

    private long dropLeft = 0;

    public ItemManager(TeamManager teamManager, String text) {
        this.teamManager = teamManager;
        this.text = text;
    }

    int taskId;
    public void start() {
        this.dropLeft = 0;
        this.bossBar = Bukkit.createBossBar(this.text, BarColor.BLUE, BarStyle.SOLID);
        this.bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach(this.bossBar::addPlayer);

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlockRace.instance(), () -> {
            if(dropLeft == 0) {
                dropLeft = GameManager.get().dropInterval();
                this.teamManager.teams().forEach(t -> spawn(t.getIsland().clone().add(0.5, 1, 0.5)));
            }
            updateBossBar();
            dropLeft--;
        }, 0, 20);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
        this.bossBar.removeAll();
        this.bossBar.setVisible(false);
    }

    private void updateBossBar() {
        this.bossBar.setProgress((float) Math.min(1.0, Math.max(0.0, (double) dropLeft / GameManager.get().dropInterval())));
        this.bossBar.setTitle(text.replaceAll("%S%", String.valueOf(Format.time(this.dropLeft))));
    }
    public void addPlayer(org.bukkit.entity.Player player) {
        this.bossBar.addPlayer(player);
    }
    public void removePlayer(org.bukkit.entity.Player player) {
        this.bossBar.removePlayer(player);
    }

    private void spawn(Location loc) {
        Item item = null;
        while(item == null) {
            Material material = Material.values()[new Random().nextInt(Material.values().length)];
            if(material.isItem() && !material.name().contains("_SPAWN_EGG") && !material.equals(Material.ENDER_PEARL) && !material.name().contains("_BED")) {
                ItemStack stack = new ItemStack(material);
                item = loc.getWorld().spawn(loc.clone().add(0, 0.5, 0), Item.class);
                item.setItemStack(stack);
                item.setGlowing(true);
                item.setVelocity(new Vector(0, 0, 0));
            }
        }
    }

}
