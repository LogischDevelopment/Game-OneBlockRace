package tv.logisch.oneBlockRace.gui.setting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;

public class SettingGUIListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        if(!PlainTextComponentSerializer.plainText().serialize(e.getView().title()).equals(OneBlockRace.instance().prefix() + "Settings")) return;
        Bukkit.getScheduler().runTaskLaterAsynchronously(OneBlockRace.instance(), () -> {
            if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.CRAFTING)) {
                if (DurationGUI.has(player)) {
                    DurationGUI gui = DurationGUI.get(player);
                    gui.close();
                } else if (IslandGUI.has(player)) {
                    IslandGUI gui = IslandGUI.get(player);
                    gui.close();
                } else if (UtilityGUI.has(player)) {
                    UtilityGUI gui = UtilityGUI.get(player);
                    gui.close();
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player p)) return;
        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if(!PlainTextComponentSerializer.plainText().serialize(e.getView().title()).equals(OneBlockRace.instance().prefix() + "Settings")) return;
        if(!DurationGUI.has(p) && !IslandGUI.has(p) && !UtilityGUI.has(p)) return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        NamespacedKey key = GameManager.get().settingsKey();
        if(clicked.getItemMeta() == null || !clicked.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return;
        }

        String action = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if(action == null) return;

        if(action.equalsIgnoreCase("open_duration")) {
            DurationGUI.get(p).open();
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            return;
        } else if(action.equalsIgnoreCase("open_island")) {
            IslandGUI.get(p).open();
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            return;
        } else if(action.equalsIgnoreCase("open_utilities")) {
            UtilityGUI.get(p).open();
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            return;
        }

        switch (action) {
            case "game_duration" -> {
                if(!GameManager.get().state().equals(GameState.WAITING)) return;
                if (e.isLeftClick() && !e.isShiftClick()) {
                    if (GameManager.get().time() <= 5 * 60) return;
                    GameManager.get().time(GameManager.get().time() - 5 * 60);
                } else if (e.isRightClick() && !e.isShiftClick()) {
                    GameManager.get().time(GameManager.get().time() + 5 * 60);
                } else if (e.isShiftClick() && e.isRightClick()) {
                    GameManager.get().time(GameManager.get().time() + 15 * 60);
                } else if (e.isShiftClick() && e.isLeftClick()) {
                    if (GameManager.get().time() <= 15 * 60) return;
                    GameManager.get().time(GameManager.get().time() - 15 * 60);
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                DurationGUI.guis.forEach(DurationGUI::update);
                return;
            }
            case "drop_interval" -> {
                if(!GameManager.get().state().equals(GameState.WAITING)) return;
                if (e.isLeftClick() && !e.isShiftClick()) {
                    if (GameManager.get().dropInterval() <= 1) return;
                    GameManager.get().dropInterval(GameManager.get().dropInterval() - 1);
                } else if (e.isRightClick() && !e.isShiftClick()) {
                    GameManager.get().dropInterval(GameManager.get().dropInterval() + 1);
                } else if (e.isShiftClick() && e.isRightClick()) {
                    GameManager.get().dropInterval(GameManager.get().dropInterval() + 5);
                } else if (e.isShiftClick() && e.isLeftClick()) {
                    if (GameManager.get().dropInterval() <= 5) return;
                    GameManager.get().dropInterval(GameManager.get().dropInterval() - 5);
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                DurationGUI.guis.forEach(DurationGUI::update);
                return;
            }
            case "shopping_duration" -> {
                if(!GameManager.get().state().equals(GameState.WAITING) && !GameManager.get().state().equals(GameState.STARTING)  && !GameManager.get().state().equals(GameState.RUNNING)) return;
                if (e.isLeftClick() && !e.isShiftClick()) {
                    if (GameManager.get().shoppingTime() <= 5) return;
                    GameManager.get().shoppingTime(GameManager.get().shoppingTime() - 5);
                } else if (e.isRightClick() && !e.isShiftClick()) {
                    GameManager.get().shoppingTime(GameManager.get().shoppingTime() + 5);
                } else if (e.isShiftClick() && e.isRightClick()) {
                    GameManager.get().shoppingTime(GameManager.get().shoppingTime() + 10);
                } else if (e.isShiftClick() && e.isLeftClick()) {
                    if (GameManager.get().shoppingTime() <= 10) return;
                    GameManager.get().shoppingTime(GameManager.get().shoppingTime() - 10);
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                DurationGUI.guis.forEach(DurationGUI::update);
                return;
            }
            case "island_width" -> {
                if(!GameManager.get().state().equals(GameState.WAITING)) return;
                if (e.isLeftClick()) {
                    if (GameManager.get().islandWidth() <= 1) return;
                    GameManager.get().islandWidth(GameManager.get().islandWidth() - 1);
                } else if (e.isRightClick()) {
                    GameManager.get().islandWidth(GameManager.get().islandWidth() + 1);
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                IslandGUI.guis.forEach(IslandGUI::update);
                return;
            }
            case "toggle_pvp_phase" -> {
                if(!GameManager.get().state().equals(GameState.WAITING) && !GameManager.get().state().equals(GameState.STARTING) && !GameManager.get().state().equals(GameState.RUNNING)) return;
                if (e.isLeftClick()) {
                    GameManager.get().pvpPhase(!GameManager.get().pvpPhase());
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                UtilityGUI.guis.forEach(UtilityGUI::update);
                return;
            }
            case "toggle_destroying" -> {
                if(!GameManager.get().state().equals(GameState.WAITING) && !GameManager.get().state().equals(GameState.STARTING) && !GameManager.get().state().equals(GameState.RUNNING)) return;
                if (e.isLeftClick()) {
                    GameManager.get().canDestroy(!GameManager.get().canDestroy());
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                UtilityGUI.guis.forEach(UtilityGUI::update);
                return;
            }
            case "toggle_keep_inventory" -> {
                if(!GameManager.get().state().equals(GameState.WAITING) && !GameManager.get().state().equals(GameState.STARTING) && !GameManager.get().state().equals(GameState.RUNNING)) return;
                if (e.isLeftClick()) {
                    GameManager.get().keepInventory(!GameManager.get().keepInventory());
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                UtilityGUI.guis.forEach(UtilityGUI::update);
                return;
            }
            case "toggle_gravity" -> {
                if(!GameManager.get().state().equals(GameState.WAITING) && !GameManager.get().state().equals(GameState.STARTING) && !GameManager.get().state().equals(GameState.RUNNING)) return;
                if (e.isLeftClick()) {
                    GameManager.get().gravity(!GameManager.get().gravity());
                }
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                UtilityGUI.guis.forEach(UtilityGUI::update);
                return;
            }
        }

    }

}
