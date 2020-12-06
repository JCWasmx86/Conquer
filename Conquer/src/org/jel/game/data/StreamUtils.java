package org.jel.game.data;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jel.game.utils.Graph;

/**
 * An utilities class to convert an graph of cities to a stream and apply
 * several operations.
 */
public final class StreamUtils {
	public static void forEach(Graph<City> cities, Clan clan, Consumer<City> consumer) {
		StreamUtils.forEach(cities, clan.getId(), consumer);
	}

	public static void forEach(final Graph<City> cities, Consumer<City> consumer) {
		StreamUtils.getCitiesAsStream(cities).forEach(consumer);
	}

	public static void forEach(Graph<City> graph, int i, Consumer<City> consumer) {
		StreamUtils.forEach(graph, a -> a.getClanId() == i, consumer);
	}

	public static void forEach(final Graph<City> cities, Predicate<City> predicate, Consumer<City> consumer) {
		StreamUtils.getCitiesAsStream(cities, predicate).forEach(consumer);
	}

	public static Stream<City> getCitiesAroundCity(final Graph<City> cities, final City middle) {
		return cities.getConnected(middle).stream();
	}

	public static Stream<City> getCitiesAroundCity(Graph<City> cities, City middle, Clan clan) {
		return StreamUtils.getCitiesAroundCity(cities, middle, clan.getId());
	}

	public static Stream<City> getCitiesAroundCity(final Graph<City> cities, final City middle, final int clan) {
		return cities.getConnected(middle).stream().filter(a -> a.getClanId() == clan);
	}

	public static Stream<City> getCitiesAroundCity(final Graph<City> cities, final City middle,
			final Predicate<City> predicate) {
		return cities.getConnected(middle).stream().filter(predicate);
	}

	public static Stream<City> getCitiesAroundCityNot(Graph<City> cities, City middle, Clan clan) {
		return StreamUtils.getCitiesAroundCityNot(cities, middle, clan.getId());
	}

	public static Stream<City> getCitiesAroundCityNot(final Graph<City> cities, final City middle, final int clan) {
		return cities.getConnected(middle).stream().filter(a -> a.getClanId() != clan);
	}

	public static Stream<City> getCitiesAroundCityNot(final Graph<City> graph, final City source,
			final Predicate<City> object) {
		return StreamUtils.getCitiesAroundCityNot(graph, source, source.getClanId()).filter(object);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities) {
		return Stream.of(cities.getValues(new City[0]));
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final Clan clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() == clan.getId());
	}

	public static Stream<City> getCitiesAsStream(Graph<City> cities, Clan clan, Comparator<City> comparator) {
		return StreamUtils.getCitiesAsStream(cities, clan.getId(), comparator);
	}

	public static Stream<City> getCitiesAsStream(Graph<City> cities, Clan clan, Predicate<City> predicate) {
		return StreamUtils.getCitiesAsStream(cities, clan.getId(), predicate);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final int clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() == clan);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final int clan,
			final Comparator<City> comp) {
		return StreamUtils.getCitiesAsStream(cities, clan).sorted(comp);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final int clan,
			final Predicate<City> predicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() == clan).filter(predicate);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final Predicate<City> filterPredicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(filterPredicate);
	}

	public static Stream<City> getCitiesAsStream(final Graph<City> cities, final Predicate<City> predicateA,
			final Predicate<City> predicateB) {
		return StreamUtils.getCitiesAsStream(cities).filter(predicateA).filter(predicateB);
	}

	public static Stream<City> getCitiesAsStreamNot(Graph<City> cities, Clan clan) {
		return StreamUtils.getCitiesAsStreamNot(cities, clan.getId());
	}

	public static Stream<City> getCitiesAsStreamNot(Graph<City> cities, Clan clan, Predicate<City> predicate) {
		return StreamUtils.getCitiesAsStreamNot(cities, clan.getId(), predicate);
	}

	public static Stream<City> getCitiesAsStreamNot(final Graph<City> cities, final int clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() != clan);
	}

	public static Stream<City> getCitiesAsStreamNot(final Graph<City> cities, final int clan,
			final Predicate<City> predicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() != clan).filter(predicate);
	}

	private StreamUtils() {

	}
}
