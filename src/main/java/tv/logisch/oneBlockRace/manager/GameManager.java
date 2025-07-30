package tv.logisch.oneBlockRace.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.scoreboard.Scoreboard;
import tv.logisch.oneBlockRace.team.Team;
import tv.logisch.oneBlockRace.team.TeamManager;
import tv.logisch.oneBlockRace.utils.AnimationUtils;
import tv.logisch.oneBlockRace.utils.Format;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
    private World gameWorld;
    private World waitingWorld;
    private World pvpWorld;
    private long dropInterval = 10;
    private long teamSize = 1;
    private int islandWidth = 2;
    private boolean keepInventory = false;
    private boolean canDestroy = false;
    private boolean gravity = true;
    private boolean pvpPhase = true;
    private int shoppingTime = 120;
    private long time = 1800;
    private long timeLeft = time;

    private final NamespacedKey shopKey = new NamespacedKey("logisch_obr", "shop");
    private final NamespacedKey settingsKey = new NamespacedKey("logisch_obr", "settings");

    private List<UUID> skippingVotes;

    private TeamManager teamManager;
    private ItemManager itemManager;
    private IslandManager islandManager;

    public GameManager() {
        this.gameWorld = Bukkit.createWorld(new WorldCreator("world"));
        this.waitingWorld = Bukkit.createWorld(new WorldCreator("waiting"));
        this.pvpWorld = Bukkit.createWorld(new WorldCreator("pvp"));
        this.teamManager = new TeamManager();
        this.islandManager = new IslandManager(islandWidth);
        this.itemManager = new ItemManager(teamManager, "§b§lOBR §8» §7Next drop in §f%S% §7seconds!");
        this.skippingVotes = new ArrayList<>();
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
            this.startScoreboardUpdater();
            AnimationUtils.startAnimation();
        });

    }

    public void startShopping() {
        Bukkit.getScheduler().cancelTask(scoreboardTaskId);
        itemManager.stop();
        AnimationUtils.stopAnimation();
        Scoreboard.scoreboards.getFirst().unregister();

        this.state = GameState.SHOPPING;
        this.teamManager.setupCoins();
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(this.waitingWorld.getSpawnLocation());
            p.getInventory().clear();
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"The shopping phase has started!"));
            p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"You have §f"+(Format.time(this.shoppingTime))+" §7to buy items!"));
            p.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1)));
            p.sendTitlePart(TitlePart.TITLE, Component.text("§b§lShopping Phase"));
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            p.setLevel(999);

            ItemStack stack = new ItemStack(Material.CHEST);
            stack.editMeta(m -> {
                m.displayName(Component.text("§8» §6§lShop"));
                m.lore(List.of(
                        Component.text("§7Klicke hier, um den Shop zu öffnen!")
                ));
                m.getPersistentDataContainer().set(this.shopKey(), PersistentDataType.STRING, "open");
            });
            p.getInventory().setItem(8, stack);
            stack = new ItemStack(Material.ANVIL);
            stack.editMeta(m -> {
                m.displayName(Component.text("§8» §6§lAmboss"));
                m.lore(List.of(
                        Component.text("§7Interagiere mit dem Amboss, um deine Items zu verzaubern!")
                ));
                m.getPersistentDataContainer().set(this.shopKey, PersistentDataType.STRING, "anvil");
            });
            p.getInventory().setItem(7, stack);
        }

        AtomicInteger taskId = new AtomicInteger();
        taskId.set(Bukkit.getScheduler().runTaskTimer(OneBlockRace.instance(), () -> {
            this.shoppingTime--;
            if(this.shoppingTime <= 0) {
                Bukkit.getScheduler().cancelTask(taskId.get());
                this.startPVP();
                return;
            }
            if(this.shoppingTime % 60 == 0 || this.shoppingTime == 30 || this.shoppingTime == 15 || this.shoppingTime == 10 || this.shoppingTime <= 5) {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.sendMessage(OneBlockRace.instance().prefix() + "The pvp phase starts in §f" + this.shoppingTime + "§7 seconds!");
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    p.sendActionBar(Component.text(OneBlockRace.instance().prefix()+"Current coins: §f"+this.teamManager.getTeam(p).coins()));
                });
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.sendActionBar(Component.text(OneBlockRace.instance().prefix()+"Current coins: §f"+this.teamManager.getTeam(p).coins()));
                });
            }
        }, 20, 20).getTaskId());
    }

    public void startPVP() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.teleport(this.pvpWorld.getSpawnLocation());
            p.sendMessage(OneBlockRace.instance().prefix() + "The §fPVP Phase §7has started!");
            p.playSound(p, Sound.ITEM_GOAT_HORN_SOUND_1, 1.0f, 1.0f);
            p.setGameMode(GameMode.SURVIVAL);
            p.setLevel(0);
            p.setExp(0);
            for (ItemStack content : p.getInventory().getContents()) {
                if(content == null) continue;
                if(content.getType().equals(Material.CHEST) || content.getType().equals(Material.ANVIL)) {
                    if(content.getPersistentDataContainer().has(this.shopKey, PersistentDataType.STRING)) {
                        content.setAmount(0);
                    }
                }
            }
        });
        this.state = GameState.PVP;
    }

    public void stopAfterPvp(Player winner) {
        state = GameState.ENDING;

        String winnerName = winner != null ? "§a§l" + winner.getName() : "N/A";

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§b§lOBR §8» §7The game has ended!");
            p.sendMessage("§b§lOBR §8» §7Winner: " + winnerName);
            p.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1)));
            p.sendTitlePart(TitlePart.SUBTITLE, Component.text("§7Congratulations!"));
            p.sendTitlePart(TitlePart.TITLE, Component.text(winnerName));
            p.playSound(p, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            p.setGameMode(GameMode.SPECTATOR);
            p.getInventory().clear();
            p.sendMessage("§b§lOBR §8» §7The server stops in 15 seconds!");
            p.setAllowFlight(true);
            p.setFlying(true);
        }

        state = GameState.ENDED;
        this.serverStopCountdown(15);
    }

    public void stop() {
        state = GameState.ENDING;
        Bukkit.getScheduler().cancelTask(scoreboardTaskId);
        itemManager.stop();
        AnimationUtils.stopAnimation();

        Team winner = GameManager.get().teamManager().getTop(1).stream().findFirst().orElse(null);
        String winnerName = winner != null ? "§a§l" + winner.players().getFirst().getName() + " §8(§f"+winner.getScore()+"§8)" : "N/A §8(§f0§8)";

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§b§lOBR §8» §7The game has ended!");
            p.sendMessage("§b§lOBR §8» §7Winner: " + winnerName);
            p.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1)));
            p.sendTitlePart(TitlePart.SUBTITLE, Component.text("§7Congratulations!"));
            p.sendTitlePart(TitlePart.TITLE, Component.text(winnerName));
            p.playSound(p, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            p.setGameMode(GameMode.SPECTATOR);
            p.getInventory().clear();
            p.sendMessage("§b§lOBR §8» §7The server stops in 1 minute!");
            p.setAllowFlight(true);
            p.setFlying(true);
        }

        state = GameState.ENDED;
        this.serverStopCountdown(60);

    }

    public void serverStopCountdown(int countdown) {
        AtomicInteger stopSeconds = new AtomicInteger(countdown);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlockRace.instance(), () -> {
            stopSeconds.getAndDecrement();
            if(stopSeconds.get() == 30 || stopSeconds.get() == 15 || stopSeconds.get() == 10 || stopSeconds.get() <= 5) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("§b§lOBR §8» §7The server will stop in §f" + stopSeconds + "§7 seconds!");
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }
            }
            if(stopSeconds.get() <= 0) {
                Bukkit.getServer().shutdown();
            }
        }, 20, 20);
    }

    int scoreboardTaskId = 0;
    public void startScoreboardUpdater() {
        scoreboardTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlockRace.instance(), () -> {
            Scoreboard.scoreboards.forEach(Scoreboard::update);
        }, 20, 20);
    }

    int taskId;
    public void startTimer() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlockRace.instance(), () -> {
            timeLeft--;
            if (timeLeft <= 0) {
                if(this.pvpPhase) startShopping();
                else stop();
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
                    p.sendMessage("§b§lOBR §8» §7The phase will end in §f" + Format.time(timeLeft) + "§7!");
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }

            }
        }, 20, 20);
    }

    public int getRequiredVotesToSkip() {
        return Bukkit.getOnlinePlayers().size()/ 2 + 1;
    }

    public boolean addSkippingVote(Player player) {
        if(skippingVotes.contains(player.getUniqueId())) return false;
        skippingVotes.add(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(target -> {
            target.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§f"+player.getName()+" §7has voted to skip shopping! §8[§f"+GameManager.get().skippingVotes().size()+"§8/§f"+GameManager.get().getRequiredVotesToSkip()+"§8]"));
        });
        if(skippingVotes.size() >= this.getRequiredVotesToSkip()) {
            if(this.state.equals(GameState.SHOPPING)) {
                if(this.shoppingTime <= 10) {
                    return false;
                }
                this.shoppingTime(10);
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§aThe shopping phase has been skipped!"));
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                });
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean isHost(UUID uuid) {
        return OneBlockRace.instance().gameConfig().hostUUID().equals(uuid);
    }

}
