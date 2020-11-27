package org.jel.game.data.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.Resource;
import org.jel.game.utils.Graph;

public interface StrategyObject {

	void attack(City source, City target, byte sourceClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged);

	void attack(City source, City target, byte sourceOfClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged, boolean reallyPlayer);

	double defenseStrengthOfCity(City city);

	Graph<City> getCities();

	Graph<Integer> getRelations();

	default double getRelationship(Clan a, Clan b) {
		return this.getRelationship(a.getId(), b.getId());
	}

	default double getRelationship(int a, int b) {
		return this.getRelations().getWeight(a, b);
	}

	Iterable<City> getWeakestCityInRatioToSurroundingEnemyCities(List<City> cities);

	default Iterable<City> getWeakestCityInRatioToSurroundingEnemyCities(Stream<City> cities) {
		return this.getWeakestCityInRatioToSurroundingEnemyCities(cities.collect(Collectors.toList()));
	}

	long maximumNumberToMove(byte clanId, double weight, long numberOfSoldiers);

	void moveSoldiers(City source, Stream<City> reachableCities, byte clanId, boolean managedByPlayer, City target,
			long numberOfSoldiersToMoveIfManaged);

	Stream<City> reachableCities(City city);

	void recruitSoldiers(double maxToPay, byte clanId, City city, boolean managedByPlayer, double numberOfSoldiers);

	boolean sendGift(Clan source, Clan destination, Gift gift);

	boolean upgradeDefense(byte clan);

	boolean upgradeDefense(byte clan, City city);

	boolean upgradeOffense(byte clan);

	boolean upgradeResource(byte clan, Resource resc, City a);

	boolean upgradeSoldiers(byte clan);
}
