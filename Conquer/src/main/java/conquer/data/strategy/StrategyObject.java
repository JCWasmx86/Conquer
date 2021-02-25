package conquer.data.strategy;

import java.util.stream.Stream;

import conquer.data.ConquerInfo;
import conquer.data.Gift;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.Resource;
import conquer.utils.Graph;

/**
 * An interface providing everything that is needed for strategies.
 */
public interface StrategyObject {

	/**
	 * Attack a city. An {@code IllegalArgumentException} is thrown if:
	 * <ul>
	 * <li>{@code source.getClan()==target.getName()}</li>
	 * <li>{@code source==target}</li>
	 * <li>{@code isConnected(source,target)==false}</li>
	 * <ul>
	 *
	 * @param source                          Source city. May not be {@code null},
	 *                                        otherwise an
	 *                                        {@code IllegalArgumentException} is
	 *                                        thrown.
	 * @param target                          Destination city. May not be
	 *                                        {@code null}, otherwise an
	 *                                        {@code IllegalArgumentException} is
	 *                                        thrown.
	 * @param managedByPlayer                 If {@code true},
	 *                                        {@code numberOfSoldiersToMoveIfManaged}
	 *                                        is used for the number of soldiers to
	 *                                        use for attack, otherwise an
	 *                                        implementation defined default
	 *                                        algorithm is used.
	 * @param numberOfSoldiersToMoveIfManaged If {@code managedByPlayer} is
	 *                                        {@code true}, it is interpreted,
	 *                                        otherwise not. If it is interpreted
	 *                                        and it is negative, an
	 *                                        {@code IllegalArgumentException} is
	 *                                        thrown.
	 */
	void attack(ICity source, ICity target, boolean managedByPlayer, long numberOfSoldiersToMoveIfManaged);

	/**
	 * Returns whether troops can be moved from {@code source} to {@code target}.
	 *
	 * @param source Source city. May not be {@code null}.
	 * @param target Destination city. May not be {@code null}.
	 *
	 * @return {@code true} if troops can be moved from {@code source} to
	 * {@code target}.
	 */
	default boolean canMove(final ICity source, final ICity target) {
		if (source == null) {
			throw new IllegalArgumentException("source == null");
		}
		if (target == null) {
			throw new IllegalArgumentException("target == null");
		}
		return this.getCities().isConnected(source, target);
	}

	/**
	 * Return a graph of all cities.
	 *
	 * @return All cities as graph.
	 */
	Graph<ICity> getCities();

	/**
	 * Describes the relationship between the different clans.
	 *
	 * @return A graph describing the relationships between the clans.
	 */
	Graph<Integer> getRelations();

	/**
	 * Return a value describing the relationship between {@code a} and {@code b}.
	 * It should be in the range (inclusively) {@code [0;100]}.
	 * <p>
	 * If the clans are equal, an {@code IllegalArgumentException} is thrown.
	 *
	 * @param a First clan. May not be {@code null}, otherwise an
	 *          {@code IllegalArgumentException} is thrown.
	 * @param b Second clan. May not be {@code null}, otherwise an
	 *          {@code IllegalArgumentException} is thrown.
	 *
	 * @return Relationship value.
	 */
	double getRelationship(final IClan a, final IClan b);

	/**
	 * Get the relationship between the clan {@code clan} and the clan of the city
	 * {@code city}.
	 * <p>
	 * If {@code clan} and {@code city.getClan()} are equals, an
	 * {@code IllegalArgumentException} will be thrown.
	 *
	 * @param clan Clan. May not be {@code null}, otherwise an
	 *             {@code IllegalArgumentException} is thrown.
	 * @param city City. May not be {@code null}, otherwise an
	 *             {@code IllegalArgumentException} is thrown.
	 *
	 * @return
	 */
	default double getRelationship(final IClan clan, final ICity city) {
		if (clan == null) {
			throw new IllegalArgumentException("city==null");
		}
		if (city == null) {
			throw new IllegalArgumentException("city==null");
		}
		if (clan == city.getClan()) {
			throw new IllegalArgumentException("clan==city.getClan()");
		}
		return this.getRelationship(clan, city.getClan());
	}

	/**
	 * Return the maximum number of soldiers to move from {@code first} to
	 * {@code second}.
	 * <p>
	 * An {@code IllegalArgumentException} is thrown if:
	 * <ul>
	 * <li>{@code first==second}</li>
	 * <li>{@code first} is not connected with {@code second}</li>
	 * </ul>
	 *
	 * @param clan                    The clan determining the amount of coins. May
	 *                                not be {@code null}, otherwise an
	 *                                {@code IllegalArgumentException} is thrown.
	 * @param first                   Source city. May not be {@code null},
	 *                                otherwise an {@code IllegalArgumentException}
	 *                                is thrown.
	 * @param second                  Destination city. May not be {@code null},
	 *                                otherwise an {@code IllegalArgumentException}
	 *                                is thrown.
	 * @param maximumNumberOfSoldiers An upper value limiting the amount of soldiers
	 *                                to move.
	 *
	 * @return Maximum number of soldiers that can be moved.
	 */
	default long maximumNumberToMove(final IClan clan, final ICity first, final ICity second,
									 final long maximumNumberOfSoldiers) {
		if (clan == null) {
			throw new IllegalArgumentException("clan==null");
		}
		if (first == null) {
			throw new IllegalArgumentException("first==null");
		}
		if (second == null) {
			throw new IllegalArgumentException("second==null");
		}
		if (first == second) {
			throw new IllegalArgumentException("first==second");
		}
		if (!this.getCities().isConnected(first, second)) {
			throw new IllegalArgumentException("Not connected!");
		}
		return this.maximumNumberToMove(clan, this.getCities().getWeight(first, second), maximumNumberOfSoldiers);
	}

