package com.gamerbah.gamezonesmp.command;

public class ReportCommand {} /*implements CommandExecutor {

	private final SMP plugin;

	public ReportCommand(SMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player      player  = (Player) sender;
		GameProfile profile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());

		if (profile != null) {
			if (profile.isMuted()) {
				player.sendMessage(C.RED + C.BOLD + "Sorry! " + C.GRAY + "You cannot report players while muted!");
				return true;
			}

			if (args.length != 1) {
				plugin.sendIncorrectUsage(player, "/report <player>");
				return true;
			}

			GameProfile target = plugin.getProfileManager().getProfile(args[0]);
			if (target == null) {
				player.sendMessage(C.RED + "That player isn't online!");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}

			if (target == profile) {
				player.sendMessage(C.RED + "You can't report yourself! Unless you have something to tell us.... *gives suspicious look*");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}

			PunishmentManager punishmentManager = plugin.getPunishmentManager();
			if (!punishmentManager.getReportCooldown().containsKey(player.getUniqueId())) {
				ReportMenu reportMenu = new ReportMenu(plugin, player, target);
				punishmentManager.getReportBuilders().put(player.getUniqueId(), null);
				punishmentManager.getReportArray().put(player.getUniqueId(), new ArrayList<>());
				reportMenu.build(player).open();
				EventSound.playSound(player, EventSound.INVENTORY_OPEN);
			} else {
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				int time = punishmentManager.getReportCooldown().get(player.getUniqueId());
				player.sendMessage(C.RED + "You must wait " + C.YELLOW + time + " seconds " + C.RED + "before you report another player!");
				return false;
			}
			return true;
		}
		return false;
	}

}*/
