package org.jel.game.data;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jel.game.data.builtin.DefensiveStrategyProvider;
import org.jel.game.data.builtin.ModerateStrategyProvider;
import org.jel.game.data.builtin.OffensiveStrategyProvider;
import org.jel.game.data.builtin.RandomStrategyProvider;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.data.strategy.StrategyProvider;
import org.jel.game.messages.AnnihilationMessage;
import org.jel.game.messages.AttackLostMessage;
import org.jel.game.messages.BetterRelationshipMessage;
import org.jel.game.messages.ConquerMessage;
import org.jel.game.messages.RandomEvent;
import org.jel.game.messages.RandomEventMessage;
import org.jel.game.messages.WorseRelationshipMessage;
import org.jel.game.plugins.AttackHook;
import org.jel.game.plugins.CityKeyHandler;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.KeyHandler;
import org.jel.game.plugins.MessageListener;
import org.jel.game.plugins.MoneyHook;
import org.jel.game.plugins.MoveHook;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.plugins.RecruitHook;
import org.jel.game.plugins.ResourceHook;
import org.jel.game.utils.Graph;

/**
 * One of the most important classes as it combines all together.
 */
public final class Game implements PluginInterface, StrategyObject {
	private static final int MAX_LEVEL = 1000;
	private static final double GROWTH_REDUCE_FACTOR = 0.95;
	private static final double GROWTH_LIMIT = 1.10;
	private static final double WEAK_GROWTH_REDUCE_FACTOR = 0.9;
	private static final double ALTERNATIVE_GROWTH_LIMIT = 1.075;
	private static final int SOFT_POPULATION_LIMIT = 1_000_000;
	private static final int MAX_SELECTOR_VALUE = 5000;
	private static final int RETAINED_PEOPLE = 5;
	private static final int FALLBACK_POPULATION = 15;
	private static final int MAXIMUM_SURVIVING_PEOPLE_CONQUERED = 60;
	private static final int MINIMUM_SURVIVING_PEOPLE_CITY_CONQUERED = 20;
	private static final int MAXIMUM_SURVIVING_PEOPLE_ALL_DEAD = 80;
	private static final int MINIMUM_SURVIVING_PEOPLE_ALL_DEAD = 68;
	private static final int MAXIMUM_SURVIVING_PEOPLE_ATTACK_DEFEATED = 90;
	private static final int MINIMUM_SURVIVING_PEOPLE_ATTACK_DEFEATED = 80;
	private static final int RELATIONSHIP_CHANGE_CITY_CONQUERED = 10;
	private static final double RELATIONSHIP_CHANGE_ALL_DEAD = 7.5;
	private static final int RELATIONSHIP_CHANGE_ATTACK_DEFEATED = 5;
	private final Random random = new SecureRandom();
	private List<Clan> clans;
	private Image background;
	private Graph<City> cities;
	private final EventList events = new EventList();
	private final StrategyProvider[] strategies;
	private boolean isPlayersTurn = true;
	private byte numPlayers = -1;
	private Graph<Integer> relations;
	private int currentRound = 1;
	private final GamePluginData data = new GamePluginData();
	private PlayerGiftCallback playerGiftCallback;
	private boolean resumed;
	private File directory;

	Game() {
		this.data.setRecruitHooks(new ArrayList<>());
		this.data.setAttackHooks(new ArrayList<>());
		this.data.setMoveHooks(new ArrayList<>());
		this.data.setResourceHooks(new ArrayList<>());
		this.data.setMoneyHooks(new ArrayList<>());
		this.data.setCityKeyHandlers(new HashMap<>());
		this.data.setExtraMusic(new ArrayList<>());
		this.data.setKeybindings(new HashMap<>());
		this.strategies = new StrategyProvider[Byte.MAX_VALUE];
		this.strategies[0] = new DefensiveStrategyProvider();
		this.strategies[1] = new ModerateStrategyProvider();
		this.strategies[2] = new OffensiveStrategyProvider();
		this.strategies[3] = new RandomStrategyProvider();
	}

	@Override
	public void addAttackHook(final AttackHook ah) {
		this.throwIfNull(ah);
		this.data.getAttackHooks().add(ah);
	}

	@Override
	public void addCityKeyHandler(final String key, final CityKeyHandler ckh) {
		this.throwIfNull(key, "key==null");
		this.throwIfNull(ckh, "ckh==null");
		if (this.data.getCityKeyHandlers().containsKey(key)) {
			Shared.LOGGER.warning("Overwriting city key binding for key: \"" + key + "\"!");
		}
		this.data.getCityKeyHandlers().put(key, ckh);
	}

	/**
	 * Add a context in order to initialise strategies and other things.
	 *
	 * @param context The context obtained by {@link XMLReader#readInfo()}
	 */
	public void addContext(final GlobalContext context) {
		this.throwIfNull(context, "context==null");
		this.throwIfNull(context.getPlugins(), "context.getPlugins()==null");
		this.throwIfNull(context.getStrategies(), "context.getStrategies()==null");
		this.throwIfNull(context.getInstalledMaps(), "context.getInstalledMaps()==null");
		this.data.setPlugins(context.getPlugins());
		context.getStrategies().forEach(st -> {
			final int idx = st.getId();
			if (idx >= 127) {
				Shared.LOGGER.error("idx >= 127");
			} else if (idx < 0) {
				Shared.LOGGER.error("idx < 0");
			} else if ((this.strategies[idx] != null)) {
				Shared.LOGGER.error("Slot " + idx + " is already set!");
			} else {
				this.strategies[idx] = st;
				Shared.logLevel1(st.getName() + " has index " + idx);
			}
		});
	}

	@Override
	public void addKeyHandler(final String key, final KeyHandler handler) {
		this.throwIfNull(key, "key==null");
		this.throwIfNull(handler, "handler==null");
		if (this.data.getKeybindings().containsKey(key)) {
			Shared.LOGGER.warning("Overwriting key binding for key: \"" + key + "\"!");
		}
		this.data.getKeybindings().put(key, handler);
	}

