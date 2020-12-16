package org.jel.game.data;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jel.game.data.strategy.Strategy;
import org.jel.game.plugins.Plugin;
import org.jel.game.utils.Graph;

public class SavedGame {
	private final String name;

	public SavedGame(final String name) {
		this.name = name;
	}

	private byte[] createSha(final File file) throws NoSuchAlgorithmException, IOException {
		final var digest = MessageDigest.getInstance("SHA-3");
		try (InputStream fis = new FileInputStream(file)) {
			var n = 0;
			final var buffer = new byte[8192];
			while (n != -1) {
				n = fis.read(buffer);
				if (n > 0) {
					digest.update(buffer, 0, n);
				}
			}
		}
		return digest.digest();
	}

	private byte[] extractBytes(final Image image) throws IOException {
		try (var baos = new ByteArrayOutputStream(4096 * 4096)) {
			ImageIO.write(this.toBufferedImage(image), "png", baos);
			return baos.toByteArray();
		}
	}

	private void readCities(final DataInputStream dis, final Game game) throws IOException {
		final var cities = new Graph<City>(game.getNumPlayers());
		final var numberOfCities = dis.readInt();
		for (var i = 0; i < numberOfCities; i++) {
			final var city = new City(game);
			city.setName(dis.readUTF());
			city.setDefenseBonus(dis.readDouble());
			city.setClan(game.getClan(dis.read()));
			city.setDefense(dis.readDouble());
			city.setGrowth(dis.readDouble());
			final var imageLen = dis.readInt();
			final var bytes = dis.readNBytes(imageLen);
			final var image = ImageIO.read(new ByteArrayInputStream(bytes));
			city.setImage(image);
			final var numLevels = dis.read();
			final List<Integer> levels = new ArrayList<>();
			for (var j = 0; j < numLevels; j++) {
				levels.add(dis.readInt());
			}
			city.setLevels(levels);
			city.setAttacksOfPlayer(dis.readLong());
			city.setNumberOfPeople(dis.readLong());
			city.setNumberOfSoldiers(dis.readLong());
			final var numProds = dis.read();
			final List<Double> productions = new ArrayList<>();
			for (var j = 0; j < numProds; j++) {
				productions.add(dis.readDouble());
			}
			city.setProductionRates(productions);
			city.setX(dis.readInt());
			city.setY(dis.readInt());
			city.setNumberOfRoundsWithZeroPeople(dis.readInt());
			city.setOldValue(dis.readDouble());
			cities.add(city);
		}
		final var n = dis.readInt();
		for (var i = 0; i < n; i++) {
			cities.addUndirectedEdge(dis.readInt(), dis.readInt(), dis.readDouble());
		}
		game.setGraph(cities);
	}

