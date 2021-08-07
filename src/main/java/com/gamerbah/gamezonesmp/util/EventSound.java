package com.gamerbah.gamezonesmp.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public enum EventSound {

	ACTION_FAIL(Sound.ITEM_FLINTANDSTEEL_USE, 1, 1, null, 0, 0),
	ACTION_SUCCESS(Sound.BLOCK_NOTE_BLOCK_HARP, 2F, 1F, null, 0, 0),

	CHAT_TAGGED(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.85F, Sound.ENTITY_ITEM_PICKUP, 1F, 1F),

	CLICK(Sound.BLOCK_COMPARATOR_CLICK, 0.4F, 1.5F, null, 0, 0),
	COMMAND_NEEDS_CONFIRMATION(Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1, null, 0, 0),

	ITEM_RECEIVE_EPIC(Sound.ENTITY_SKELETON_DEATH, 0.75F, 0.5F, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 0.9F),
	ITEM_RECEIVE_LEGENDARY(Sound.ENTITY_BLAZE_DEATH, 0.75F, 0.6F, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1F),

	INVENTORY_OPEN(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.2F, 1.2F, null, 0, 0),
	INVENTORY_ACTION(Sound.UI_BUTTON_CLICK, 1.0F, 0.5F, null, 0, 0),
	INVENTORY_BACK(Sound.UI_BUTTON_CLICK, 0.8F, 0.5F, null, 0, 0);

	private final Sound sound1;
	private final float pitch1;
	private final float vol1;
	private final Sound sound2;
	private final float pitch2;
	private final float vol2;

	public static void playSound(Player player, EventSound eventSound) {
		if (player != null && player.isOnline()) {
			player.playSound(player.getLocation(), eventSound.getSound1(), eventSound.getVol1(), eventSound.getPitch1());
			player.playSound(player.getLocation(), eventSound.getSound2(), eventSound.getVol2(), eventSound.getPitch2());
		}
	}
}