	@Override
	public void addMessageListener(MessageListener ml) {
		this.throwIfNull(ml);
		this.events.addListener(ml);

	}

	@Override
	public void addMoneyHook(final MoneyHook mh) {
		this.throwIfNull(mh);
		this.data.getMoneyHooks().add(mh);
	}

	@Override
	public void addMoveHook(final MoveHook mh) {
		this.throwIfNull(mh);
		this.data.getMoveHooks().add(mh);
	}

	@Override
	public void addMusic(final String fileName) {
		this.throwIfNull(fileName);
		this.data.getExtraMusic().add(fileName);
	}

	@Override
	public void addRecruitHook(final RecruitHook rh) {
		this.throwIfNull(rh);
		this.data.getRecruitHooks().add(rh);
	}

	@Override
	public void addResourceHook(final ResourceHook rh) {
		this.throwIfNull(rh);
		this.data.getResourceHooks().add(rh);
	}

	private long aiCalculateNumberOfTroopsToAttackWith(final City src, final byte clan, final City destination) {
		var powerOfAttacker = src.getNumberOfSoldiers();
		if (powerOfAttacker == 0) {
			return 0;
		}
		powerOfAttacker = this.maximumNumberToMove(clan, this.getCities().getWeight(src, destination), powerOfAttacker);
		return powerOfAttacker;
	}

	@Override
	public void attack(final City src, final City destination, final byte clan, final boolean managed, final long num) {
		this.throwIfNull(src, "src==null");
		this.throwIfNull(destination, "destination==null");
		this.checkClan(clan);
		this.attack(src, destination, clan, managed, num, true);
	}

	@Override
	public void attack(final City src, final City destination, final byte clan, final boolean managed, final long num,
			final boolean reallyPlayer) {
		this.throwIfNull(src, "src==null");
		this.throwIfNull(destination, "destination==null");
		this.checkClan(clan);
		if (reallyPlayer && (src.getClan() != Shared.PLAYER_CLAN)) {
			throw new IllegalArgumentException(
					"reallyPlayer is true, but the source city is not clan 0: " + src.getClan());
		}
		if (this.cantAttack(src, destination)) {
			return;
		}
		final var powerOfAttacker = this.calculatePowerOfAttacker(src, clan, destination, managed, reallyPlayer, num);
		if ((powerOfAttacker == 0) || ((src.getClan() != 0) && (powerOfAttacker == 1))) {
			return;
		}
		final var diff = this.setup(clan, powerOfAttacker, src, destination);
		src.setNumberOfSoldiers(src.getNumberOfSoldiers() - powerOfAttacker);
		this.data.getAttackHooks().forEach(a -> a.before(src, destination, powerOfAttacker));
		var relationshipValue = this.getRelationship(src.getClan(), destination.getClan());
		double numberOfSurvivingPeople;
		final var destinationClan = destination.getClan();
		long survivingSoldiers;
		AttackResult result;
		if (diff > 0) {// Attack was defeated
			final var destinationClanObj = this.getClan(destination);
			var remainingSoldiersDefender = (diff - destination.getDefense());
			remainingSoldiersDefender /= destination.getBonus();
			remainingSoldiersDefender /= destinationClanObj.getSoldiersDefenseStrength();
			remainingSoldiersDefender /= destinationClanObj.getSoldiersStrength();
			var surviving = (long) Math.abs(remainingSoldiersDefender);
			final var numSoldiers = destination.getNumberOfSoldiers();
			if (numSoldiers == 0) {
				surviving = 0;
			} else if (numSoldiers < surviving) {
				surviving = numSoldiers;
			}
			destination.setNumberOfSoldiers(surviving);
			this.events.add(new AttackLostMessage(src, destination, powerOfAttacker));
			relationshipValue -= Game.RELATIONSHIP_CHANGE_ATTACK_DEFEATED;
			numberOfSurvivingPeople = Shared.randomPercentage(Game.MINIMUM_SURVIVING_PEOPLE_ATTACK_DEFEATED,
					Game.MAXIMUM_SURVIVING_PEOPLE_ATTACK_DEFEATED);
			survivingSoldiers = surviving;
			result = AttackResult.ATTACK_DEFEATED;
		} else if (diff == 0) {// All soldiers are dead
			destination.setNumberOfSoldiers(0);
			this.events.add(new AnnihilationMessage(src, destination, powerOfAttacker));
			relationshipValue -= Game.RELATIONSHIP_CHANGE_ALL_DEAD;
			numberOfSurvivingPeople = Shared.randomPercentage(Game.MINIMUM_SURVIVING_PEOPLE_ALL_DEAD,
					Game.MAXIMUM_SURVIVING_PEOPLE_ALL_DEAD);
			survivingSoldiers = 0;
			result = AttackResult.ALL_SOLDIERS_DEAD;
		} else {// Conquered
			var cleanedDiff = diff;
			final var srcClan = this.getClan(src);
			cleanedDiff /= srcClan.getSoldiersOffenseStrength();
			cleanedDiff /= srcClan.getSoldiersStrength();
			destination.setNumberOfSoldiers((long) -cleanedDiff);
			relationshipValue -= Game.RELATIONSHIP_CHANGE_CITY_CONQUERED;
			numberOfSurvivingPeople = Shared.randomPercentage(Game.MINIMUM_SURVIVING_PEOPLE_CITY_CONQUERED,
					Game.MAXIMUM_SURVIVING_PEOPLE_CONQUERED);
			survivingSoldiers = (long) -cleanedDiff;
			result = AttackResult.CITY_CONQUERED;
			this.events.add(new ConquerMessage(src, destination, powerOfAttacker));
			destination.setClan(this.getClan(src));
		}
		if (relationshipValue < 0) {
			relationshipValue = 0;
		}
		destination.setNumberOfPeople((long) (destination.getNumberOfPeople() * numberOfSurvivingPeople));
		this.getRelations().addDirectedEdge(src.getClan(), destinationClan, relationshipValue, relationshipValue);
		this.data.getAttackHooks().forEach(a -> a.after(src, destination, survivingSoldiers, result));
	}

