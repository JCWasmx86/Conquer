package conquer.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

public class Graph<T> implements Consumer<T> {
	private final double[][] matrix;
	private ArrayList<T> values = new ArrayList<>();
	private final ArrayList<Boolean> visited = new ArrayList<>();

	private int cnt = 0;
	private boolean cached = false;

	private final Map<T, Map<T, Boolean>> map = new HashMap<>();

	/**
	 * Create a new graph with the specified size.
	 *
	 * @param number The fixed number, no further element can be added
	 * @throws IllegalArgumentException If number &lt; 0
	 */
	public Graph(final int number) {
		if (number < 0) {
			throw new IllegalArgumentException("number < 0");
		}
		this.values = new ArrayList<>(number);
		this.matrix = new double[number][number];
		for (var i = 0; i < number; i++) {
			for (var j = 0; j < number; j++) {
				this.matrix[i][j] = (i == j) ? -2 : -1;
			}
		}
	}

	/**
	 * The usage is discouraged. Set the value at the index {@code i} to {@code t}.
	 *
	 * @param i index
	 * @param t value
	 * @throws IllegalArgumentException if the index is smaller than 0 or bigger
	 *                                  than the size of the graph.
	 */
	public void _set(final int i, final T t) {
		this.checkIndex(i);
		this.values.set(i, t);
	}

	/**
	 * Don't use!
	 */
	@Override
	public void accept(final T t) {
		this.cnt++;
	}

	/**
	 * Add a value.
	 *
	 * @param t The element to add to the graph.
	 * @return The index of the added element
	 */
	public int add(final T t) {
		if (this.values.size() == this.matrix.length) {
			throw new IndexOutOfBoundsException();
		}
		this.values.add(t);
		this.visited.add(false);
		return this.values.size() - 1;
	}

	/**
	 * Add a directed edge with a specified weight. (The edge is from a to b)
	 *
	 * @param a     The first edge
	 * @param b     The second edge
	 * @param value Weight
	 * @throws IllegalArgumentException if {@code value} is equals to -1 or -2.
	 */
	public void addDirectedEdge(final int a, final int b, final double value) {
		if ((value == -1) || (value == -2)) {
			throw new IllegalArgumentException("Bad value");
		}
		this.checkIndex(a);
		this.checkIndex(b);
		this.matrix[a][b] = value;
	}

	/**
	 * Add a directed edge with a specified weight.
	 *
	 * @param a  The first edge
	 * @param b  The second edge
	 * @param ab Weight of the edge from a to b.
	 * @param ba Weight of the edge from b to a.
	 * @throws IllegalArgumentException if {@code ab} or {@code ba} is equals to -1
	 *                                  or -2.
	 */
	public void addDirectedEdge(final int a, final int b, final double ab, final double ba) {
		if ((ab == -2) || (ab == -1) || (ba == -2) || (ba == -1)) {
			throw new IllegalArgumentException("Bad values: " + ab + "//" + ba);
		}
		this.checkIndex(a);
		this.checkIndex(b);
		this.matrix[a][b] = ab;
		this.matrix[b][a] = ba;
	}

	/**
	 * Add an undirected edge.
	 *
	 * @param idxA  Index of the first node
	 * @param idxB  Index of the second node.
	 * @param value The weight.
	 * @throws IllegalArgumentException if {@code value} is equals to -1 or -2.
	 */
	public void addUndirectedEdge(final int idxA, final int idxB, final double value) {
		this.addDirectedEdge(idxA, idxB, value, value);
	}

	/**
	 * Returns a list of indices, that are adjacent to the given.
	 *
	 * @param idx The index.
	 * @return List of indices of adjacent nodes.
	 */
	public List<Integer> allConnections(final int idx) {
		this.checkIndex(idx);
		final List<Integer> ret = new ArrayList<>();
		for (var i = 0; i < this.values.size(); i++) {
			if (this.matrix[idx][i] != -1) {
				ret.add(i);
			}
		}
		return ret;
	}

	private void checkIndex(final int idx) {
		if (idx < 0) {
			throw new IllegalArgumentException("idx<0");
		} else if (idx >= this.values.size()) {
			throw new IllegalArgumentException("index is bigger than the number of elements!");
		}
	}

