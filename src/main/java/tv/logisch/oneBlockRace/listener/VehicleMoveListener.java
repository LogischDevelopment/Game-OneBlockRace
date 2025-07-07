package tv.logisch.oneBlockRace.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.team.Team;

public class VehicleMoveListener implements Listener {

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {

        Location f = e.getFrom();
        Location t = e.getTo();
        if (f.getBlockX() == t.getBlockX() && f.getBlockY() == t.getBlockY() && f.getBlockZ() == t.getBlockZ()) {
            return;
        }

        e.getVehicle().getPassengers().forEach(entity -> {
            if(!(entity instanceof Player p)) return;
            if(!p.getGameMode().equals(GameMode.SURVIVAL)) return;

            if(GameManager.get().state().equals(GameState.RUNNING)) {

                Team team = GameManager.get().teamManager().getTeam(p);
                if(team == null) {
                    if(t.getBlockY() < 20) {
                        p.teleport(p.getWorld().getSpawnLocation());
                    }
                    return;
                }
                Location island = team.getIsland();
                if(island == null) {
                    return; // No island found for the team
                }
                if(t.getWorld() != island.getWorld()) {
                    return; // Vehicle is not in the correct world
                }
                if(t.getBlockY() < island.getBlockY() - 10) {
                    p.teleport(island.clone().add(0.5, 1, 0.5));
                    p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
                    p.setFireTicks(0);
                    p.setFallDistance(0);
                    if(!GameManager.get().keepInventory()) {
                        p.getInventory().clear();
                        p.getInventory().setArmorContents(new ItemStack[0]);
                        p.getInventory().setHeldItemSlot(0);
                        p.setFoodLevel(20);
                        p.setSaturation(20);
                        p.setExp(0);
                        p.setLevel(0);
                    }
                }

                int x1 = island.getBlockX() - GameManager.get().islandWidth();
                int x2 = island.getBlockX() + GameManager.get().islandWidth();

                if(t.getBlockX() < x1 || t.getBlockX() > x2) {
                    p.teleport(f);
                }

            }
        });

    }

}
