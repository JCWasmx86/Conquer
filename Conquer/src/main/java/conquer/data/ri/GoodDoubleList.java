package conquer.data.ri;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

//A list, that only allows non-negative, non-NaN, non-infinity values
<<<<<<< HEAD
final class GoodDoubleList extends ArrayList<Double> {
	@Serial
	private static final long serialVersionUID = 1502828784649438269L;
	private boolean allowNegative;
=======
class GoodDoubleList extends ArrayList<Double> {
    private static final long serialVersionUID = 1502828784649438269L;
    private boolean allowNegative = false;
>>>>>>> parent of f8bbb68 (Formatting)

    GoodDoubleList(final Collection<Double> initial) {
        this.addAll(initial);
    }

    GoodDoubleList(final Collection<Double> initial, final boolean b) {
        this(b);
        this.addAll(initial);
    }

    GoodDoubleList() {
        this(false);
    }

    GoodDoubleList(final boolean b) {
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
        } else if ((!this.allowNegative) && (element < 0)) {
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
