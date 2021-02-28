package conquer.data;

import java.awt.*;
import java.util.List;

/**
 * Models a city on the map.
 */
public interface ICity extends Comparable<ICity> {
    /**
     * Called at the end of the round.
     */
    void endOfRound();

    /**
     * Gives the defensebonus: Suppose there are 500 Soldiers in the city and there
     * is a defense bonus of 1.03. Now they have the same power as 515 soldiers.
     *
     * @return The defensebonus
     */
    double getBonus();

    /**
     * Returns the clan of the city.
     *
     * @return The clan.
     */
    IClan getClan();

    /**
     * Returns the clan id.
     *
     * @return The clan id
     */
    int getClanId();

    /**
     * Get the difference between the production of coins per round and the use of
     * coins per round.
     *
     * @return The difference
     */
    double getCoinDiff();

    /**
     * Gives the base defense: The base defense means, that even if a city has no
     * soldiers, it has a bit of defense. (Think of towers and walls)
     *
     * @return The base defense
     */
    double getDefense();

    /**
     * Returns the strength of a city based on its own values and the clan.
     *
     * @return The defense strength of the city.
     */
    double getDefenseStrength();

    /**
     * Returns the growth of a city. The growth of a city is the factor, for which
     * the number of persons increases every round. For example: 1000 Persons and a
     * growth of 1.01: Next round: 1010 persons<br>
     * Next round: 1020 persons<br>
     *
     * @return The growth of the city.
     */
    double getGrowth();

    /**
     * Returns the icon of this city.
     *
     * @return The icon of the city.
     */
    Image getImage();

    /**
     * Returns a handle to the information object
     *
     * @return A handle to the information object.
     */
    ConquerInfo getInfo();

    /**
     * Returns the number of levels of each resource of the city.
     *
     * @return The levels
     */
    List<Integer> getLevels();

    /**
     * Get the name of the city
     *
     * @return The name of the city.
     */
    String getName();

    /**
     * Returns the number of persons in the city
     *
     * @return Number of persons in the city.
     */
    long getNumberOfPeople();

    /**
     * Returns the number of soldiers in the city.
     *
     * @return Number of soldiers in this city
     */
    long getNumberOfSoldiers();

    /**
     * Gives the how much every resource is produced every round.
     *
     * @return A mutable list with the production rates.
     */
    List<Double> getProductions();

    /**
     * Returns the x-Position
     *
     * @return x-Position
     */
    int getX();

    /**
     * Returns the y-Position.
     *
     * @return y-Position
     */
    int getY();

    /**
     * Returns whether this city is owned by the player.
     *
     * @return {@code true} if the player owns this city.
     */
    boolean isPlayerCity();

    /**
     * Get the production of a resource per round
     *
     * @param resource Resource - May not be null
     * @return The production of a resource per round.
     */
    double productionPerRound(Resource resource);

    /**
     * Change the clan of the city.
     *
     * @param clan The new clan. May not be {@code null}, otherwise a
     *             {@link IllegalArgumentException} will be thrown.
     */
    void setClan(IClan clan);

    /**
     * Set the defense strength of the city.
     *
     * @param newPowerOfUpdate The new strength. May not be negative, , otherwise a
     *                         {@link IllegalArgumentException} will be thrown.
     */
    void setDefense(double newPowerOfUpdate);

    /**
     * Set the growth factor of the city.
     *
     * @param growth The new strength. May not be negative, , otherwise a
     *               {@link IllegalArgumentException} will be thrown.
     */
    void setGrowth(double growth);

    /**
     * Set the number of people in the city.
     *
     * @param numberOfPeople The new number of people. May not be negative, ,
     *                       otherwise a {@link IllegalArgumentException} will be
     *                       thrown.
     */
    void setNumberOfPeople(long numberOfPeople);

    /**
     * Set the number of soldiers in the city.
     *
     * @param numberOfSoldiers The new number of soldiers. May not be negative, ,
     *                         otherwise a {@link IllegalArgumentException} will be
     *                         thrown.
     */
    void setNumberOfSoldiers(long numberOfSoldiers);

}