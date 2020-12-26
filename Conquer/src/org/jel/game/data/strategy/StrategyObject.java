package org.jel.game.data.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jel.game.data.Gift;
import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.data.Resource;
import org.jel.game.utils.Graph;

public interface StrategyObject {

	default void attack(final ICity source, final ICity target, final boolean managedByPlayer,
			final long numberOfSoldiersToMoveIfManaged) {
		this.attack(source, target, managedByPlayer, numberOfSoldiersToMoveIfManaged, true);
	}

	void attack(ICity source, ICity target, boolean managedByPlayer, long numberOfSoldiersToMoveIfManaged,
			boolean reallyPlayer);

	double defenseStrengthOfCity(ICity city);

	Graph<ICity> getCities();

	Graph<Integer> getRelations();

	double getRelationship(final IClan a, final IClan b);

	default double getRelationship(final IClan clan, final ICity city) {
		if (clan == null) {
			throw new IllegalArgumentException("city==null");
		}
		if (city == null) {
			throw new IllegalArgumentException("city==null");
		}
		if (clan.getId() == city.getClanId()) {
			throw new IllegalArgumentException("clan.getID()==city.getClan()");
		}
		return this.getRelationship(clan, city.getClan());
	}

	Iterable<ICity> getWeakestCityInRatioToSurroundingEnemyCities(List<ICity> cities);

	default Iterable<ICity> getWeakestCityInRatioToSurroundingEnemyCities(final Stream<ICity> cities) {
		if (cities == null) {
			throw new IllegalArgumentException("cities == null");
		}
		return this.getWeakestCityInRatioToSurroundingEnemyCities(cities.collect(Collectors.toList()));
	}

	default long maximumNumberToMove(final IClan clan, final ICity first, final ICity second,
			final long maximumNumberOfSoldiers) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		if (first == null) {
			throw new IllegalArgumentException("first==null");
		}
		if (second == null) {
			throw new IllegalArgumentException("second==null");
		}
		if (first == second) {
			throw new IllegalArgumentException("first==second");
		}
		return this.maximumNumberToMove(clan, this.getCities().getWeight(first, second), maximumNumberOfSoldiers);
	}

	long maximumNumberToMove(IClan clanId, double weight, long maximumNumberOfSoldiers);

	void moveSoldiers(ICity source, Stream<ICity> reachableCities, boolean managedByPlayer, ICity target,
			long numberOfSoldiersToMoveIfManaged);

	Stream<ICity> reachableCities(ICity city);

	void recruitSoldiers(double maxToPay, ICity city, boolean managedByPlayer, double numberOfSoldiers);

	boolean sendGift(IClan source, IClan destination, Gift gift);

	boolean upgradeDefense(IClan clan);

	boolean upgradeDefense(ICity city);

	boolean upgradeOffense(IClan clan);

	boolean upgradeResource(Resource resc, ICity a);

	boolean upgradeSoldiers(IClan clan);
}