	private long calculatePowerOfAttacker(final City src, final byte clan, final City destination,
			final boolean managed, final boolean reallyPlayer, final long numberOfSoldiers) {
		if (!managed) {
			return this.aiCalculateNumberOfTroopsToAttackWith(src, clan, destination);
		} else {
			if (reallyPlayer) {
				destination.attackByPlayer();
			}
			return numberOfSoldiers;
		}
	}

	private double calculatePowerOfDefender(final City city) {
		final var clan = this.clans.get(city.getClan());
		return city.getDefense() + (city.getNumberOfSoldiers() * city.getBonus() * clan.getSoldiersDefenseStrength()
				* clan.getSoldiersStrength());
	}

	/**
	 * Calculate whether the player won or lost.
	 *
	 * @return
	 */
	public Result calculateResult() {
		return StreamUtils.getCitiesAsStream(this.getCities(), Shared.PLAYER_CLAN).count() == 0 ? Result.CPU_WON
				: Result.PLAYER_WON;
	}

	private boolean cantAttack(final City src, final City destination) {
		return (src.getClan() == destination.getClan()) || (src == destination)
				|| (!this.cities.isConnected(src, destination));
	}

	private void checkClan(final int clan) {
		if (clan < 0) {
			throw new IllegalArgumentException("clan < 0: " + clan);
		} else if (clan >= this.clans.size()) {
			throw new IllegalArgumentException("clan >= this.clans.size(): " + clan);
		}
	}

	private void cpuPlay() {
		// Skip clan of the player
		final var order = IntStream.range(Shared.PLAYER_CLAN + 1, this.getNumPlayers()).boxed()
				.collect(Collectors.toList());
		Collections.shuffle(order);
		order.forEach(this::executeCPUPlay);
		this.isPlayersTurn = true;
	}

	/**
	 * Returns the current round.
	 *
	 * @return Current round.
	 */
	public int currentRound() {
		return this.currentRound;
	}

	@Override
	public double defenseStrengthOfCity(final City c) {
		this.throwIfNull(c, "c==null");
		final var clan = this.clans.get(c.getClan());
		return c.getDefenseStrength(clan);
	}

	private void eval(int selector, int clanOne, int clanTwo, Random r) {
		if ((selector >= 4500) && (selector < 4650)) {
			this.worseRelationship(r, clanOne, clanTwo);
		} else if ((selector >= 4500) && (selector < 4700)) {
			this.improveRelationship(r, clanOne, clanTwo);
		} else if (selector >= 4500) {
			final var newValue = this.relations.getWeight(clanOne, clanTwo)
					+ (Math.random() > 0.5 ? Math.random() : -Math.random());
			final var clampedToZero = newValue < 0 ? 0 : newValue;
			final var clampedToHundred = clampedToZero > 100 ? 100 : clampedToZero;
			this.relations.addDirectedEdge(clanOne, clanTwo, clampedToHundred, clampedToHundred);
		}

	}

	private void events() {
		Stream.of(this.getCities().getValues(new City[0])).forEach(a -> {
			final var number = this.random.nextInt(20_000_000);
			var factorOfPeople = 1.0;
			var factorOfSoldiers = 1.0;
			var growthFactor = 1.0;
			RandomEvent re = null;
			if (Shared.isBetween(number, 0, 300)) {// pestilence
				factorOfPeople = Shared.randomPercentage(30, 80);
				factorOfSoldiers = Shared.randomPercentage(30, 80);
				growthFactor = Shared.randomPercentage(85, 95);
				re = RandomEvent.PESTILENCE;
			} else if (Shared.isBetween(number, 3200, 3300)) {// fire
				factorOfPeople = Shared.randomPercentage(85, 95);
				factorOfSoldiers = Shared.randomPercentage(80, 90);
				growthFactor = Shared.randomPercentage(80, 95);
				re = RandomEvent.FIRE;
			} else if (Shared.isBetween(number, 4500, 6500)) {// growth
				factorOfPeople = Shared.randomPercentage(105, 115);
				growthFactor = Shared.randomPercentage(104, 112.5);
				re = RandomEvent.GROWTH;
			} else if (Shared.isBetween(number, 7900, 8900)) {// bad harvesting
				factorOfPeople = Shared.randomPercentage(85, 90);
				factorOfSoldiers = Shared.randomPercentage(80, 90);
				growthFactor = Shared.randomPercentage(85, 90);
				re = RandomEvent.CROP_FAILURE;
			} else if (Shared.isBetween(number, 12_670, 12_900)) {// rebellion
				factorOfPeople = Shared.randomPercentage(28, 60);
				factorOfSoldiers = Shared.randomPercentage(20, 85);
				growthFactor = Shared.randomPercentage(15, 30);
				re = RandomEvent.REBELLION;
			} else if (Shared.isBetween(number, 19_200, 19_330)) {// civil war
				factorOfPeople = Shared.randomPercentage(2, 12);
				factorOfSoldiers = Shared.randomPercentage(20, 45);
				growthFactor = Shared.randomPercentage(2, 30);
				re = RandomEvent.CIVIL_WAR;
			} else if (Shared.isBetween(number, 239_400, 250_100)) {// migration
				factorOfPeople = Shared.randomPercentage(102, 135);
				factorOfSoldiers = Shared.randomPercentage(100, 103.5);
				growthFactor = Shared.randomPercentage(107, 145);
				re = RandomEvent.MIGRATION;
			} else if (Shared.isBetween(number, 405_200, 409_900)) {// economic growth
				growthFactor = Shared.randomPercentage(102, 105);
				re = RandomEvent.ECONOMIC_GROWTH;
			} else if (Shared.isBetween(number, 562_000, 569_900)) {// pandemic
				factorOfPeople = Shared.randomPercentage(2, 35);
				factorOfSoldiers = Shared.randomPercentage(0, 20);
				growthFactor = Shared.randomPercentage(45, 60);
				re = RandomEvent.PANDEMIC;
			} else if (Shared.isBetween(number, 1_000_000, 5_000_000)) {// accident
				final var numberOfPeople = a.getNumberOfPeople() - (this.random.nextInt(12) + 1);
				a.setNumberOfPeople(numberOfPeople < 0 ? 0 : numberOfPeople);
			} else if (Shared.isBetween(number, 5_100_100, 5_100_200)) {// alchemic accident
				factorOfPeople = Shared.randomPercentage(92, 99);
				factorOfSoldiers = Shared.randomPercentage(92, 99);
				growthFactor = Shared.randomPercentage(98, 99);
				re = RandomEvent.ACCIDENT;
			} else if (Shared.isBetween(number, 12_010_000, 12_015_000)) {// sabotage
				factorOfPeople = Shared.randomPercentage(80, 90);
				factorOfSoldiers = Shared.randomPercentage(75, 90);
				growthFactor = Shared.randomPercentage(90, 98);
				re = RandomEvent.SABOTAGE;
			}
			if ((factorOfPeople == 1) && (factorOfSoldiers == 1) && (growthFactor == 1)) {
				return;
			}
			if ((long) (a.getNumberOfPeople() * factorOfPeople) < 0) {
				a.setNumberOfPeople(0);
			} else {
				a.setNumberOfPeople((long) (a.getNumberOfPeople() * factorOfPeople));
			}
			if (a.getNumberOfPeople() < 0) {
				a.setNumberOfPeople(5);
			}
			if ((long) (a.getNumberOfSoldiers() * factorOfPeople) < 0) {
				a.setNumberOfSoldiers(0);
			} else {
				a.setNumberOfSoldiers((long) (a.getNumberOfSoldiers() * factorOfSoldiers));
			}
			a.setGrowth(a.getGrowth() * growthFactor);
			this.events.add(new RandomEventMessage(re, factorOfPeople, factorOfSoldiers, growthFactor, a));
		});
	}

