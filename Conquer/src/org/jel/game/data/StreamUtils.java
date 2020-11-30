package org.jel.game.data;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jel.game.utils.Graph;

public final class StreamUtils {
	public static void forEach(final Graph<City> cities, Consumer<City> consumer) {
		StreamUtils.getCitiesAsStream(cities).forEach(consumer);
	}

	public static void forEach(Graph<City> graph, int i, Consumer<City> consumer) {
		StreamUtils.forEach(graph, a -> a.getClan() == i, consumer);
	}

	public static void forEach(final Graph<City> cities, Predicate<City> predicate, Consumer<City> consumer) {
		StreamUtils.getCitiesAsStream(cities, predicate).forEach(consumer);
	}

	public static Stream<City> getCitiesAroundCity(final Graph<City> cities, final City middle) {
		return cities.getConnected(middle).stream();
	}

	public static Stream<City> getCitiesAroundCity(final Graph<City> cities, final City middle, final int clan) {
		return cities.getConnected(middle).stream().filter(a -> a.getClan() == clan);
	}

	public static Stream<City> getCitiesAroundCity(final Graph<City> cities, final City middle,
			final Predicate<City> predicate) {
		return cities.getConnected(middle).stream().filter(predicate);
	}

	public static Stream<City> getCitiesAroundCityNot(final Graph<City> cities, final City middle, final int clan) {
		return cities.getConnected(middle).stream().filter(a -> a.getClan() != clan);
	}

	public static Stream<City> getCitiesAroundCityNot(final Graph<City> graph, final City source,
			final Predicate<City> object) {
		return StreamUtils.getCitiesAroundCity(graph, source).filter(object);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities) {
		return Stream.of(cities.getValues(new City[0]));
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final int clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClan() == clan);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final Clan clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClan() == clan.getId());
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final int clan,
			final Comparator<City> comp) {
		return StreamUtils.getCitiesAsStream(cities, clan).sorted(comp);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final int clan,
			final Predicate<City> predicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClan() == clan).filter(predicate);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final Predicate<City> filterPredicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(filterPredicate);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final Predicate<City> predicateA,
			final Predicate<City> predicateB) {
		return StreamUtils.getCitiesAsStream(cities).filter(predicateA).filter(predicateB);
	}

	public static Stream<City> getCitiesAsStreamNot(final Graph<City> cities, final int clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClan() != clan);
	}

	public static Stream<City> getCitiesAsStreamNot(final Graph<City> cities, final int clan,
			final Predicate<City> predicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClan() != clan).filter(predicate);
	}

	private StreamUtils() {

	}
}
