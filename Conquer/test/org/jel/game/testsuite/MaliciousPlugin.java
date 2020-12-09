package org.jel.game.testsuite;

import org.jel.game.data.City;
import org.jel.game.data.Result;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.utils.Graph;

public class MaliciousPlugin implements Plugin {
	private int numErrors;

	@Override
	public void exit(Result result) {
		Plugin.super.exit(result);
	}

	@Override
	public void init(PluginInterface pluginInterface) {
		Plugin.super.init(pluginInterface);
		numErrors = 0;
		boolean hasThrown = true;
		try {
			pluginInterface.addAttackHook(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-Attackhook");
		hasThrown = true;
		try {
			pluginInterface.addCityKeyHandler(null, (a, b) -> {
			});
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add CityKeyHandler for null!");
		hasThrown = true;
		try {
			pluginInterface.addCityKeyHandler("a", null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		hasThrown = true;
		try {
			pluginInterface.addKeyHandler(null, a -> {
			});
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add KeyHandler for null!");
		hasThrown = true;
		try {
			pluginInterface.addKeyHandler("a", null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-KeyHandler!");
		hasThrown = true;
		try {
			pluginInterface.addMessageListener(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-MessageListener!");
		hasThrown = true;
		try {
			pluginInterface.addMoneyHook(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-MoneyHook!");
		hasThrown = true;
		try {
			pluginInterface.addMoveHook(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-MoveHook!");
		hasThrown = true;
		try {
			pluginInterface.addMusic(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-Music!");
		hasThrown = true;
		try {
			pluginInterface.addRecruitHook(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-RecruitHook!");
		hasThrown = true;
		try {
			pluginInterface.addResourceHook(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-ResourceHook!");
	}

	private void checkThrown(boolean hasThrown, String message) {
		if (!hasThrown) {
			System.err.println("[ERROR] " + message);
			numErrors++;
		}
	}

	int getNumberOfErrors() {
		return this.numErrors;
	}

	@Override
	public String getName() {
		return "Malicious";
	}

	@Override
	public void handle(Graph<City> cities, Context ctx) {
		boolean hasThrown = true;
		try {
			ctx.appendToEventList(null);
			hasThrown = false;
		} catch (IllegalArgumentException iae) {

		}
		checkThrown(hasThrown, "Was able to add null-Event!");
		hasThrown = true;
		try {
			ctx.getClan(ctx.getClanNames().size());
			hasThrown = false;
		} catch (IndexOutOfBoundsException ioobe) {
		}
		checkThrown(hasThrown, "Was able to access out of bounds!");
		hasThrown = true;
		try {
			ctx.getClanNames().add("");
			hasThrown = false;
		} catch (UnsupportedOperationException iae) {
		}
		checkThrown(hasThrown, "Was able to add element to list");
	}

}
