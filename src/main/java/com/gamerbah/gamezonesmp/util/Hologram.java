package com.gamerbah.gamezonesmp.util;
/* Created by GamerBah on 11/1/2017 */

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class Hologram {

	private final Type              type;
	private final Location          location;
	private final ArrayList<String> lines    = new ArrayList<>();
	private final ArrayList<Entity> entities = new ArrayList<>();

	public Hologram(Type type, Location location, boolean tagVisible, String... lines) {
		this.type     = type;
		this.location = location;
		List<String> list = Arrays.asList(lines);
		Collections.reverse(list);
		this.lines.addAll(list);
		this.lines.forEach(line -> {
			assert this.location.getWorld() != null;
			if (type == Type.ARMOR_STAND) {
				Location loc = this.location.clone().add(0, 0.5, 0);
				loc.add(0.5, 0.3 * this.lines.indexOf(line), 0.5);
				ArmorStand stand = (ArmorStand) this.location.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
				stand.setVisible(false);
				stand.setBasePlate(false);
				stand.setSmall(true);
				stand.setCustomName(line);
				stand.setCustomNameVisible(tagVisible && !line.isEmpty());
				stand.setCollidable(false);
				stand.setGravity(false);
				stand.setAI(false);
				stand.setSilent(true);
				stand.setInvulnerable(true);
				stand.setCanPickupItems(false);
				entities.add(stand);
			} else if (type == Type.ENDERMITE) {
				Location loc = this.location.clone().add(0, 0.9, 0);
				loc.add(0.5, 0.3 * this.lines.indexOf(line), 0.5);
				Endermite entity = (Endermite) this.location.getWorld().spawnEntity(loc, EntityType.ENDERMITE);
				entity.setAware(false);
				entity.setInvisible(true);
				entity.setCustomName(line);
				entity.setCustomNameVisible(tagVisible && !line.isEmpty());
				entity.setCollidable(false);
				entity.setGravity(false);
				entity.setAI(false);
				entity.setSilent(true);
				entity.setInvulnerable(true);
				entity.setCanPickupItems(false);
				entities.add(entity);
			}
		});
	}

	public void remove() {
		this.entities.forEach(Entity::remove);
	}

	public void setVisible(boolean visible) {
		entities.forEach(armorStand -> armorStand.setCustomNameVisible(visible));
	}

	public enum Type {

		ARMOR_STAND,
		ENDERMITE,
		SHULKER

	}

}
