package org.jel.game.testsuite;

import org.jel.game.data.ICity;
import org.jel.game.data.Result;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.utils.Graph;

public class MaliciousPlugin implements Plugin {
	private int numErrors;

	private void checkThrown(final boolean hasThrown, final String message) {
		if (!hasThrown) {
			System.err.println("[ERROR] " + message);
			this.numErrors++;
		}
	}

	@Override
	public void exit(final Result result) {
		Plugin.super.exit(result);
	}

	@Override
	public String getName() {
		return "Malicious";
	}

	int getNumberOfErrors() {
		return this.numErrors;
	}

	@Override
	public void handle(final Graph<ICity> cities, final Context ctx) {
		var hasThrown = true;
		try {
			ctx.appendToEventList(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-Event!");
		hasThrown = true;
		try {
			ctx.getClan(ctx.getClanNames().size());
			hasThrown = false;
		} catch (final IndexOutOfBoundsException ioobe) {
		}
		this.checkThrown(hasThrown, "Was able to access out of bounds!");
		hasThrown = true;
		try {
			ctx.getClanNames().add("");
			hasThrown = false;
		} catch (final UnsupportedOperationException iae) {
		}
		this.checkThrown(hasThrown, "Was able to add element to list");
	}

	@Override
	public void init(final PluginInterface pluginInterface) {
		Plugin.super.init(pluginInterface);
		this.numErrors = 0;
		var hasThrown = true;
		try {
			pluginInterface.addAttackHook(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-Attackhook");
		hasThrown = true;
		try {
			pluginInterface.addCityKeyHandler(null, (a, b) -> {
			});
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add CityKeyHandler for null!");
		hasThrown = true;
		try {
			pluginInterface.addCityKeyHandler("a", null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		hasThrown = true;
		try {
			pluginInterface.addKeyHandler(null, a -> {
			});
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add KeyHandler for null!");
		hasThrown = true;
		try {
			pluginInterface.addKeyHandler("a", null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-KeyHandler!");
		hasThrown = true;
		try {
			pluginInterface.addMessageListener(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-MessageListener!");
		hasThrown = true;
		try {
			pluginInterface.addMoneyHook(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-MoneyHook!");
		hasThrown = true;
		try {
			pluginInterface.addMoveHook(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-MoveHook!");
		hasThrown = true;
		try {
			pluginInterface.addMusic(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-Music!");
		hasThrown = true;
		try {
			pluginInterface.addRecruitHook(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-RecruitHook!");
		hasThrown = true;
		try {
			pluginInterface.addResourceHook(null);
			hasThrown = false;
		} catch (final IllegalArgumentException iae) {

		}
		this.checkThrown(hasThrown, "Was able to add null-ResourceHook!");
	}

}