	/**
	 * Does a depth-first-search from the specified start index.
	 *
	 * @param start    Start index
	 * @param consumer The consumer that will accept every visited value.
	 */
	public void dfs(final int start, final Consumer<T> consumer) {
		this.checkIndex(start);
		if (consumer == null) {
			throw new IllegalArgumentException("consumer==null");
		}
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

	/**
	 * Returns all bridges. A bridge is a connection, that, if removed, disconnects
	 * the graph, so it has two parts.
	 *
	 * @return A list of bridges (Two indices are one bridge)
	 */
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

	/**
	 * Returns every possible component. A component is a part of the graph that
	 * will result, if a bridge if removed.
	 *
	 * @return A list with every possible component.
	 */
	public List<Pair<Set<Integer>, Set<Integer>>> getComponents() {
		final var bridges = this.getBridges();
		final var ret = new ArrayList<Pair<Set<Integer>, Set<Integer>>>();
		for (final var bridge : bridges) {
			ret.add(this.getTwoAreas(bridge));
		}
		return ret;
	}

	/**
	 * Returns values of nodes that are connected to the first node with the
	 * specified value.
	 *
	 * @param comp The comparator to compare every value with the given.
	 * @param a    The value to search for
	 * @return Values of nodes that are connected to the node with the specified
	 *         value.
	 * @throws NoSuchElementException If the specified value couldn't be found.
	 */
	public List<T> getConnected(final Comparator<T> comp, final T a) {
		if (comp == null) {
			throw new IllegalArgumentException("comparator==null");
		}
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

	/**
	 * Returns a list of connected values to the value
	 *
	 * @param a
	 * @return All connected values.
	 * @throws NoSuchElementException If {@code a} wasn't be found
	 */
	public List<T> getConnected(final T a) {
		final List<T> ret = new ArrayList<>();
		final var idx = this.values.indexOf(a);
		if (idx == -1) {
			throw new NoSuchElementException();
		}
		for (var i = 0; i < this.values.size(); i++) {
			if ((this.matrix[i][idx] != -1) && (this.matrix[i][idx] != -2) && (i != idx)) {
				ret.add(this.values.get(i));
			}
		}
		return ret;
	}

	/**
	 * Returns a list of all connections. (A connection is: (index,index,weight)).
	 * It may include all connections, e.g. (1,2,2.0) and (2,1,2.0)
	 *
	 * @return All connections
	 */
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

	/**
	 * Returns two areas. An area is a set of nodes that will result, if a bridge is
	 * removed.
	 *
	 * @param bridge The bridge to remove.
	 * @return Two areas.
	 */
	public Pair<Set<Integer>, Set<Integer>> getTwoAreas(final Pair<Integer, Integer> bridge) {
		this.checkIndex(bridge.first());
		this.checkIndex(bridge.second());
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

	/**
	 * Get value at specified index
	 *
	 * @param idx The index
	 * @return Value at the index.
	 */
	public T getValue(final int idx) {
		this.checkIndex(idx);
		return this.values.get(idx);
	}

	/**
	 * Convert the graph to an array.
	 *
	 * @param t
	 * @return Array representing the values in the graph.
	 */
	public T[] getValues(final T[] t) {
		return this.values.toArray(t);
	}

	/**
	 * Get the weight between the given values.
	 *
	 * @param c First value
	 * @param a Second value
	 * @return The weight between this two values. -1 and -2 means, these are not
	 *         connected.
	 */
	public double getWeight(final T c, final T a) {
		final var i1 = this.values.indexOf(c);
		final var i2 = this.values.indexOf(a);
		if ((i1 == -1) || (i2 == -2)) {
			throw new NoSuchElementException();
		}
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

	/**
	 * Initializes a cache. This may provide faster lookup times.
	 */
	public void initCache() {
		this.map.clear();
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

	/**
	 * Returns whether this graph is connected. (=Every node is reachable from every
	 * other)
	 *
	 * @return True if the graph is connected.
	 */
	public boolean isConnected() {
		this.cnt = 0;
		this.dfs(0, this);
		return this.cnt == this.values.size();
	}

	/**
	 * Returns whether two nodes of the given values are connected
	 *
	 * @param c First value
	 * @param a Second value
	 * @return True if these are connected.
	 */
	public boolean isConnected(final T c, final T a) {
		if (c == a) {
			return false;
		}
		if (!this.cached) {
			final var i1 = this.values.indexOf(c);
			final var i2 = this.values.indexOf(a);
			this.checkIndex(i1);
			this.checkIndex(i2);
			return (this.matrix[i1][i2] != -1) && (this.matrix[i1][i2] != -2);
		} else {
			return this.map.get(c).get(a);
		}
	}
}
