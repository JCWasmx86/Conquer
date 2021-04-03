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

class BuiltinSharedTest {
	@Test
	void testClanSum() {
		final var clan = new IClan() {

			@Override
			public double getCoins() {
				return 100;
			}

			@Override
			public void setCoins(final double coins) {
				//Unused
			}

			@Override
			public Color getColor() {
				return null;
			}

			@Override
			public void setColor(final Color color) {
				//Unused
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
			public void setFlags(final int flags) {
				//Unused
			}

			@Override
			public int getId() {
				return 0;
			}

			@Override
			public void setId(final int id) {
				//Unused
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void setName(final String name) {
				//Unused
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
			public void setResources(final List<Double> resources) {
				//Unused
			}

			@Override
			public List<Double> getResourceStats() {
				return null;
			}

			@Override
			public void setResourceStats(final List<Double> resourceStats) {
				//Unused
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
			public void setStrategy(final Strategy strategy) {
				//Unused
			}

			@Override
			public void init(final StrategyProvider[] strategies, final Version version) {
				//Unused
			}

			@Override
			public boolean isPlayerClan() {
				return false;
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
			public void setStrategyData(final StrategyData strategyData) {
				//Unused
			}

			@Override
			public void update(final int currentRound) {
				//Unused
			}
		};
		Assertions.assertEquals(190.0d, BuiltinShared.sum(clan));
	}

	@Test
	void testClanSumNull() {
		Assertions.assertThrows(InternalError.class, () -> BuiltinShared.sum((IClan) null));
	}

	@Test
	void testGiftSum() {
		Assertions.assertEquals(190.0d, BuiltinShared.sum(new Gift(List.of(10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0,
				10.0, 10.0), 100.0d)));
	}

	@Test
	void testGiftSumNull() {
		Assertions.assertThrows(InternalError.class, () -> BuiltinShared.sum((Gift) null));
	}
}
