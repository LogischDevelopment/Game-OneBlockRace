package tv.logisch.oneBlockRace.gui;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.utils.Format;

import java.util.ArrayList;
import java.util.List;

public class SettingGUI {

    public static List<SettingGUI> guis = new ArrayList<>();
    public static SettingGUI get(Player player) {
        for(SettingGUI gui : guis) {
            if(gui.p.equals(player)) {
                return gui;
            }
        }
        return new SettingGUI(player);
    }
    public static boolean has(Player player) {
        for(SettingGUI gui : guis) {
            if(gui.p.equals(player)) {
                return true;
            }
        }
        return false;
    }

    @Getter @Accessors(fluent = true)
    private final Player p;

    @Getter @Accessors(fluent = true)
    private int page;

    private Inventory inventory;

    private SettingGUI(Player player) {
        this.p = player;
        this.page = 1;
        guis.add(this);
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this.p, 45, Component.text("§b§lOBR §8» §7Settings"));
        update();
    }

    public void update() {
        ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        stack.editMeta(m -> m.setHideTooltip(true));
        for(int i = 0; i < 9; i++) {
            this.inventory.setItem(i, stack);
        }
        for(int i = 36; i < 45; i++) {
            this.inventory.setItem(i, stack);
        }

        /* GAME DURATION */
        stack = new ItemStack(Material.CLOCK, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Game Duration"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + Format.time(GameManager.get().time())));
            lore.add(Component.text("§fL-click§8: §7Decrease by 5 minute"));
            lore.add(Component.text("§fR-click§8: §7Increase by 5 minute"));
            lore.add(Component.text("§fShift + R-click§8: §7Increase by 10 minutes"));
            lore.add(Component.text("§fShift + L-click§8: §7Decrease by 10 minutes"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("obr", "setting"), PersistentDataType.STRING, "game_duration");
        });
        this.inventory.setItem(18, stack);

        /* DROP INTERVAL */
        stack = new ItemStack(Material.HOPPER, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Drop Interval"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + Format.time(GameManager.get().dropInterval())));
            lore.add(Component.text("§fL-click§8: §7Decrease by 1 second"));
            lore.add(Component.text("§fR-click§8: §7Increase by 1 second"));
            lore.add(Component.text("§fShift + R-click§8: §7Increase by 5 seconds"));
            lore.add(Component.text("§fShift + L-click§8: §7Decrease by 5 seconds"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("obr", "setting"), PersistentDataType.STRING, "drop_interval");
        });
        this.inventory.setItem(20, stack);

        /* ISLAND WIDTH */
        stack = new ItemStack(Material.ENDER_PEARL, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Island Width"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + GameManager.get().islandWidth() + " blocks/side"));
            lore.add(Component.text("§fL-click§8: §7Decrease by 1 block/side"));
            lore.add(Component.text("§fR-click§8: §7Increase by 1 block/side"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("obr", "setting"), PersistentDataType.STRING, "island_width");
        });
        this.inventory.setItem(22, stack);

        /* KEEP INVENTORY */
        stack = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.get().keepInventory()) stack = new ItemStack(Material.GREEN_DYE, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Keep Inventory"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + (GameManager.get().keepInventory() ? "Enabled" : "Disabled")));
            lore.add(Component.text("§fL-click§8: §7Toggle keep inventory"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("obr", "setting"), PersistentDataType.STRING, "keep_inventory");
        });
        this.inventory.setItem(24, stack);

        /* CAN DESTROY */
        stack = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.get().canDestroy()) stack = new ItemStack(Material.GREEN_DYE, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Can Destroy"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + (GameManager.get().canDestroy() ? "Enabled" : "Disabled")));
            lore.add(Component.text("§fL-click§8: §7Toggle can destroy"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("obr", "setting"), PersistentDataType.STRING, "can_destroy");
        });
        this.inventory.setItem(26, stack);

        this.p.openInventory(this.inventory);

    }

    public void close() {
        guis.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingGUI that)) return false;
        return p.equals(that.p);
    }

    @Override
    public int hashCode() {
        return ("obr_gui_" + p.getUniqueId().toString()).hashCode();
    }

}
