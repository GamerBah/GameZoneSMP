package com.gamerbah.gamezonesmp.util.shop;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.M;
import com.gamerbah.inventorytoolkit.ClickEvent;
import com.gamerbah.inventorytoolkit.GameInventory;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.UUID;

public class ItemShop extends GameInventory {

	private final GameZoneSMP plugin;

	private final UUID   owner;
	private final String name;

	@Getter
	private final ArrayList<ShopItem> shopItems;

	public ItemShop(GameZoneSMP plugin, String name, UUID owner, ArrayList<ShopItem> shopItems) {
		super(name, 54);
		setAllowCreative(true);
		setAllowUserPlacement(false);

		this.plugin = plugin;
		this.owner = owner;
		this.name = name;
		this.shopItems = shopItems;
	}

	public void create() {
		shopItems.forEach(shopItem -> {
			if (shopItem.getItem() != null) {
				var item = new ItemBuilder(shopItem.getItem());
				item.clone().lore(shopItem.getBuyText()).onClick(new ClickEvent((p) -> {
					var profile = plugin.getProfileManager().getProfile(p.getUniqueId());
					assert profile != null;
					if (profile.getDollars() >= shopItem.getBuyPrice()) {
						if (p.getInventory().firstEmpty() == -1) {
							p.closeInventory();
							M.error(p, "You don't have enough inventory space");
							M.info(p, "Clear some space in your inventory and try again");
						} else {
							EventSound.playSound(p, EventSound.ACTION_SUCCESS);
							profile.removeDollars(shopItem.getBuyPrice());
							p.getInventory().addItem(shopItem.getItem());

							if (owner != null) {
								var ownerProfile = plugin.getProfileManager().getProfile(owner);
								assert ownerProfile != null;
								ownerProfile.addDollars(shopItem.getBuyPrice());
								shopItem.setStock(shopItem.getStock() - 1);
							}
						}
					} else {
						EventSound.playSound(p, EventSound.ACTION_FAIL);
					}
				}));

				addButton(shopItem.getSlot(), item);
			}
		});
	}

	public void serialize(int id) {
		final String path = "shops." + id + ".";
		plugin.getServerShops().set(path + "name", this.name);
		plugin.getServerShops().set(path + "owner", owner != null ? this.owner.toString() : "SERVER");
		plugin.getServerShops().set(path + "items.size", shopItems.size());
		for (int i = 0; i < shopItems.size(); i++) {
			shopItems.get(i).serialize(plugin, path + "items." + i + ".");
		}
		shopItems.clear();
	}

}
