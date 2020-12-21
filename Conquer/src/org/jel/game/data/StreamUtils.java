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
	public static void forEach(final Graph<ICity> cities, final Clan clan, final Consumer<ICity> consumer) {
		StreamUtils.forEach(cities, clan.getId(), consumer);
	}

	public static void forEach(final Graph<ICity> cities, final Consumer<ICity> consumer) {
		StreamUtils.getCitiesAsStream(cities).forEach(consumer);
	}

	public static void forEach(final Graph<ICity> graph, final int i, final Consumer<ICity> consumer) {
		StreamUtils.forEach(graph, a -> a.getClanId() == i, consumer);
	}

	public static void forEach(final Graph<ICity> cities, final Predicate<ICity> predicate,
			final Consumer<ICity> consumer) {
		StreamUtils.getCitiesAsStream(cities, predicate).forEach(consumer);
	}

	public static Stream<ICity> getCitiesAroundCity(final Graph<ICity> cities, final ICity middle) {
		return cities.getConnected(middle).stream();
	}

	public static Stream<ICity> getCitiesAroundCity(final Graph<ICity> cities, final ICity middle, final Clan clan) {
		return StreamUtils.getCitiesAroundCity(cities, middle, clan.getId());
	}

	public static Stream<ICity> getCitiesAroundCity(final Graph<ICity> cities, final ICity middle, final int clan) {
		return cities.getConnected(middle).stream().filter(a -> a.getClanId() == clan);
	}

	public static Stream<ICity> getCitiesAroundCity(final Graph<ICity> cities, final ICity middle,
			final Predicate<ICity> predicate) {
		return cities.getConnected(middle).stream().filter(predicate);
	}

	public static Stream<ICity> getCitiesAroundCityNot(final Graph<ICity> cities, final ICity middle, final Clan clan) {
		return StreamUtils.getCitiesAroundCityNot(cities, middle, clan.getId());
	}

	public static Stream<ICity> getCitiesAroundCityNot(final Graph<ICity> cities, final ICity middle, final int clan) {
		return cities.getConnected(middle).stream().filter(a -> a.getClanId() != clan);
	}

	public static Stream<ICity> getCitiesAroundCityNot(final Graph<ICity> graph, final ICity source,
			final Predicate<ICity> object) {
		return StreamUtils.getCitiesAroundCityNot(graph, source, source.getClanId()).filter(object);
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities) {
		return Stream.of(cities.getValues(new ICity[0]));
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final Clan clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() == clan.getId());
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final Clan clan,
			final Comparator<ICity> comparator) {
		return StreamUtils.getCitiesAsStream(cities, clan.getId(), comparator);
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final Clan clan,
			final Predicate<ICity> predicate) {
		return StreamUtils.getCitiesAsStream(cities, clan.getId(), predicate);
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final int clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() == clan);
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final int clan,
			final Comparator<ICity> comp) {
		return StreamUtils.getCitiesAsStream(cities, clan).sorted(comp);
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final int clan,
			final Predicate<ICity> predicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() == clan).filter(predicate);
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final Predicate<ICity> filterPredicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(filterPredicate);
	}

	public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final Predicate<ICity> predicateA,
			final Predicate<ICity> predicateB) {
		return StreamUtils.getCitiesAsStream(cities).filter(predicateA).filter(predicateB);
	}

	public static Stream<ICity> getCitiesAsStreamNot(final Graph<ICity> cities, final Clan clan) {
		return StreamUtils.getCitiesAsStreamNot(cities, clan.getId());
	}

	public static Stream<ICity> getCitiesAsStreamNot(final Graph<ICity> cities, final Clan clan,
			final Predicate<ICity> predicate) {
		return StreamUtils.getCitiesAsStreamNot(cities, clan.getId(), predicate);
	}

	public static Stream<ICity> getCitiesAsStreamNot(final Graph<ICity> cities, final int clan) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() != clan);
	}

	public static Stream<ICity> getCitiesAsStreamNot(final Graph<ICity> cities, final int clan,
			final Predicate<ICity> predicate) {
		return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClanId() != clan).filter(predicate);
	}

	private StreamUtils() {

	}
}
