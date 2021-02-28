package conquer.plugins;

import conquer.data.EventList;

/**
 * An interface providing several possibilities to hook into internal events of
 * the game.
 */
public interface PluginInterface {

	/**
	 * Registers a callback function that is called as soon as an attack is started.
	 *
	 * @param attackHook The callback function, may not be null
	 */
	void addAttackHook(AttackHook attackHook);

	/**
	 * Register a keyhandler for a specified key that is called as soon as a city is
	 * focused and a key was presses
	 *
	 * @param key The key to listen for. May not be null
	 * @param ckh The handler. May not be null
	 */
	void addCityKeyHandler(String key, CityKeyHandler ckh);

	/**
	 * Register a keyhandler that is not specific to a city.
	 *
	 * @param key     The key to listen for. May not be null
	 * @param handler The handler. May not be null
	 */
	void addKeyHandler(String key, KeyHandler handler);

	/**
	 * Add a MessageListener that is called when a message had been added to the
	 * EventList.
	 *
	 * @param messageListener
	 */
	void addMessageListener(MessageListener messageListener);

	/**
	 * Register a callback function that is called as soon as all money was
	 * "produced" for a clan.
	 *
	 * @param mh The callback. May not be null
	 */
	void addMoneyHook(MoneyHook mh);

	/**
	 * Register a callback function that is called when soldiers are moved between
	 * two cities (No attack)
	 *
	 * @param mh The callback. May not be null
	 */
	void addMoveHook(MoveHook mh);

	/**
	 * Register music
	 *
	 * @param fileName Filename, may not be null.
	 */
	@Deprecated(forRemoval = true)
	void addMusic(String fileName);

	/**
	 * Adds a callback that is called when soldiers are recruited
	 *
	 * @param rh The callback. May not be null
	 */
	void addRecruitHook(RecruitHook rh);

	/**
	 * Adds a callback function that is called for each city after the production of
	 * resources.
	 *
	 * @param rh The callback. May not be null
	 */
	void addResourceHook(ResourceHook rh);

	/**
	 * Returns the eventlist.
	 *
	 * @return Current eventlist.
	 */
	EventList getEventList();
}