	/**
	 * Returns the maximum number of soldiers, that can be moved over the distance
	 * {@code weight}, based on the amount of coins of {@code clan}.
	 *
	 * @param clan                    The clan determining the amount of coins. May
	 *                                not be {@code null}, otherwise an
	 *                                {@code IllegalArgumentException} is thrown.
	 * @param weight                  Distance to go. May not be negative, otherwise
	 *                                an {@code IllegalArgumentException} is thrown.
	 * @param maximumNumberOfSoldiers An upper value limiting the amount of soldiers
	 *                                to move.
	 *
	 * @return Maximum number of soldiers that can be moved.
	 */
	long maximumNumberToMove(IClan clan, double weight, long maximumNumberOfSoldiers);

	/**
	 * Attempt to move soldiers from {@code source} to another city.
	 * <p>
	 * An {@code IllegalArgumentException} is thrown, if
	 * {@code source.getClan() != target.getClan()}, iff
	 * {@code managedByExternalStrategy} is {@code true}.
	 *
	 * @param source                          Source city. May not be {@code null},
	 *                                        otherwise an
	 *                                        {@code IllegalArgumentException} is
	 *                                        thrown.
	 * @param reachableCities                 A stream of reachable cities. Can be
	 *                                        ignored and {@code null}, if
	 *                                        {@code managedByExternalStrategy} is
	 *                                        {@code true}. In this case, it may not
	 *                                        be {@code null}, otherwise an
	 *                                        {@code IllegalArgumentException} is
	 *                                        thrown.
	 * @param managedByExternalStrategy       If {@code false},
	 *                                        {@code reachableCities} is used
	 *                                        together with a default,
	 *                                        implementation defined algorithm,
	 *                                        otherwise {@code target} and
	 *                                        {@code numberOfSoldiersToMoveIfManaged}
	 *                                        are used.
	 * @param target                          If {@code managedByExternalStrategy}
	 *                                        is true, if shouldn't be {@code null},
	 *                                        otherwise an
	 *                                        {@code IllegalArgumentException} is
	 *                                        thrown.
	 * @param numberOfSoldiersToMoveIfManaged If {@code managedByExternalStrategy}
	 *                                        is true, if shouldn't be
	 *                                        {@code negative}, otherwise an
	 *                                        {@code IllegalArgumentException} is
	 *                                        thrown. Furthermore it shouldn't be
	 *                                        higher than the number of soldiers in
	 *                                        {@code source}.
	 */
	void moveSoldiers(ICity source, Stream<ICity> reachableCities, boolean managedByExternalStrategy, ICity target,
					  long numberOfSoldiersToMoveIfManaged);

	/**
	 * Recruit soldiers.
	 *
	 * @param maxToPay         Maximum amount to pay. Only evaluated if
	 *                         {@code managedByPlayer} is false. May not be negative
	 *                         in this case, otherwise an
	 *                         {@code IllegalArgumentException} is thrown.
	 * @param city             The city to recruit in. May not be {@code null},
	 *                         otherwise an {@code IllegalArgumentException} is
	 *                         thrown.
	 * @param managedByPlayer  If this value is {@code true},
	 *                         {@code numberOfSoldiers} is evaluated, otherwise an
	 *                         implementation-defined default algorithm is used.
	 * @param numberOfSoldiers If it is evaluated, it mustn't be negative, otherwise
	 *                         an {@code IllegalArgumentException} is thrown.
	 */
	void recruitSoldiers(double maxToPay, ICity city, boolean managedByPlayer, long numberOfSoldiers);

	/**
	 * Send a gift from {@code source} to {@code destination}. An
	 * {@code IllegalArgumentException} is thrown, if one of the arguments is
	 * {@code null}, the destination is extincted or the gift callback for the
	 * player is null.
	 * ({@link ConquerInfo#setPlayerGiftCallback(conquer.data.PlayerGiftCallback)})
	 *
	 * @param source      Source clan. May not be {@code null}, otherwise an
	 *                    {@code IllegalArgumentException} is thrown.
	 * @param destination Destination clan. May not be {@code null}, otherwise an
	 *                    {@code IllegalArgumentException} is thrown.
	 * @param gift        The gift to give. May not be {@code null}, otherwise an
	 *                    {@code IllegalArgumentException} is thrown.
	 *
	 * @return {@code true} if accepted, {@code false} otherwise.
	 */
	boolean sendGift(IClan source, IClan destination, Gift gift);

	/**
	 * Upgrade the defense of a city.
	 *
	 * @param city The city. May not be {@code null}, otherwise an
	 *             {@code IllegalArgumentException} is thrown.
	 *
	 * @return {@code true} if successful, {@code false} otherwise.
	 */
	boolean upgradeDefense(ICity city);

	/**
	 * Upgrade the resource production in a city.
	 *
	 * @param resc The resource to upgrade. May not be {@code null}, otherwise an
	 *             {@code IllegalArgumentException} is thrown.
	 * @param city The city. May not be {@code null}, otherwise an
	 *             {@code IllegalArgumentException} is thrown.
	 *
	 * @return {@code true} if successful, {@code false} otherwise.
	 */
	boolean upgradeResource(Resource resc, ICity city);
}
