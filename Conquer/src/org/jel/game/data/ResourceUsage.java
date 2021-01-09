package org.jel.game.data;

public class ResourceUsage {
	private double[] personUsed;
	private double[] soldierUsed;

	public ResourceUsage(final double[][] stats) {
		if (stats.length != Resource.values().length) {
			throw new IllegalArgumentException("Wrong length!");
		}
		this.personUsed = new double[Resource.values().length];
		this.soldierUsed = new double[Resource.values().length];
		for (int i = 0; i < stats.length; i++) {
			this.personUsed[i] = stats[i][0];
			this.soldierUsed[i] = stats[i][1];
		}
	}

	public double[] personUsage() {
		return personUsed;
	}

	public double[] soldierUsage() {
		return soldierUsed;
	}

	public double[] get(int idx) {
		return new double[] { this.personUsed[idx], this.soldierUsed[idx] };
	}
}
