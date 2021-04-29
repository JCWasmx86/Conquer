package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public record Scenario(String name, String background, Player[] players, City[] cities, CityConnection[] connections) {
	public void validate() {
		ValidatorUtils.throwIfNull(name, "Scenario name is missing!");
		ValidatorUtils.throwIfNull(background, "Background image is missing!");
		ValidatorUtils.throwIfNull(players, "Players are missing!");
		ValidatorUtils.throwIfNull(cities, "Cities are missing!");
		ValidatorUtils.throwIfNull(connections, "CityConnections are missing!");
		this.validate(this.players);
		this.validate(this.cities);
		this.validate(this.connections);
	}

	private void validate(final CityConnection[] connections) {
		for (final var connection : connections) {
			ValidatorUtils.throwIfNull(connection, "Connection is null!");
			connection.validate(this.cities);
		}
	}

	private void validate(final City[] cities) {
		for (final var city : cities) {
			ValidatorUtils.throwIfNull(city, "City is null!");
			city.validate(this.players);
		}
		final var set = Arrays.stream(cities).map(City::name).collect(Collectors.toSet());
		if (set.size() == cities.length) {
			return;
		} else {
			final var len = cities.length;
			for (var i = 0; i < len; i++) {
				for (var j = 0; j < len; j++) {
					if (i != j && cities[i].name().equals(cities[j].name())) {
						throw new IllegalArgumentException("Duplicated city name: " + cities[i].name());
					}
				}
			}
		}
	}

	private void validate(final Player[] players) {
		for (final var player : players) {
			ValidatorUtils.throwIfNull(player, "Player is null!");
			player.validate();
		}
		final var set = Arrays.stream(players).map(Player::name).collect(Collectors.toSet());
		if (set.size() == players.length) {
			return;
		} else {
			final var len = players.length;
			for (var i = 0; i < len; i++) {
				for (var j = 0; j < len; j++) {
					if (i != j && players[i].name().equals(players[j].name())) {
						throw new IllegalArgumentException("Duplicated player name: " + players[i].name());
					}
				}
			}
		}
	}
}
