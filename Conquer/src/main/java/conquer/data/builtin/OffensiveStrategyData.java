package conquer.data.builtin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import conquer.data.Shared;
import conquer.data.strategy.StrategyData;

public final class OffensiveStrategyData implements StrategyData {
<<<<<<< HEAD
	private static final int MAX_ROUND_NUMBER = 20;
	private final Random random;
	private int counter;
	private OffensiveStrategy action;

	public OffensiveStrategyData() {
		this.random = new Random(System.nanoTime());
		this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
		this.action = OffensiveStrategy.EXPAND;
	}

	OffensiveStrategyData(final byte[] dataBytes) {
		this();
		this.action = OffensiveStrategy.values()[dataBytes[0]];
		this.counter = ByteBuffer.wrap(dataBytes, 1, dataBytes.length - 1).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	public OffensiveStrategy getAction() {
		return this.action;
	}

	@Override
	public void save(final OutputStream out) throws IOException {
		out.write(this.action.ordinal());
		out.write(ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN).putInt(this.counter).array());
	}

	@Override
	public void update(final int currentRound) {
		this.counter--;
		if (this.counter == 0) {
			if (this.action == OffensiveStrategy.EXPAND) {
				this.action = OffensiveStrategy.UPGRADE;
				this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
				Shared.logLevel1(
					"Offensive-strategy: " + OffensiveStrategy.UPGRADE + " for " + this.counter + " rounds");

			} else {
				this.action = OffensiveStrategy.EXPAND;
				this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
				Shared.logLevel1(
					"Offensive-strategy: " + OffensiveStrategy.EXPAND + " for " + this.counter + " rounds");
			}
		}
	}
=======
    private static final int MAX_ROUND_NUMBER = 20;
    private int counter;
    private OffensiveStrategy action;
    private final Random random;

    public OffensiveStrategyData() {
        this.random = new Random(System.nanoTime());
        this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
        this.action = OffensiveStrategy.EXPAND;
    }

    OffensiveStrategyData(final byte[] dataBytes) {
        this();
        this.action = OffensiveStrategy.values()[dataBytes[0]];
        this.counter = ByteBuffer.wrap(dataBytes, 1, dataBytes.length - 1).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public OffensiveStrategy getAction() {
        return this.action;
    }

    @Override
    public void save(final OutputStream out) throws IOException {
        out.write(this.action.ordinal());
        out.write(ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN).putInt(this.counter).array());
    }

    @Override
    public void update(final int currentRound) {
        this.counter--;
        if (this.counter == 0) {
            if (this.action == OffensiveStrategy.EXPAND) {
                this.action = OffensiveStrategy.UPGRADE;
                this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
                Shared.logLevel1(
                        "Offensive-strategy: " + OffensiveStrategy.UPGRADE + " for " + this.counter + " rounds");

            } else {
                this.action = OffensiveStrategy.EXPAND;
                this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
                Shared.logLevel1(
                        "Offensive-strategy: " + OffensiveStrategy.EXPAND + " for " + this.counter + " rounds");
            }
        }
    }
>>>>>>> parent of f8bbb68 (Formatting)
}
