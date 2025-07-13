package tv.logisch.oneBlockRace.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.team.Team;
import tv.logisch.oneBlockRace.team.TeamManager;
import tv.logisch.oneBlockRace.utils.AnimationUtils;
import tv.logisch.oneBlockRace.utils.Format;

import java.util.UUID;

@Getter
@Setter
@Accessors(fluent = true)
public class GameManager {

    private static GameManager instance;
    public static GameManager get() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    private GameState state = GameState.WAITING;
    private World world;
    private long dropInterval = 10;
    private long teamSize = 1;
    private int islandWidth = 2;
    private boolean keepInventory = false;
    private boolean canDestroy = false;
    private boolean gravity = true;
    private long time = 1800;
    private long timeLeft = time;

    private TeamManager teamManager;
    private ItemManager itemManager;
    private IslandManager islandManager;

    public GameManager() {
        this.world = Bukkit.createWorld(new WorldCreator("world"));
        this.teamManager = new TeamManager();
        this.islandManager = new IslandManager(islandWidth);
        this.itemManager = new ItemManager(teamManager, "§b§lOBR §8» §7Next drop in §f%S% §7seconds!");
    }

    public void start() {
        state = GameState.STARTING;
        timeLeft = time;

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(org.bukkit.GameMode.SURVIVAL);
            p.setAllowFlight(false);
            p.setFlying(false);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[0]);
            p.getInventory().setHeldItemSlot(0);
            p.setExp(0);
            p.setLevel(0);
            Team team = new Team();
            team.addPlayer(p);
            Location loc = islandManager.createIsland();
            team.setIsland(loc);
            teamManager.addTeam(team);
            team.teleportToIsland();
        }

        Bukkit.getScheduler().runTaskAsynchronously(OneBlockRace.instance(), () -> {
            for(int i = 5; i > 0; i--) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("§b§lOBR §8» §7The game starts in §f" + i + "§7 seconds!");
                    if(i <= 3) {
                        p.playSound(p, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    }
                }
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {  }
            }
            state = GameState.RUNNING;
            for(Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("§b§lOBR §8» §7The game has started!");
                p.playSound(p, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }

            itemManager.start();
            this.startTimer();
            AnimationUtils.startAnimation();
        });

    }

    public void stop() {
        state = GameState.ENDING;

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§b§lOBR §8» §7The game has ended!");
            p.playSound(p, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            p.setGameMode(GameMode.SPECTATOR);
            p.getInventory().clear();
            p.sendMessage("§b§lOBR §8» §7The server stops in 1 minute!");
            p.setAllowFlight(true);
            p.setFlying(true);
        }

        itemManager.stop();
        AnimationUtils.stopAnimation();
        state = GameState.ENDED;

        Bukkit.getScheduler().runTaskLater(OneBlockRace.instance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.kick(Component.text("§b§lOBR §8» §7The server is restarting!"));
            }
            Bukkit.getServer().shutdown();
        }, 1200L); // 1 minute in ticks

    }

    int taskId;
    public void startTimer() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlockRace.instance(), () -> {
            timeLeft--;
            if (timeLeft <= 0) {
                stop();
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            } else if(
                    timeLeft % 3600 == 0 || // every full hour
                    timeLeft == 1800 || // 30 minutes
                    timeLeft == 900 || // 15 minutes
                    timeLeft == 300 || // 5 minutes
                    timeLeft == 180 || // 3 minutes
                    timeLeft == 120 || // 2 minutes
                    timeLeft == 60 || // 1 minute
                    timeLeft == 30 || // 30 seconds
                    timeLeft == 15 || // 15 seconds
                    timeLeft == 10 || // 10 seconds
                    timeLeft == 5 || // 5 seconds
                    timeLeft <= 3// 3 seconds and below
            ) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("§b§lOBR §8» §7The game will end in §f" + Format.time(timeLeft) + "§7!");
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }

            }
        }, 20, 20);
    }

    public boolean isHost(UUID uuid) {
        return OneBlockRace.instance().gameConfig().hostUUID().equals(uuid);
    }

}
