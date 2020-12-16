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

	default void attack(final City source, final City target, final byte sourceClan, final boolean managedByPlayer,
			final long numberOfSoldiersToMoveIfManaged) {
		this.attack(source, target, sourceClan, managedByPlayer, numberOfSoldiersToMoveIfManaged, true);
	}

	void attack(City source, City target, byte sourceOfClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged, boolean reallyPlayer);

	default void attack(final City source, final City target, final Clan sourceClan, final boolean managedByPlayer,
			final long numberOfSoldiersToMoveIfManaged) {
		this.attack(source, target, (byte) sourceClan.getId(), managedByPlayer, numberOfSoldiersToMoveIfManaged, true);
	}

	default void attack(final City source, final City target, final Clan sourceOfClan, final boolean managedByPlayer,
			final long numberOfSoldiersToMoveIfManaged, final boolean reallyPlayer) {
		this.attack(source, target, (byte) sourceOfClan.getId(), managedByPlayer, numberOfSoldiersToMoveIfManaged,
				reallyPlayer);
	}

	double defenseStrengthOfCity(City city);

	Graph<City> getCities();

	Graph<Integer> getRelations();

	default double getRelationship(final Clan clan, final City city) {
		if (clan.getId() == city.getClanId()) {
			throw new IllegalArgumentException("clan.getID()==city.getClan()");
		}
		return this.getRelationship(clan.getId(), city.getClanId());
	}

	default double getRelationship(final Clan a, final Clan b) {
		return this.getRelationship(a.getId(), b.getId());
	}

	default double getRelationship(final int a, final int b) {
		return this.getRelations().getWeight(a, b);
	}

	Iterable<City> getWeakestCityInRatioToSurroundingEnemyCities(List<City> cities);

	default Iterable<City> getWeakestCityInRatioToSurroundingEnemyCities(final Stream<City> cities) {
		return this.getWeakestCityInRatioToSurroundingEnemyCities(cities.collect(Collectors.toList()));
	}

	long maximumNumberToMove(byte clanId, double weight, long maximumNumberOfSoldiers);

	default long maximumNumberToMove(final Clan clan, final City first, final City second,
			final long maximumNumberOfSoldiers) {
		if (first == second) {
			throw new IllegalArgumentException("first==second");
		}
		return this.maximumNumberToMove((byte) clan.getId(), this.getCities().getWeight(first, second),
				maximumNumberOfSoldiers);
	}

	default long maximumNumberToMove(final Clan clan, final double weight, final long maximumNumberOfSoldiers) {
		return this.maximumNumberToMove((byte) clan.getId(), weight, maximumNumberOfSoldiers);
	}

	void moveSoldiers(City source, Stream<City> reachableCities, byte clanId, boolean managedByPlayer, City target,
			long numberOfSoldiersToMoveIfManaged);

	default void moveSoldiers(final City source, final Stream<City> reachableCities, final Clan clan,
			final boolean managedByPlayer, final City target, final long numberOfSoldiersToMoveIfManaged) {
		this.moveSoldiers(source, reachableCities, (byte) clan.getId(), managedByPlayer, target,
				numberOfSoldiersToMoveIfManaged);
	}

	Stream<City> reachableCities(City city);

	void recruitSoldiers(double maxToPay, byte clanId, City city, boolean managedByPlayer, double numberOfSoldiers);

	default void recruitSoldiers(final double maxToPay, final Clan clan, final City city, final boolean managedByPlayer,
			final double numberOfSoldiers) {
		this.recruitSoldiers(maxToPay, (byte) clan.getId(), city, managedByPlayer, numberOfSoldiers);
	}

	boolean sendGift(Clan source, Clan destination, Gift gift);

	boolean upgradeDefense(byte clan);

	boolean upgradeDefense(byte clan, City city);

	default boolean upgradeDefense(final Clan clan) {
		return this.upgradeDefense((byte) clan.getId());
	}

	default boolean upgradeDefense(final Clan clan, final City city) {
		return this.upgradeDefense((byte) clan.getId(), city);
	}

	boolean upgradeOffense(byte clan);

	default boolean upgradeOffense(final Clan clan) {
		return this.upgradeOffense((byte) clan.getId());
	}

	boolean upgradeResource(byte clan, Resource resc, City a);

	default boolean upgradeResource(final Clan clan, final Resource resource, final City city) {
		return this.upgradeResource((byte) clan.getId(), resource, city);
	}

	boolean upgradeSoldiers(byte clan);

	default boolean upgradeSoldiers(final Clan clan) {
		return this.upgradeSoldiers((byte) clan.getId());
	}
}
