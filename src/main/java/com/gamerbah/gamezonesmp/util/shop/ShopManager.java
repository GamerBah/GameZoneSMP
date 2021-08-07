package com.gamerbah.gamezonesmp.util.shop;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.M;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ShopManager {

	private final GameZoneSMP plugin;

	@Getter
	private final ArrayList<ItemShop> playerShops;

	@Getter
	private final HashMap<String, ItemShop> serverShops;
	@Getter
	private final HashMap<UUID, ItemShop>   shopEditors;

	public ShopManager(GameZoneSMP plugin) {
		this.plugin = plugin;
		this.serverShops = new HashMap<>();
		this.playerShops = new ArrayList<>();
		this.shopEditors = new HashMap<>();
	}

	public void setupServerShops() {
		for (int x = 0; x < plugin.getServerShops().getInt("shops.size"); x++) {
			final String path = "shops." + x + ".";
			String       name = plugin.getServerShops().getString(path + "name");
			String       uuid = plugin.getServerShops().getString(path + "owner");
			if (uuid != null) {
				int itemAmount = plugin.getServerShops().getInt(path + "items.size");

				var shopItems = new ArrayList<ShopItem>(itemAmount);
				for (int i = 0; i < itemAmount; i++) {
					String    itemPath  = path + "items." + i + ".";
					ItemStack itemStack = plugin.getServerShops().getItemStack(itemPath + "itemStack");
					int       slot      = plugin.getServerShops().getInt(itemPath + "slot");
					int       buyPrice  = plugin.getServerShops().getInt(itemPath + "buy");
					int       sellPrice = plugin.getServerShops().getInt(itemPath + "sell");
					int       stock     = plugin.getServerShops().getInt(itemPath + "stock");

					var item = new ShopItem(itemStack, slot, buyPrice, sellPrice, stock);
					shopItems.add(item);
				}
				var owner = uuid.equals("SERVER") ? null : UUID.fromString(uuid);
				var shop  = new ItemShop(plugin, name, owner, shopItems);
				shop.create();
				serverShops.put(name, shop);
			} else {
				M.log(Level.SEVERE, "Error getting owner for Shop with ID " + x);
			}
		}
	}

}
