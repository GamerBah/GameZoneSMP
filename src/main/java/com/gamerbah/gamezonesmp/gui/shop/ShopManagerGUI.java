package com.gamerbah.gamezonesmp.gui.shop;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import com.gamerbah.gamezonesmp.util.shop.ItemShop;
import com.gamerbah.inventorytoolkit.ClickEvent;
import com.gamerbah.inventorytoolkit.GameInventory;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopManagerGUI extends GameInventory {

	private final GameZoneSMP plugin;
	private final Player      player;
	private final ItemShop    shop;

	public ShopManagerGUI(GameZoneSMP plugin, Player player, ItemShop shop) {
		super("Shop Manager", 27);
		setAllowCreative(true);

		this.plugin = plugin;
		this.player = player;
		this.shop = shop;

		plugin.getShopManager().getShopEditors().put(player.getUniqueId(), shop);

		var items = new ItemBuilder(Material.CRAFTING_TABLE);
		items.name(Hex.ORANGE + C.BOLD + "Manage Items")
		     .lore(Hex.SLATE + "Edit what items you're selling")
		     .lore(Hex.SLATE + "and what slot they are in")
		     .onClick(new ClickEvent(p -> new ShopItemGUI(plugin, p, shop).build(p).open()));

		var prices = new ItemBuilder(Material.EMERALD);
		prices.name(Hex.GREEN + C.BOLD + "Manage Prices")
		      .lore(Hex.SLATE + "Change the prices of")
		      .lore(Hex.SLATE + "the items you're selling");

		var stock = new ItemBuilder(Material.CHEST);
		stock.name(Hex.GREEN + C.BOLD + "Manage Stock")
		     .lore(Hex.SLATE + "Keep track of your items' stock")
		     .lore(Hex.SLATE + "and refill items when needed");

		var settings = new ItemBuilder(Material.COMPARATOR);
		settings.name(Hex.GREEN + C.BOLD + "Shop Settings")
		        .lore(Hex.SLATE + "Change how your shop behaves")
		        .lore(Hex.SLATE + "and how things are sold");

		addButton(10, items);
		addButton(12, prices);
		addButton(14, stock);
		addButton(16, settings);
	}

}
