package conquer.data.ri;

<<<<<<< HEAD
import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
=======
import conquer.data.*;
import conquer.utils.Graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
>>>>>>> parent of f8bbb68 (Formatting)
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import conquer.data.ConquerInfo;
import conquer.data.ConquerInfoReader;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.InstalledScenario;
import conquer.data.Resource;
import conquer.data.Shared;
import conquer.utils.Graph;

/**
 * The class that builds a Game from a scenariofile.
 */
public final class ScenarioFileReader implements ConquerInfoReader {
    private final InstalledScenario scenario;

    /**
     * Create a new Reader with the specified filename as input.
     *
     * @param is Scenario
     */
    public ScenarioFileReader(final InstalledScenario is) {
        this.scenario = is;
    }

    // Only used for Shared#loadReferenceImplementationfile
    public ScenarioFileReader() {
        this(null);
    }

<<<<<<< HEAD
	/**
	 * Read the inputfile and build an uninitialized game.
	 *
	 * @return An uninitialized game or null in case of an error.
	 */
	@Override
	public ConquerInfo build() {
		final InputStream stream;
		try {
			stream = this.scenario.file() == null ? this.scenario.in()
				: Files.newInputStream(Paths.get(new File(this.scenario.file()).toURI()));
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
			throw new RuntimeException(e);
		}
		return this.readFromStream(stream);
	}

	private ConquerInfo readFromStream(final InputStream stream) {
		final var game = new Game();
		try (final var dis = new DataInputStream(stream)) {
			final var mag1 = dis.read();
			final var mag2 = dis.read();
			if ((mag1 != 0xAA) || (mag2 != 0x55)) {
				throw new IllegalArgumentException("Wrong magic number!");
			}
			game.setBackground(this.readBackground(dis));
			final var numPlayers = dis.readInt();
			if ((numPlayers == 1) || (numPlayers == 0)) {
				throw new IllegalArgumentException("Expected at least 2 players, got " + numPlayers);
			}
			this.throwIfNegative(numPlayers, "Expected a non negative number of players, got " + numPlayers);
			game.setPlayers(numPlayers);
			final List<IClan> tmp = new ArrayList<>();
			this.initClans(dis, tmp, numPlayers, game);
			this.readColors(dis, numPlayers, tmp);
			final var relations = this.readRelations(dis, numPlayers);
			game.setRelations(relations);
			final var g = this.readCities(dis, tmp, game);
			game.setClans(tmp);
			game.setGraph(g);
		} catch (final IOException ioe) {
			Shared.LOGGER.exception(ioe);
			throw new RuntimeException(ioe);
		}
		if (!game.getCities().isConnected()) {
			throw new IllegalArgumentException("Disconnected graph!");
		}
		return game;
	}

	private Image readBackground(final DataInputStream dis) throws IOException {
		final var numBytesOfBackgroundImage = dis.readInt();
		this.throwIfNegative(numBytesOfBackgroundImage, "Expected a non negative number of bytes in background, " +
			"got " + numBytesOfBackgroundImage);
		final var data = new byte[numBytesOfBackgroundImage];
		final var bytesRead = dis.read(data);
		if (bytesRead != numBytesOfBackgroundImage) {
			throw new RuntimeException(
				"bytesRead: " + bytesRead + " != numBytesOfBackgroundImage: " + numBytesOfBackgroundImage);
		}
		return ImageIO.read(new ByteArrayInputStream(data));
	}

	private void initClans(final DataInput dis, final List<IClan> tmp, final int numPlayers, final ConquerInfo game) throws IOException {
		for (var i = 0; i < numPlayers; i++) {
			final var d = dis.readDouble();
			this.throwIfNegative(d, "Expected a non negative number of coins, got " + d);
			tmp.add(new Clan());
			tmp.get(i).setCoins(d);
			((Clan) tmp.get(i)).setInfo(game);
		}
		for (var i = 0; i < numPlayers; i++) {
			final var s = dis.readUTF();
			tmp.get(i).setId(i);
			tmp.get(i).setName(s);
		}
		for (var i = 0; i < numPlayers; i++) {
			final var flags = dis.readInt();
			tmp.get(i).setFlags(flags);
		}
	}

	private void throwIfNegative(final double n, final String message) {
		if (n < 0) {
			throw new IllegalArgumentException(message);
		}
	}

