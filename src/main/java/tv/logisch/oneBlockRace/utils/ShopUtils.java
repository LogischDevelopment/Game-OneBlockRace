package tv.logisch.oneBlockRace.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tv.logisch.oneBlockRace.manager.GameManager;

import java.util.List;

public class ShopUtils {

    public static ItemStack createShopItem(Material type, String name, String description, int price) {
        double newPrice = price * 0.2; // Convert price to coins
        price = (int) newPrice;
        ItemStack item = new ItemStack(type);
        int finalPrice = price;
        item.editMeta(meta -> {
            meta.displayName(Component.text(name));
            meta.lore(List.of(Component.text(description), Component.text("Price: " + finalPrice + " coins")));
            meta.getPersistentDataContainer().set(GameManager.get().shopKey(), PersistentDataType.STRING, "item_buy_"+ finalPrice);
        });
        return item;
    }

}
