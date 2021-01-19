package org.jel.game.data;

/**
 * A class describing the usage of each resource.
 */
public class ResourceUsage {
	private final double[] personUsed;
	private final double[] soldierUsed;
	private final double coinsPerRoundPerPerson;

	/**
	 * The {@code stats}-array has this layout:
	 *
	 * <pre>
	 * [0][1]
	 * [0][1]
	 * [0][1]
	 * [0][1]
	 * [0][1]
	 * [0][1]
	 * [0][1]
	 * [0][1]
	 * [0][1]
	 * </pre>
	 *
	 * Every row in this array is for one resource. The index is obtained by calling
	 * {@link Resource#getIndex()}. Each subarray has two entries.
	 * {@code subarray[0]} is the use of the resource per person,
	 * {@code subarray[1]} is the usage of the resource per soldier.
	 *
	 * No value may be negative, null, NaN or infinite, otherwise an
	 * {@code IllegalArgumentException} will be thrown.
	 *
	 * @param stats                  Statistics, may not be {@code null}.
	 * @param coinsPerRoundPerPerson
	 */
	public ResourceUsage(final double[][] stats, final double coinsPerRoundPerPerson) {
		if (stats == null) {
			throw new IllegalArgumentException("stats==null");
		} else if (stats.length != Resource.values().length) {
			throw new IllegalArgumentException("Wrong length, expected it to be equals to Resource.values().length");
		}
		this.isBad(coinsPerRoundPerPerson);
		this.personUsed = new double[Resource.values().length];
		this.soldierUsed = new double[Resource.values().length];
		for (var i = 0; i < stats.length; i++) {
			if (stats[i] == null) {
				throw new IllegalArgumentException("stats[i] is null");
			} else if (stats[i].length != 2) {
				throw new IllegalArgumentException(
						"Expected stats[i] to be of length 2: {resourceUsageOfPerson;resourceUsageOfSoldier}");
			}
			this.isBad(stats[i][0]);
			this.isBad(stats[i][1]);
			this.personUsed[i] = stats[i][0];
			this.soldierUsed[i] = stats[i][1];
		}
		this.coinsPerRoundPerPerson = coinsPerRoundPerPerson;
	}

	private void isBad(final double d) {
		if (d < 0) {
			throw new IllegalArgumentException("argument < 0");
		} else if (Double.isNaN(d)) {
			throw new IllegalArgumentException("argument is nan");
		} else if (Double.isInfinite(d)) {
			throw new IllegalArgumentException("argument is infinite");
		}
	}

	/**
	 * Returns the amount of resources for every resource, that every person uses
	 * per round.
	 *
	 * @return Amount of resources, used by every person.
	 */
	public double[] personUsage() {
		return this.personUsed;
	}

	/**
	 * Returns the amount of resources for every resource, that every soldier uses
	 * per round.
	 *
	 * @return Amount of resources, used by every soldier.
	 */
	public double[] soldierUsage() {
		return this.soldierUsed;
	}

	/**
	 * Returns the subarray, as described at
	 * {@link ResourceUsage#ResourceUsage(double[][], double)}.
	 *
	 * @param idx The index.
	 * @return Subarray, but just a copy.
	 */
	public double[] get(final int idx) {
		return new double[] { this.personUsed[idx], this.soldierUsed[idx] };
	}

	/**
	 * Gives the number of coins every person produces in each round.
	 *
	 * @return Number of coins.
	 */
	public double getCoinsPerRoundPerPerson() {
		return this.coinsPerRoundPerPerson;
	}
}
