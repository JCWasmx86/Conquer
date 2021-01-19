package org.jel.game.data;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A gift that is sent from one clan to another.
 */
public final class Gift {
	private final Map<Resource, Double> map = new EnumMap<>(Resource.class);
	private final double numberOfCoins;

	/**
	 * Creates an empty gift.
	 */
	public Gift() {
		this.numberOfCoins = 0;
		for (final var r : Resource.values()) {
			this.map.put(r, 0.0);
		}
	}

	/**
	 * Creates an empty gift. An {@link IllegalArgumentException} is thrown if:<br>
	 * <ul>
	 * <li>{@code numberOfCoins} is negative</li>
	 * <li>{@code resources} is {@code null}</li>
	 * <li>{@code resources.size() != Resource.values().length}</li>
	 * <li>Or one of the doubles in {@code resources} is either null, negative, NaN
	 * or infinite.</li>
	 * </ul>
	 *
	 * @param resources     The amount of resources that are gifted.
	 * @param numberOfCoins The amount of coins to gift.
	 */
	public Gift(final List<Double> resources, final double numberOfCoins) {
		if (numberOfCoins < 0) {
			throw new IllegalArgumentException("numberOfCoins < 0");
		} else if (resources == null) {
			throw new IllegalArgumentException("resources == null");
		} else if (resources.size() != Resource.values().length) {
			throw new IllegalArgumentException("resources.size() != Resource.values().length");
		}
		this.numberOfCoins = numberOfCoins;
		for (var i = 0; i < resources.size(); i++) {
			if (resources.get(i) < 0) {
				throw new IllegalArgumentException("resources.get(i) < 0, i=" + i);
			} else if (Double.isNaN(resources.get(i))) {
				throw new IllegalArgumentException("resources.get(i) isNaN" + i);
			} else if (Double.isInfinite(resources.get(i))) {
				throw new IllegalArgumentException("resources.get(i) isInfinite" + i);
			}
			this.map.put(Resource.values()[i], resources.get(i));
		}
	}

	/**
	 * Returns a copy of the map that provides a mapping between the resource and
	 * the amount of it that should be gifted.
	 *
	 * @return Map describing the amount of every resource.
	 */
	public Map<Resource, Double> getMap() {
		return new EnumMap<>(this.map);
	}

	/**
	 * Gives the number of coins
	 *
	 * @return Number of coins.
	 */
	public double getNumberOfCoins() {
		return this.numberOfCoins;
	}
}
