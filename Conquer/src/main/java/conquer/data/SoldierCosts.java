package conquer.data;

/**
 * A data carrier describing all costs around soldiers.
 */
public record SoldierCosts(double coinsPerMovePerSoldier, double coinsPerMoveOfSoldierBase,
						   double coinsPerSoldierInitial, double coinsPerSoldierPerRound, double ironPerSoldierInitial,
						   double stonePerSoldierInitial, double woodPerSoldierInitial) {
	/**
	 * No value may be negative, NaN or infinite. Otherwise an
	 * {@code IllegalArgumentException} will be thrown.
	 *
	 * @param coinsPerMovePerSoldier
	 * @param coinsPerMoveOfSoldierBase
	 * @param coinsPerSoldierInitial
	 * @param coinsPerSoldierPerRound
	 * @param ironPerSoldierInitial
	 * @param stonePerSoldierInitial
	 * @param woodPerSoldierInitial
	 */
	public SoldierCosts {
		this.isBad(coinsPerMovePerSoldier);
		this.isBad(coinsPerMoveOfSoldierBase);
		this.isBad(coinsPerSoldierPerRound);
		this.isBad(ironPerSoldierInitial);
		this.isBad(stonePerSoldierInitial);
		this.isBad(woodPerSoldierInitial);
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
}