	private Graph<ICity> readCities(final DataInputStream dis, final List<? extends IClan> clans, final ConquerInfo game) throws IOException {
		final int numCities = dis.readShort();
		this.throwIfNegative(numCities, "numCities < 0: " + numCities);
		final var g = new Graph<ICity>(numCities);
		for (var i = 0; i < numCities; i++) {
			g.add(new City(game));
		}
		for (var i = 0; i < numCities; i++) {
			final var bytesOfPicture = dis.readInt();
			this.throwIfNegative(bytesOfPicture,
				"Expected a non negative number of bytes in image ,got " + bytesOfPicture);
			final var pic = new byte[bytesOfPicture];
			final var bytesRead = dis.read(pic);
			if (bytesRead != bytesOfPicture) {
				throw new RuntimeException("bytesRead: " + bytesRead + " != bytesOfPicture: " + bytesOfPicture);
			}
			final var c = new CityBuilder(game);
			c.setImage(ImageIO.read(new ByteArrayInputStream(pic)));
			final var clanN = dis.readInt();
			this.throwIfNegative(clanN, "clan < 0: " + clanN);
			if (clanN >= clans.size()) {
				throw new IllegalArgumentException("clan >= numPlayers : " + clanN);
			}
			c.setClan(clans.get(clanN));
			final var numPeople = dis.readInt();
			this.throwIfNegative(numPeople, "numPeople < 0: " + numPeople);
			c.setNumberOfPeople(numPeople);
			final var numSoldiers = dis.readInt();
			this.throwIfNegative(numSoldiers, "numSoldiers < 0: " + numSoldiers);
			c.setNumberOfSoldiers(numSoldiers);
			final var x = dis.readInt();
			this.throwIfNegative(x, "x < 0: " + x);
			c.setX(x);
			final var y = dis.readInt();
			this.throwIfNegative(y, "y < 0: " + y);
			c.setY(y);
			final var defense = dis.readInt();
			this.throwIfNegative(defense, "defense < 0: " + defense);
			c.setDefense(defense);
			final var bonus = dis.readDouble();
			this.throwIfNegative(bonus, "bonus < 0: " + bonus);
			c.setDefenseBonus(bonus);
			final var growth = dis.readDouble();
			this.throwIfNegative(growth, "growth < 0: " + growth);
			c.setGrowth(growth);
			final var cityN = dis.readUTF();
			c.setName(cityN);
			g._set(i, c.build());
			this.readConnections(dis, i, numCities, g);
			final List<Double> productions = new ArrayList<>();
			for (var j = 0; j < Resource.values().length; j++) {
				final var prodRate = dis.readDouble();
				this.throwIfNegative(prodRate, "prodRate < 0: " + prodRate);
				productions.add(prodRate);
			}
			c.setProductionRates(productions);
		}
		return g;
	}

	private void readConnections(final DataInput dis, final int i, final int numCities, final Graph<ICity> g) throws IOException {
		final int numConnections = dis.readShort();
		this.throwIfNegative(numConnections, "numConnections < 0: " + numConnections);
		for (var s = 0; s < numConnections; s++) {
			final int otherCityIndex = dis.readShort();
			this.throwIfNegative(otherCityIndex, "otherCityIndex < 0: " + otherCityIndex);
			if (otherCityIndex == i) {
				throw new IllegalArgumentException("Can't have a connection to itself!");
			} else if (otherCityIndex > numCities) {
				throw new IllegalArgumentException("Index out of range: " + otherCityIndex);
			}
			final var distanceToCity = dis.readDouble();
			this.throwIfNegative(distanceToCity, "distanceToCity < 0: " + distanceToCity);
			g.addDirectedEdge(otherCityIndex, i, distanceToCity, distanceToCity);
		}
	}

	private Graph<Integer> readRelations(final DataInput dis, final int numPlayers) throws IOException {
		final var numberOfRelations = dis.readInt();
		this.throwIfNegative(numberOfRelations,
			"Expected a non negative number of relations, got " + numberOfRelations);
		final var relations = new Graph<Integer>(numPlayers);
		for (var i = 0; i < numPlayers; i++) {
			relations.add(i);
		}
		for (var i = 0; i < numPlayers; i++) {
			for (var j = 0; j < numPlayers; j++) {
				if (i == j) {
					relations.addDirectedEdge(i, j, Double.MAX_VALUE, Double.MAX_VALUE);
				} else {
					relations.addDirectedEdge(i, j, 50, 50);
				}
			}
		}
		for (var i = 0; i < numberOfRelations; i++) {
			final var firstClan = dis.readInt();
			if (firstClan >= numPlayers) {
				throw new IllegalArgumentException("firstClan > numPlayers: " + firstClan);
			}
			this.throwIfNegative(firstClan, "firstClan < 0: " + firstClan);
			final var secondClan = dis.readInt();
			if (secondClan >= numPlayers) {
				throw new IllegalArgumentException("secondClan > numPlayers: " + secondClan);
			}
			this.throwIfNegative(secondClan, "secondClan < 0: " + secondClan);
			if (firstClan == secondClan) {
				throw new IllegalArgumentException("firstClan == secondClan: " + firstClan);
			}
			final var value = dis.readInt() % 101;
			this.throwIfNegative(value, "value < 0: " + value);
			relations.addDirectedEdge(firstClan, secondClan, value, value);
		}
		return relations;
	}

