package org.jel.game.data;

public class ResourceUsage {
	private final double[] personUsed;
	private final double[] soldierUsed;
	private final double coinsPerRoundPerPerson;

	public ResourceUsage(final double[][] stats, final double coinsPerRoundPerPerson) {
		if (stats == null) {
			throw new IllegalArgumentException("stats==null");
		} else if (stats.length != Resource.values().length) {
			throw new IllegalArgumentException("Wrong length, expected it to be equals to Resource.values().length");
		}
		isBad(coinsPerRoundPerPerson);
		this.personUsed = new double[Resource.values().length];
		this.soldierUsed = new double[Resource.values().length];
		for (var i = 0; i < stats.length; i++) {
			if (stats[i] == null) {
				throw new IllegalArgumentException("stats[i] is null");
			} else if (stats[i].length != 2) {
				throw new IllegalArgumentException(
						"Expected stats[i] to be of length 2: {resourceUsageOfPerson;resourceUsageOfSoldier}");
			}
			isBad(stats[i][0]);
			isBad(stats[i][1]);
			this.personUsed[i] = stats[i][0];
			this.soldierUsed[i] = stats[i][1];
		}
		this.coinsPerRoundPerPerson = coinsPerRoundPerPerson;
	}

	private void isBad(double d) {
		if (d < 0) {
			throw new IllegalArgumentException("argument < 0");
		} else if (Double.isNaN(d)) {
			throw new IllegalArgumentException("argument is nan");
		} else if (Double.isInfinite(d)) {
			throw new IllegalArgumentException("argument is infinite");
		}
	}

	public double[] personUsage() {
		return this.personUsed;
	}

	public double[] soldierUsage() {
		return this.soldierUsed;
	}

	public double[] get(final int idx) {
		return new double[] { this.personUsed[idx], this.soldierUsed[idx] };
	}

	public double getCoinsPerRoundPerPerson() {
		return this.coinsPerRoundPerPerson;
	}
}
