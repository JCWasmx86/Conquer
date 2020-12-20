package org.jel.game.data;

import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.util.function.Consumer;

import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;

/**
 * Another interface providing information.
 */
public interface ConquerInfo extends StrategyObject, PluginInterface {

	void addContext(GlobalContext context);

	void executeActions();

	Clan getClan(int index);

	List<String> getClanNames();

	List<Double> getCoins();

	List<Color> getColors();

	void init();

	default boolean isDead(final Clan clan) {
		if (clan == null) {
			throw new IllegalArgumentException("clan == null");
		}
		return this.isDead(clan.getId());
	}

	boolean isDead(int clan);

	default long maximumNumberOfSoldiersToRecruit(final Clan clan, final long limit) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		return this.maximumNumberOfSoldiersToRecruit(clan.getId(), limit);
	}

	long maximumNumberOfSoldiersToRecruit(final int clan, final long limit);

	default void upgradeDefenseFully(final Clan clan, final City city) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		if (city == null) {
			throw new IllegalArgumentException("city==null");
		}
		this.upgradeDefenseFully(clan.getId(), city);
	}

	void upgradeDefenseFully(final int clan, final City city);

	default void upgradeResourceFully(final Clan clan, final Resource resources, final City city) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		if (city == null) {
			throw new IllegalArgumentException("city==null");
		}
		this.upgradeResourceFully(clan.getId(), resources, city);
	}

	void upgradeResourceFully(final int clan, final Resource resources, final City city);

	default void setErrorHandler(Consumer<Throwable> onError) {

	}

	default boolean onlyOneClanAlive() {
		return StreamUtils.getCitiesAsStream(this.getCities()).map(City::getClanId).distinct().count() == 1;
	}

	int currentRound();

	List<Clan> getClans();

	Image getBackground();

	boolean isPlayersTurn();

	void setPlayersTurn(boolean b);

	List<Plugin> getPlugins();

	Result calculateResult();

	void setPlayerGiftCallback(PlayerGiftCallback giftCallback);

	void exit(Result calculateResult);

	List<String> getExtraMusic();

	default void upgradeSoldiersDefenseFully(int id) {
		var b = true;
		while (b) {
			b = this.upgradeDefense(id);
		}
	}

	default void upgradeSoldiersOffenseFully(int id) {
		var b = true;
		while (b) {
			b = this.upgradeOffense(id);
		}
	}

	default void upgradeSoldiersFully(int id) {
		var b = true;
		while (b) {
			b = this.upgradeSoldiers(id);
		}
	}

	int getNumPlayers();
	
	ConquerSaver getSaver(String name);
}
