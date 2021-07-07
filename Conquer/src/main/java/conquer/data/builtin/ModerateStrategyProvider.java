package conquer.data.builtin;

import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyProvider;

public final class ModerateStrategyProvider implements StrategyProvider {

    @Override
    public Strategy buildStrategy() {
        return new ModerateStrategyImpl();
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getName() {
        return "moderate";
    }

}
