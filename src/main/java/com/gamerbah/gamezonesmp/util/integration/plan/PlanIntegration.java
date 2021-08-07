package com.gamerbah.gamezonesmp.util.integration.plan;
/* Created by GamerBah on 6/8/21 */

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ExtensionService;
import com.gamerbah.gamezonesmp.GameZoneSMP;

public class PlanIntegration {

	private final GameZoneSMP plugin;

	public PlanIntegration(GameZoneSMP plugin) {
		this.plugin = plugin;

		try {
			DataExtension extension = new HybridExtension(plugin);
			ExtensionService.getInstance().register(extension);
		} catch (NoClassDefFoundError e) {
			// Plan is not installed, handle exception
			System.out.println("Plan is not installed!");
		} catch (IllegalStateException e) {
			// Plan is not enabled, handle exception
			System.out.println("Plan is not enabled!");
		} catch (IllegalArgumentException e) {
			// The DataExtension implementation has an implementation error, handle exception
			System.out.println("Implementation error in the extension.");
		}
	}

}
