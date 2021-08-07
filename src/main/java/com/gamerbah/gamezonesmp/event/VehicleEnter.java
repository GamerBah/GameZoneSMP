package com.gamerbah.gamezonesmp.event;
/* Created by GamerBah on 7/24/2018 */

import com.gamerbah.gamezonesmp.util.message.C;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.text.DecimalFormat;
import java.util.Objects;

public class VehicleEnter implements Listener {

	@EventHandler
	public void onMount(VehicleEnterEvent event) {
		if (event.getVehicle() instanceof Horse horse && event.getEntered() instanceof Player player) {
			double jump = -0.1817584952 * Math.pow(horse.getJumpStrength(), 3) +
			              3.689713992 * Math.pow(horse.getJumpStrength(), 2) + 2.128599134 * horse.getJumpStrength() -
			              0.343930367;
			double speed = Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue() * 43;

			String speedColor = C.RED;
			if (speed >= 5 && speed < 7) {
				speedColor = C.GOLD;
			}
			if (speed >= 7 && speed < 9) {
				speedColor = C.YELLOW;
			}
			if (speed >= 9 && speed < 11) {
				speedColor = C.GREEN;
			}
			if (speed >= 11 && speed < 13) {
				speedColor = C.AQUA;
			}
			if (speed >= 13) {
				speedColor = C.PINK;
			}

			String jumpColor = C.PINK;
			if (jump < 2) {
				jumpColor = C.RED;
			}
			if (jump >= 2 && jump < 3) {
				jumpColor = C.GOLD;
			}
			if (jump >= 3 && jump < 4) {
				jumpColor = C.YELLOW;
			}
			if (jump >= 4 && jump < 5) {
				jumpColor = C.GREEN;
			}

			var speedString = C.GRAY + "Speed: " + speedColor + new DecimalFormat("##.##").format(speed) + " m/s";
			var jumpString  = C.GRAY + "    Jump: " + jumpColor + new DecimalFormat("##.##").format(jump) + " blocks";

			player.sendTitle(" ", speedString + jumpString, 5, 50, 20);
		}
	}

}