	private void readColors(final DataInput dis, final int numPlayers, final List<? extends IClan> clans) throws IOException {
		for (var i = 0; i < numPlayers; i++) {
			final var r = dis.readInt();
			this.throwIfNegative(r, "r < 0: " + r);
			if (r > 255) {
				throw new IllegalArgumentException("r to big: " + r);
			}
			final var g = dis.readInt();
			this.throwIfNegative(g, "g < 0: " + g);
			if (g > 255) {
				throw new IllegalArgumentException("g to big: " + g);
			}
			final var b = dis.readInt();
			this.throwIfNegative(b, "b < 0: " + b);
			if (b > 255) {
				throw new IllegalArgumentException("b to big: " + b);
			}
			clans.get(i).setColor(new Color(r, g, b));
		}
	}
=======
    /**
     * Read the inputfile and build an uninitialized game.
     *
     * @return An uninitialized game or null in case of an error.
     */
    @Override
    public ConquerInfo build() {
        final InputStream stream;
        try {
            stream = this.scenario.file() == null ? this.scenario.in()
                    : Files.newInputStream(Paths.get(new File(this.scenario.file()).toURI()));
        } catch (final IOException e) {
            Shared.LOGGER.exception(e);
            throw new RuntimeException(e);
        }
        return this.readFromStream(stream);
    }

