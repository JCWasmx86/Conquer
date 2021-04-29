package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Scenario {
	private final String name;
	private final String background;
	private final Player[] players;
	private final City[] cities;
	private final CityConnection[] connections;

	public Scenario(String name, String background, Player[] players, City[] cities, CityConnection[] connections) {
		this.name = name;
		this.background = background;
		this.players = players;
		this.cities = cities;
		this.connections = connections;
	}

	public void validate() {
		ValidatorUtils.throwIfNull(this.name, "Scenario name is missing!");
		ValidatorUtils.throwIfNull(this.background, "Background image is missing!");
		ValidatorUtils.throwIfNull(this.players, "Players are missing!");
		ValidatorUtils.throwIfNull(this.cities, "Cities are missing!");
		ValidatorUtils.throwIfNull(this.connections, "CityConnections are missing!");
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
		if (set.size() != cities.length) {
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
		if (set.size() != players.length) {
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

	public String name() { return this.name; }

	public String background() { return this.background; }

	public Player[] players() { return this.players; }

	public City[] cities() { return this.cities; }

	public CityConnection[] connections() { return this.connections; }

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Scenario) obj;
		return Objects.equals(this.name, that.name) &&
			Objects.equals(this.background, that.background) &&
			Objects.equals(this.players, that.players) &&
			Objects.equals(this.cities, that.cities) &&
			Objects.equals(this.connections, that.connections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.background, this.players, this.cities, this.connections);
	}

	@Override
	public String toString() {
		return "Scenario[" +
			"name=" + this.name + ", " +
			"background=" + this.background + ", " +
			"players=" + this.players + ", " +
			"cities=" + this.cities + ", " +
			"connections=" + this.connections + ']';
	}

}
