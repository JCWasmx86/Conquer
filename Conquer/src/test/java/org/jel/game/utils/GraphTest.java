package org.jel.game.utils;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

public class GraphTest {

	private static final int N_VALUES = 10;

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeSize() {
		new Graph<>(-1);
	}

	@Test
	public void testConnected() {
		final var graph = new Graph<Integer>(2);
		graph.add(1);
		graph.add(2);
		MatcherAssert.assertThat("It is connected, although it shouldn't!", !graph.isConnected(1, 2));
		graph.addDirectedEdge(0, 1, 2.0);
		MatcherAssert.assertThat("It isn't connected, although it shouldn't!", graph.isConnected(1, 2));
		MatcherAssert.assertThat("It is connected, although it shouldn't!", !graph.isConnected(2, 1));
		graph.addDirectedEdge(1, 0, 2.0);
		MatcherAssert.assertThat("It isn't connected, although it shouldn!", graph.isConnected(2, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadEdgeWeight1() {
		final var graph = new Graph<Integer>(2);
		graph.add(1);
		graph.add(2);
		graph.addDirectedEdge(0, 1, -2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadEdgeWeight2() {
		final var graph = new Graph<Integer>(2);
		graph.add(1);
		graph.add(2);
		graph.addDirectedEdge(0, 1, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadEdgeWeight3() {
		final var graph = new Graph<Integer>(2);
		graph.add(1);
		graph.add(2);
		graph.addDirectedEdge(0, 1, 2, -2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_set() {
		final var graph = new Graph<Integer>(3);
		graph.add(1);
		graph.add(2);
		graph._set(3, null);
	}

	@Test
	public void test_set1() {
		final var graph = new Graph<Integer>(3);
		graph.add(1);
		graph.add(2);
		graph._set(1, null);
		MatcherAssert.assertThat("Wrong value: " + graph.getValue(1) + " expected null", graph.getValue(1) == null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testAddTooMuch() {
		final var graph = new Graph<Integer>(2);
		graph.add(1);
		graph.add(2);
		graph.add(3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_set2() {
		final var graph = new Graph<Integer>(3);
		graph.add(1);
		graph.add(2);
		graph._set(-1, null);
	}

	@Test
	public void testAdd() {
		final var graph = new Graph<Integer>(3);
		graph.add(1);
		graph.add(2);
		MatcherAssert.assertThat("Bad value: " + graph.getValue(1) + " expected 2!", graph.getValue(1) == 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAllConnectionsNegativeGraph() {
		new Graph<Integer>(3).allConnections(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAllConnectionsargTooBigGraph() {
		new Graph<Integer>(3).allConnections(1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void dfsNullComparator() {
		final var g = new Graph<Integer>(3);
		g.add(0);
		g.dfs(0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void dfsBadIndex() {
		final var g = new Graph<Integer>(3);
		g.add(0);
		g.dfs(1, a -> {
		});
	}

	@Test
	public void testDFS() {
		final var g = this.getTestGraph();
		final var i = new AtomicInteger();
		g.dfs(0, a -> i.incrementAndGet());
		MatcherAssert.assertThat("Wrong size: " + i.get() + " vs expected " + GraphTest.N_VALUES,
				i.get() == GraphTest.N_VALUES);
	}

	@Test
	public void testBridges() {
		final var g = this.getTestGraph();
		final var bridges = g.getBridges();
		assert bridges.size() == 1;
		assert bridges.get(0).first() == 4;
		assert bridges.get(0).second() == 5;
	}

	@Test
	public void testComponents() {
		final var g = this.getTestGraph();
		final var bridges = g.getBridges();
		assert bridges.size() == 1;
		assert bridges.get(0).first() == 4;
		assert bridges.get(0).second() == 5;
		final var components = g.getComponents();
		assert components.size() == 1;
		final var c = components.get(0);
		Assert.assertEquals(c.first(), Set.of(0, 1, 2, 3, 4));
	}

	@Test
	public void getWeight() {
		final var g = this.getTestGraph();
		MatcherAssert.assertThat("getWeight failed", g.getWeight(0, 1) == 2.3);
	}

	@Test
	public void testIsConnected() {
		final var g = new Graph<Integer>(2);
		g.add(1);
		g.add(2);
		MatcherAssert.assertThat("Shouldn't be connected", !g.isConnected());
		MatcherAssert.assertThat("Should be connected", this.getTestGraph().isConnected());
	}

	@Test
	public void testConnection() {
		final var g = this.getTestGraph();
		MatcherAssert.assertThat("Shouldn't be connected", !g.isConnected(0, 9));
		MatcherAssert.assertThat("Should be connected", g.isConnected(4, 5));
	}

	private Graph<Integer> getTestGraph() {
		final var ret = new Graph<Integer>(GraphTest.N_VALUES);
		for (var i = 0; i < GraphTest.N_VALUES; i++) {
			ret.add(i);
		}
		ret.addUndirectedEdge(0, 1, 2.3);
		ret.addUndirectedEdge(2, 1, 2.3);
		ret.addUndirectedEdge(0, 2, 2.3);
		ret.addUndirectedEdge(2, 3, 2.3);
		ret.addUndirectedEdge(0, 3, 2.3);
		ret.addUndirectedEdge(3, 4, 2.3);
		ret.addUndirectedEdge(0, 4, 2.3);
		ret.addUndirectedEdge(4, 5, 2.3);// Bridge
		ret.addUndirectedEdge(5, 6, 2.3);
		ret.addUndirectedEdge(7, 8, 2.3);
		ret.addUndirectedEdge(5, 7, 2.3);
		ret.addUndirectedEdge(5, 8, 2.3);
		ret.addUndirectedEdge(5, 9, 2.3);
		ret.addUndirectedEdge(6, 9, 2.3);
		ret.addUndirectedEdge(8, 9, 2.3);
		return ret;
	}
}