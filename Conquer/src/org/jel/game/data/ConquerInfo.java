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

	void addContext(final GlobalContext context);

	Result calculateResult();

	int currentRound();

	void executeActions();

	void exit(final Result calculateResult);

	Image getBackground();

	IClan getClan(final int index);

	List<String> getClanNames();

	List<IClan> getClans();

	List<Double> getCoins();

	List<Color> getColors();

	List<String> getExtraMusic();

	int getNumPlayers();

	List<Plugin> getPlugins();

	ConquerSaver getSaver(final String name);

	Version getVersion();

	void init();

	boolean isDead(final IClan clan);

	boolean isPlayersTurn();

	long maximumNumberOfSoldiersToRecruit(final IClan clan, final long limit);

	default boolean onlyOneClanAlive() {
		return StreamUtils.getCitiesAsStream(this.getCities()).map(ICity::getClan).distinct().count() == 1;
	}

	default void setErrorHandler(final Consumer<Throwable> onError) {

	}

	void setPlayerGiftCallback(PlayerGiftCallback giftCallback);

	void setPlayersTurn(boolean b);

	void upgradeDefenseFully(final ICity city);

	void upgradeResourceFully(final Resource resources, final ICity city);

	IClan getPlayerClan();
	
	@SuppressWarnings("deprecation")
	default ResourceUsage getResourceUsage() {
		return new ResourceUsage(Shared.getDataValues());
	}
}
