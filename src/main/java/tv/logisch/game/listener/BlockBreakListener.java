package tv.logisch.game.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tv.logisch.game.enums.GameState;
import tv.logisch.game.manager.GameManager;
import tv.logisch.game.team.Team;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

        if(!GameManager.get().state().equals(GameState.RUNNING)) {
            e.setCancelled(true);
            return;
        }

        Team team = GameManager.get().teamManager().getTeam(e.getPlayer());
        if(team == null) {
            e.setCancelled(true);
            return;
        }

        if(!GameManager.get().canDestroy()) {
            e.setCancelled(team.placedBlocks().contains(e.getBlock()));
            return;
        }

        if(e.getBlock().getLocation().getBlockZ() < 1) {
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
        if(e.getBlock().getLocation().getBlockX() < x1 || e.getBlock().getLocation().getBlockX() > x2) {
            e.setCancelled(true);
            return;
        }

        team.removeBlock(e.getBlock());

    }

}
