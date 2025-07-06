package tv.logisch.oneBlockRace.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.team.Team;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof org.bukkit.entity.Player p)) return;

        if(!GameManager.get().state().equals(GameState.RUNNING)) {
            e.setCancelled(true);
            return;
        }

        if(p.getLocation().getBlockY() > 30) {
            e.setCancelled(true);
        }

        if(e.getFinalDamage() >= p.getHealth()) {
            e.setCancelled(true);
            p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
            p.setFireTicks(0);
            p.setFallDistance(0);
            p.setFoodLevel(20);
            p.setSaturation(20);
            p.setExp(0);
            p.setLevel(0);

            if(!GameManager.get().keepInventory()) {
                p.getInventory().clear();
                p.getInventory().setArmorContents(new ItemStack[0]);
                p.getInventory().setHeldItemSlot(0);
            }
            p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
            p.setAllowFlight(false);
            p.setFlying(false);

            Team team = GameManager.get().teamManager().getTeam(p);
            if(team != null) {
                team.teleportToIsland(p);
            } else {
                p.teleport(p.getWorld().getSpawnLocation());
            }
        }
    }

}
