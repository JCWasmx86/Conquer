package conquer.data;

import conquer.data.strategy.StrategyObject;
import conquer.utils.Graph;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An utilities class to convert an graph of cities to a stream and apply
 * several operations.
 */
public final class StreamUtils {
    public static void forEach(final Graph<ICity> cities, final IClan clan, final Consumer<ICity> consumer) {
        StreamUtils.forEach(cities, a -> a.getClan() == clan, consumer);
    }

    public static void forEach(final Graph<ICity> cities, final Consumer<ICity> consumer) {
        StreamUtils.getCitiesAsStream(cities).forEach(consumer);
    }

    public static void forEach(final Graph<ICity> cities, final Predicate<ICity> predicate,
                               final Consumer<ICity> consumer) {
        StreamUtils.getCitiesAsStream(cities, predicate).forEach(consumer);
    }

    public static Stream<ICity> getCitiesAroundCity(final Graph<ICity> cities, final ICity middle) {
        return cities.getConnected(middle).stream();
    }

    public static Stream<ICity> getCitiesAroundCity(final StrategyObject info, final Graph<ICity> cities,
                                                    final ICity middle) {
        return StreamUtils.getCitiesAsStream(cities).filter(a -> info.canMove(middle, a));
    }

    public static Stream<ICity> getCitiesAroundCity(final Graph<ICity> cities, final ICity middle, final IClan clan) {
        return StreamUtils.getCitiesAroundCity(cities, middle, a -> a.getClan() == clan);
    }

    public static Stream<ICity> getCitiesAroundCity(final StrategyObject info, final Graph<ICity> cities,
                                                    final ICity middle, final IClan clan) {
        return StreamUtils.getCitiesAroundCity(info, cities, middle, a -> a.getClan() == clan);
    }

    public static Stream<ICity> getCitiesAroundCity(final Graph<ICity> cities, final ICity middle,
                                                    final Predicate<ICity> predicate) {
        return cities.getConnected(middle).stream().filter(predicate);
    }

    public static Stream<ICity> getCitiesAroundCity(final StrategyObject info, final Graph<ICity> cities,
                                                    final ICity middle, final Predicate<ICity> predicate) {
        return StreamUtils.getCitiesAroundCity(info, cities, middle).filter(predicate);
    }

    public static Stream<ICity> getCitiesAroundCityNot(final Graph<ICity> cities, final ICity middle,
                                                       final IClan clan) {
        return StreamUtils.getCitiesAroundCity(cities, middle, a -> a.getClan() != clan);
    }

    public static Stream<ICity> getCitiesAroundCityNot(final StrategyObject info, final Graph<ICity> cities,
                                                       final ICity middle, final IClan clan) {
        return StreamUtils.getCitiesAroundCity(info, cities, middle, a -> a.getClan() != clan);
    }

    public static Stream<ICity> getCitiesAroundCityNot(final Graph<ICity> graph, final ICity source,
                                                       final Predicate<ICity> object) {
        return StreamUtils.getCitiesAroundCityNot(graph, source, source.getClan()).filter(object);
    }

    public static Stream<ICity> getCitiesAroundCityNot(final StrategyObject info, final Graph<ICity> graph,
                                                       final ICity source, final Predicate<ICity> object) {
        return StreamUtils.getCitiesAroundCityNot(info, graph, source, source.getClan()).filter(object);
    }

    public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities) {
        return Stream.of(cities.getValues(new ICity[0]));
    }

    public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final IClan clan) {
        return StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClan() == clan);
    }

    public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final IClan clan,
                                                  final Comparator<ICity> comparator) {
        return StreamUtils.getCitiesAsStream(cities, a -> a.getClan() == clan).sorted(comparator);
    }

    public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final IClan clan,
                                                  final Predicate<ICity> predicate) {
        return StreamUtils.getCitiesAsStream(cities, a -> a.getClan() == clan, predicate);
    }

    public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final Predicate<ICity> filterPredicate) {
        return StreamUtils.getCitiesAsStream(cities).filter(filterPredicate);
    }

    public static Stream<ICity> getCitiesAsStream(final Graph<ICity> cities, final Predicate<ICity> predicateA,
                                                  final Predicate<ICity> predicateB) {
        return StreamUtils.getCitiesAsStream(cities).filter(predicateA).filter(predicateB);
    }

    public static Stream<ICity> getCitiesAsStreamNot(final Graph<ICity> cities, final IClan clan) {
        return StreamUtils.getCitiesAsStream(cities, a -> a.getClan() != clan);
    }

    public static Stream<ICity> getCitiesAsStreamNot(final Graph<ICity> cities, final IClan clan,
                                                     final Predicate<ICity> predicate) {
        return StreamUtils.getCitiesAsStream(cities, a -> a.getClan() != clan, predicate);
    }

    private StreamUtils() {

    }
}