	private Clan readClan(final File file, final Game game) throws IOException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		try (var dis = new DataInputStream(new FileInputStream(file))) {
			final var clan = new Clan();
			clan.setId(dis.read());
			clan.setName(dis.readUTF());
			clan.setCoins(dis.readDouble());
			clan.setColor(new Color(dis.read(), dis.read(), dis.read()));
			final var numResources = dis.read();
			final List<Double> resources = new ArrayList<>();
			for (var i = 0; i < numResources; i++) {
				resources.add(dis.readDouble());
			}
			clan.setResources(resources);
			final var numStats = dis.read();
			final List<Double> stats = new ArrayList<>();
			for (var i = 0; i < numStats; i++) {
				stats.add(dis.readDouble());
			}
			clan.setResourceStats(stats);
			clan.setSoldiersStrength(dis.readDouble());
			clan.setSoldiersDefenseStrength(dis.readDouble());
			clan.setSoldiersOffenseStrength(dis.readDouble());
			clan.setSoldiersLevel(dis.readInt());
			clan.setSoldiersDefenseLevel(dis.readInt());
			clan.setSoldiersOffenseLevel(dis.readInt());
			clan.setFlags(dis.read());
			final var hasStrategyData = dis.readBoolean();
			byte[] dataBytes = null;
			if (hasStrategyData) {
				final var n = dis.readInt();
				dataBytes = dis.readNBytes(n);
			}
			final var n = dis.readInt();
			final var bytes = dis.readNBytes(n);
			final var className = dis.readUTF();
			final var strategy = (Strategy) Class.forName(className).getConstructor().newInstance();
			final var strategyData = strategy.resume(game, bytes, hasStrategyData, dataBytes);
			clan.setStrategy(strategy);
			clan.setStrategyData(strategyData);
			return clan;
		}
	}

	private List<Clan> readClans(final File saveDirectory, final Game game) throws IOException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		final List<Clan> ret = new ArrayList<>();
		final var files = saveDirectory.listFiles(pathname -> pathname.getAbsolutePath()
				.replace(pathname.getParentFile().getAbsolutePath(), "").matches(".*\\.clan\\.save$"));
		for (final var file : files) {
			ret.add(this.readClan(file, game));
		}
		return ret.stream().sorted((a, b) -> Integer.compare(a.getId(), b.getId())).collect(Collectors.toList());
	}

	private List<Plugin> readPlugins(final File saveDirectory, final Game game)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException, IOException {
		final List<Plugin> ret = new ArrayList<>();
		final var files = saveDirectory.listFiles(pathname -> pathname.getAbsolutePath()
				.replace(pathname.getParentFile().getAbsolutePath(), "").matches(".*\\.plugin\\.save$"));
		for (final var file : files) {
			final var pluginName = file.getName();
			final var plugin = (Plugin) Class.forName(pluginName).getConstructor().newInstance();
			plugin.resume(game, Files.newInputStream(Paths.get(file.toURI())));
			ret.add(plugin);
		}
		return ret;
	}

	public Game restore() throws Exception {
		final var game = new Game();
		final var saveDirectory = new File(new File(Shared.BASE_DIRECTORY, "saves"), this.name);
		this.restore(game, saveDirectory);
		game.setClans(this.readClans(saveDirectory, game));
		final var plugins = this.readPlugins(saveDirectory, game);
		game.setPlugins(plugins);
		game.resume(this.name);
		return game;
	}

	private void restore(final Game game, final File saveDirectory) throws IOException {
		try (var dis = new DataInputStream(new FileInputStream(new File(saveDirectory, this.name + ".game.save")))) {
			final var imageLen = dis.readInt();
			final var bytes = dis.readNBytes(imageLen);
			final var image = ImageIO.read(new ByteArrayInputStream(bytes));
			game.setBackground(image);
			game.setPlayers((byte) dis.read());
			game.setRound(dis.readInt());
			this.readCities(dis, game);
			final var relations = new Graph<Integer>(game.getNumPlayers());
			final var n = dis.readInt();
			for (var i = 0; i < game.getNumPlayers(); i++) {
				relations.add(i);
			}
			for (var i = 0; i < n; i++) {
				relations.addUndirectedEdge(dis.readInt(), dis.readInt(), dis.readDouble());
			}
			game.setRelations(relations);
		}
	}

	private void save(final Clan clan, final OutputStream outputStream) throws IOException {
		try (var dos = new DataOutputStream(outputStream)) {
			dos.write(clan.getId());
			dos.writeUTF(clan.getName());
			dos.writeDouble(clan.getCoins());
			final var color = clan.getColor();
			dos.write(color.getRed());
			dos.write(color.getGreen());
			dos.write(color.getBlue());
			final var resources = clan.getResources();
			dos.write(resources.size());
			resources.forEach(t -> {
				try {
					dos.writeDouble(t);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			});
			final var stats = clan.getResourceStats();
			dos.write(stats.size());
			stats.forEach(t -> {
				try {
					dos.writeDouble(t);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			});
			dos.writeDouble(clan.getSoldiersStrength());
			dos.writeDouble(clan.getSoldiersDefenseStrength());
			dos.writeDouble(clan.getSoldiersOffenseStrength());
			dos.writeInt(clan.getSoldiersLevel());
			dos.writeInt(clan.getSoldiersDefenseLevel());
			dos.writeInt(clan.getSoldiersOffenseLevel());
			dos.writeInt(clan.getFlags());
			dos.writeBoolean(clan.getData() != null);
			if (clan.getData() != null) {
				try (final var out = new ByteArrayOutputStream(512)) {
					clan.getData().save(out);
					final var bytes = out.toByteArray();
					dos.writeInt(bytes.length);
					dos.write(bytes);
				}
			}
			try (final var out = new ByteArrayOutputStream(4096)) {
				clan.getStrategy().save(out);
				final var bytes = out.toByteArray();
				dos.writeInt(bytes.length);
				dos.write(bytes);
				dos.writeUTF(clan.getStrategy().getClass().getCanonicalName());
			}
		}
	}

	public void save(final Game game) throws Exception {
		final var saveDirectory = new File(new File(Shared.BASE_DIRECTORY, "saves"), this.name);
		try {
			Shared.deleteDirectory(saveDirectory);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		saveDirectory.mkdirs();
		for (final var plugin : game.getPlugins()) {
			try (final var outputStream = new FileOutputStream(
					new File(saveDirectory, plugin.getClass().getCanonicalName() + ".plugin.save"))) {
				plugin.save(outputStream);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		for (final var clan : game.getClans()) {
			try (final var outputStream = new FileOutputStream(
					new File(saveDirectory, clan.getName() + ".clan.save"))) {
				this.save(clan, outputStream);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			this.save(game, saveDirectory);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		final var files = saveDirectory.listFiles();
		try (var dos = new DataOutputStream(new FileOutputStream(new File(saveDirectory, "hashes")))) {
			dos.writeInt(files.length);
			for (final var file : files) {
				dos.writeUTF(file.getAbsolutePath());
				final var bytes = this.createSha(file);
				dos.writeInt(bytes.length);
				dos.write(bytes);
			}
		}
	}

	private void save(final Game game, final File saveDirectory) throws IOException {
		final var gameFile = new File(saveDirectory, this.name + ".game.save");
		try (var dos = new DataOutputStream(new FileOutputStream(gameFile))) {
			final var imageData = this.extractBytes(game.getBackground());
			dos.writeInt(imageData.length);
			dos.write(imageData);
			dos.write(game.getNumPlayers());
			dos.writeInt(game.currentRound());
			this.writeCities(game, dos);
			final var relations = game.getRelations();
			final var v = relations.getConnections();
			dos.writeInt(v.size());
			v.forEach(a -> {
				try {
					dos.writeInt(a.first());
					dos.writeInt(a.second());
					dos.writeDouble(a.third());
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	private BufferedImage toBufferedImage(final Image src) {
		if (src instanceof BufferedImage) {
			return (BufferedImage) src;
		}
		final var image = new BufferedImage(src.getWidth(null), src.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		final var g = image.createGraphics();
		g.drawImage(src, 0, 0, null);
		g.dispose();
		return image;
	}

	private void writeCities(final Game game, final DataOutputStream dos) throws IOException {
		final var cities = game.getCities();
		dos.writeInt(cities.getValues(new City[0]).length);
		StreamUtils.getCitiesAsStream(cities).forEach(a -> {
			try {
				dos.writeUTF(a.getName());
				dos.writeDouble(a.getBonus());
				dos.write(a.getClanId());
				dos.writeDouble(a.getDefense());
				dos.writeDouble(a.getGrowth());
				final var imageData = this.extractBytes(a.getImage());
				dos.writeInt(imageData.length);
				dos.write(imageData);
				final var levels = a.getLevels();
				dos.write(levels.size());
				levels.forEach(t -> {
					try {
						dos.writeInt(t);
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				});
				dos.writeLong(a.getNumberAttacksOfPlayer());
				dos.writeLong(a.getNumberOfPeople());
				dos.writeLong(a.getNumberOfSoldiers());
				final var productions = a.getProductions();
				dos.write(productions.size());
				productions.forEach(t -> {
					try {
						dos.writeDouble(t);
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				});
				dos.writeInt(a.getX());
				dos.writeInt(a.getY());
				dos.writeInt(a.getNumberOfRoundsWithZeroPeople());
				dos.writeDouble(a.oldOne());
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});
		final var v = cities.getConnections();
		dos.writeInt(v.size());
		v.forEach(a -> {
			try {
				dos.writeInt(a.first());
				dos.writeInt(a.second());
				dos.writeDouble(a.third());
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
