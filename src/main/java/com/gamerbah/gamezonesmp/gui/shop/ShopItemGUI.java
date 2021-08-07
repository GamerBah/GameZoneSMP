package com.gamerbah.gamezonesmp.gui.shop;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.shop.ItemShop;
import com.gamerbah.gamezonesmp.util.shop.ShopItem;
import com.gamerbah.inventorytoolkit.GameInventory;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopItemGUI extends GameInventory {

	private final GameZoneSMP plugin;
	private final Player      player;
	private final ItemShop    shop;

	public ShopItemGUI(GameZoneSMP plugin, Player player, ItemShop shop) {
		super("Shop Items", 54, new ShopManagerGUI(plugin, player, shop));
		setAllowCreative(true);
		setAllowUserPlacement(true);

		this.plugin = plugin;
		this.player = player;
		this.shop = shop;

		shop.getShopItems()
		    .forEach(shopItem -> addButton(shopItem.getSlot(),
		                                   new ItemBuilder(shopItem.getItem()).setMovingAllowed(true)));
	}

	@Override
	public void onClose(Player player) {
		var items = getInventory().getStorageContents();
		shop.getShopItems().clear();
		for (int i = 0; i < items.length; i++) {
			var itemStack = items[i];
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				var shopItem = new ShopItem(itemStack, i, 0, 0, itemStack.getAmount());
				shop.getShopItems().add(shopItem);
			}
		}
		new ShopManagerGUI(plugin, player, shop).build(player).open();
	}

}
