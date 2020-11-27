package org.jel.game.utils;

public class Triple<T1, T2, T3> {
	private final T1 t1;
	private final T2 t2;
	private final T3 t3;

	public Triple(final T1 t1, final T2 t2, final T3 t3) {
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
	}

	public T1 first() {
		return this.t1;
	}

	public T2 second() {
		return this.t2;
	}

	public T3 third() {
		return this.t3;
	}

	@Override
	public String toString() {
		return "[" + this.t1 + ";" + this.t2 + ";" + this.t3 + "]";
	}
}