	/**
	 * Plays one round. Should be called after the player played.
	 */
	public void executeActions() {
		final var start = System.nanoTime();
		this.relationshipEvents();
		this.payMoney();
		this.produceResources();
		this.useResources();
		this.growCities();
		this.events();
		this.cpuPlay();
		try {
			this.data.getPlugins()
					.forEach(a -> a.handle(this.cities, new Context(this.events, this.getClanNames(), this.clans)));
		} catch (final Throwable throwable) {// The plugin could throw everything, never trust unknown code.
			Shared.LOGGER.exception(throwable);
		}
		this.sanityCheckForGrowth();
		StreamUtils.forEach(this.cities, City::endOfRound);
		this.currentRound++;
		final var end = System.nanoTime();
		var diff = ((double) end - start);
		diff /= 1000;// 10^-6 s
		diff /= 1000;// 10^-3 s
		Shared.LOGGER.message("CPUPLAY: " + diff + "ms");
		this.isPlayersTurn = true;
	}

	private void executeCPUPlay(final Integer j) {
		this.throwIfNull(j, "j==null");
		final var clan = j.byteValue();
		if (StreamUtils.getCitiesAsStream(this.getCities(), clan).count() == 0) {
			return;
		}
		this.clans.get(clan).getStrategy().applyStrategy(this.clans.get(clan), clan, this.cities, this);
		this.clans.get(clan).update(this.currentRound);
	}

	/**
	 * Should be called when only one clan is left.
	 *
	 * @param result
	 */
	public void exit(final Result result) {
		this.throwIfNull(result, "result==null");
		this.data.getPlugins().forEach(a -> a.exit(result));
		if (this.resumed) {
			try {
				Shared.deleteDirectory(this.directory);
			} catch (final IOException e) {// We are done, we can simply ignore it, but still log it!
				Shared.LOGGER.exception(e);
			}
		}
	}

	/**
	 * Get the background picture of the scenario
	 *
	 * @return Background picture
	 */
	public Image getBackground() {
		return this.background;
	}

	@Override
	public Graph<City> getCities() {
		return this.cities;
	}

	/**
	 * Get all registered CityKeyHandlers
	 *
	 * @return Registered {@link CityKeyHandler}s
	 */
	public Map<String, CityKeyHandler> getCityKeyHandlers() {
		return this.data.getCityKeyHandlers();
	}

	private Clan getClan(City city) {
		return this.getClan(city.getClan());
	}

	/**
	 * Get reference to clan based on id
	 *
	 * @param clanId The clan id
	 * @return A reference to the clan with the id {@code clanID}
	 */
	public Clan getClan(final int clanId) {
		this.checkClan(clanId);
		return this.clans.get(clanId);
	}

	/**
	 * @return All clannames.
	 */
	public List<String> getClanNames() {
		return this.clans.stream().map(Clan::getName).collect(Collectors.toList());
	}

	/**
	 * @return All clans
	 */
	public List<Clan> getClans() {
		return this.clans;
	}

	/**
	 * @return The coins of every clan.
	 */
	public List<Double> getCoins() {
		return this.clans.stream().map(Clan::getCoins).collect(Collectors.toList());
	}

	/**
	 * @return The colors of every clan.
	 */
	public List<Color> getColors() {
		return this.clans.stream().map(Clan::getColor).collect(Collectors.toList());
	}

	@Override
	public EventList getEventList() {
		return this.events;
	}

	/**
	 * @return Every registered music.
	 */
	public List<String> getExtraMusic() {
		return this.data.getExtraMusic();
	}

	/**
	 * @return All registered Keybindings.
	 */
	public Map<String, KeyHandler> getKeybindings() {
		return this.data.getKeybindings();
	}

	/**
	 * Returns the number of players
	 *
	 * @return Number of players.
	 */
	public byte getNumPlayers() {
		return this.numPlayers;
	}

	/**
	 * @return All plugins
	 */
	public List<Plugin> getPlugins() {
		return this.data.getPlugins();
	}

	@Override
	public Graph<Integer> getRelations() {
		return this.relations;
	}

	private List<Double> getResources(final int i) {
		return this.clans.get(i).getResources();
	}

	public int getSoldiersDefenseLevel(final int clan) {
		this.checkClan(clan);
		return this.clans.get(clan).getSoldiersDefenseLevel();
	}

