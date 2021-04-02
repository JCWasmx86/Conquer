package conquer.data;

/**
 * A data carrier describing all costs around soldiers.
 */
public final class SoldierCosts {
	private final double coinsPerMovePerSoldier;
	private final double coinsPerMoveOfSoldierBase;
	private final double coinsPerSoldierInitial;
	private final double coinsPerSoldierPerRound;
	private final double ironPerSoldierInitial;
	private final double stonePerSoldierInitial;
	private final double woodPerSoldierInitial;

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
	public SoldierCosts(final double coinsPerMovePerSoldier, final double coinsPerMoveOfSoldierBase,
						final double coinsPerSoldierInitial, final double coinsPerSoldierPerRound,
						final double ironPerSoldierInitial, final double stonePerSoldierInitial,
						final double woodPerSoldierInitial) {
		this.isBad(coinsPerMovePerSoldier);
		this.isBad(coinsPerMoveOfSoldierBase);
		this.isBad(coinsPerSoldierPerRound);
		this.isBad(ironPerSoldierInitial);
		this.isBad(stonePerSoldierInitial);
		this.isBad(woodPerSoldierInitial);
		this.coinsPerMovePerSoldier = coinsPerMovePerSoldier;
		this.coinsPerMoveOfSoldierBase = coinsPerMoveOfSoldierBase;
		this.coinsPerSoldierInitial = coinsPerSoldierInitial;
		this.coinsPerSoldierPerRound = coinsPerSoldierPerRound;
		this.ironPerSoldierInitial = ironPerSoldierInitial;
		this.stonePerSoldierInitial = stonePerSoldierInitial;
		this.woodPerSoldierInitial = woodPerSoldierInitial;
	}

	public double coinsPerMovePerSoldier() {
		return this.coinsPerMovePerSoldier;
	}

	public double coinsPerMoveOfSoldierBase() {
		return this.coinsPerMoveOfSoldierBase;
	}

	public double coinsPerSoldierInitial() {
		return this.coinsPerSoldierInitial;
	}

	public double coinsPerSoldierPerRound() {
		return this.coinsPerSoldierPerRound;
	}

	public double ironPerSoldierInitial() {
		return this.ironPerSoldierInitial;
	}

	public double stonePerSoldierInitial() {
		return this.stonePerSoldierInitial;
	}

	public double woodPerSoldierInitial() {
		return this.woodPerSoldierInitial;
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
