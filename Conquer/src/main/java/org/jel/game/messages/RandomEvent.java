package org.jel.game.messages;

/**
 * Random event.
 */
public enum RandomEvent {
	PESTILENCE("RandomEvent.pestilence"), FIRE("RandomEvent.fire"), GROWTH("RandomEvent.growth"),
	CROP_FAILURE("RandomEvent.crop_failure"), REBELLION("RandomEvent.rebellion"), CIVIL_WAR("RandomEvent.civil_war"),
	MIGRATION("RandomEvent.migration"), ECONOMIC_GROWTH("RandomEvent.economic_growth"),
	PANDEMIC("RandomEvent.pandemic"), ACCIDENT("RandomEvent.accident"), SABOTAGE("RandomEvent.sabotage");

	private String propertyName;

	RandomEvent(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * Return a message id, that will be in messages.properties
	 *
	 * @return Message id.
	 */
	public String getMessage() {
		return this.propertyName;
	}
}
