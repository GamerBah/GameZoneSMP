package com.gamerbah.gamezonesmp.util;
/* Created by GamerBah on 6/5/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.message.Hex;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DeathChest {

	private final GameZoneSMP plugin;

	@Getter
	private final UUID            uuid;
	@Getter
	private final Location        location;
	@Getter
	private final List<ItemStack> drops;
	@Getter
	private final int             experience;

	@Getter
	private ArmorStand stand;

	public DeathChest(GameZoneSMP plugin, UUID uuid, Location location, List<ItemStack> drops, int experience) {
		this.plugin     = plugin;
		this.uuid       = uuid;
		this.location   = location;
		this.drops      = drops;
		this.experience = experience;
	}

	public void spawn() {
		Objects.requireNonNull(this.location.getWorld()).getBlockAt(this.location).setType(Material.ENDER_CHEST);

		GameProfile profile = plugin.getProfileManager().getProfile(this.uuid);
		assert profile != null;

		ArmorStand stand = (ArmorStand) this.location.getWorld()
		                                             .spawnEntity(this.location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.setBasePlate(false);
		stand.setSmall(true);
		stand.setCustomName(profile.displayName() + Hex.SLATE + "'s Death Chest");
		stand.setCustomNameVisible(true);
		stand.setCollidable(false);
		stand.setGravity(false);
		stand.setAI(false);
		stand.setSilent(true);
		stand.setInvulnerable(true);
		stand.setCanPickupItems(false);
		this.stand = stand;
	}

	public void collect(Player player) {
		Objects.requireNonNull(this.location.getWorld()).getBlockAt(this.location).setType(Material.AIR);
		stand.remove();

		if (drops.size() > 0) {
			ItemStack[] items = new ItemStack[drops.size()];
			player.getInventory()
			      .addItem(drops.toArray(items))
			      .forEach((slot, item) -> Objects.requireNonNull(this.location.getWorld()).dropItemNaturally(this.location, item));
		}
		player.giveExp(experience);
	}

	public void remove() {
		stand.remove();
		Objects.requireNonNull(this.location.getWorld()).getBlockAt(this.location).setType(Material.AIR);
	}

	public void serialize(int id) {
		final String path = "deathChests." + id + ".";
		plugin.getConfig().set(path + "uuid", this.uuid.toString());
		plugin.getConfig().set(path + "location.world", Objects.requireNonNull(this.location.getWorld()).getName());
		plugin.getConfig().set(path + "location.x", this.location.getBlockX());
		plugin.getConfig().set(path + "location.y", this.location.getBlockY());
		plugin.getConfig().set(path + "location.z", this.location.getBlockZ());
		plugin.getConfig().set(path + "experience", this.experience);
		plugin.getConfig().set(path + "drops.size", this.drops.size());
		for (int x = 0; x < this.drops.size(); x++) {
			plugin.getConfig().set(path + "drops." + x, this.drops.get(x));
		}
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
