package tv.logisch.game.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import tv.logisch.game.enums.GameState;
import tv.logisch.game.manager.GameManager;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(!e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && !e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) return;

        Location f = e.getFrom();
        Location t = e.getTo();
        if(f.getBlockX() == t.getBlockX() && f.getBlockY() == t.getBlockY() && f.getBlockZ() == t.getBlockZ()) {
            return;
        }
        if(GameManager.get().state().equals(GameState.STARTING)) {
            e.setCancelled(true);
            return;
        }
        if(!GameManager.get().state().equals(GameState.RUNNING)) {
            if(t.getBlockY() < 50) {
                e.setTo(e.getPlayer().getWorld().getSpawnLocation().clone().add(0, 1, 0));
            }
            return;
        }
        if(GameManager.get().teamManager().getTeam(e.getPlayer()) == null) {
            if(t.getBlockY() < 20) {
                e.setTo(e.getPlayer().getWorld().getSpawnLocation().clone().add(0.5, 1, 0.5));
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
            e.getPlayer().setHealth(e.getPlayer().getAttribute(Attribute.MAX_HEALTH).getValue());
            e.getPlayer().setFireTicks(0);
            e.getPlayer().setFallDistance(0);

            if(!GameManager.get().keepInventory()) {
                e.getPlayer().getInventory().clear();
                e.getPlayer().getInventory().setArmorContents(new ItemStack[0]);
                e.getPlayer().getInventory().setHeldItemSlot(0);
                e.getPlayer().setFoodLevel(20);
                e.getPlayer().setSaturation(20);
                e.getPlayer().setExp(0);
                e.getPlayer().setLevel(0);
                e.getPlayer().getActivePotionEffects().forEach(effect -> e.getPlayer().removePotionEffect(effect.getType()));
            }
            e.getPlayer().setAllowFlight(false);
            e.getPlayer().setFlying(false);
            return;
        }

        int x1 = island.getBlockX() - GameManager.get().islandWidth();
        int x2 = island.getBlockX() + GameManager.get().islandWidth();

        if(t.getBlockX() < x1 || t.getBlockX() > x2) {
            e.setCancelled(true);
        }

    }

}