	public double getSoldiersDefenseStrength(final int clan) {
		this.checkClan(clan);
		return this.clans.get(clan).getSoldiersDefenseStrength();
	}

	public int getSoldiersLevel(final int clan) {
		this.checkClan(clan);
		return this.clans.get(clan).getSoldiersLevel();
	}

	public int getSoldiersOffenseLevel(final int clan) {
		this.checkClan(clan);
		return this.clans.get(clan).getSoldiersOffenseLevel();
	}

	public double getSoldiersOffenseStrength(final int clan) {
		this.checkClan(clan);
		return this.clans.get(clan).getSoldiersOffenseStrength();
	}

	public double getSoldiersStrength(final int clan) {
		this.checkClan(clan);
		return this.clans.get(clan).getSoldiersStrength();
	}

	@Override
	public List<City> getWeakestCityInRatioToSurroundingEnemyCities(final List<City> reachableCities) {
		this.throwIfNull(reachableCities, "reachableCities==null");
		return Stream.of(reachableCities.toArray(new City[0])).sorted((a, b) -> {
			final var defense = this.defenseStrengthOfCity(a);
			final var neighbours = StreamUtils.getCitiesAroundCityNot(this.cities, a, a.getClan())
					.collect(Collectors.toList());
			final var attack = neighbours.stream().mapToDouble(City::getNumberOfSoldiers).sum();
			final var defenseB = this.defenseStrengthOfCity(b);
			final var neighboursB = StreamUtils.getCitiesAroundCityNot(this.cities, b, b.getClan())
					.collect(Collectors.toList());
			final var attackB = neighboursB.stream().mapToDouble(City::getNumberOfSoldiers).sum();
			final var diff = attack - defense;
			final var diff2 = attackB - defenseB;
			return Double.compare(diff, diff2);
		}).collect(Collectors.toList());
	}

	private void growCities() {
		StreamUtils.forEach(this.cities, a -> {
			final var cnt = a.getNumberOfPeople();
			final var l = cnt < 0 ? 0 : cnt;
			var lNew = (long) (cnt * a.getGrowth());
			// Every city will get at least one person per round.
			if ((lNew - l) == 0) {
				lNew++;
			}
			a.setNumberOfPeople(lNew < 0 ? Game.FALLBACK_POPULATION : lNew);
		});
	}

	/**
	 * @return Returns {@code true} if only the player is left.
	 */
	public boolean hasResult() {
		final var others = StreamUtils.getCitiesAsStreamNot(this.getCities(), 0).count();
		final var player = StreamUtils.getCitiesAsStream(this.getCities(), 0).count();
		return (others == 0) || (player == 0);
	}

	private void improveRelationship(final Random r, final int clanOne, final int clanTwo) {
		final var bigger = Math.abs(r.nextGaussian() * 20);
		final var oldValue = this.relations.getWeight(clanOne, clanTwo);
		var newValue = oldValue + bigger;
		if (newValue > 100) {
			newValue = 100;
		}
		if ((newValue - oldValue) == 0) {
			return;
		}
		this.relations.addDirectedEdge(clanOne, clanTwo, newValue, newValue);
		this.events.add(
				new BetterRelationshipMessage(this.clans.get(clanOne), this.clans.get(clanTwo), oldValue, newValue));
	}

	/**
	 * Initialises everything. Has to be called.
	 */
	public void init() {
		this.clans.forEach(a -> a.init(this.strategies));
		if (this.data.getPlugins() != null) {
			this.data.getPlugins().forEach(a -> a.init(this));
		}
		this.cities.initCache();
	}

	/**
	 * Returns whether a clan is dead.
	 *
	 * @param clan
	 */
	public boolean isDead(Clan clan) {
		return this.isDead(clan.getId());
	}

	/**
	 * Returns whether a clan is dead.
	 *
	 * @param clan
	 */
	public boolean isDead(int clan) {
		return StreamUtils.getCitiesAsStream(this.cities, clan).count() == 0;
	}

	private boolean isInFriendlyCountry(final City c) {
		return this.cities.getConnected(c).stream().filter(a -> a.getClan() != c.getClan()).count() == 0;
	}

	/**
	 * Returns whether it is the players' turn.
	 */
	public boolean isPlayersTurn() {
		return this.isPlayersTurn;
	}

	public long maximumNumberOfSoldiersToRecruit(final byte clan, final long limit) {
		this.checkClan(clan);
		final var resourcesOfClan = this.clans.get(clan).getResources();
		final List<Long> numbers = new ArrayList<>();
		numbers.add(limit);
		numbers.add((long) (this.clans.get(clan).getCoins() / Shared.COINS_PER_SOLDIER_INITIAL));
		numbers.add((long) (resourcesOfClan.get(Resource.IRON.getIndex()) / Shared.IRON_PER_SOLDIER_INITIAL));
		numbers.add((long) (resourcesOfClan.get(Resource.WOOD.getIndex()) / Shared.WOOD_PER_SOLDIER_INITIAL));
		numbers.add((long) (resourcesOfClan.get(Resource.STONE.getIndex()) / Shared.STONE_PER_SOLDIER_INITIAL));
		Collections.sort(numbers);
		return numbers.get(0);
	}

	@Override
	public long maximumNumberToMove(final byte clan, final double distance, final long l) {
		this.checkClan(clan);
		if (distance < 0) {
			throw new IllegalArgumentException("distance < 0 : " + distance);
		}
		if (l < 0) {
			throw new IllegalArgumentException("l < 0 : " + l);
		}
		final var maxPay = (long) (this.clans.get(clan).getCoins()
				/ (Shared.COINS_PER_MOVE_OF_SOLDIER_BASE + (Shared.COINS_PER_MOVE_OF_SOLDIER * distance)));
		return Math.min(l, maxPay);
	}

