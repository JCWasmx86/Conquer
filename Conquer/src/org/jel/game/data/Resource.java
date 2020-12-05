package org.jel.game.data;

/**
 * Enumerates all available resources.
 */
public enum Resource {
	WHEAT(0, "wheat.png", "Wheat"), FISH(1, "fish.png", "Fish"), WOOD(2, "wood.png", "Wood"),
	COAL(3, "coal.png", "Coal"), MEAT(4, "meat.png", "Meat"), IRON(5, "iron.png", "Iron"),
	TEXTILE(6, "textile.png", "Textile"), LEATHER(7, "leather.png", "Leather"), STONE(8, "stone.png", "Stone");

	private String image;
	private int index;
	private String name;

	Resource(final int index, final String image, final String name) {
		this.index = index;
		this.image = image;
		this.name = name;
	}

	/**
	 * Returns the image of the resource
	 *
	 * @return Filename of the icon.
	 */
	public String getImage() {
		return this.image;
	}

	/**
	 * Returns the index of it.
	 *
	 * @return Index
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Returns the name of the resource
	 *
	 * @return Name of the resource
	 */
	public String getName() {
		return this.name;
	}
}
