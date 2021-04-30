package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import conquer.ri.datatool.tool.DataFile;

public record Scenario(String name, String background,
					   Player[] players, City[] cities,
					   CityConnection[] connections, Relation[] relations) {

	public void validate() {
		ValidatorUtils.throwIfNull(this.name, "Scenario name is missing!");
		ValidatorUtils.throwIfNull(this.background, "Background image is missing!");
		ValidatorUtils.throwIfNull(this.players, "Players are missing!");
		ValidatorUtils.throwIfNull(this.cities, "Cities are missing!");
		ValidatorUtils.throwIfNull(this.connections, "CityConnections are missing!");
		this.validate(this.players);
		this.validate(this.cities);
		this.validate(this.connections);
		if(this.relations != null) {
			for (final var relation : relations) {
				relation.validate(this.players);
			}
		}
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

	public DataFile toDataFile() {
		final var df = new DataFile().setBackground(this.background);
		for (final var player : this.players) {
			df.addPlayer(player.initialCoins(), player.name(), player.clanColor().toColor(), player.flags());
		}
		for (final var city : this.cities) {
			df.addCity(new conquer.ri.datatool.tool.City(city.growth(), city.icon(), city.name(),
				this.getClanId(city.clan()), city.numberOfPeople(), city.numberOfSoldiers(), city.x(), city.y(),
				city.defense(), city.defenseBonus(), city.productions().toList()));
		}
		for (final var connection : this.connections) {
			df.addCityConnection(this.getCityId(connection.from()), this.getCityId(connection.to()),
				connection.distance());
		}
		if (this.relations != null) {
			for (final var relation : this.relations) {
				df.addRelation(this.getClanId(relation.first()), this.getClanId(relation.second()),
					relation.relation());
			}
		}
		return df;
	}

	private int getCityId(String city) {
		for (var i = 0; i < this.cities.length; i++) {
			if (this.cities[i].name().equals(city)) {
				return i;
			}
		}
		throw new InternalError("Unreachable!");
	}

	private int getClanId(String clan) {
		for (var i = 0; i < this.players.length; i++) {
			if (this.players[i].name().equals(clan)) {
				return i;
			}
		}
		throw new InternalError("Unreachable!");
	}
}