	@Override
	public void moveSoldiers(final City src, final Stream<City> reachableCities, final byte i, final boolean managed,
			final City other, final long num) {
		this.checkClan(i);
		this.throwIfNull(src, "src==null");
		if (num < 0) {
			throw new IllegalArgumentException("num < 0 : " + num);
		}
		final var saved = reachableCities == null ? new ArrayList<City>()
				: reachableCities.collect(Collectors.toList());
		if (!managed && saved.isEmpty()) {
			return;
		}
		City destination;
		List<City> list = null;
		if (!managed) {
			list = this.getWeakestCityInRatioToSurroundingEnemyCities(saved).stream()
					.filter(a -> a.getClan() == src.getClan()).collect(Collectors.toList());
			if (list.isEmpty()) {
				return;
			}
			destination = list.get(list.size() - 1);
		} else {
			destination = other;
		}
		if (src == destination) {
			return;
		}
		long moveAmount;
		if (!managed) {
			if (this.isInFriendlyCountry(src)) {
				// This city has no connections to other clans==>Move all troops to the borders.
				moveAmount = src.getNumberOfSoldiers();
			} else {
				// Which city was attacked more often by the player?
				if (destination.getNumberAttacksOfPlayer() > src.getNumberAttacksOfPlayer()) {
					moveAmount = (int) (0.7 * src.getNumberOfSoldiers());
				} else {
					moveAmount = (int) (0.3 * src.getNumberOfSoldiers());
				}
			}
			moveAmount = this.maximumNumberToMove(i, this.getCities().getWeight(src, destination), moveAmount);
			if (moveAmount == 0) {
				return;
			}
		} else {
			moveAmount = num;
		}
		destination.setNumberOfSoldiers(destination.getNumberOfSoldiers() + moveAmount);
		this.payForMove(i, moveAmount, this.getCities().getWeight(src, destination));
		src.setNumberOfSoldiers(src.getNumberOfSoldiers() - moveAmount);
		final var finalMoveAmout = moveAmount;
		this.data.getMoveHooks().forEach(a -> a.handleMove(src, destination, finalMoveAmout));
	}

	/**
	 * Returns whether only one clan is alive.
	 *
	 * @return
	 */
	public boolean onlyOneClanAlive() {
		return StreamUtils.getCitiesAsStream(this.cities).map(City::getClan).distinct().count() == 1;
	}

	private void pay(final byte clan, final double subtract) {
		if ((clan < 0) || (clan >= this.clans.size())) {
			throw new IllegalArgumentException("clan outside of range");
		}
		this.setCoins(clan, this.getCoins().get(clan) - subtract);
	}

	private void payForMove(final byte clan, final long numSoldiers, final double weight) {
		this.pay(clan,
				numSoldiers * (Shared.COINS_PER_MOVE_OF_SOLDIER_BASE + (Shared.COINS_PER_MOVE_OF_SOLDIER * weight)));
	}

	private void payMoney() {
		StreamUtils.forEach(this.cities, city -> {
			final var clan = this.getClan(city);
			final var toGet = city.getCoinDiff();
			clan.setCoins(clan.getCoins() + toGet);
		});
		this.clans.forEach(clan -> this.data.getMoneyHooks().forEach(
				a -> a.moneyPaid(StreamUtils.getCitiesAsStream(this.cities, clan).collect(Collectors.toList()), clan)));

	}

	private void produceResources() {
		StreamUtils.forEach(this.getCities(), city -> {
			final var clan = this.clans.get(city.getClan());
			final var resourcesOfClan = clan.getResources();
			for (var i = 0; i < resourcesOfClan.size(); i++) {
				final var productions = (city.getNumberOfPeople() * city.getProductions().get(i));
				resourcesOfClan.set(i, resourcesOfClan.get(i) + productions);
				clan.getResourceStats().set(i, clan.getResourceStats().get(i) + productions);
			}
		});
	}

	@Override
	public Stream<City> reachableCities(final City c) {
		this.throwIfNull(c, "c==null");
		return StreamUtils.getCitiesAsStream(this.getCities(), a -> this.getCities().isConnected(c, a));
	}

	@Override
	public void recruitSoldiers(final double maxToPay, final byte clan, final City c, final boolean managed,
			final double count) {
		this.checkClan(clan);
		this.throwIfNull(c, "c==null");
		if (maxToPay < 0) {
			throw new IllegalArgumentException("maxToPay < 0 :" + maxToPay);
		}
		var numberToRecruit = 0L;
		// Default algorithm used, if the strategy didn't provide one itself.
		if (!managed) {
			if ((maxToPay < 0) || (c.getNumberOfPeople() < Game.RETAINED_PEOPLE)) {
				return;
			}
			numberToRecruit = (int) (maxToPay / Shared.COINS_PER_SOLDIER_INITIAL);
			numberToRecruit = Math.min(c.getNumberOfPeople() - Game.RETAINED_PEOPLE, numberToRecruit);
			numberToRecruit = this.maximumNumberOfSoldiersToRecruit(clan, numberToRecruit);
			if (numberToRecruit == 0) {
				return;
			}
		} else {
			numberToRecruit = (long) Math.min(this.maximumNumberOfSoldiersToRecruit(clan, (long) count), count);
		}
		c.setNumberOfPeople(c.getNumberOfPeople() - numberToRecruit);
		c.setNumberOfSoldiers(c.getNumberOfSoldiers() + numberToRecruit);
		final var resourcesOfClan = this.getResources(clan);
		final var ironNew = resourcesOfClan.get(Resource.IRON.getIndex())
				- (numberToRecruit * Shared.IRON_PER_SOLDIER_INITIAL);
		final var woodNew = resourcesOfClan.get(Resource.WOOD.getIndex())
				- (numberToRecruit * Shared.WOOD_PER_SOLDIER_INITIAL);
		final var stoneNew = resourcesOfClan.get(Resource.STONE.getIndex())
				- (numberToRecruit * Shared.STONE_PER_SOLDIER_INITIAL);
		resourcesOfClan.set(Resource.IRON.getIndex(), ironNew);
		resourcesOfClan.set(Resource.WOOD.getIndex(), woodNew);
		resourcesOfClan.set(Resource.STONE.getIndex(), stoneNew);
		this.setCoins(clan, this.getCoins().get(clan) - (numberToRecruit * Shared.COINS_PER_SOLDIER_INITIAL));
		final var finalNumberToRecruit = numberToRecruit;
		this.data.getRecruitHooks().forEach(a -> a.recruited(c, finalNumberToRecruit));
	}

