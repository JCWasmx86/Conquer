package org.jel.game.data;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jel.game.utils.Graph;

public final class Reader {
	private final File file;

	public Reader(final String f) {
		this.file = new File(f);
	}

	// TODO: Refactor, maybe use JSON/XML???
	public Game buildGame() {
		final var game = new Game();
		try (var dis = new DataInputStream(Files.newInputStream(Paths.get(this.file.toURI())))) {
			final var mag1 = dis.read();
			final var mag2 = dis.read();
			if ((mag1 != 0xAA) && (mag2 != 0x55)) {
				Shared.LOGGER.error("Wrong magic number!");
				return null;
			}
			final var numBytesOfBackgroundImage = dis.readInt();
			if (numBytesOfBackgroundImage < 0) {
				Shared.LOGGER.error(
						"Expected a non negative number of bytes in background, got " + numBytesOfBackgroundImage);
				return null;
			}
			final var data = new byte[numBytesOfBackgroundImage];
			var bytesRead = dis.read(data);
			if (bytesRead != numBytesOfBackgroundImage) {
				Shared.LOGGER.error(
						"bytesRead: " + bytesRead + " != numBytesOfBackgroundImage: " + numBytesOfBackgroundImage);
				return null;
			}
			final var gi = ImageIO.read(new ByteArrayInputStream(data));
			game.setBackground(gi);
			final var numPlayers = dis.readByte();
			if ((numPlayers == 1) || (numPlayers == 0)) {
				Shared.LOGGER.error("Expected at least 2 players, got " + numPlayers);
				return null;
			} else if (numPlayers < 0) {
				Shared.LOGGER.error("Expected a non negative number of players, got " + numPlayers);
				return null;
			}
			game.setPlayers(numPlayers);
			final List<Clan> tmp = new ArrayList<>();
			for (var i = 0; i < numPlayers; i++) {
				final var d = dis.readDouble();
				if (d < 0) {
					Shared.LOGGER.error("Expected a non negative number of coins, got " + d);
					return null;
				}
				tmp.add(new Clan());
				tmp.get(i).setCoins(d);
			}
			for (var i = 0; i < numPlayers; i++) {
				final String s;
				try {
					s = dis.readUTF();
				} catch (final UTFDataFormatException utf) {
					Shared.LOGGER.error("Invalid utf!");
					return null;
				}
				tmp.get(i).setId(i);
				tmp.get(i).setName(s);
			}
			for (var i = 0; i < numPlayers; i++) {
				final int flags = dis.readByte();
				tmp.get(i).setFlags(flags);
			}
			final List<Color> colors = new ArrayList<>();
			for (var i = 0; i < numPlayers; i++) {
				final var r = dis.readInt();
				if (r < 0) {
					Shared.LOGGER.error("r < 0: " + r);
					return null;
				} else if (r > 255) {
					Shared.LOGGER.error("r to big: " + r);
					return null;
				}
				final var g = dis.readInt();
				if (g < 0) {
					Shared.LOGGER.error("g < 0: " + g);
					return null;
				} else if (g > 255) {
					Shared.LOGGER.error("g to big: " + g);
					return null;
				}
				final var b = dis.readInt();
				if (b < 0) {
					Shared.LOGGER.error("b < 0: " + b);
					return null;
				} else if (b > 255) {
					Shared.LOGGER.error("b to big: " + b);
					return null;
				}
				colors.add(new Color(r, g, b));
				tmp.get(i).setColor(colors.get(i));
			}
			final var numberOfRelations = dis.readInt();
			if (numberOfRelations < 0) {
				Shared.LOGGER.error("Expected a non negative number of relations, got  " + numberOfRelations);
				return null;
			}
			final var relations = new Graph<Integer>(numPlayers);
			for (var i = 0; i < numPlayers; i++) {
				relations.add(i);
			}
			for (var i = 0; i < numPlayers; i++) {
				for (var j = 0; j < numPlayers; j++) {
					if (i != j) {
						relations.addDirectedEdge(i, j, 50, 50);
					} else {
						relations.addDirectedEdge(i, j, Double.MAX_VALUE, Double.MAX_VALUE);
					}
				}
			}
			for (var i = 0; i < numberOfRelations; i++) {
				final var firstClan = dis.readByte();
				if (firstClan >= numPlayers) {
					Shared.LOGGER.error("firstClan > numPlayers: " + firstClan);
				}
				final var secondClan = dis.readByte();
				if (secondClan >= numPlayers) {
					Shared.LOGGER.error("secondClan > numPlayers: " + secondClan);
				}
				final var value = dis.readInt() % 101;
				relations.addDirectedEdge(firstClan, secondClan, value, value);
			}
			game.setRelations(relations);
			final int numCities = dis.readShort();
			final var g = new Graph<City>(numCities);
			for (var i = 0; i < numCities; i++) {
				g.add(new City(game));
			}
			for (var i = 0; i < numCities; i++) {
				final var bytesOfPicture = dis.readInt();
				if (bytesOfPicture < 0) {
					Shared.LOGGER.error("Expected a non negative number of bytes in image ,got " + bytesOfPicture);
					return null;
				}
				final var pic = new byte[bytesOfPicture];
				bytesRead = dis.read(pic);
				if (bytesRead != bytesOfPicture) {
					Shared.LOGGER.error("bytesRead: " + bytesRead + " != bytesOfPicture: " + bytesOfPicture);
					return null;
				}
				final var c = new City(game);
				c.setImage(ImageIO.read(new ByteArrayInputStream(pic)));
				final var clanN = dis.readInt();
				if (clanN < 0) {
					Shared.LOGGER.error("clan < 0: " + clanN);
					return null;
				}
				if (clanN >= numPlayers) {
					Shared.LOGGER.error("clan >= numPlayers : " + clanN);
					return null;
				}
				c.setClan(tmp.get(clanN));
				final var numPeople = dis.readInt();
				if (numPeople < 0) {
					Shared.LOGGER.error("numPeople < 0: " + numPeople);
					return null;
				}
				c.setNumberOfPeople(numPeople);
				final var numSoldiers = dis.readInt();
				if (numSoldiers < 0) {
					Shared.LOGGER.error("numSoldiers < 0: " + numSoldiers);
					return null;
				}
				c.setNumberOfSoldiers(numSoldiers);
				final var x = dis.readInt();
				if (x < 0) {
					Shared.LOGGER.error("x < 0: " + x);
					return null;
				}
				c.setX(x);
				final var y = dis.readInt();
				if (y < 0) {
					Shared.LOGGER.error("y < 0: " + y);
					return null;
				}
				c.setY(y);
				final var defense = dis.readInt();
				if (defense < 0) {
					Shared.LOGGER.error("defense < 0: " + defense);
					return null;
				}
				c.setDefense(defense);
				final var bonus = dis.readDouble();
				if (bonus < 0) {
					Shared.LOGGER.error("bonus < 0: " + bonus);
					return null;
				}
				c.setDefenseBonus(bonus);
				final var growth = dis.readDouble();
				if (growth < 0) {
					Shared.LOGGER.error("growth < 0: " + growth);
					return null;
				}
				c.setGrowth(growth);
				final String cityN;
				try {
					cityN = dis.readUTF();
				} catch (final UTFDataFormatException utf) {
					Shared.LOGGER.error("Invalid utf!");
					return null;
				}
				c.setName(cityN);
				g._set(i, c);
				final int numConnections = dis.readShort();
				for (var s = 0; s < numConnections; s++) {
					final int otherCityIndex = dis.readShort();
					if (otherCityIndex < 0) {
						Shared.LOGGER.error("otherCityIndex < 0: " + otherCityIndex);
						return null;
					} else if (otherCityIndex == i) {
						Shared.LOGGER.error("Can\'t have a connection to itself!");
						return null;
					} else if (otherCityIndex > numCities) {
						Shared.LOGGER.error("Index out of range: " + otherCityIndex);
						return null;
					}
					final var distanceToCity = dis.readDouble();
					if (distanceToCity < 0) {
						Shared.LOGGER.error("distanceToCity < 0: " + distanceToCity);
						return null;
					}
					g.addDirectedEdge(otherCityIndex, i, distanceToCity, distanceToCity);
				}
				final List<Double> productions = new ArrayList<>();
				for (var j = 0; j < Resource.values().length; j++) {
					final var prodRate = dis.readDouble();
					if (prodRate < 0) {
						Shared.LOGGER.error("prodRate < 0: " + prodRate);
						return null;
					}
					productions.add(prodRate);
				}
				c.setProductionRates(productions);
			}
			game.setClans(tmp);
			game.setGraph(g);
		} catch (final IOException ioe) {
			Shared.LOGGER.exception(ioe);
			return null;
		}
		if (!game.getCities().isConnected()) {
			Shared.LOGGER.error("Disconnected graph!");
			return null;
		}
		return game;
	}

}
