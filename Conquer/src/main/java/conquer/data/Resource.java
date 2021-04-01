package conquer.data;

import conquer.Messages;

/**
 * Enumerates all available resources.
 */
public enum Resource {
	WHEAT(0, "wheat.png", Messages.getString("Resource.wheat")), //$NON-NLS-1$ //$NON-NLS-2$
	FISH(1, "fish.png", Messages.getString("Resource.fish")), //$NON-NLS-1$ //$NON-NLS-2$
	WOOD(2, "wood.png", Messages.getString("Resource.wood")), //$NON-NLS-1$ //$NON-NLS-2$
	COAL(3, "coal.png", Messages.getString("Resource.coal")), MEAT(4, "meat.png", Messages.getString("Resource.meat"))
	, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	IRON(5, "iron.png", Messages.getString("Resource.iron")), //$NON-NLS-1$ //$NON-NLS-2$
	TEXTILE(6, "textile.png", Messages.getString("Resource.textile")), //$NON-NLS-1$ //$NON-NLS-2$
	LEATHER(7, "leather.png", Messages.getString("Resource.leather")), //$NON-NLS-1$ //$NON-NLS-2$
	STONE(8, "stone.png", Messages.getString("Resource.stone")); //$NON-NLS-1$ //$NON-NLS-2$

	private final String image;
	private final int index;
	private final String name;

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
