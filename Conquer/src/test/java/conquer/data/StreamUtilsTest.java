package conquer.data;


import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyData;
import conquer.data.strategy.StrategyProvider;
import conquer.utils.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StreamUtilsTest {

	private List<IClan> clans;
	private Graph<ICity> cities;
	private List<ICity> cityList;

	@BeforeAll
	public void setupDummyData() {
		this.clans = new ArrayList<>();
		for (var i = 0; i < 4; i++) {
			this.clans.add(new DummyClan("DummyClan_" + i));
		}
		this.cityList = new ArrayList<>();
		this.cityList.add(new DummyCity(this.clans.get(0), "Clan_0_0"));
		this.cityList.add(new DummyCity(this.clans.get(0), "Clan_0_1"));
		this.cityList.add(new DummyCity(this.clans.get(0), "Clan_0_2"));
		this.cityList.add(new DummyCity(this.clans.get(0), "Clan_0_3"));
		this.cityList.add(new DummyCity(this.clans.get(1), "Clan_1_0"));
		this.cityList.add(new DummyCity(this.clans.get(1), "Clan_1_1"));
		this.cityList.add(new DummyCity(this.clans.get(1), "Clan_1_2"));
		this.cityList.add(new DummyCity(this.clans.get(1), "Clan_1_3"));
		this.cityList.add(new DummyCity(this.clans.get(2), "Clan_2_0"));
		this.cityList.add(new DummyCity(this.clans.get(2), "Clan_2_1"));
		this.cityList.add(new DummyCity(this.clans.get(2), "Clan_2_2"));
		this.cityList.add(new DummyCity(this.clans.get(2), "Clan_2_3"));
		this.cityList.add(new DummyCity(this.clans.get(3), "Clan_3_0"));
		this.cityList.add(new DummyCity(this.clans.get(3), "Clan_3_1"));
		this.cityList.add(new DummyCity(this.clans.get(3), "Clan_3_2"));
		this.cityList.add(new DummyCity(this.clans.get(3), "Clan_3_3"));
		this.cities = new Graph<>(this.cityList.size());
		this.cityList.forEach(this.cities::add);
		this.cities.addUndirectedEdge(0, 1, 1.0);
		this.cities.addUndirectedEdge(1, 2, 1.0);
		this.cities.addUndirectedEdge(1, 3, 1.0);
		this.cities.addUndirectedEdge(3, 4, 1.0);
		this.cities.addUndirectedEdge(2, 4, 1.0);
		this.cities.addUndirectedEdge(4, 5, 1.0);
		this.cities.addUndirectedEdge(4, 6, 1.0);
		this.cities.addUndirectedEdge(5, 7, 1.0);
		this.cities.addUndirectedEdge(7, 8, 1.0);
		this.cities.addUndirectedEdge(8, 9, 1.0);
		this.cities.addUndirectedEdge(9, 10, 1.0);
		this.cities.addUndirectedEdge(10, 11, 1.0);
		this.cities.addUndirectedEdge(10, 12, 1.0);
		this.cities.addUndirectedEdge(12, 15, 1.0);
		this.cities.addUndirectedEdge(13, 15, 1.0);
		this.cities.addUndirectedEdge(13, 14, 1.0);
		Assertions.assertTrue(this.cities.isConnected(), "Cities aren't connected!");
	}

	@Test
	void testForEach() {
		final var counter = new AtomicInteger();
		StreamUtils.forEach(this.cities, a -> counter.incrementAndGet());
		Assertions.assertEquals(counter.get(), 16, "Didn't visit all cities");
	}

	class DummyCity implements ICity {
		private IClan clan;
		private String name;

		public DummyCity(final IClan clan, final String name) {
			this.clan = clan;
			this.name = name;
		}

		@Override
		public void endOfRound() {

		}

		@Override
		public double getBonus() {
			return 0;
		}

		@Override
		public IClan getClan() {
			return this.clan;
		}

		@Override
		public int getClanId() {
			return 0;
		}

		@Override
		public double getCoinDiff() {
			return 0;
		}

		@Override
		public double getDefense() {
			return 0;
		}

		@Override
		public double getDefenseStrength() {
			return 0;
		}

		@Override
		public double getGrowth() {
			return 0;
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public ConquerInfo getInfo() {
			return null;
		}

		@Override
		public List<Integer> getLevels() {
			return null;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public long getNumberOfPeople() {
			return 0;
		}

		@Override
		public long getNumberOfSoldiers() {
			return 0;
		}

		@Override
		public List<Double> getProductions() {
			return null;
		}

		@Override
		public int getX() {
			return 0;
		}

		@Override
		public int getY() {
			return 0;
		}

		@Override
		public boolean isPlayerCity() {
			return false;
		}

		@Override
		public double productionPerRound(Resource resource) {
			return 0;
		}

		@Override
		public void setClan(IClan clan) {

		}

		@Override
		public void setDefense(double newPowerOfUpdate) {

		}

		@Override
		public void setGrowth(double growth) {

		}

		@Override
		public void setNumberOfPeople(long numberOfPeople) {

		}

		@Override
		public void setNumberOfSoldiers(long numberOfSoldiers) {

		}

		@Override
		public int compareTo(ICity o) {
			return 0;
		}
	}

	class DummyClan implements IClan {

		private final String name;

		DummyClan(final String name) {
			this.name = name;
		}

		@Override
		public double getCoins() {
			return 0;
		}

		@Override
		public Color getColor() {
			return null;
		}

		@Override
		public StrategyData getData() {
			return null;
		}

		@Override
		public int getFlags() {
			return 0;
		}

		@Override
		public int getId() {
			return 0;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public List<Double> getResources() {
			return null;
		}

		@Override
		public List<Double> getResourceStats() {
			return null;
		}

		@Override
		public int getSoldiersDefenseLevel() {
			return 0;
		}

		@Override
		public double getSoldiersDefenseStrength() {
			return 0;
		}

		@Override
		public int getSoldiersLevel() {
			return 0;
		}

		@Override
		public int getSoldiersOffenseLevel() {
			return 0;
		}

		@Override
		public double getSoldiersOffenseStrength() {
			return 0;
		}

		@Override
		public double getSoldiersStrength() {
			return 0;
		}

		@Override
		public Strategy getStrategy() {
			return null;
		}

		@Override
		public void init(StrategyProvider[] strategies, Version version) {

		}

		@Override
		public boolean isPlayerClan() {
			return false;
		}

		@Override
		public void setCoins(double coins) {

		}

		@Override
		public void setColor(Color color) {

		}

		@Override
		public void setFlags(int flags) {

		}

		@Override
		public void setId(int id) {

		}

		@Override
		public void setName(String name) {

		}

		@Override
		public void setResources(List<Double> resources) {

		}

		@Override
		public void setResourceStats(List<Double> resourceStats) {

		}

		@Override
		public boolean upgradeSoldiersDefense() {
			return false;
		}

		@Override
		public boolean upgradeSoldiers() {
			return false;
		}

		@Override
		public boolean upgradeSoldiersOffense() {
			return false;
		}

		@Override
		public void setStrategy(Strategy strategy) {

		}

		@Override
		public void setStrategyData(StrategyData strategyData) {

		}

		@Override
		public void update(int currentRound) {

		}
	}

}