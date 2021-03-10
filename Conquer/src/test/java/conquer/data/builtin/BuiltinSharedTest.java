package conquer.data.builtin;

import conquer.data.Gift;
import conquer.data.IClan;
import conquer.data.Resource;
import conquer.data.Version;
import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyData;
import conquer.data.strategy.StrategyProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BuiltinSharedTest {
	@Test
	void testClanSum() {
		final var clan = new IClan() {

			@Override
			public double getCoins() {
				return 100;
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
				return null;
			}

			@Override
			public List<Double> getResources() {
				final var ret = new ArrayList<Double>();
				for (int i = 0; i < Resource.values().length; i++) {
					ret.add(10.0);
				}
				return ret;
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
		};
		Assertions.assertEquals(190.0d, BuiltinShared.sum(clan));
	}

	@Test
	void testClanSumNull() {
		Assertions.assertThrows(InternalError.class, () -> {
			BuiltinShared.sum((IClan) null);
		});
	}

	@Test
	void testGiftSum() {
		Assertions.assertEquals(190.0d, BuiltinShared.sum(new Gift(List.of(10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0), 100.0d)));
	}

	@Test
	void testGiftSumNull() {
		Assertions.assertThrows(InternalError.class, () -> {
			BuiltinShared.sum((Gift) null);
		});
	}
}