	private void relationshipEvents() {
		final var r = new Random(System.nanoTime());
		final var size = this.clans.size();
		final var numTries = r.nextInt((size / 2) + 1);
		for (var i = 0; i < numTries; i++) {
			final var selector = r.nextInt(Game.MAX_SELECTOR_VALUE);
			final var clanOne = r.nextInt(size);
			if (this.isDead(clanOne)) {
				continue;
			}
			var clanTwo = r.nextInt(size);
			while ((clanTwo == clanOne) && !this.isDead(clanTwo)) {
				clanTwo = r.nextInt(size);
			}
			this.eval(selector, clanOne, clanTwo, r);
		}
	}

	void resume(String name) {
		this.resumed = true;
		this.directory = new File(new File(Shared.BASE_DIRECTORY, "saves"), name);
	}

	private void sanityCheckForGrowth() {
		StreamUtils.forEach(this.cities, a -> a.getGrowth() > Game.GROWTH_LIMIT,
				a -> a.setGrowth(a.getGrowth() * Game.GROWTH_REDUCE_FACTOR));
		StreamUtils.forEach(this.cities, a -> {
			while (a.getGrowth() > Game.ALTERNATIVE_GROWTH_LIMIT) {
				a.setGrowth(a.getGrowth() * Game.WEAK_GROWTH_REDUCE_FACTOR);
			}
			if ((a.getNumberOfPeople() > Game.SOFT_POPULATION_LIMIT) && (a.getGrowth() > 1)) {
				a.setGrowth(1.001);
			}
		});
	}

	@Override
	public boolean sendGift(Clan source, Clan destination, Gift gift) {
		this.throwIfNull(source, "source==null");
		this.throwIfNull(destination, "destination==null");
		this.throwIfNull(gift, "gift==null");
		if (source.getId() == destination.getId()) {
			throw new IllegalArgumentException("source==destination");
		} else if (this.isDead(destination)) {
			throw new IllegalArgumentException("Destination clan is extinct!");
		}
		boolean acceptedGift;
		if (destination.getId() != Shared.PLAYER_CLAN) {
			acceptedGift = destination.getStrategy().acceptGift(source, destination, gift,
					this.getRelationship(source.getId(), destination.getId()), a -> {
						final var d = a < 0 ? 0 : (a > 100 ? 100 : a);
						this.relations.addUndirectedEdge(source.getId(), destination.getId(), d);
					}, this);
		} else {
			acceptedGift = this.playerGiftCallback.acceptGift(source, destination, gift,
					this.getRelationship(source.getId(), destination.getId()), a -> {
						final var d = a < 0 ? 0 : (a > 100 ? 100 : a);
						this.relations.addUndirectedEdge(source.getId(), destination.getId(), d);
					}, this);
		}
		if (acceptedGift) {
			source.setCoins(source.getCoins() - gift.getNumberOfCoins());
			destination.setCoins(destination.getCoins() + gift.getNumberOfCoins());
			final var a = source.getResources();
			final var b = destination.getResources();
			gift.getMap().entrySet().forEach(d -> {
				final var index = d.getKey().getIndex();
				final var value = d.getValue();
				a.set(index, a.get(index) - value);
				b.set(index, b.get(index) + value);
			});
		}
		return acceptedGift;
	}

	void setBackground(final Image gi) {
		this.throwIfNull(gi, "gi==null");
		if (this.background != null) {
			throw new UnsupportedOperationException("Can't change image!");
		}
		this.background = gi;
	}

	void setClans(final List<Clan> clans) {
		this.throwIfNull(clans, "clans==null");
		if (this.clans != null) {
			throw new UnsupportedOperationException("Can't change clans!");
		}
		this.clans = clans;
	}

	private void setCoins(final byte clan, final double v) {
		this.checkClan(clan);
		this.clans.get(clan).setCoins(v);
	}

	public void setGraph(final Graph<City> g) {
		this.throwIfNull(g, "g==null");
		if (this.cities != null) {
			throw new UnsupportedOperationException("Can't change graph!");
		}
		this.cities = g;
	}

	public void setPlayerGiftCallback(PlayerGiftCallback pgc) {
		this.throwIfNull(pgc, "PlayerGiftCallback==null");
		this.playerGiftCallback = pgc;
	}

	void setPlayers(final byte numPlayers) {
		if (numPlayers <= 0) {
			throw new IllegalArgumentException("numPlayers<=0");
		} else if (this.numPlayers != -1) {
			throw new UnsupportedOperationException("Can't change number of players");
		}
		this.numPlayers = numPlayers;
	}

	public void setPlayersTurn(final boolean b) {
		this.isPlayersTurn = b;
	}

	void setPlugins(List<Plugin> plugins) {
		this.data.setPlugins(plugins);
	}

	void setRelations(final Graph<Integer> relations) {
		this.throwIfNull(relations, "relations==null");
		if (this.relations != null) {
			throw new UnsupportedOperationException("Can't change relations");
		}
		this.relations = relations;
	}

	void setRound(int r) {
		this.currentRound = r;
	}

	private double setup(byte clan, long powerOfAttacker, City src, City destination) {
		final var srcClan = this.clans.get(src.getClan());
		this.payForMove(clan, powerOfAttacker, this.getCities().getWeight(src, destination));
		var newPowerOfAttacker = powerOfAttacker * srcClan.getSoldiersStrength();
		newPowerOfAttacker *= srcClan.getSoldiersOffenseStrength();
		final var powerOfDefender = this.calculatePowerOfDefender(destination);
		return powerOfDefender - newPowerOfAttacker;
	}

	private void throwIfNull(final Object obj) {
		if (obj == null) {
			throw new IllegalArgumentException("No null allowed!");
		}
	}

	private void throwIfNull(final Object obj, final String string) {
		if (obj == null) {
			throw new IllegalArgumentException(string);
		}
	}

