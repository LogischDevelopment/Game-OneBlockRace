package tv.logisch.oneBlockRace.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.team.Team;

public class EntityPlaceListener implements Listener {

    @EventHandler
    public void onEntityPlace(EntityPlaceEvent e) {

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
        if(e.getBlock().getX() < x1 || e.getBlock().getX() > x2) {
            e.setCancelled(true);
            return;
        }

    }

}
