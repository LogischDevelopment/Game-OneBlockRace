package tv.logisch.oneBlockRace.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.scoreboard.Scoreboard;
import tv.logisch.oneBlockRace.team.Team;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        e.joinMessage(Component.empty());
        for(Player target : Bukkit.getOnlinePlayers()) {
            target.sendMessage(Component.text("§a§lJOIN §8» §f" + e.getPlayer().getName() + " §7joined the game!"));
        }

        Player p = e.getPlayer();
        if(GameManager.get().state().equals(GameState.RUNNING)) {

            Team team1 = GameManager.get().teamManager().getTeam(p);
            if(team1 != null) {
                team1.teleportToIsland(e.getPlayer());
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
                GameManager.get().itemManager().addPlayer(e.getPlayer());
                Scoreboard.scoreboards.add(new Scoreboard(p));
                return;
            }

            p.setGameMode(GameMode.SURVIVAL);
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
            Location loc = GameManager.get().islandManager().createIsland();
            team.setIsland(loc);
            GameManager.get().teamManager().addTeam(team);
            team.teleportToIsland(p);
            GameManager.get().itemManager().addPlayer(e.getPlayer());
        } else {
            e.getPlayer().setGameMode(GameMode.ADVENTURE);
            e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation().clone().add(0.5, 1, 0.5));
        }

        Scoreboard.scoreboards.add(new Scoreboard(p));

    }

}
