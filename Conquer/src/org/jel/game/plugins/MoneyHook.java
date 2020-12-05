package org.jel.game.plugins;

import java.util.List;

import org.jel.game.data.City;
import org.jel.game.data.Clan;

@FunctionalInterface
public interface MoneyHook {
	
	void moneyPaid(List<City> cities, Clan clan);
}
