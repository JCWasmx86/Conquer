package org.jel.game.data;

import java.awt.Color;
import java.util.List;

import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.plugins.PluginInterface;

/**
 * Another interface providing information.
 */
public interface ConquerInfo extends StrategyObject, PluginInterface {
	public boolean isDead(int clan);

	public default boolean isDead(Clan clan) {
		return this.isDead(clan.getId());
	}

	public List<String> getClanNames();

	public long maximumNumberOfSoldiersToRecruit(final int clan, final long limit);

	public default long maximumNumberOfSoldiersToRecruit(final Clan clan, final long limit) {
		return maximumNumberOfSoldiersToRecruit(clan.getId(), limit);
	}

	public Clan getClan(int index);

	public List<Color> getColors();

	public List<Double> getCoins();
	
	public void upgradeResourceFully(final int clan, final Resource resources, final City city);
	
	public default void upgradeResourceFully(final Clan clan, final Resource resources, final City city) {
		this.upgradeResourceFully(clan.getId(), resources, city);
	}
	public void upgradeDefenseFully(final int clan, final City city);
	
	
	public default void upgradeDefenseFully(final Clan clan, final City city) {
		this.upgradeDefenseFully(clan.getId(), city);
	}
}