	@Override
	public boolean upgradeDefense(final byte clan) {
		this.checkClan(clan);
		final var c = this.clans.get(clan);
		final var currLevel = c.getSoldiersDefenseLevel();
		if (currLevel == Game.MAX_LEVEL) {
			return false;
		}
		final var costs = Shared.upgradeCostsForOffenseAndDefense(currLevel + 1);
		if (costs > c.getCoins()) {
			return false;
		}
		c.setCoins(c.getCoins() - costs);
		c.setSoldiersDefenseLevel(currLevel + 1);
		c.setSoldiersDefenseStrength(1 + Shared.newPowerOfSoldiersForOffenseAndDefense(currLevel + 1));
		return true;
	}

	@Override
	public boolean upgradeDefense(final byte clan, final City city) {
		this.checkClan(clan);
		this.throwIfNull(city, "city==null");
		final var coins = this.getCoins();
		final var costs = Shared.costs(city.getLevels().get(Resource.values().length) + 1);
		if ((costs > coins.get(clan)) || (city.getLevels().get(Resource.values().length) == Game.MAX_LEVEL)) {
			return false;
		}
		this.setCoins(clan, coins.get(clan) - costs);
		var defense = city.getDefense();
		defense = defense < 1 ? 1 : defense;
		city.setDefense(Shared.newPowerOfUpdate(city.getLevels().get(Resource.values().length) + 1, defense));
		city.getLevels().set(Resource.values().length, city.getLevels().get(Resource.values().length) + 1);
		return true;
	}

	public void upgradeDefenseFully(final byte b, final City city) {
		this.throwIfNull(city, "city==null");
		this.checkClan(b);
		var shouldNotBreak = true;
		while (shouldNotBreak) {
			shouldNotBreak = this.upgradeDefense(b, city);
		}
	}

	@Override
	public boolean upgradeOffense(final byte i) {
		this.checkClan(i);
		final var c = this.clans.get(i);
		final var currLevel = c.getSoldiersOffenseLevel();
		if (currLevel == Game.MAX_LEVEL) {
			return false;
		}
		final var costs = Shared.upgradeCostsForOffenseAndDefense(currLevel + 1);
		if (costs > c.getCoins()) {
			return false;
		}
		c.setCoins(c.getCoins() - costs);
		c.setSoldiersOffenseLevel(currLevel + 1);
		c.setSoldiersOffenseStrength(1 + Shared.newPowerOfSoldiersForOffenseAndDefense(currLevel + 1));
		return true;
	}

	@Override
	public boolean upgradeResource(final byte clan, final Resource resc, final City city) {
		this.checkClan(clan);
		this.throwIfNull(city, "city==null");
		this.throwIfNull(resc, "resc==null");
		final var coins = this.getCoins();
		final var index = resc.getIndex();
		final var costs = Shared.costs(city.getLevels().get(index) + 1);
		if ((costs > coins.get(clan)) || (city.getLevels().get(index) == Game.MAX_LEVEL)) {
			return false;
		}
		this.setCoins(clan, coins.get(clan) - costs);
		city.getProductions().set(resc.getIndex(),
				Shared.newPowerOfUpdate(city.getLevels().get(index + 1), city.getProductions().get(index)));
		city.getLevels().set(resc.getIndex(), city.getLevels().get(index) + 1);
		return true;
	}

	public void upgradeResourceFully(final byte b, final Resource resources, final City city) {
		this.throwIfNull(city, "city==null");
		this.throwIfNull(resources, "resources==null");
		this.checkClan(b);
		var shouldNotBreak = true;
		while (shouldNotBreak) {
			shouldNotBreak = this.upgradeResource(b, resources, city);
		}
	}

	@Override
	public boolean upgradeSoldiers(final byte i) {
		this.checkClan(i);
		final var c = this.clans.get(i);
		final var currLevel = c.getSoldiersLevel();
		if (currLevel == Game.MAX_LEVEL) {
			return false;
		}
		final var costs = Shared.upgradeCostsForSoldiers(currLevel + 1);
		if (costs > c.getCoins()) {
			return false;
		}
		c.setCoins(c.getCoins() - costs);
		c.setSoldiersLevel(currLevel + 1);
		c.setSoldiersStrength(1 + Shared.newPowerForSoldiers(currLevel + 1));
		return true;
	}

	public void upgradeSoldiersDefenseFully(byte i) {
		var b = true;
		while (b) {
			b = this.upgradeDefense(i);
		}
	}

	public void upgradeSoldiersFully(byte i) {
		var b = true;
		while (b) {
			b = this.upgradeSoldiers(i);
		}
	}

	public void upgradeSoldiersOffenseFully(byte i) {
		var b = true;
		while (b) {
			b = this.upgradeOffense(i);
		}
	}

	private void useResources() {
		StreamUtils.forEach(this.cities, city -> {
			final var resources2 = this.clans.get(city.getClan()).getResources();
			final var stats = this.clans.get(city.getClan()).getResourceStats();
			for (var i = 0; i < Shared.getDataValues().length; i++) {
				final var va = Shared.getDataValues()[i];
				final var use = ((city.getNumberOfSoldiers() * va[1]) + (city.getNumberOfPeople() * va[0]));
				resources2.set(i, resources2.get(i) - use);
				stats.set(i, stats.get(i) - use);
			}
			for (var i = 0; i < Resource.values().length; i++) {
				if (resources2.get(i) < 0) {
					resources2.set(i, 0.0);
				}
			}
			this.data.getResourceHooks().forEach(a -> a.analyzeStats(city, stats, this.clans.get(city.getClan())));
		});
	}

	private void worseRelationship(final Random r, final int clanOne, final int clanTwo) {
		final var smaller = Math.abs(r.nextGaussian() * 20);
		final var oldValue = this.relations.getWeight(clanOne, clanTwo);
		var newValue = oldValue - smaller;
		if (newValue <= 0) {
			newValue = 0;
		}
		this.relations.addDirectedEdge(clanOne, clanTwo, newValue, newValue);
		if ((newValue - oldValue) == 0) {
			return;
		}
		this.events.add(
				new WorseRelationshipMessage(this.clans.get(clanOne), this.clans.get(clanTwo), oldValue, newValue));
	}
}
