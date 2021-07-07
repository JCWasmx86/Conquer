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
		Shared.isBad(coinsPerMovePerSoldier);
		Shared.isBad(coinsPerMoveOfSoldierBase);
		Shared.isBad(coinsPerSoldierPerRound);
		Shared.isBad(ironPerSoldierInitial);
		Shared.isBad(stonePerSoldierInitial);
		Shared.isBad(woodPerSoldierInitial);
	}
}
