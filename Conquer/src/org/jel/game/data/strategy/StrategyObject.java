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

	default void attack(City source, City target, byte sourceClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged) {
		this.attack(source, target, sourceClan, managedByPlayer, numberOfSoldiersToMoveIfManaged, true);
	}

	default void attack(City source, City target, Clan sourceClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged) {
		this.attack(source, target, (byte) sourceClan.getId(), managedByPlayer, numberOfSoldiersToMoveIfManaged, true);
	}

	void attack(City source, City target, byte sourceOfClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged, boolean reallyPlayer);

	default void attack(City source, City target, Clan sourceOfClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged, boolean reallyPlayer) {
		this.attack(source, target, (byte) sourceOfClan.getId(), managedByPlayer, numberOfSoldiersToMoveIfManaged);
	}

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

	default long maximumNumberToMove(Clan clan, double weight, long numberOfSoldiers) {
		return maximumNumberToMove((byte) clan.getId(), weight, numberOfSoldiers);
	}

	void moveSoldiers(City source, Stream<City> reachableCities, byte clanId, boolean managedByPlayer, City target,
			long numberOfSoldiersToMoveIfManaged);

	default void moveSoldiers(City source, Stream<City> reachableCities, Clan clan, boolean managedByPlayer,
			City target, long numberOfSoldiersToMoveIfManaged) {
		moveSoldiers(source, reachableCities, (byte) clan.getId(), managedByPlayer, target, numberOfSoldiersToMoveIfManaged);
	}

	Stream<City> reachableCities(City city);

	void recruitSoldiers(double maxToPay, byte clanId, City city, boolean managedByPlayer, double numberOfSoldiers);

	default void recruitSoldiers(double maxToPay, Clan clan, City city, boolean managedByPlayer,
			double numberOfSoldiers) {
		recruitSoldiers(maxToPay, (byte) clan.getId(), city, managedByPlayer, numberOfSoldiers);
	}

	boolean sendGift(Clan source, Clan destination, Gift gift);

	boolean upgradeDefense(byte clan);

	default boolean upgradeDefense(Clan clan) {
		return upgradeDefense((byte) clan.getId());
	}

	boolean upgradeDefense(byte clan, City city);

	default boolean upgradeDefense(Clan clan, City city) {
		return upgradeDefense((byte) clan.getId(), city);
	}

	boolean upgradeOffense(byte clan);

	default boolean upgradeOffense(Clan clan) {
		return upgradeOffense((byte) clan.getId());
	}

	boolean upgradeResource(byte clan, Resource resc, City a);

	default boolean upgradeResource(Clan clan, Resource resc, City city) {
		return upgradeResource((byte) clan.getId(), resc, city);
	}

	boolean upgradeSoldiers(byte clan);

	default boolean upgradeSoldiers(Clan clan) {
		return upgradeSoldiers((byte) clan.getId());
	}

	default double getRelationship(Clan clan, City a) {
		return getRelationship(clan.getId(), a.getClan());
	}
}
