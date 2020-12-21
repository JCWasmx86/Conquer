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

	Result calculateResult();

	int currentRound();

	void executeActions();

	void exit(Result calculateResult);

	Image getBackground();

	Clan getClan(int index);

	List<String> getClanNames();

	List<Clan> getClans();

	List<Double> getCoins();

	List<Color> getColors();

	List<String> getExtraMusic();

	int getNumPlayers();

	List<Plugin> getPlugins();

	ConquerSaver getSaver(String name);

	void init();

	default boolean isDead(final Clan clan) {
		if (clan == null) {
			throw new IllegalArgumentException("clan == null");
		}
		return this.isDead(clan.getId());
	}

	boolean isDead(int clan);

	boolean isPlayersTurn();

	default long maximumNumberOfSoldiersToRecruit(final Clan clan, final long limit) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		return this.maximumNumberOfSoldiersToRecruit(clan.getId(), limit);
	}

	long maximumNumberOfSoldiersToRecruit(final int clan, final long limit);

	default boolean onlyOneClanAlive() {
		return StreamUtils.getCitiesAsStream(this.getCities()).map(City::getClanId).distinct().count() == 1;
	}

	default void setErrorHandler(final Consumer<Throwable> onError) {

	}

	void setPlayerGiftCallback(PlayerGiftCallback giftCallback);

	void setPlayersTurn(boolean b);

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

	default void upgradeSoldiersDefenseFully(final int id) {
		var b = true;
		while (b) {
			b = this.upgradeDefense(id);
		}
	}

	default void upgradeSoldiersFully(final int id) {
		var b = true;
		while (b) {
			b = this.upgradeSoldiers(id);
		}
	}

	default void upgradeSoldiersOffenseFully(final int id) {
		var b = true;
		while (b) {
			b = this.upgradeOffense(id);
		}
	}
	
	Version getVersion();
}
