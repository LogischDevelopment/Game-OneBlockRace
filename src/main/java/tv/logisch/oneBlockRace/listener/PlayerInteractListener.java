package tv.logisch.oneBlockRace.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.team.Team;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        if(e.getInteractionPoint() == null) return;

        if(GameManager.get().state().equals(GameState.STARTING)) {
            e.setCancelled(true);
            return;
        }
        if(!GameManager.get().state().equals(GameState.RUNNING)) {
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
        if(e.getInteractionPoint().getBlockX() < x1 || e.getInteractionPoint().getBlockX() > x2) {
            e.setCancelled(true);
            return;
        }

    }

}
