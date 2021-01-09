package org.jel.game.data;

public class ResourceUsage {
	private final double[] personUsed;
	private final double[] soldierUsed;

	public ResourceUsage(final double[][] stats) {
		if (stats.length != Resource.values().length) {
			throw new IllegalArgumentException("Wrong length!");
		}
		this.personUsed = new double[Resource.values().length];
		this.soldierUsed = new double[Resource.values().length];
		for (var i = 0; i < stats.length; i++) {
			this.personUsed[i] = stats[i][0];
			this.soldierUsed[i] = stats[i][1];
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
}
