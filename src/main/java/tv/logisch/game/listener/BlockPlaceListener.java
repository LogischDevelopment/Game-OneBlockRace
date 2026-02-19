package tv.logisch.game.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tv.logisch.game.OneBlockRace;
import tv.logisch.game.enums.GameState;
import tv.logisch.game.manager.GameManager;
import tv.logisch.game.team.Team;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

        if(GameManager.get().state().equals(GameState.PVP) || GameManager.get().state().equals(GameState.PROTECTION)) {
            if (e.getBlockPlaced().getType().equals(Material.BRICKS)) {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(OneBlockRace.instance(), () -> {
                    e.getBlockPlaced().setType(Material.AIR);
                }, 7*20L);
                return;
            }
            if(e.getBlockPlaced().getType().equals(Material.TNT)) {
                e.setCancelled(false);
                e.getBlockPlaced().setType(Material.AIR);

                Location loc = e.getBlockPlaced().getLocation().add(0.5, 0, 0.5);
                TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawn(loc, TNTPrimed.class);
                tnt.setSource(e.getPlayer());
                tnt.setFuseTicks(80); // 4 seconds
                return;
            }
            if(e.getBlockPlaced().getType().equals(Material.COBWEB)) {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(OneBlockRace.instance(), () -> {
                    e.getBlockPlaced().setType(Material.AIR);
                }, 16*20L);
                return;
            }
            if(e.getBlockPlaced().getType().equals(Material.LAVA)) {
                e.setCancelled(false);
                return;
            }
            if(e.getBlockPlaced().getType().equals(Material.WATER)) {
                e.setCancelled(false);
                return;
            }
            if(e.getBlockPlaced().getType().equals(Material.PUFFERFISH)) {
                e.setCancelled(false);
                return;
            }
        }

        if(!GameManager.get().state().equals(GameState.RUNNING)) {
            e.setCancelled(true);
            return;
        }

        if(e.getBlockPlaced().getLocation().getBlockZ() < 1) {
            e.setCancelled(true);
            return;
        }

        Team team = GameManager.get().teamManager().getTeam(e.getPlayer());
        if(team == null) {
            e.setCancelled(true);
            return;
        }

        int x = team.getIsland().getBlockX();
        int x1 = x - GameManager.get().islandWidth();
        int x2 = x + GameManager.get().islandWidth();

        if(x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if(e.getBlockPlaced().getLocation().getBlockX() < x1 || e.getBlockPlaced().getLocation().getBlockX() > x2) {
            e.setCancelled(true);
            return;
        }

        if(!e.getBlockPlaced().getType().name().toLowerCase().contains("sapling")) {
            team.addBlock(e.getBlockPlaced());
            GameManager.get().updateScoreboard(e.getPlayer());
        }

    }

}
