package tv.logisch.oneBlockRace.gui.setting;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.manager.GameManager;
import tv.logisch.oneBlockRace.utils.Format;

import java.util.ArrayList;
import java.util.List;

public class DurationGUI {

    public static List<DurationGUI> guis = new ArrayList<>();
    public static DurationGUI get(Player player) {
        for (DurationGUI gui : guis) {
            if (gui.p.equals(player)) {
                return gui;
            }
        }
        DurationGUI newGui = new DurationGUI(player);
        guis.add(newGui);
        return newGui;
    }
    public static boolean has(Player player) {
        for(DurationGUI gui : guis) {
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

    private DurationGUI(Player player) {
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
        ItemStack activeCategory = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        activeCategory.editMeta(m -> m.setHideTooltip(true));
        this.inventory.setItem(10, activeCategory);

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
        this.inventory.setItem(28, placeholderStack);


        /* ITEMS */
        ItemStack item = new ItemStack(Material.ELYTRA, 1);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lGame Duration"));
            m.lore(List.of(
                    Component.text("§7Current: §f" + Format.time(GameManager.get().time())),
                    Component.empty(),
                    Component.text("§fL-click§8: §7Decrease by 5 minutes"),
                    Component.text("§fShift + L-click§8: §7Decrease by 15 minutes"),
                    Component.empty(),
                    Component.text("§fR-click§8: §7Increase by 5 minutes"),
                    Component.text("§fShift + R-click§8: §7Increase by 15 minutes")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "game_duration");
        });
        this.inventory.setItem(21, item);

        item = new ItemStack(Material.DROPPER, 1);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lDrop Interval"));
            m.lore(List.of(
                    Component.text("§7Current: §f" + Format.time(GameManager.get().dropInterval())),
                    Component.empty(),
                    Component.text("§fL-click§8: §7Decrease by 1 second"),
                    Component.text("§fShift + L-click§8: §7Decrease by 5 seconds"),
                    Component.empty(),
                    Component.text("§fR-click§8: §7Increase by 1 second"),
                    Component.text("§fShift + R-click§8: §7Increase by 5 seconds")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "drop_interval");
        });
        this.inventory.setItem(23, item);

        item = new ItemStack(Material.LIGHT_BLUE_BUNDLE, 1);
        item.editMeta(m -> {
            m.displayName(Component.text("§8» §f§lShopping Duration"));
            m.lore(List.of(
                    Component.text("§7Current: §f" + Format.time(GameManager.get().shoppingTime())),
                    Component.empty(),
                    Component.text("§fL-click§8: §7Decrease by 5 second"),
                    Component.text("§fShift + L-click§8: §7Decrease by 10 seconds"),
                    Component.empty(),
                    Component.text("§fR-click§8: §7Increase by 5 seconds"),
                    Component.text("§fShift + R-click§8: §7Increase by 10 seconds")
            ));
            m.getPersistentDataContainer().set(GameManager.get().settingsKey(), PersistentDataType.STRING, "shopping_duration");
        });
        this.inventory.setItem(25, item);

        this.p.openInventory(this.inventory);
    }

    public void close() {
        guis.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DurationGUI shopGUI)) return false;
        return p.equals(shopGUI.p);
    }

    @Override
    public int hashCode() {
        return ("getdown_shop_gui_" + p.getUniqueId().toString()).hashCode();
    }

}