    private ConquerInfo readFromStream(final InputStream stream) {
        final var game = new Game();
        try (final var dis = new DataInputStream(stream)) {
            final var mag1 = dis.read();
            final var mag2 = dis.read();
            if ((mag1 != 0xAA) || (mag2 != 0x55)) {
                throw new RuntimeException("Wrong magic number!");
            }
            final var numBytesOfBackgroundImage = dis.readInt();
            if (numBytesOfBackgroundImage < 0) {
                throw new RuntimeException(
                        "Expected a non negative number of bytes in background, got " + numBytesOfBackgroundImage);
            }
            final var data = new byte[numBytesOfBackgroundImage];
            var bytesRead = dis.read(data);
            if (bytesRead != numBytesOfBackgroundImage) {
                throw new RuntimeException(
                        "bytesRead: " + bytesRead + " != numBytesOfBackgroundImage: " + numBytesOfBackgroundImage);
            }
            final var gi = ImageIO.read(new ByteArrayInputStream(data));
            game.setBackground(gi);
            final var numPlayers = dis.readInt();
            if ((numPlayers == 1) || (numPlayers == 0)) {
                throw new RuntimeException("Expected at least 2 players, got " + numPlayers);
            } else if (numPlayers < 0) {
                throw new RuntimeException("Expected a non negative number of players, got " + numPlayers);
            }
            game.setPlayers(numPlayers);
            final List<IClan> tmp = new ArrayList<>();
            for (var i = 0; i < numPlayers; i++) {
                final var d = dis.readDouble();
                if (d < 0) {
                    throw new RuntimeException("Expected a non negative number of coins, got " + d);
                }
                tmp.add(new Clan());
                tmp.get(i).setCoins(d);
                ((Clan) tmp.get(i)).setInfo(game);
            }
            for (var i = 0; i < numPlayers; i++) {
                final var s = dis.readUTF();
                tmp.get(i).setId(i);
                tmp.get(i).setName(s);
            }
            for (var i = 0; i < numPlayers; i++) {
                final var flags = dis.readInt();
                tmp.get(i).setFlags(flags);
            }
            final List<Color> colors = new ArrayList<>();
            for (var i = 0; i < numPlayers; i++) {
                final var r = dis.readInt();
                if (r < 0) {
                    throw new RuntimeException("r < 0: " + r);
                } else if (r > 255) {
                    throw new RuntimeException("r to big: " + r);
                }
                final var g = dis.readInt();
                if (g < 0) {
                    throw new RuntimeException("g < 0: " + g);
                } else if (g > 255) {
                    throw new RuntimeException("g to big: " + g);
                }
                final var b = dis.readInt();
                if (b < 0) {
                    throw new RuntimeException("b < 0: " + b);
                } else if (b > 255) {
                    throw new RuntimeException("b to big: " + b);
                }
                colors.add(new Color(r, g, b));
                tmp.get(i).setColor(colors.get(i));
            }
            final var numberOfRelations = dis.readInt();
            if (numberOfRelations < 0) {
                throw new RuntimeException("Expected a non negative number of relations, got  " + numberOfRelations);
            }
            final var relations = new Graph<Integer>(numPlayers);
            for (var i = 0; i < numPlayers; i++) {
                relations.add(i);
            }
            for (var i = 0; i < numPlayers; i++) {
                for (var j = 0; j < numPlayers; j++) {
                    if (i == j) {
                        relations.addDirectedEdge(i, j, Double.MAX_VALUE, Double.MAX_VALUE);
                    } else {
                        relations.addDirectedEdge(i, j, 50, 50);
                    }
                }
            }
            for (var i = 0; i < numberOfRelations; i++) {
                final var firstClan = dis.readInt();
                if (firstClan >= numPlayers) {
                    throw new RuntimeException("firstClan > numPlayers: " + firstClan);
                }
                final var secondClan = dis.readInt();
                if (secondClan >= numPlayers) {
                    throw new RuntimeException("secondClan > numPlayers: " + secondClan);
                }
                final var value = dis.readInt() % 101;
                relations.addDirectedEdge(firstClan, secondClan, value, value);
            }
            game.setRelations(relations);
            final int numCities = dis.readShort();
            final var g = new Graph<ICity>(numCities);
            for (var i = 0; i < numCities; i++) {
                g.add(new City(game));
            }
            for (var i = 0; i < numCities; i++) {
                final var bytesOfPicture = dis.readInt();
                if (bytesOfPicture < 0) {
                    throw new RuntimeException(
                            "Expected a non negative number of bytes in image ,got " + bytesOfPicture);
                }
                final var pic = new byte[bytesOfPicture];
                bytesRead = dis.read(pic);
                if (bytesRead != bytesOfPicture) {
                    throw new RuntimeException("bytesRead: " + bytesRead + " != bytesOfPicture: " + bytesOfPicture);
                }
                final var c = new CityBuilder(game);
                c.setImage(ImageIO.read(new ByteArrayInputStream(pic)));
                final var clanN = dis.readInt();
                if (clanN < 0) {
                    throw new RuntimeException("clan < 0: " + clanN);
                }
                if (clanN >= numPlayers) {
                    throw new RuntimeException("clan >= numPlayers : " + clanN);
                }
                c.setClan(tmp.get(clanN));
                final var numPeople = dis.readInt();
                if (numPeople < 0) {
                    throw new RuntimeException("numPeople < 0: " + numPeople);
                }
                c.setNumberOfPeople(numPeople);
                final var numSoldiers = dis.readInt();
                if (numSoldiers < 0) {
                    throw new RuntimeException("numSoldiers < 0: " + numSoldiers);
                }
                c.setNumberOfSoldiers(numSoldiers);
                final var x = dis.readInt();
                if (x < 0) {
                    throw new RuntimeException("x < 0: " + x);
                }
                c.setX(x);
                final var y = dis.readInt();
                if (y < 0) {
                    throw new RuntimeException("y < 0: " + y);
                }
                c.setY(y);
                final var defense = dis.readInt();
                if (defense < 0) {
                    throw new RuntimeException("defense < 0: " + defense);
                }
                c.setDefense(defense);
                final var bonus = dis.readDouble();
                if (bonus < 0) {
                    throw new RuntimeException("bonus < 0: " + bonus);

                }
                c.setDefenseBonus(bonus);
                final var growth = dis.readDouble();
                if (growth < 0) {
                    throw new RuntimeException("growth < 0: " + growth);
                }
                c.setGrowth(growth);
                final var cityN = dis.readUTF();
                c.setName(cityN);
                g._set(i, c.build());
                final int numConnections = dis.readShort();
                for (var s = 0; s < numConnections; s++) {
                    final int otherCityIndex = dis.readShort();
                    if (otherCityIndex < 0) {
                        throw new RuntimeException("otherCityIndex < 0: " + otherCityIndex);
                    } else if (otherCityIndex == i) {
                        throw new RuntimeException("Can't have a connection to itself!");

                    } else if (otherCityIndex > numCities) {
                        throw new RuntimeException("Index out of range: " + otherCityIndex);
                    }
                    final var distanceToCity = dis.readDouble();
                    if (distanceToCity < 0) {
                        throw new RuntimeException("distanceToCity < 0: " + distanceToCity);
                    }
                    g.addDirectedEdge(otherCityIndex, i, distanceToCity, distanceToCity);
                }
                final List<Double> productions = new ArrayList<>();
                for (var j = 0; j < Resource.values().length; j++) {
                    final var prodRate = dis.readDouble();
                    if (prodRate < 0) {
                        throw new RuntimeException("prodRate < 0: " + prodRate);
                    }
                    productions.add(prodRate);
                }
                c.setProductionRates(productions);
            }
            game.setClans(tmp);
            game.setGraph(g);
        } catch (final IOException ioe) {
            Shared.LOGGER.exception(ioe);
            throw new RuntimeException(ioe);
        }
        if (!game.getCities().isConnected()) {
            throw new RuntimeException("Disconnected graph!");
        }
        return game;
    }
>>>>>>> parent of f8bbb68 (Formatting)

    public ConquerInfo read(final InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("in==null");
        }
        return this.readFromStream(in);
    }

}
