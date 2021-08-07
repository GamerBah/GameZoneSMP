package com.gamerbah.gamezonesmp.util;
/* Created by GamerBah on 6/5/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Data
public class ChunkLoader {

	private final GameZoneSMP plugin;

	private final UUID     owner;
	private final Location location;
	private final Chunk    chunk;

	private boolean  tagVisible = true;
	private Hologram hologram;

	public ChunkLoader(GameZoneSMP plugin, UUID owner, Location location, Chunk chunk) {
		this.plugin   = plugin;
		this.owner    = owner;
		this.location = location;
		this.chunk    = chunk;
	}

	public void create() {
		GameProfile profile = plugin.getProfileManager().getProfile(this.owner);
		assert profile != null;

		String[] lines = {
				profile.displayName() + Hex.SLATE + "'s", Hex.ORANGE + C.BOLD + "Chunk Loader"
		};

		this.hologram = new Hologram(Hologram.Type.ARMOR_STAND, location, tagVisible, lines);
		chunk.setForceLoaded(true);
	}

	public void remove() {
		hologram.remove();
		chunk.setForceLoaded(false);
	}

	public static ItemStack getItemStack() {
		ItemStack stack = new ItemStack(Material.RESPAWN_ANCHOR);
		ItemMeta meta = stack.getItemMeta();
		assert meta != null;
		meta.setDisplayName(Hex.ORANGE + "Chunk Loader");
		String[] lore = {Hex.SLATE + "Force-loads the chunk it is placed in"};
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		return stack;
	}

	public void serialize(int id) {
		final String path = "chunkLoaders." + id + ".";
		plugin.getConfig().set(path + "uuid", this.owner.toString());
		plugin.getConfig().set(path + "visible", this.tagVisible);
		plugin.getConfig().set(path + "world", Objects.requireNonNull(this.location.getWorld()).getName());
		plugin.getConfig().set(path + "location.x", this.location.getBlockX());
		plugin.getConfig().set(path + "location.y", this.location.getBlockY());
		plugin.getConfig().set(path + "location.z", this.location.getBlockZ());
		plugin.getConfig().set(path + "chunk.x", this.chunk.getX());
		plugin.getConfig().set(path + "chunk.z", this.chunk.getZ());
	}

	public boolean sameLocationAs(Location location) {
		boolean world = Objects.requireNonNull(this.location.getWorld())
		                       .getName()
		                       .equals(Objects.requireNonNull(location.getWorld()).getName());
		boolean x = this.location.getBlockX() == location.getBlockX();
		boolean y = this.location.getBlockY() == location.getBlockY();
		boolean z = this.location.getBlockZ() == location.getBlockZ();

		return world && x && y && z;
	}

}
