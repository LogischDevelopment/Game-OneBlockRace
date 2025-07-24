package tv.logisch.oneBlockRace.gui.setting;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.manager.GameManager;

import java.util.ArrayList;
import java.util.List;

public class UtilityGUI {

    public static List<UtilityGUI> guis = new ArrayList<>();
    public static UtilityGUI get(Player player) {
        for (UtilityGUI gui : guis) {
            if (gui.p.equals(player)) {
                return gui;
            }
        }
        UtilityGUI newGui = new UtilityGUI(player);
        guis.add(newGui);
        return newGui;
    }
    public static boolean has(Player player) {
        for(UtilityGUI gui : guis) {
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

    private UtilityGUI(Player player) {
        this.p = player;
        this.page = 1;
        guis.add(this);
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this.p, 45, Component.text(OneBlockRace.instance().prefix() + "Settings"));
        update();
    }

    public void update() {
        ItemStack placeholderStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        placeholderStack.editMeta(m -> m.setHideTooltip(true));
        for(int i = 0; i < 9; i++) {
            this.inventory.setItem(i, placeholderStack);
        }
        for(int i = 36; i < 45; i++) {
            this.inventory.setItem(i, placeholderStack);
        }

        /* CATEGORIES */
        ItemStack itemStack = new ItemStack(Material.CLOCK, 1);
        itemStack.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lDuration Settings"));
            m.lore(List.of(
                Component.text("§7Click to setup the duration settings")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "open_duration");
        });
        this.inventory.setItem(9, itemStack);
        this.inventory.setItem(10, placeholderStack);

        itemStack = new ItemStack(Material.BEDROCK, 1);
        itemStack.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lIsland Settings"));
            m.lore(List.of(
                Component.text("§7Click to setup the island settings")
            ));
            m.getPersistentDataContainer().set(GameManager.get().shopKey(), PersistentDataType.STRING, "open_island");
        });
        this.inventory.setItem(18, itemStack);
        this.inventory.setItem(19, placeholderStack);

        itemStack = new ItemStack(Material.LEVER, 1);
        itemStack.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lUtilities"));
            m.lore(List.of(
                Component.text("§7Click to setup the utilities settings")
            ));
            m.getPersistentDataContainer().set(GameManager.get().shopKey(), PersistentDataType.STRING, "open_utilities");
        });
        this.inventory.setItem(27, itemStack);
        ItemStack activeCategory = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        activeCategory.editMeta(m -> m.setHideTooltip(true));
        this.inventory.setItem(28, activeCategory);


        /* ITEMS */
        ItemStack item = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.get().pvpPhase()) item = item.withType(Material.GREEN_DYE);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lPvP-Phase"));
            m.lore(List.of(
                    Component.text("§7Current: " + (GameManager.get().pvpPhase() ? "§aEnabled" : "§cDisabled")),
                    Component.empty(),
                    Component.text("§fR-Click: §7Toggle PvP-Phase")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "toggle_pvp_phase");
        });
        this.inventory.setItem(20, item);

        item = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.get().canDestroy()) item = item.withType(Material.GREEN_DYE);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lDestroying"));
            m.lore(List.of(
                    Component.text("§7Current: " + (GameManager.get().canDestroy() ? "§aEnabled" : "§cDisabled")),
                    Component.empty(),
                    Component.text("§fR-Click: §7Toggle destroying")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "toggle_destroying");
        });
        this.inventory.setItem(22, item);

        item = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.get().keepInventory()) item = item.withType(Material.GREEN_DYE);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lKeep Inventory"));
            m.lore(List.of(
                    Component.text("§7Current: " + (GameManager.get().keepInventory() ? "§aEnabled" : "§cDisabled")),
                    Component.empty(),
                    Component.text("§fR-Click: §7Toggle keep inventory")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "toggle_keep_inventory");
        });
        this.inventory.setItem(24, item);

        item = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.get().gravity()) item = item.withType(Material.GREEN_DYE);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lGravity"));
            m.lore(List.of(
                    Component.text("§7Current: " + (GameManager.get().gravity() ? "§aEnabled" : "§cDisabled")),
                    Component.empty(),
                    Component.text("§fR-Click: §7Toggle gravity")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "toggle_gravity");
        });
        this.inventory.setItem(26, item);

        this.p.openInventory(this.inventory);
    }

    public void close() {
        guis.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UtilityGUI shopGUI)) return false;
        return p.equals(shopGUI.p);
    }

    @Override
    public int hashCode() {
        return ("getdown_shop_gui_" + p.getUniqueId().toString()).hashCode();
    }

}
