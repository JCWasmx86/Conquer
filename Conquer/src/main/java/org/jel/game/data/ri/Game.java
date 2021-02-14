package org.jel.game.data.ri;

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
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jel.game.data.AttackResult;
import org.jel.game.data.ConquerInfo;
import org.jel.game.data.ConquerSaver;
import org.jel.game.data.EventList;
import org.jel.game.data.Gift;
import org.jel.game.data.GlobalContext;
import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.data.PlayerGiftCallback;
import org.jel.game.data.Resource;
import org.jel.game.data.Result;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;
import org.jel.game.data.Version;
import org.jel.game.data.builtin.DefensiveStrategyProvider;
import org.jel.game.data.builtin.ModerateStrategyProvider;
import org.jel.game.data.builtin.OffensiveStrategyProvider;
import org.jel.game.data.builtin.RandomStrategyProvider;
import org.jel.game.data.strategy.StrategyProvider;
import org.jel.game.messages.AnnihilationMessage;
import org.jel.game.messages.AttackLostMessage;
import org.jel.game.messages.BetterRelationshipMessage;
import org.jel.game.messages.ConquerMessage;
import org.jel.game.messages.ExtinctionMessage;
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
import org.jel.game.plugins.RecruitHook;
import org.jel.game.plugins.ResourceHook;
import org.jel.game.utils.Graph;

final class Game implements ConquerInfo {
	private static final double GROWTH_REDUCE_FACTOR = 0.95;
	private static final double GROWTH_LIMIT = 1.05;
	private static final double WEAK_GROWTH_REDUCE_FACTOR = 0.9;
	private static final double ALTERNATIVE_GROWTH_LIMIT = 1.055;
	private static final int SOFT_POPULATION_LIMIT = 500_000;
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
	private static final int MAX_STRATEGIES = 2048;
	private final Random random = new SecureRandom();
	private List<IClan> clans;
	private Image background;
	private Graph<ICity> cities;
	private final EventList events = new EventList();
	private final StrategyProvider[] strategies;
	private boolean isPlayersTurn = true;
	private int numPlayers = -1;
	private Graph<Integer> relations;
	private int currentRound = 1;
	private final GamePluginData data = new GamePluginData();
	private PlayerGiftCallback playerGiftCallback;
	private boolean resumed;
	private File directory;
	private Consumer<Throwable> throwableConsumer;
	private GlobalContext context;

