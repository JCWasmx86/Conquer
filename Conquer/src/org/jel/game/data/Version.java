package org.jel.game.data;

public record Version(int major, int minor, int patch) implements Comparable<Version> {

	@Override
	public String toString() {
		return major + "." + minor + "." + patch;
	}

	@Override
	public int compareTo(Version o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (this.major > o.major) {
			return 1;
		} else if (this.major < o.major) {
			return -1;
		}
		if (this.minor > o.minor) {
			return 1;
		} else if (this.minor < o.minor) {
			return -1;
		}
		if (this.minor > o.minor) {
			return 1;
		} else if (this.minor < o.minor) {
			return -1;
		}
		if (this.patch > o.patch) {
			return 1;
		} else if (this.patch < o.patch) {
			return -1;
		}
		return 0;
	}
}
