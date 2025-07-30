package tv.logisch.game.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import tv.logisch.game.OneBlockRace;
import tv.logisch.game.team.TeamManager;
import tv.logisch.game.utils.Format;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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



    private static final List<Material> VALID_MATERIALS = Arrays.stream(Material.values())
            .filter(m -> !m.name().contains("LEGACY"))
            .filter(m -> !m.name().contains("_SPAWN_EGG"))
            .filter(m -> !m.name().contains("_BED"))
            .filter(m -> m != Material.ENDER_PEARL)
            .filter(Material::isItem)
            .toList();

    private final Random random = new Random();

    private void spawn(Location loc) {
        if (VALID_MATERIALS.isEmpty()) {
            OneBlockRace.instance().logger().warning("Keine gültigen Materialien zum Spawnen gefunden!");
            return;
        }

        Material material = VALID_MATERIALS.get(random.nextInt(VALID_MATERIALS.size()));
        ItemStack stack = new ItemStack(material);
        Item item = loc.getWorld().spawn(loc.clone().add(0, 0.5, 0), Item.class);
        item.setItemStack(stack);
        item.setGlowing(true);
        item.setVelocity(new Vector(0, 0, 0));
    }


}