	Game() {
		this.data.setRecruitHooks(new ArrayList<>());
		this.data.setAttackHooks(new ArrayList<>());
		this.data.setMoveHooks(new ArrayList<>());
		this.data.setResourceHooks(new ArrayList<>());
		this.data.setMoneyHooks(new ArrayList<>());
		this.data.setCityKeyHandlers(new HashMap<>());
		this.data.setExtraMusic(new ArrayList<>());
		this.data.setKeybindings(new HashMap<>());
		this.strategies = new StrategyProvider[Game.MAX_STRATEGIES];
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

	@Override
	public void addContext(final GlobalContext context) {
		this.throwIfNull(context, "context==null");
		this.throwIfNull(context.getPlugins(), "context.getPlugins()==null");
		this.throwIfNull(context.getStrategies(), "context.getStrategies()==null");
		this.throwIfNull(context.getInstalledMaps(), "context.getInstalledMaps()==null");
		this.data.setPlugins(context.getPlugins());
		this.context = context;
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
	public void addMessageListener(final MessageListener ml) {
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

	private long aiCalculateNumberOfTroopsToAttackWith(final ICity src, final ICity destination) {
		final var powerOfAttacker = src.getNumberOfSoldiers();
		if (powerOfAttacker == 0) {
			return 0;
		}
		return this.maximumNumberToMove(src.getClan(), src, destination, powerOfAttacker);
	}

	@Override
	public void attack(final ICity src, final ICity destination, final boolean managed, final long num) {
		this.throwIfNull(src, "src==null");
		this.throwIfNull(destination, "destination==null");
		this.checkPreconditions(managed, num);
		this.cantAttack(src, destination);
		final var powerOfAttacker = this.calculatePowerOfAttacker(src, destination, managed, num);
		if (((powerOfAttacker == 0) && !src.isPlayerCity()) || ((!src.isPlayerCity()) && (powerOfAttacker == 1))) {
			return;
		}
		final var diff = this.setup(powerOfAttacker, src, destination);
		src.setNumberOfSoldiers(src.getNumberOfSoldiers() - powerOfAttacker);
		this.data.getAttackHooks().forEach(a -> a.before(src, destination, powerOfAttacker));
		var relationshipValue = this.getRelationship(src.getClan(), destination.getClan());
		double numberOfSurvivingPeople;
		final var destinationClan = destination.getClan();
		long survivingSoldiers;
		AttackResult result;
		if (diff > 0) {// Attack was defeated
			final var destinationClanObj = this.getClan(destination);
			final var remainingSoldiersDefender = this.numberOfSurvivingDefenders(diff, destination,
					destinationClanObj);
			var surviving = remainingSoldiersDefender;
			final var numSoldiers = destination.getNumberOfSoldiers();
			if (numSoldiers == 0) {
				surviving = 0;
			} else if (numSoldiers < surviving) {
				surviving = numSoldiers;
			}
			survivingSoldiers = surviving;
			destination.setNumberOfSoldiers(surviving);
			this.events.add(new AttackLostMessage(src, destination, powerOfAttacker));
			relationshipValue -= Game.RELATIONSHIP_CHANGE_ATTACK_DEFEATED;
			numberOfSurvivingPeople = Shared.randomPercentage(Game.MINIMUM_SURVIVING_PEOPLE_ATTACK_DEFEATED,
					Game.MAXIMUM_SURVIVING_PEOPLE_ATTACK_DEFEATED);
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
			survivingSoldiers = this.calculateNumberOfSurvivingAttackers(diff, src);
			destination.setNumberOfSoldiers(survivingSoldiers);
			relationshipValue -= Game.RELATIONSHIP_CHANGE_CITY_CONQUERED;
			numberOfSurvivingPeople = Shared.randomPercentage(Game.MINIMUM_SURVIVING_PEOPLE_CITY_CONQUERED,
					Game.MAXIMUM_SURVIVING_PEOPLE_CONQUERED);
			result = AttackResult.CITY_CONQUERED;
			this.events.add(new ConquerMessage(src, destination, powerOfAttacker));
			destination.setClan(src.getClan());
		}
		if (relationshipValue < 0) {
			relationshipValue = 0;
		}
		destination.setNumberOfPeople((long) (destination.getNumberOfPeople() * numberOfSurvivingPeople));
		this.getRelations().addDirectedEdge(src.getClanId(), destinationClan.getId(), relationshipValue,
				relationshipValue);
		this.data.getAttackHooks().forEach(a -> a.after(src, destination, survivingSoldiers, result));
		this.checkExtinction(result, destinationClan);
	}

	private boolean bad(final double d) {
		return (d < 0) || Double.isNaN(d) || Double.isInfinite(d);
	}

	private long calculateNumberOfSurvivingAttackers(final double diff, final ICity src) {
		var cleanedDiff = diff;
		final var srcClan = this.getClan(src);
		cleanedDiff /= srcClan.getSoldiersOffenseStrength();
		cleanedDiff /= srcClan.getSoldiersStrength();
		return (long) -cleanedDiff;
	}

	private long calculatePowerOfAttacker(final ICity src, final ICity destination, final boolean managed,
			final long numberOfSoldiers) {
		if (!managed) {
			return this.aiCalculateNumberOfTroopsToAttackWith(src, destination);
		} else {
			if (destination.isPlayerCity() && (destination instanceof City c)) {
				c.attackByPlayer();
			}
			return numberOfSoldiers;
		}
	}

	private double calculatePowerOfDefender(final ICity city) {
		final var clan = city.getClan();
		return city.getDefense() + (city.getNumberOfSoldiers() * city.getBonus() * clan.getSoldiersDefenseStrength()
				* clan.getSoldiersStrength());
	}

	@Override
	public Result calculateResult() {
		return StreamUtils.getCitiesAsStream(this.getCities(), this.getPlayerClan()).count() == 0 ? Result.CPU_WON
				: Result.PLAYER_WON;
	}

	private void cantAttack(final ICity src, final ICity destination) {
		if (src.getClan() == destination.getClan()) {
			throw new IllegalArgumentException("Same clan");
		} else if (src == destination) {
			throw new IllegalArgumentException("Same city");
		} else if (!this.cities.isConnected(src, destination)) {
			throw new IllegalArgumentException("Unreachable");
		}
	}

	private void checkExtinction(final AttackResult result, final IClan destinationClan) {
		if ((result == AttackResult.CITY_CONQUERED) && this.isDead(destinationClan)) {
			this.events.add(new ExtinctionMessage(destinationClan));
		}
	}

	private void checkPreconditions(final boolean managed, final long num) {
		if (managed && (num < 0)) {
			throw new IllegalArgumentException("number of soldiers is smaller than zero!");
		}
	}

	private void cpuPlay() {
		// Skip clan of the player
		final var order = this.clans.stream().filter(a -> !a.isPlayerClan()).collect(Collectors.toList());
		Collections.shuffle(order);
		order.forEach(this::executeCPUPlay);
		this.isPlayersTurn = true;
	}

	@Override
	public int currentRound() {
		return this.currentRound;
	}

	private void eval(final int selector, final int clanOne, final int clanTwo, final Random r) {
		if (this.isDead(this.getClan(clanOne)) || this.isDead(this.getClan(clanTwo))) {
			return;
		}
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

	private boolean isBetween(final int x, final int lower, final int upper) {
		return (lower <= x) && (x <= upper);
	}

	private void events() {
		Stream.of(this.getCities().getValues(new ICity[0])).forEach(a -> {
			final var number = this.random.nextInt(20_000_000);
			var factorOfPeople = 1.0;
			var factorOfSoldiers = 1.0;
			var growthFactor = 1.0;
			RandomEvent re = null;
			if (this.isBetween(number, 0, 300)) {// pestilence
				factorOfPeople = Shared.randomPercentage(30, 80);
				factorOfSoldiers = Shared.randomPercentage(30, 80);
				growthFactor = Shared.randomPercentage(85, 95);
				re = RandomEvent.PESTILENCE;
			} else if (this.isBetween(number, 3200, 3300)) {// fire
				factorOfPeople = Shared.randomPercentage(85, 95);
				factorOfSoldiers = Shared.randomPercentage(80, 90);
				growthFactor = Shared.randomPercentage(80, 95);
				re = RandomEvent.FIRE;
			} else if (this.isBetween(number, 4500, 6500)) {// growth
				factorOfPeople = Shared.randomPercentage(105, 115);
				growthFactor = Shared.randomPercentage(104, 112.5);
				re = RandomEvent.GROWTH;
			} else if (this.isBetween(number, 7900, 8900)) {// bad harvesting
				factorOfPeople = Shared.randomPercentage(85, 90);
				factorOfSoldiers = Shared.randomPercentage(80, 90);
				growthFactor = Shared.randomPercentage(85, 90);
				re = RandomEvent.CROP_FAILURE;
			} else if (this.isBetween(number, 12_670, 12_900)) {// rebellion
				factorOfPeople = Shared.randomPercentage(28, 60);
				factorOfSoldiers = Shared.randomPercentage(20, 85);
				growthFactor = Shared.randomPercentage(15, 30);
				re = RandomEvent.REBELLION;
			} else if (this.isBetween(number, 19_200, 19_330)) {// civil war
				factorOfPeople = Shared.randomPercentage(2, 12);
				factorOfSoldiers = Shared.randomPercentage(20, 45);
				growthFactor = Shared.randomPercentage(2, 30);
				re = RandomEvent.CIVIL_WAR;
			} else if (this.isBetween(number, 239_400, 250_100)) {// migration
				factorOfPeople = Shared.randomPercentage(102, 135);
				factorOfSoldiers = Shared.randomPercentage(100, 103.5);
				growthFactor = Shared.randomPercentage(107, 145);
				re = RandomEvent.MIGRATION;
			} else if (this.isBetween(number, 405_200, 409_900)) {// economic growth
				growthFactor = Shared.randomPercentage(102, 105);
				re = RandomEvent.ECONOMIC_GROWTH;
			} else if (this.isBetween(number, 562_000, 569_900)) {// pandemic
				factorOfPeople = Shared.randomPercentage(2, 35);
				factorOfSoldiers = Shared.randomPercentage(0, 20);
				growthFactor = Shared.randomPercentage(45, 60);
				re = RandomEvent.PANDEMIC;
			} else if (this.isBetween(number, 1_000_000, 5_000_000)) {// accident
				final var numberOfPeople = a.getNumberOfPeople() - (this.random.nextInt(12) + 1);
				a.setNumberOfPeople(numberOfPeople < 0 ? 0 : numberOfPeople);
			} else if (this.isBetween(number, 5_100_100, 5_100_200)) {// alchemic accident
				factorOfPeople = Shared.randomPercentage(92, 99);
				factorOfSoldiers = Shared.randomPercentage(92, 99);
				growthFactor = Shared.randomPercentage(98, 99);
				re = RandomEvent.ACCIDENT;
			} else if (this.isBetween(number, 12_010_000, 12_015_000)) {// sabotage
				factorOfPeople = Shared.randomPercentage(80, 90);
				factorOfSoldiers = Shared.randomPercentage(75, 90);
				growthFactor = Shared.randomPercentage(90, 98);
				re = RandomEvent.SABOTAGE;
			}
			if ((factorOfPeople == 1) && (factorOfSoldiers == 1) && (growthFactor == 1)) {
				return;
			}
			a.setNumberOfPeople((long) (a.getNumberOfPeople() * factorOfPeople));
			a.setNumberOfSoldiers((long) (a.getNumberOfSoldiers() * factorOfSoldiers));
			a.setGrowth(a.getGrowth() * growthFactor);
			this.events.add(new RandomEventMessage(re, factorOfPeople, factorOfSoldiers, growthFactor, a));
		});
	}

	@Override
	public void executeActions() {
		final var start = System.nanoTime();
		this.sanityCheckForGrowth();
		this.sanityCheckForBadCityValues();
		this.sanityCheckForBadClanValues();
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
			if (this.throwableConsumer != null) {
				this.throwableConsumer.accept(throwable);
			}
		}
		this.sanityCheckForGrowth();
		this.sanityCheckForBadCityValues();
		this.sanityCheckForBadClanValues();
		StreamUtils.forEach(this.cities, ICity::endOfRound);
		this.currentRound++;
		final var end = System.nanoTime();
		var diff = ((double) end - start);
		diff /= 1000;// 10^-6 s
		diff /= 1000;// 10^-3 s
		Shared.LOGGER.message("CPUPLAY: " + diff + "ms");
		this.isPlayersTurn = true;
	}

	private void executeCPUPlay(final IClan clan) {
		if (this.isDead(clan)) {
			return;
		}
		clan.getStrategy().applyStrategy(clan, this.cities, this);
		clan.update(this.currentRound);
	}

	@Override
	public void exit(final Result result) {
		this.throwIfNull(result, "result==null");
		this.data.getPlugins().forEach(a -> a.exit(result));
		if (this.resumed) {
			try {
				Shared.deleteDirectory(this.directory);
			} catch (final IOException e) {// We are done, we can simply ignore it, but still log it!
				Shared.LOGGER.exception(e);
				if (this.throwableConsumer != null) {
					this.throwableConsumer.accept(e);
				}
			}
		}
	}

	@Override
	public Image getBackground() {
		return this.background;
	}

	@Override
	public Graph<ICity> getCities() {
		return this.cities;
	}

	public Map<String, CityKeyHandler> getCityKeyHandlers() {
		return this.data.getCityKeyHandlers();
	}

	private IClan getClan(final ICity city) {
		return city.getClan();
	}

	@Override
	public IClan getClan(final int clanId) {
		if ((clanId < 0) || (clanId >= this.numPlayers)) {
			throw new IllegalArgumentException("clanId out of bounds");
		}
		return this.clans.get(clanId);
	}

	@Override
	public List<String> getClanNames() {
		return this.clans.stream().map(IClan::getName).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public List<IClan> getClans() {
		return this.clans;
	}

	@Override
	public List<Double> getCoins() {
		return this.clans.stream().map(IClan::getCoins).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public List<Color> getColors() {
		return this.clans.stream().map(IClan::getColor).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public EventList getEventList() {
		return this.events;
	}

	@Override
	public List<String> getExtraMusic() {
		return this.data.getExtraMusic();
	}

	public Map<String, KeyHandler> getKeybindings() {
		return this.data.getKeybindings();
	}

	@Override
	public int getNumPlayers() {
		return this.numPlayers;
	}

	@Override
	public List<Plugin> getPlugins() {
		return this.data.getPlugins();
	}

	@Override
	public Graph<Integer> getRelations() {
		return this.relations;
	}

	@Override
	public ConquerSaver getSaver(final String name) {
		return new GameSaver(name);
	}

	@Override
	public Version getVersion() {
		return Shared.getReferenceImplementationVersion();
	}

	private List<ICity> getWeakestCityInRatioToSurroundingEnemyCities(final List<ICity> reachableCities) {
		this.throwIfNull(reachableCities, "reachableCities==null");
		reachableCities.forEach(this::throwIfNull);
		return Stream.of(reachableCities.toArray(new City[0])).sorted((a, b) -> {
			final var defense = a.getDefenseStrength();
			final var neighbours = StreamUtils.getCitiesAroundCityNot(this.cities, a, a.getClan())
					.collect(Collectors.toList());
			final var attack = neighbours.stream().mapToDouble(ICity::getNumberOfSoldiers).sum();
			final var defenseB = b.getDefenseStrength();
			final var neighboursB = StreamUtils.getCitiesAroundCityNot(this.cities, b, b.getClan())
					.collect(Collectors.toList());
			final var attackB = neighboursB.stream().mapToDouble(ICity::getNumberOfSoldiers).sum();
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

	public boolean hasResult() {
		final var others = StreamUtils.getCitiesAsStreamNot(this.getCities(), this.getPlayerClan()).count();
		final var player = StreamUtils.getCitiesAsStream(this.getCities(), this.getPlayerClan()).count();
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

	@Override
	public void init() {
		this.data.setPlugins(this.data.getPlugins().stream().filter(a -> a.compatibleTo(this.getVersion()))
				.collect(Collectors.toList()));
		this.context.getStrategies().forEach(provider -> {
			final var idx = provider.getId();
			if (idx >= Game.MAX_STRATEGIES) {
				Shared.LOGGER.error("idx >= " + Game.MAX_STRATEGIES);
			} else if (idx < 0) {
				Shared.LOGGER.error("idx < 0: " + provider.getClass().getCanonicalName());
			} else if ((this.strategies[idx] != null)) {
				Shared.LOGGER.error("Slot " + idx + " is already set!");
			} else {
				this.strategies[idx] = provider;
				Shared.logLevel1(provider.getName() + " has index " + idx);
			}
		});
		this.clans.forEach(a -> a.init(this.strategies, this.getVersion()));
		if (this.data.getPlugins() != null) {
			this.data.getPlugins().forEach(a -> a.init(this));
		}
		this.cities.initCache();
	}

	@Override
	public boolean isDead(final IClan clan) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		return StreamUtils.getCitiesAsStream(this.cities, clan).count() == 0;
	}

	private boolean isInFriendlyCountry(final ICity c) {
		return this.cities.getConnected(c).stream().filter(a -> a.getClan() != c.getClan()).count() == 0;
	}

	@Override
	public boolean isPlayersTurn() {
		return this.isPlayersTurn;
	}

	@Override
	public long maximumNumberOfSoldiersToRecruit(final IClan clan, final long limit) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		if (limit < 0) {
			throw new IllegalArgumentException("limit < 0");
		}
		final var resourcesOfClan = clan.getResources();
		final List<Long> numbers = new ArrayList<>();
		final var costs = this.getSoldierCosts();
		numbers.add(limit);
		numbers.add((long) (clan.getCoins() / costs.coinsPerSoldierInitial()));
		numbers.add((long) (resourcesOfClan.get(Resource.IRON.getIndex()) / costs.ironPerSoldierInitial()));
		numbers.add((long) (resourcesOfClan.get(Resource.WOOD.getIndex()) / costs.woodPerSoldierInitial()));
		numbers.add((long) (resourcesOfClan.get(Resource.STONE.getIndex()) / costs.stonePerSoldierInitial()));
		Collections.sort(numbers);
		return numbers.get(0);
	}

	@Override
	public long maximumNumberToMove(final IClan clan, final double distance, final long numberOfSoldiers) {
		if (distance < 0) {
			throw new IllegalArgumentException("distance < 0 : " + distance);
		}
		if (numberOfSoldiers < 0) {
			throw new IllegalArgumentException("numberOfSoldiers < 0 : " + numberOfSoldiers);
		}
		final var costs = this.getSoldierCosts();
		final var maxPay = (long) (clan.getCoins()
				/ (costs.coinsPerMoveOfSoldierBase() + (costs.coinsPerMovePerSoldier() * distance)));
		return Math.min(numberOfSoldiers, maxPay);
	}

	private List<ICity> moveSoldiersCheck(final boolean managed, final ICity src, final Stream<ICity> reachableCities,
			final ICity other, final long numberOfSoldiersToMove) {
		this.throwIfNull(src, "src==null");
		if (!managed && (reachableCities == null)) {
			throw new IllegalArgumentException("Not managed, but reachableCities==null");
		} else if (managed) {
			if (numberOfSoldiersToMove < 0) {
				throw new IllegalArgumentException("num < 0 : " + numberOfSoldiersToMove);
			} else if (src.getClan() != other.getClan()) {
				throw new IllegalArgumentException("src.clan!=destination.clan");
			} else if (numberOfSoldiersToMove > src.getNumberOfSoldiers()) {
				throw new IllegalArgumentException("numberOfSoldiersToMove > src.numberOfSoldiers");
			}
		}
		return reachableCities == null ? new ArrayList<>() : reachableCities.collect(Collectors.toList());
	}

	private long calculateMoveAmount(final ICity src, final ICity destination) {
		long moveAmount;
		if (this.isInFriendlyCountry(src)) {
			// This city has no connections to other clans==>Move all troops to the borders.
			moveAmount = src.getNumberOfSoldiers();
		} else {// Which city was attacked more often by the player?
			final var destinationIsCity = destination instanceof City;
			final var sourceIsCity = src instanceof City;
			// Workaround to allow deterministic output
			final var bool3 = (destinationIsCity && sourceIsCity)
					? ((City) src).getNumberAttacksOfPlayer() > ((City) destination).getNumberAttacksOfPlayer()
					: false;
			if (bool3) {
				moveAmount = (int) (0.7 * src.getNumberOfSoldiers());
			} else {
				moveAmount = (int) (0.3 * src.getNumberOfSoldiers());
			}
		}
		return moveAmount;
	}

	@Override
	public void moveSoldiers(final ICity src, final Stream<ICity> reachableCities, final boolean managed,
			final ICity other, final long numberOfSoldiersToMove) {
		final var saved = this.moveSoldiersCheck(managed, src, reachableCities, other, numberOfSoldiersToMove);
		ICity destination;
		List<ICity> list = null;
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
		if (src == destination) {// This should theoretically be a critical error,....
			return;
		}
		long moveAmount;
		if (!managed) {
			moveAmount = this.calculateMoveAmount(src, destination);
			moveAmount = this.maximumNumberToMove(src.getClan(), src, destination, moveAmount);
			if (moveAmount == 0) {
				return;
			}
		} else {
			moveAmount = numberOfSoldiersToMove;
		}
		destination.setNumberOfSoldiers(destination.getNumberOfSoldiers() + moveAmount);
		this.payForMove(src.getClan(), moveAmount, this.getCities().getWeight(src, destination));
		src.setNumberOfSoldiers(src.getNumberOfSoldiers() - moveAmount);
		final var finalMoveAmout = moveAmount;
		this.data.getMoveHooks().forEach(a -> a.handleMove(src, destination, finalMoveAmout));
	}

	private long numberOfSurvivingDefenders(final double diff, final ICity destination,
			final IClan destinationClanObj) {
		var remainingSoldiersDefender = (diff - destination.getDefense());
		remainingSoldiersDefender /= destination.getBonus();
		remainingSoldiersDefender /= destinationClanObj.getSoldiersDefenseStrength();
		remainingSoldiersDefender /= destinationClanObj.getSoldiersStrength();
		return (long) Math.abs(remainingSoldiersDefender);
	}

	@Override
	public boolean onlyOneClanAlive() {
		return StreamUtils.getCitiesAsStream(this.cities).map(ICity::getClan).distinct().count() == 1;
	}

	private void pay(final IClan srcClan, final double subtract) {
		srcClan.setCoins(srcClan.getCoins() - subtract);
	}

	private void payForMove(final IClan srcClan, final long numSoldiers, final double weight) {
		final var costs = this.getSoldierCosts();
		this.pay(srcClan,
				numSoldiers * (costs.coinsPerMoveOfSoldierBase() + (costs.coinsPerMovePerSoldier() * weight)));
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
			final var clan = city.getClan();
			final var resourcesOfClan = clan.getResources();
			for (var i = 0; i < resourcesOfClan.size(); i++) {
				final var productions = (city.getNumberOfPeople() * city.getProductions().get(i));
				resourcesOfClan.set(i, resourcesOfClan.get(i) + productions);
				clan.getResourceStats().set(i, clan.getResourceStats().get(i) + productions);
			}
		});
	}

	@Override
	public void recruitSoldiers(final double maxToPay, final ICity c, final boolean managed, final long count) {
		this.throwIfNull(c, "c==null");
		final var clan = c.getClan();
		if ((!managed) && (maxToPay < 0)) {
			throw new IllegalArgumentException("maxToPay < 0 :" + maxToPay);
		} else if (managed && (count < 0)) {
			throw new IllegalArgumentException("count < 0 :" + count);
		}
		final var costs = this.getSoldierCosts();
		var numberToRecruit = 0L;
		// Default algorithm used, if the strategy didn't provide one itself.
		if (!managed) {
			if ((maxToPay < 0) || (c.getNumberOfPeople() < Game.RETAINED_PEOPLE)) {
				return;
			}
			numberToRecruit = (int) (maxToPay / costs.coinsPerSoldierInitial());
			numberToRecruit = Math.min(c.getNumberOfPeople() - Game.RETAINED_PEOPLE, numberToRecruit);
			numberToRecruit = this.maximumNumberOfSoldiersToRecruit(clan, numberToRecruit);
			if (numberToRecruit == 0) {
				return;
			}
		} else {
			numberToRecruit = Math.min(this.maximumNumberOfSoldiersToRecruit(clan, count), count);
		}
		c.setNumberOfPeople(c.getNumberOfPeople() - numberToRecruit);
		c.setNumberOfSoldiers(c.getNumberOfSoldiers() + numberToRecruit);
		final var resourcesOfClan = clan.getResources();
		final var ironNew = resourcesOfClan.get(Resource.IRON.getIndex())
				- (numberToRecruit * costs.ironPerSoldierInitial());
		final var woodNew = resourcesOfClan.get(Resource.WOOD.getIndex())
				- (numberToRecruit * costs.woodPerSoldierInitial());
		final var stoneNew = resourcesOfClan.get(Resource.STONE.getIndex())
				- (numberToRecruit * costs.stonePerSoldierInitial());
		resourcesOfClan.set(Resource.IRON.getIndex(), ironNew);
		resourcesOfClan.set(Resource.WOOD.getIndex(), woodNew);
		resourcesOfClan.set(Resource.STONE.getIndex(), stoneNew);
		clan.setCoins(clan.getCoins() - (numberToRecruit * costs.coinsPerSoldierInitial()));
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
			if (this.isDead(this.clans.get(clanOne))) {
				continue;
			}
			var clanTwo = r.nextInt(size);
			while ((clanTwo == clanOne) && !this.isDead(this.clans.get(clanTwo))) {
				clanTwo = r.nextInt(size);
			}
			this.eval(selector, clanOne, clanTwo, r);
		}
	}

	void resume(final String name) {
		this.resumed = true;
		this.directory = new File(Shared.SAVE_DIRECTORY, name);
	}

	private void sanityCheckForBadCityValues() {
		StreamUtils.getCitiesAsStream(this.cities).forEach(city -> {
			if (city == null) {
				throw new InternalError("null value in city graph!");
			}
			if (this.bad(city.getBonus())) {
				throw new InternalError(city.getName() + " has a bonus lower equals zero!");
			}
			if (city.getClan() == null) {
				throw new InternalError(city.getName() + " has null clan!");
			}
			if (city.getClanId() < 0) {
				throw new InternalError(city.getName() + " has clanId < 0");
			}
			if (this.bad(city.getDefense())) {
				throw new InternalError(city.getName() + " has defense < 0 or is NaN or infinite!");
			}
			if (this.bad(city.getGrowth())) {
				throw new InternalError(city.getName() + " has a negative growth");
			}
			if (city.getImage() == null) {
				throw new InternalError(city.getName() + " has a null image");
			}
			if (city.getLevels().size() != (Resource.values().length + 1)) {
				throw new InternalError(city.getName() + " has an invalid size of the levels");
			}
			this.sanityCheckOfLevels(city.getLevels(), city);
			if (city.getNumberOfPeople() < 0) {
				throw new InternalError(city.getName() + " has a negative population");
			}
			if (city.getNumberOfSoldiers() < 0) {
				throw new InternalError(city.getName() + " has a negative number of soliders");
			}
			if (city.getX() < 0) {
				throw new InternalError(city.getName() + "has bad X-Position");
			}
			if (city.getY() < 0) {
				throw new InternalError(city.getName() + "has bad Y-Position");
			}
			if (city.getProductions().size() != Resource.values().length) {
				throw new InternalError(city.getName() + " has an invalid size of the productions");
			}
			this.sanityCheckOfProductions(city.getProductions(), city);
		});
	}

	private void sanityCheckForBadClanValues() {
		this.clans.forEach(clan -> {
			if (clan == null) {
				throw new InternalError("null value in clans");
			}
			if (this.bad(clan.getCoins())) {
				throw new InternalError(clan.getName() + " has a bad number of coins: " + clan.getCoins());
			}
			if (clan.getId() < 0) {
				throw new InternalError(clan.getName() + " has a bad id: " + clan.getId());
			}
			if (clan.getResources().size() != Resource.values().length) {
				throw new InternalError(
						clan.getName() + "has a bad size of the resources list: " + clan.getResources().size());
			}
			this.sanityCheckResources(clan);
			if (clan.getResourceStats().size() != Resource.values().length) {
				throw new InternalError(clan.getName() + "has a bad size of the resource stats list: "
						+ clan.getResourceStats().size());
			}
			this.sanityCheckResourceStats(clan);
		});
	}

	private void sanityCheckForGrowth() {
		StreamUtils.forEach(this.cities, a -> a.getGrowth() > Game.GROWTH_LIMIT,
				a -> a.setGrowth(a.getGrowth() * Game.GROWTH_REDUCE_FACTOR));
		StreamUtils.forEach(this.cities, a -> {
			while (a.getGrowth() > Game.ALTERNATIVE_GROWTH_LIMIT) {
				a.setGrowth(a.getGrowth() * Game.WEAK_GROWTH_REDUCE_FACTOR);
			}
			if ((a.getNumberOfPeople() > Game.SOFT_POPULATION_LIMIT) && (a.getGrowth() > 0.8)) {
				a.setGrowth(a.getGrowth() * 0.6);
			}
		});
	}

	private void sanityCheckOfLevels(final List<Integer> levels, final ICity city) {
		levels.forEach(a -> {
			if (a == null) {
				throw new InternalError(city.getName() + " has null value in levels");
			} else if ((a < 0) || (a > this.getMaximumLevel())) {
				throw new InternalError(city.getName() + " has a too small/too big value in levels: " + a);
			}
		});
	}

	private void sanityCheckOfProductions(final List<Double> productions, final ICity city) {
		productions.forEach(a -> {
			if (a == null) {
				throw new InternalError(city.getName() + " has null value in productions");
			} else if (this.bad(a)) {
				throw new InternalError(city.getName() + " has a bad value in productions: " + a);
			}
		});
	}

	private void sanityCheckResources(final IClan clan) {
		clan.getResources().forEach(value -> {
			if (value == null) {
				throw new InternalError(clan.getName() + " has a null value in resources");
			}
			if (this.bad(value)) {
				throw new InternalError(clan.getName() + "has a bad value in resources: " + value);
			}
		});
	}

	private void sanityCheckResourceStats(final IClan clan) {
		clan.getResourceStats().forEach(value -> {
			if (value == null) {
				throw new InternalError(clan.getName() + " has a null value in resource stats");
			}
			if (Double.isNaN(value) || Double.isInfinite(value)) {
				throw new InternalError(clan.getName() + "has a bad value in resource stats: " + value);
			}
		});
	}

	@Override
	public boolean sendGift(final IClan source, final IClan destination, final Gift gift) {
		this.throwIfNull(source, "source==null");
		this.throwIfNull(destination, "destination==null");
		this.throwIfNull(gift, "gift==null");
		this.throwIfNull(this.playerGiftCallback, "playerGiftCallback==null");
		if (source == destination) {
			throw new IllegalArgumentException("source==destination");
		} else if (this.isDead(destination)) {
			throw new IllegalArgumentException("Destination clan is extincted!");
		}
		if ((gift.getNumberOfCoins() == 0)
				&& (gift.getMap().entrySet().stream().filter(a -> a.getValue() != 0).count() == 0)) {
			return false;
		}
		boolean acceptedGift;
		final DoubleConsumer dc = newValue -> {
			final var d = newValue < 0 ? 0 : (newValue > 100 ? 100 : newValue);
			this.relations.addUndirectedEdge(source.getId(), destination.getId(), d);
		};
		final var relationship = this.getRelationship(source, destination);
		if (!destination.isPlayerClan()) {
			acceptedGift = destination.getStrategy().acceptGift(source, destination, gift, relationship, dc, this);
		} else {
			acceptedGift = this.playerGiftCallback.acceptGift(source, destination, gift, relationship, dc, this);
		}
		if (acceptedGift) {
			this.calculateChanges(source, destination, gift);
		}
		return acceptedGift;
	}

	private void calculateChanges(final IClan source, final IClan destination, final Gift gift) {
		if (source.getCoins() < gift.getNumberOfCoins()) {
			throw new IllegalArgumentException("More coins were gifted than available!");
		}
		source.setCoins(source.getCoins() - gift.getNumberOfCoins());
		destination.setCoins(destination.getCoins() + gift.getNumberOfCoins());
		final var a = source.getResources();
		final var b = destination.getResources();
		gift.getMap().entrySet().forEach(d -> {
			final var index = d.getKey().getIndex();
			final var value = d.getValue();
			if (a.get(index) < value) {
				throw new IllegalArgumentException("More " + d.getKey().getName() + " was gifted than available: ("
						+ a.get(index) + "/" + value + ")");
			}
			a.set(index, a.get(index) - value);
			b.set(index, b.get(index) + value);
		});
	}

	@Override
	public double getRelationship(final IClan a, final IClan b) {
		this.throwIfNull(a);
		this.throwIfNull(b);
		if (a == b) {
			throw new IllegalArgumentException("clanA==clanB");
		}
		return this.relations.getWeight(a.getId(), b.getId());
	}

	void setBackground(final Image gi) {
		this.throwIfNull(gi, "gi==null");
		if (this.background != null) {
			throw new UnsupportedOperationException("Can't change image!");
		}
		this.background = gi;
	}

	void setClans(final List<IClan> clans) {
		this.throwIfNull(clans, "clans==null");
		if (this.clans != null) {
			throw new UnsupportedOperationException("Can't change clans!");
		}
		this.clans = clans;
	}

	@Override
	public void setErrorHandler(final Consumer<Throwable> handler) {
		this.throwableConsumer = handler;
	}

	void setGraph(final Graph<ICity> g) {
		this.throwIfNull(g, "g==null");
		if (this.cities != null) {
			throw new UnsupportedOperationException("Can't change graph!");
		}
		this.cities = g;
	}

	@Override
	public void setPlayerGiftCallback(final PlayerGiftCallback pgc) {
		this.throwIfNull(pgc, "PlayerGiftCallback==null");
		this.playerGiftCallback = pgc;
	}

	void setPlayers(final int numPlayers) {
		if (numPlayers <= 0) {
			throw new IllegalArgumentException("numPlayers<=0");
		} else if (this.numPlayers != -1) {
			throw new UnsupportedOperationException("Can't change number of players");
		}
		this.numPlayers = numPlayers;
	}

	@Override
	public void setPlayersTurn(final boolean b) {
		this.isPlayersTurn = b;
	}

	void setPlugins(final List<Plugin> plugins) {
		this.data.setPlugins(plugins);
	}

	void setRelations(final Graph<Integer> relations) {
		this.throwIfNull(relations, "relations==null");
		if (this.relations != null) {
			throw new UnsupportedOperationException("Can't change relations");
		}
		this.relations = relations;
	}

	void setRound(final int r) {
		this.currentRound = r;
	}

	private double setup(final long powerOfAttacker, final ICity src, final ICity destination) {
		final var srcClan = src.getClan();
		this.payForMove(srcClan, powerOfAttacker, this.getCities().getWeight(src, destination));
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
	public boolean upgradeDefense(final ICity city) {
		this.throwIfNull(city, "city==null");
		final var levels = city.getLevels();
		final var costs = this.getPlayerClan().costs(levels.get(Resource.values().length) + 1);
		final var clan = city.getClan();
		if ((costs > clan.getCoins()) || (levels.get(Resource.values().length) == this.getMaximumLevel())) {
			return false;
		}
		clan.setCoins(clan.getCoins() - costs);
		var defense = city.getDefense();
		defense = defense < 1 ? 1 : defense;
		city.setDefense(this.getPlayerClan().newPowerOfUpdate(levels.get(Resource.values().length) + 1, defense));
		levels.set(Resource.values().length, levels.get(Resource.values().length) + 1);
		return true;
	}

	@Override
	public void upgradeDefenseFully(final ICity city) {
		this.throwIfNull(city, "city==null");
		var shouldNotBreak = true;
		while (shouldNotBreak) {
			shouldNotBreak = this.upgradeDefense(city);
		}
	}

	@Override
	public boolean upgradeResource(final Resource resc, final ICity city) {
		this.throwIfNull(city, "city==null");
		this.throwIfNull(resc, "resc==null");
		final var index = resc.getIndex();
		final var levels = city.getLevels();
		final var costs = this.getPlayerClan().costs(levels.get(index) + 1);
		final var clan = city.getClan();
		if ((costs > clan.getCoins()) || (levels.get(index) == this.getMaximumLevel())) {
			return false;
		}
		clan.setCoins(clan.getCoins() - costs);
		city.getProductions().set(resc.getIndex(),
				this.getPlayerClan().newPowerOfUpdate(levels.get(index + 1), city.getProductions().get(index)));
		levels.set(resc.getIndex(), levels.get(index) + 1);
		return true;
	}

	@Override
	public void upgradeResourceFully(final Resource resources, final ICity city) {
		this.throwIfNull(city, "city==null");
		this.throwIfNull(resources, "resources==null");
		var shouldNotBreak = true;
		while (shouldNotBreak) {
			shouldNotBreak = this.upgradeResource(resources, city);
		}
	}

	private void useResources() {
		StreamUtils.forEach(this.cities, city -> {
			final var resources2 = city.getClan().getResources();
			final var stats = city.getClan().getResourceStats();
			final var copy = new GoodDoubleList(true);
			for (var i = 0; i < Resource.values().length; i++) {
				final var va = this.getResourceUsage().get(i);
				final var use = ((city.getNumberOfSoldiers() * va[1]) + (city.getNumberOfPeople() * va[0]));
				copy.add(resources2.get(i) - use);
				stats.set(i, stats.get(i) - use);
			}
			for (var i = 0; i < Resource.values().length; i++) {
				if (copy.get(i) < 0) {
					copy.set(i, 0.0);
				}
				resources2.set(i, copy.get(i));
			}
			this.data.getResourceHooks().forEach(a -> a.analyzeStats(city, stats, city.getClan()));
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

	@Override
	public IClan getPlayerClan() {
		return this.clans.get(0);
	}

}
