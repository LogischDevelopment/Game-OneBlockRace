package tv.logisch.oneBlockRace.gui.shop;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.team.Team;

public class ShopGUIListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        if(!PlainTextComponentSerializer.plainText().serialize(e.getView().title()).equals(OneBlockRace.instance().prefix() + "Shop")) return;
        Bukkit.getScheduler().runTaskLaterAsynchronously(OneBlockRace.instance(), () -> {
            if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.CRAFTING)) {
                if (WeaponGUI.has(player)) {
                    WeaponGUI gui = WeaponGUI.get(player);
                    gui.close();
                } else if (ArmorGUI.has(player)) {
                    ArmorGUI gui = ArmorGUI.get(player);
                    gui.close();
                } else if (UtilityGUI.has(player)) {
                    UtilityGUI gui = UtilityGUI.get(player);
                    gui.close();
                } else if (EnchantingGUI.has(player)) {
                    EnchantingGUI gui = EnchantingGUI.get(player);
                    gui.close();
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player p)) return;
        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if(!PlainTextComponentSerializer.plainText().serialize(e.getView().title()).equals(OneBlockRace.instance().prefix() + "Shop")) return;
        if(!WeaponGUI.has(p) && !ArmorGUI.has(p) && !UtilityGUI.has(p) && !EnchantingGUI.has(p)) return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        NamespacedKey key = GameManager.get().shopKey();
        if(clicked.getItemMeta() == null || !clicked.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return;
        }

        String action = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if(action == null) return;

        if(action.equalsIgnoreCase("open_weapons")) {
            WeaponGUI.get(p).open();
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            return;
        } else if(action.equalsIgnoreCase("open_armor")) {
            ArmorGUI.get(p).open();
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            return;
        } else if(action.equalsIgnoreCase("open_utilities")) {
            UtilityGUI.get(p).open();
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            return;
        } else if(action.equalsIgnoreCase("open_enchantments")) {
            EnchantingGUI.get(p).open();
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            return;
        }

        if(action.startsWith("item_buy_")) {
            int cost = Integer.parseInt(action.split("_")[2]);
            Team team = GameManager.get().teamManager().getTeam(p);
            if(team.coins() < cost) {
                p.sendMessage(OneBlockRace.instance().prefix() + "§cYou do not have enough coins to buy this item.");
                return;
            }
            team.coins(team.coins() - cost);
            ItemStack item = clicked.clone();
            item.editMeta(m -> {
                m.getPersistentDataContainer().remove(key);
            });
            p.getInventory().addItem(item);
            Component name = item.getItemMeta().displayName();
            p.sendMessage(OneBlockRace.instance().prefix() + "§aYou have bought " + item.getAmount() + "x " + (name == null ? "Unknown" : PlainTextComponentSerializer.plainText().serialize(name)) + " for §e" + cost + " coins§a.");
            p.sendActionBar(Component.text(OneBlockRace.instance().prefix()+"Current coins: §f" + team.coins()));
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            return;
        }
    }

    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent e) {
        if(!GameManager.get().state().equals(GameState.PVP)) return;
        if(!e.getSlot().equals(EquipmentSlot.HEAD)) return;

        if(e.getNewItem().getType().equals(Material.TURTLE_HELMET)) {
            Player player = e.getPlayer();
            AttributeInstance attribute = player.getAttribute(Attribute.SCALE);
            if (attribute == null) player.registerAttribute(Attribute.SCALE);
            attribute.setBaseValue(0.8);
            return;
        }
        if(e.getOldItem().getType().equals(Material.TURTLE_HELMET)) {
            Player player = e.getPlayer();
            AttributeInstance attribute = player.getAttribute(Attribute.SCALE);
            if (attribute == null) player.registerAttribute(Attribute.SCALE);
            attribute.setBaseValue(1);
            return;
        }

    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent e) {
        if(!GameManager.get().state().equals(GameState.PVP)) return;

        ItemStack item = e.getItem();
        if(item.getType().equals(Material.SUSPICIOUS_STEW)) {
            int random = (int) (Math.random() * 2) + 1;

            if(random == 1) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 15*20, 1));
            } else {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 15*20, 1));
            }
        } else if(item.getType().equals(Material.GOLDEN_CARROT)) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(!e.getAction().isRightClick()) return;
        if(e.getItem() == null) return;
        if(!e.getItem().getPersistentDataContainer().has(GameManager.get().shopKey())) return;

        String s = e.getItem().getPersistentDataContainer().get(GameManager.get().shopKey(), PersistentDataType.STRING);
        if(s == null || s.isEmpty()) return;

        if(s.equalsIgnoreCase("open")) {
            WeaponGUI.get(e.getPlayer()).open();
            return;
        }
        if(s.equalsIgnoreCase("anvil")) {
            AnvilView anvilView = MenuType.ANVIL.create(e.getPlayer());
            e.getPlayer().openInventory(anvilView);
            return;
        }

    }

}
