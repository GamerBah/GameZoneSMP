package com.gamerbah.gamezonesmp.util.integration.plan;
/* Created by GamerBah on 6/8/21 */

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.gamerbah.gamezonesmp.GameZoneSMP;

@PluginInfo(name = "HybridSurvival", iconName = "feather-alt", iconFamily = Family.SOLID, color = Color.PURPLE)
public class HybridExtension implements DataExtension {

	private final GameZoneSMP plugin;

	public HybridExtension(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

}
