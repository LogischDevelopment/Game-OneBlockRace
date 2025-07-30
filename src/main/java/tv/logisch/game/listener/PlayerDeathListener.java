package tv.logisch.game.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tv.logisch.game.OneBlockRace;
import tv.logisch.game.enums.GameState;
import tv.logisch.game.manager.GameManager;
import tv.logisch.game.team.Team;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof org.bukkit.entity.Player p)) return;

        if(GameManager.get().state().equals(GameState.RUNNING)) {
            if(p.getLocation().getBlockY() > GameManager.get().islandManager().y() - 5) {
                e.setCancelled(true);
                return;
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

                    ItemStack mainHand = p.getInventory().getItemInMainHand();
                    ItemStack offHand = p.getInventory().getItemInOffHand();
                    ItemStack totem = null;
                    boolean offhand = false;

                    if (mainHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
                        totem = mainHand;
                    } else if (offHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
                        totem = offHand;
                        offhand = true;
                    }

                    if (totem != null) {
                        p.setFireTicks(0);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                        p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
                        p.playEffect(EntityEffect.PROTECTED_FROM_DEATH);

                        if (totem.getAmount() > 1) {
                            totem.setAmount(totem.getAmount() - 1);
                        } else {
                            if (offhand) {
                                p.getInventory().setItemInOffHand(null);
                            } else {
                                p.getInventory().setItemInMainHand(null);
                            }
                        }
                    } else {
                        p.getInventory().clear();
                        p.getInventory().setArmorContents(new ItemStack[0]);
                        p.getInventory().setHeldItemSlot(0);
                    }
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
            return;
        }

        if(GameManager.get().state().equals(GameState.PVP)) {
            if(e.getFinalDamage() >= p.getHealth()) {
                e.setCancelled(true);

                ItemStack mainHand = p.getInventory().getItemInMainHand();
                ItemStack offHand = p.getInventory().getItemInOffHand();
                ItemStack totem = null;
                boolean offhand = false;

                if (mainHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
                    totem = mainHand;
                } else if (offHand.getType().equals(Material.TOTEM_OF_UNDYING)) {
                    totem = offHand;
                    offhand = true;
                }

                if (totem != null) {
                    p.setFireTicks(0);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 45*20, 2));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5*20, 2));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40*20, 1));
                    p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
                    p.playEffect(EntityEffect.PROTECTED_FROM_DEATH);

                    if (totem.getAmount() > 1) {
                        totem.setAmount(totem.getAmount() - 1);
                    } else {
                        if (offhand) {
                            p.getInventory().setItemInOffHand(null);
                        } else {
                            p.getInventory().setItemInMainHand(null);
                        }
                    }
                    return;
                }

                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_DEATH, 2, 2);
                p.playEffect(EntityEffect.ENTITY_DEATH);
                if(!GameManager.get().keepInventory()) {
                    p.getInventory().forEach(is -> p.getWorld().dropItemNaturally(p.getLocation(), is));
                    p.getInventory().clear();
                }
                p.setGameMode(GameMode.SPECTATOR);
                AtomicInteger count = new AtomicInteger();
                AtomicReference<Player> winner = new AtomicReference<>();
                Bukkit.getOnlinePlayers().forEach(target -> {
                    target.sendMessage(Component.text(OneBlockRace.instance().prefix() + "§f" + p.getName() + " §7is disqualified!"));
                    if (target.getGameMode().equals(GameMode.SURVIVAL)) {
                        count.getAndIncrement();
                        winner.set(target);
                    }
                });
                if(count.get() <= 1) {
                    GameManager.get().stopAfterPvp(winner.get());
                }
            }
            return;
        }

        e.setCancelled(true);

    }

}
