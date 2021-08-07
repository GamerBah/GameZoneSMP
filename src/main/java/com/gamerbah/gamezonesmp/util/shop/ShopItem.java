package com.gamerbah.gamezonesmp.util.shop;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class ShopItem {

	@Getter
	@Setter
	private ItemStack item;

	@Getter
	@Setter
	private int slot, buyPrice, sellPrice, stock;

	public ShopItem(ItemStack item, int slot, int buyPrice, int sellPrice, int stock) {
		this.item = item;
		this.slot = slot;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.stock = stock;
	}

	public ShopItem(ItemStack item, int slot, int buyPrice, int stock) {
		this(item, slot, buyPrice, 0, stock);
	}

	public ShopItem(ItemStack item, int slot, int buyPrice) {
		this(item, slot, buyPrice, 0, 0);
	}

	public boolean isInStock() {
		return stock == -1 || stock > 0;
	}

	public String getBuyText() {
		var    each  = new DecimalFormat("##.##").format(buyPrice / item.getAmount());
		String line1 = stock == 0 ? Hex.RED : Hex.GREEN + "BUY " + Hex.DARK_GRAY + "(RIGHT-CLICK)\n";
		String line2 = Hex.SLATE + "Quantity: " + C.YELLOW + item.getAmount() + "\n";
		String line3 = Hex.SLATE + "Price: " + Hex.GREEN + "$" + buyPrice + "\n";
		String line4 = Hex.DARK_GRAY + "(" + each + " each)\n";
		String oos   = Hex.SLATE + C.STRIKETHROUGH + "Price: $" + buyPrice + Hex.DARK_RED + "OUT OF STOCK\n";

		return line1 + line2 + (stock == 0 ? oos : line3 + line4);
	}

	public void serialize(final GameZoneSMP plugin, final String path) {
		plugin.getServerShops().set(path + "itemStack", this.item.serialize());
		plugin.getServerShops().set(path + "slot", this.slot);
		plugin.getServerShops().set(path + "buy", this.buyPrice);
		plugin.getServerShops().set(path + "sell", this.sellPrice);
		plugin.getServerShops().set(path + "stock", this.stock);
		item = null;
	}

}
