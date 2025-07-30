package tv.logisch.game.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import tv.logisch.game.manager.GameManager;

public class BlockPhysicsListener implements Listener {

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(event.getBlock().getWorld().getName().equals("pvp") || event.getBlock().getWorld().getName().equals("waiting")) {
            Block block = event.getBlock();
            Material type = block.getType();

            if (isFallingBlock(type)) {
                Block below = block.getRelative(BlockFace.DOWN);

                if (!below.getType().isSolid()) {
                    event.setCancelled(true);
                }
            }
            return;
        }
        if(GameManager.get().gravity()) return;
        Block block = event.getBlock();
        Material type = block.getType();

        if (isFallingBlock(type)) {
            Block below = block.getRelative(BlockFace.DOWN);

            if (!below.getType().isSolid()) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isFallingBlock(Material material) {
        return material == Material.SAND
                || material == Material.RED_SAND
                || material == Material.GRAVEL
                || material == Material.ANVIL
                || material == Material.CHIPPED_ANVIL
                || material == Material.DAMAGED_ANVIL
                || material.name().contains("CONCRETE_POWDER"); // optional
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(GameManager.get().gravity()) return;
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
        }
    }

}
