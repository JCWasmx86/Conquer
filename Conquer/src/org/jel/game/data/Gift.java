package org.jel.game.data;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Gift {
	private final Map<Resource, Double> map = new EnumMap<>(Resource.class);
	private final double numberOfCoins;

	public Gift() {
		this.numberOfCoins = 0;
		for (final var r : Resource.values()) {
			this.map.put(r, 0.0);
		}
	}

	public Gift(List<Double> resources, double numberOfCoins) {
		if (numberOfCoins < 0) {
			throw new IllegalArgumentException("numberOfCoins<0");
		}
		this.numberOfCoins = numberOfCoins;
		for (var i = 0; i < resources.size(); i++) {
			if (resources.get(i) < 0) {
				throw new IllegalArgumentException("resources.get(i) < 0, i=" + i);
			}
			this.map.put(Resource.values()[i], resources.get(i));
		}
	}

	public Map<Resource, Double> getMap() {
		return new EnumMap<>(this.map);
	}

	public double getNumberOfCoins() {
		return this.numberOfCoins;
	}
}
