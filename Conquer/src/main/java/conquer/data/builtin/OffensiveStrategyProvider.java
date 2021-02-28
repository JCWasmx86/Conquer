package conquer.data.builtin;

import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyProvider;

public final class OffensiveStrategyProvider implements StrategyProvider {

    @Override
    public Strategy buildStrategy() {
        return new OffensiveStrategyImpl();
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public String getName() {
        return "offensive";
    }

}
