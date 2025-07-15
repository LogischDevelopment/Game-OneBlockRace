package tv.logisch.oneBlockRace.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class BlockGrowListener implements Listener {

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {

        if(e.getBlock().getWorld().getName().toLowerCase().contains("waiting")) e.setCancelled(true);

    }

}
