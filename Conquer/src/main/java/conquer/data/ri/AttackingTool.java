package conquer.data.ri;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.utils.Graph;

final class AttackingTool {
	private final ICity src;
	private final ICity destination;
	private final boolean managed;
	private final long num;
	private Graph<ICity> cities;
	private QuadFunction<IClan, ICity, ICity, Long, Long> maximumNumberToMove;

	AttackingTool(ICity src, ICity destination, boolean managed, long num) {
		this.src = src;
		this.destination = destination;
		this.managed = managed;
		this.num = num;
	}

	void initVars(final Graph<ICity> cities, QuadFunction<IClan, ICity, ICity, Long, Long> maximumNumberToMove) {
		this.cities = cities;
		this.maximumNumberToMove = maximumNumberToMove;
	}

	void firstChecks() {
		if (managed && (num < 0)) {
			throw new IllegalArgumentException("number of soldiers is smaller than zero!");
		}
		if (src.getClan() == destination.getClan()) {
			throw new IllegalArgumentException("Same clan");
		} else if (src == destination) {
			throw new IllegalArgumentException("Same city");
		} else if (!cities.isConnected(src, destination)) {
			throw new IllegalArgumentException("Unreachable");
		}
	}

	boolean isAborted() {
		final var powerOfAttacker = this.calculatePowerOfAttacker(src, destination, managed, num);
		return (((powerOfAttacker == 0) && !src.isPlayerCity()) || ((!src.isPlayerCity()) && (powerOfAttacker == 1)));
	}

	private long calculatePowerOfAttacker(final ICity src, final ICity destination, final boolean managed,
										  final long numberOfSoldiers) {
		if (managed) {
			if (destination.isPlayerCity() && (destination instanceof City c)) {
				c.attackByPlayer();
			}
			return numberOfSoldiers;
		} else {
			return this.aiCalculateNumberOfTroopsToAttackWith(src, destination);
		}
	}

	private long aiCalculateNumberOfTroopsToAttackWith(final ICity src, final ICity destination) {
		final var powerOfAttacker = src.getNumberOfSoldiers();
		if (powerOfAttacker == 0) {
			return 0;
		}
		return this.maximumNumberToMove.apply(src.getClan(), src, destination, powerOfAttacker);
	}
}
