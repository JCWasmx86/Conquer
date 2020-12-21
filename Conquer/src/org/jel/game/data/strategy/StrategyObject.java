package org.jel.game.data.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.ICity;
import org.jel.game.data.Resource;
import org.jel.game.utils.Graph;

public interface StrategyObject {

	default void attack(final ICity source, final ICity target, final Clan sourceClan, final boolean managedByPlayer,
			final long numberOfSoldiersToMoveIfManaged) {
		if (sourceClan == null) {
			throw new IllegalArgumentException("sourceClan==null");
		}
		this.attack(source, target, sourceClan.getId(), managedByPlayer, numberOfSoldiersToMoveIfManaged, true);
	}

	default void attack(final ICity source, final ICity target, final Clan sourceClan, final boolean managedByPlayer,
			final long numberOfSoldiersToMoveIfManaged, final boolean reallyPlayer) {
		if (sourceClan == null) {
			throw new IllegalArgumentException("sourceClan==null");
		}
		this.attack(source, target, sourceClan.getId(), managedByPlayer, numberOfSoldiersToMoveIfManaged, reallyPlayer);
	}

	default void attack(final ICity source, final ICity target, final int sourceClan, final boolean managedByPlayer,
			final long numberOfSoldiersToMoveIfManaged) {
		this.attack(source, target, sourceClan, managedByPlayer, numberOfSoldiersToMoveIfManaged, true);
	}

	void attack(ICity source, ICity target, int sourceOfClan, boolean managedByPlayer,
			long numberOfSoldiersToMoveIfManaged, boolean reallyPlayer);

	double defenseStrengthOfCity(ICity city);

	Graph<ICity> getCities();

	Graph<Integer> getRelations();

	default double getRelationship(final Clan a, final Clan b) {
		if (a == null) {
			throw new IllegalArgumentException("a==null");
		}
		if (b == null) {
			throw new IllegalArgumentException("b==null");
		}
		return this.getRelationship(a.getId(), b.getId());
	}

	default double getRelationship(final Clan clan, final ICity city) {
		if (clan == null) {
			throw new IllegalArgumentException("city==null");
		}
		if (city == null) {
			throw new IllegalArgumentException("city==null");
		}
		if (clan.getId() == city.getClanId()) {
			throw new IllegalArgumentException("clan.getID()==city.getClan()");
		}
		return this.getRelationship(clan.getId(), city.getClanId());
	}

	default double getRelationship(final int a, final int b) {
		if (a < 0) {
			throw new IllegalArgumentException("a < 0");
		}
		if (b < 0) {
			throw new IllegalArgumentException("b < 0");
		}
		// If the value is too big, it can't be determined.
		return this.getRelations().getWeight(a, b);
	}

	Iterable<ICity> getWeakestCityInRatioToSurroundingEnemyCities(List<ICity> cities);

	default Iterable<ICity> getWeakestCityInRatioToSurroundingEnemyCities(final Stream<ICity> cities) {
		if (cities == null) {
			throw new IllegalArgumentException("cities == null");
		}
		return this.getWeakestCityInRatioToSurroundingEnemyCities(cities.collect(Collectors.toList()));
	}

	default long maximumNumberToMove(final Clan clan, final double weight, final long maximumNumberOfSoldiers) {
		if (clan == null) {
			throw new IllegalArgumentException("clan == null");
		}
		return this.maximumNumberToMove(clan.getId(), weight, maximumNumberOfSoldiers);
	}

	default long maximumNumberToMove(final Clan clan, final ICity first, final ICity second,
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
		return this.maximumNumberToMove(clan.getId(), this.getCities().getWeight(first, second),
				maximumNumberOfSoldiers);
	}

	long maximumNumberToMove(int clanId, double weight, long maximumNumberOfSoldiers);

	default void moveSoldiers(final ICity source, final Stream<ICity> reachableCities, final Clan clan,
			final boolean managedByPlayer, final ICity target, final long numberOfSoldiersToMoveIfManaged) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		this.moveSoldiers(source, reachableCities, clan.getId(), managedByPlayer, target,
				numberOfSoldiersToMoveIfManaged);
	}

	void moveSoldiers(ICity source, Stream<ICity> reachableCities, int clanId, boolean managedByPlayer, ICity target,
			long numberOfSoldiersToMoveIfManaged);

	Stream<ICity> reachableCities(ICity city);

	default void recruitSoldiers(final double maxToPay, final Clan clan, final ICity city,
			final boolean managedByPlayer, final double numberOfSoldiers) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		} else if (city == null) {
			throw new IllegalArgumentException("city==null");
		} else if (city.getClanId() != clan.getId()) {

			throw new IllegalArgumentException("clan!=city.clan");
		}
		this.recruitSoldiers(maxToPay, clan.getId(), city, managedByPlayer, numberOfSoldiers);
	}

	void recruitSoldiers(double maxToPay, int clanId, ICity city, boolean managedByPlayer, double numberOfSoldiers);

	boolean sendGift(Clan source, Clan destination, Gift gift);

	default boolean upgradeDefense(final Clan clan) {
		return this.upgradeDefense(clan.getId());
	}

	default boolean upgradeDefense(final Clan clan, final ICity city) {
		return this.upgradeDefense(clan.getId(), city);
	}

	boolean upgradeDefense(int clan);

	boolean upgradeDefense(int clan, ICity city);

	default boolean upgradeOffense(final Clan clan) {
		return this.upgradeOffense(clan.getId());
	}

	boolean upgradeOffense(int clan);

	default boolean upgradeResource(final Clan clan, final Resource resource, final ICity city) {
		return this.upgradeResource(clan.getId(), resource, city);
	}

	boolean upgradeResource(int clan, Resource resc, ICity a);

	default boolean upgradeSoldiers(final Clan clan) {
		return this.upgradeSoldiers(clan.getId());
	}

	boolean upgradeSoldiers(int clan);
}
