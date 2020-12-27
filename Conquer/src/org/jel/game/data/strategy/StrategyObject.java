package org.jel.game.data.strategy;

import java.util.stream.Stream;

import org.jel.game.data.Gift;
import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.data.Resource;
import org.jel.game.utils.Graph;

public interface StrategyObject {

	void attack(ICity source, ICity target, boolean managedByPlayer, long numberOfSoldiersToMoveIfManaged);

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
		if (clan == city.getClan()) {
			throw new IllegalArgumentException("clan==city.getClan()");
		}
		return this.getRelationship(clan, city.getClan());
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

	long maximumNumberToMove(IClan clan, double weight, long maximumNumberOfSoldiers);

	void moveSoldiers(ICity source, Stream<ICity> reachableCities, boolean managedByExternalStrategy, ICity target,
			long numberOfSoldiersToMoveIfManaged);

	void recruitSoldiers(double maxToPay, ICity city, boolean managedByPlayer, long numberOfSoldiers);

	boolean sendGift(IClan source, IClan destination, Gift gift);

	boolean upgradeDefense(ICity city);

	boolean upgradeResource(Resource resc, ICity a);
}
