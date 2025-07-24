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
import tv.logisch.oneBlockRace.utils.Format;

import java.util.ArrayList;
import java.util.List;

public class IslandGUI {

    public static List<IslandGUI> guis = new ArrayList<>();
    public static IslandGUI get(Player player) {
        for (IslandGUI gui : guis) {
            if (gui.p.equals(player)) {
                return gui;
            }
        }
        IslandGUI newGui = new IslandGUI(player);
        guis.add(newGui);
        return newGui;
    }
    public static boolean has(Player player) {
        for(IslandGUI gui : guis) {
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

    private IslandGUI(Player player) {
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
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "open_island");
        });
        this.inventory.setItem(18, itemStack);
        ItemStack activeCategory = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        activeCategory.editMeta(m -> m.setHideTooltip(true));
        this.inventory.setItem(19, activeCategory);

        itemStack = new ItemStack(Material.LEVER, 1);
        itemStack.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lUtilities"));
            m.lore(List.of(
                Component.text("§7Click to setup the utilities settings")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "open_utilities");
        });
        this.inventory.setItem(27, itemStack);
        this.inventory.setItem(28, placeholderStack);


        /* ITEMS */
        ItemStack item = new ItemStack(Material.RAIL, 1);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lIsland Width"));
            m.lore(List.of(
                    Component.text("§7Current: §f" + GameManager.get().islandWidth() + " blocks/side"),
                    Component.empty(),
                    Component.text("§fL-click§8: §7Decrease by 1 block/side"),
                    Component.empty(),
                    Component.text("§fR-click§8: §7Increase by 1 block/side")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "island_width");
        });
        this.inventory.setItem(23, item);

        this.p.openInventory(this.inventory);
    }

    public void close() {
        guis.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IslandGUI shopGUI)) return false;
        return p.equals(shopGUI.p);
    }

    @Override
    public int hashCode() {
        return ("getdown_shop_gui_" + p.getUniqueId().toString()).hashCode();
    }

}
