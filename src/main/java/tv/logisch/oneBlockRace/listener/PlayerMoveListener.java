package tv.logisch.oneBlockRace.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        Location f = e.getFrom();
        Location t = e.getTo();
        if(f.getBlockX() == t.getBlockX() && f.getBlockY() == t.getBlockY() && f.getBlockZ() == t.getBlockZ()) {
            return;
        }
        if(!GameManager.get().state().equals(GameState.RUNNING)) {
            if(t.getBlockY() < 20) {
                e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
            }
            return;
        }
        if(GameManager.get().teamManager().getTeam(e.getPlayer()) == null) {
            if(t.getBlockY() < 20) {
                e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
            }
            return;
        }

        Location island = GameManager.get().teamManager().getTeam(e.getPlayer()).getIsland();
        if(island == null) {
            return;
        }
        if(t.getWorld() != island.getWorld()) {
            return;
        }

        if(t.getBlockY() < island.getBlockY() - 10) {
            e.getPlayer().teleport(island.clone().add(0.5, 1, 0.5));
            return;
        }

        int x1 = island.getBlockX() - GameManager.get().islandWidth() / 2;
        int x2 = island.getBlockX() + GameManager.get().islandWidth() / 2;

        if(t.getBlockX() < x1 || t.getBlockX() > x2) {
            e.getPlayer().setVelocity(e.getPlayer().getLocation().toVector().subtract(island.toVector()).normalize().multiply(-0.5));
        }

    }

}
