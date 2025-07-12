package tv.logisch.oneBlockRace.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockRedstoneListener implements Listener {

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent e) {
        if(e.getNewCurrent() > 0) {
            e.setNewCurrent(0);
        }
    }

}
