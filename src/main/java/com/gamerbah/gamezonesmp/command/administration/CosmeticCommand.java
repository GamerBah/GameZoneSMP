package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 2/3/21 */

public class CosmeticCommand {} /*implements CommandExecutor, TabCompleter {

	private final SMP plugin;

	public CosmeticCommand(SMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player      player  = (Player) sender;
		GameProfile profile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());
		assert profile != null;
		if (!profile.hasRank(Rank.ADMIN)) {
			player.sendMessage(C.RED + "Sorry! " + C.GRAY + "You don't have permission to use this command!");
			EventSound.playSound(player, EventSound.ACTION_FAIL);
			return true;
		}

		if (args.length != 1) {
			plugin.sendIncorrectUsage(player, "/" + label + " <player>");
			return true;
		}

		int result = 0;
		switch (args[0].toLowerCase()) {
			case "flame":
				result = 420;
				break;
			case "none":
				result = 0;
				break;
		}

		int r = result;
		ParticlePack p = (ParticlePack) plugin.getCosmeticManager().getCosmetics().stream().filter(cosmetic -> cosmetic.getId() == r).findFirst().get();

		plugin.getCosmeticManager().getEquipped().put(player.getUniqueId(), p);

		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("cosmetic")) {
			if (args.length == 1) {
				ArrayList<String> options = new ArrayList<>();
				options.add("flame");
				options.add("none");
				return options;
			}
		}
		return null;
	}

}*/
