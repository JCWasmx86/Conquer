package org.jel.game.data.ri;

import java.util.ArrayList;
import java.util.Collection;

//A list, that only allows non-negative, non-NaN, non-infinity values
class GoodDoubleList extends ArrayList<Double> {
	private static final long serialVersionUID = 1502828784649438269L;
	private boolean allowNegative = false;

	GoodDoubleList(Collection<Double> initial) {
		this.addAll(initial);
	}

	GoodDoubleList(Collection<Double> initial, boolean b) {
		this(b);
		this.addAll(initial);
	}

	GoodDoubleList() {
		this(false);
	}

	GoodDoubleList(boolean b) {
		this.allowNegative = b;
	}

	@Override
	public Double set(final int index, final Double element) {
		this.check(element);
		return super.set(index, element);
	}

	private void check(final Double element) {
		if (element == null) {
			throw new IllegalArgumentException("element==null");
		} else if ((!allowNegative) && element < 0) {
			throw new IllegalArgumentException("element < 0: " + element);
		} else if (Double.isNaN(element)) {
			throw new IllegalArgumentException("element is NaN");
		} else if (Double.isInfinite(element)) {
			throw new IllegalArgumentException("element is infinite");
		}
	}

	@Override
	public boolean add(final Double e) {
		this.check(e);
		return super.add(e);
	}

	@Override
	public void add(final int index, final Double element) {
		this.check(element);
		super.add(index, element);
	}

	@Override
	public boolean addAll(final Collection<? extends Double> c) {
		c.forEach(this::check);
		return super.addAll(c);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends Double> c) {
		c.forEach(this::check);
		return super.addAll(index, c);
	}

}
