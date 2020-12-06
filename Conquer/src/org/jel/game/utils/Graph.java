package org.jel.game.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

public final class Graph<T> implements Consumer<T> {
	private final double[][] matrix;
	private ArrayList<T> values = new ArrayList<>();
	private final ArrayList<Boolean> visited = new ArrayList<>();

	private int cnt = 0;
	private boolean cached = false;

	private final Map<T, Map<T, Boolean>> map = new HashMap<>();

	public Graph(final int number) {
		this.values = new ArrayList<>(number);
		this.matrix = new double[number][number];
		for (var i = 0; i < number; i++) {
			for (var j = 0; j < number; j++) {
				this.matrix[i][j] = (i == j) ? -2 : -1;
			}
		}
	}

	public void _set(final int i, final T t) {
		this.values.set(i, t);
	}

	@Override
	public void accept(final T t) {
		this.cnt++;
	}

	public int add(final T t) {
		this.values.add(t);
		this.visited.add(false);
		return this.values.size() - 1;
	}

	public void addDirectedEdge(final int a, final int b, final double value) {
		this.addDirectedEdge(a, b, value, 0);
	}

	public void addDirectedEdge(final int a, final int b, final double ab, final double ba) {
		this.matrix[a][b] = ab;
		this.matrix[b][a] = ba;
	}

	public void addUndirectedEdge(final int idxA, final int idxB, final double value) {
		this.matrix[idxA][idxB] = value;
		this.matrix[idxB][idxA] = value;
	}

	public List<Integer> allConnections(final int idx) {
		final List<Integer> ret = new ArrayList<>();
		for (var i = 0; i < this.values.size(); i++) {
			if (this.matrix[idx][i] != -1) {
				ret.add(i);
			}
		}
		return ret;
	}

	public void dfs(final int start, final Consumer<T> consumer) {
		for (var i = 0; i < this.visited.size(); i++) {
			this.visited.set(i, false);
		}
		this.dfs0(start, consumer);
	}

	private void dfs0(final int start, final Consumer<T> consumer) {
		this.visited.set(start, true);
		consumer.accept(this.values.get(start));
		for (var i = 0; i < this.values.size(); i++) {
			final var v = this.matrix[i][start];
			if ((v != -1) && (v != -2) && !this.visited.get(i).booleanValue()) {
				this.dfs0(i, consumer);
			}
		}
	}

	public List<Pair<Integer, Integer>> getBridges() {
		final List<Pair<Integer, Integer>> ret = new ArrayList<>();
		final var size = this.values.size();
		for (var i = 0; i < size; i++) {
			for (var j = 0; j < size; j++) {
				final var first = this.matrix[i][j];
				final var second = this.matrix[j][i];
				this.matrix[i][j] = -1;
				this.matrix[j][i] = -1;
				final var b = this.isConnected();
				if (!b) {
					var add = true;
					for (final var a : ret) {
						if ((a.first() == j) && (a.second() == i)) {
							add = false;
							break;
						}
					}
					if (add) {
						ret.add(new Pair<>(i, j));
					}
				}
				this.matrix[i][j] = first;
				this.matrix[j][i] = second;
			}
		}
		return ret;
	}

	public List<Pair<Set<Integer>, Set<Integer>>> getComponents() {
		final var bridges = this.getBridges();
		final var ret = new ArrayList<Pair<Set<Integer>, Set<Integer>>>();
		for (final var bridge : bridges) {
			ret.add(this.getTwoAreas(bridge));
		}
		return ret;
	}

	public List<T> getConnected(final Comparator<T> comp, final T a) {
		final List<T> ret = new ArrayList<>();
		var idx = -1;
		for (var i = 0; i < this.values.size(); i++) {
			if (comp.compare(a, this.values.get(i)) == 0) {
				idx = i;
				break;
			}
		}
		if (idx == -1) {
			throw new NoSuchElementException();
		}
		for (var i = 0; i < this.values.size(); i++) {
			if (this.matrix[i][idx] != -1) {
				ret.add(this.values.get(i));
			}
		}
		return ret;
	}

	public List<T> getConnected(final T a) {
		final List<T> ret = new ArrayList<>();
		final var idx = this.values.indexOf(a);
		if (idx == -1) {
			throw new NoSuchElementException();
		}
		for (var i = 0; i < this.values.size(); i++) {
			if ((this.matrix[i][idx] != -1) && (i != idx)) {
				ret.add(this.values.get(i));
			}
		}
		return ret;
	}

	public List<Triple<Integer, Integer, Double>> getConnections() {
		final var ret = new ArrayList<Triple<Integer, Integer, Double>>();
		for (var i = 0; i < this.values.size(); i++) {
			for (var j = 0; j < this.values.size(); j++) {
				final var first = this.values.get(i);
				final var second = this.values.get(j);
				if ((first != second) && this.isConnected(first, second)) {
					ret.add(new Triple<>(i, j, this.getWeight(first, second)));
				}
			}
		}
		return ret;
	}

	public Pair<Set<Integer>, Set<Integer>> getTwoAreas(final Pair<Integer, Integer> bridge) {
		final Set<Integer> a = new HashSet<>();
		final Set<Integer> b = new HashSet<>();
		final int i = bridge.first();
		final int j = bridge.second();
		final var first = this.matrix[i][j];
		final var second = this.matrix[j][i];
		this.matrix[i][j] = -1;
		this.matrix[j][i] = -1;
		this.idxDfs(i, a::add);
		this.idxDfs(j, b::add);
		this.matrix[i][j] = first;
		this.matrix[j][i] = second;
		return new Pair<>(a, b);
	}

	public T getValue(final int idx) {
		return this.values.get(idx);
	}

	public T[] getValues(final T[] t) {
		return this.values.toArray(t);
	}

	public double getWeight(final T c, final T a) {
		final var i1 = this.values.indexOf(c);
		final var i2 = this.values.indexOf(a);
		return this.matrix[i1][i2];
	}

	private void idxDfs(final int start, final Consumer<Integer> consumer) {
		for (var i = 0; i < this.visited.size(); i++) {
			this.visited.set(i, false);
		}
		this.idxDfs0(start, consumer);
	}

	private void idxDfs0(final int start, final Consumer<Integer> consumer) {
		this.visited.set(start, true);
		consumer.accept(start);
		for (var i = 0; i < this.values.size(); i++) {
			final var v = this.matrix[i][start];
			if ((v != -1) && (v != -2) && !this.visited.get(i).booleanValue()) {
				this.idxDfs0(i, consumer);
			}
		}
	}

	public void initCache() {
		for (final var a : this.values) {
			for (final var b : this.values) {
				Map<T, Boolean> c;
				if (this.map.containsKey(a)) {
					c = this.map.get(a);
				} else {
					c = new HashMap<>();
				}
				c.put(b, this.isConnected(a, b));
				this.map.put(a, c);
			}
		}
		this.cached = true;
	}

	public boolean isConnected() {
		this.cnt = 0;
		this.dfs(0, this);
		return this.cnt == this.values.size();
	}

	public boolean isConnected(final T c, final T a) {
		if (!this.cached) {
			final var i1 = this.values.indexOf(c);
			final var i2 = this.values.indexOf(a);
			return this.matrix[i1][i2] != -1;
		} else {
			return this.map.get(c).get(a);
		}
	}

}
