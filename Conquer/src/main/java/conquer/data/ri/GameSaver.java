package conquer.data.ri;

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
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;

import conquer.data.ConquerInfo;
import conquer.data.ConquerSaver;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.Shared;
import conquer.data.StreamUtils;
import conquer.data.strategy.Strategy;
import conquer.plugins.Plugin;
import conquer.utils.Graph;
import conquer.utils.Triple;

public final class GameSaver implements ConquerSaver {
	private final String name;

	public GameSaver(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("name == null");
		} else if (name.isEmpty()) {
			throw new IllegalArgumentException("name.isEmpty() == true");
		}
		this.name = name;
	}

	private byte[] createSha(final File file) throws NoSuchAlgorithmException, IOException {
		final var digest = MessageDigest.getInstance("SHA-512");
		try (final InputStream fis = new FileInputStream(file)) {
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
		try (final var baos = new ByteArrayOutputStream(4096 * 4096)) {
			ImageIO.write(this.toBufferedImage(image), "png", baos);
			return baos.toByteArray();
		}
	}

	private void readCities(final DataInputStream dis, final Game game) throws IOException {
		final var numberOfCities = dis.readInt();
		final var cities = new Graph<ICity>(numberOfCities);
		for (var i = 0; i < numberOfCities; i++) {
			final var city = new CityBuilder(game);
			city.setName(dis.readUTF());
			city.setDefenseBonus(dis.readDouble());
			city.setId(dis.readInt());
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
			cities.add(city.build());
		}
		final var n = dis.readInt();
		for (var i = 0; i < n; i++) {
			cities.addUndirectedEdge(dis.readInt(), dis.readInt(), dis.readDouble());
		}
		game.setGraph(cities);
	}

	private Clan readClan(final File file, final Game game) throws IOException, InstantiationException,
		IllegalAccessException, InvocationTargetException,
		NoSuchMethodException, ClassNotFoundException {
		try (final var dis = new DataInputStream(new FileInputStream(file))) {
			final var clan = new Clan();
			clan.setId(dis.readInt());
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
			clan.setFlags(dis.readInt());
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

	private List<IClan> readClans(final File saveDirectory, final Game game) throws IOException,
		InstantiationException,
		IllegalAccessException,
		InvocationTargetException,
		NoSuchMethodException,
		ClassNotFoundException {
		final List<IClan> ret = new ArrayList<>();
		final var files = saveDirectory.listFiles(pathname -> pathname.getAbsolutePath()
			.replace(pathname.getParentFile().getAbsolutePath(), "").matches(".*\\.clan\\.save$"));
		for (final var file : files) {
			ret.add(this.readClan(file, game));
		}
		return ret.stream().sorted(Comparator.comparingInt(IClan::getId)).toList();
	}

	private List<Plugin> readPlugins(final File saveDirectory, final Game game)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
		ClassNotFoundException, IOException {
		final List<Plugin> ret = new ArrayList<>();
		final var files = saveDirectory.listFiles(pathname -> pathname.getAbsolutePath()
			.replace(pathname.getParentFile().getAbsolutePath(), "").matches(".*\\.plugin\\.save$"));
		for (final var file : files) {
			final var pluginName = file.getName().replaceAll("\\.plugin\\.save$", "");
			final var plugin = (Plugin) Class.forName(pluginName).getConstructor().newInstance();
			try (final var stream = Files.newInputStream(Paths.get(file.toURI()))) {
				plugin.resume(game, stream);
			}
			ret.add(plugin);
		}
		return ret;
	}

	@Override
	public ConquerInfo restore() throws Exception {
		final var game = new Game();
		final var saveDirectory = new File(Shared.SAVE_DIRECTORY, this.name);
		this.restore(game, saveDirectory);
		game.setClans(this.readClans(saveDirectory, game));
		StreamUtils.getCitiesAsStream(game.getCities()).forEach(a -> a.setClan(game.getClan(a.getClanId())));
		final var plugins = this.readPlugins(saveDirectory, game);
		game.setPlugins(plugins);
		game.getClans().forEach(a -> ((Clan) a).setInfo(game));
		game.resume(this.name);
		return game;
	}

	private void restore(final Game game, final File saveDirectory) throws IOException {
		try (final var dis =
				 new DataInputStream(new FileInputStream(new File(saveDirectory, this.name + ".game.save")))) {
			final var imageLen = dis.readInt();
			final var bytes = dis.readNBytes(imageLen);
			final var image = ImageIO.read(new ByteArrayInputStream(bytes));
			game.setBackground(image);
			game.setPlayers(dis.readInt());
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

	private void save(final IClan clan, final OutputStream outputStream) throws IOException {
		try (final var dos = new DataOutputStream(outputStream)) {
			dos.writeInt(clan.getId());
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

	@Override
	public void save(final ConquerInfo info) throws Exception {
		if (!(info instanceof conquer.data.ri.Game game)) {
			throw new UnsupportedOperationException("Can't save instanceof " + info.getClass().getCanonicalName());
		}
		final var saveDirectory = new File(Shared.SAVE_DIRECTORY, this.name);
		try {
			Shared.deleteDirectory(saveDirectory);
		} catch (final IOException e) {
			// Nothing critical
			Shared.LOGGER.message("Nothing critical!");
			Shared.LOGGER.exception(e);
		}
		try {
			Files.createDirectories(Paths.get(saveDirectory.toURI()));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		for (final var plugin : game.getPlugins()) {
			final var file = new File(saveDirectory, plugin.getClass().getCanonicalName() + ".plugin.save");
			try (final var outputStream = Files.newOutputStream(Paths.get(file.toURI()))) {
				plugin.save(outputStream);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		for (final IClan clan : game.getClans()) {
			final var file = new File(saveDirectory, clan.getName() + ".clan.save");
			try (final var outputStream = Files.newOutputStream(Paths.get(file.toURI()))) {
				this.save(clan, outputStream);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			this.save(game, saveDirectory);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		Files.write(Paths.get(saveDirectory.getAbsolutePath(), "classname"),
			this.getClass().getCanonicalName().getBytes());
		final var files = saveDirectory.listFiles();
		try (final var dos = new DataOutputStream(new FileOutputStream(new File(saveDirectory, "hashes")))) {
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
		try (final var dos = new DataOutputStream(Files.newOutputStream(Paths.get(gameFile.toURI())))) {
			final var imageData = this.extractBytes(game.getBackground());
			dos.writeInt(imageData.length);
			dos.write(imageData);
			dos.writeInt(game.getNumPlayers());
			dos.writeInt(game.currentRound());
			this.writeCities(game, dos);
			final var relations = game.getRelations();
			final var v = relations.getConnections();
			var cnt = 0;
			for (final Triple<Integer, Integer, Double> element : v) {
				final var value = element.third();
				if ((value == -1) || (value == -2)) {
					continue;
				}
				cnt++;
			}
			dos.writeInt(cnt);
			v.forEach(a -> {
				try {
					final var value = a.third();
					if ((value == -1) || (value == -2)) {
						return;
					}
					dos.writeInt(a.first());
					dos.writeInt(a.second());
					dos.writeDouble(value);
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
		dos.writeInt(cities.getValues(new ICity[0]).length);
		StreamUtils.getCitiesAsStream(cities).forEach(a -> {
			if (a instanceof City c1) {
				try {
					dos.writeUTF(a.getName());
					dos.writeDouble(a.getBonus());
					dos.writeInt(a.getClanId());
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
					dos.writeLong(c1.getNumberAttacksOfPlayer());
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
					dos.writeInt(c1.getNumberOfRoundsWithZeroPeople());
					dos.writeDouble(c1.oldOne());
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new UnsupportedOperationException("Wrong class!");
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
