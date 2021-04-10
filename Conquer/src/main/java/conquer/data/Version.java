package conquer.data;

import java.util.Objects;

/**
 * A record describing a version. A version consists of three parts:
 * <ul>
 *     <li><b>major: </b>A major version number. Incremented only when breaking API.</li>
 *     <li><b>minor: </b>The minor version number. Incremented for smaller updated, e.g. bugfixes or API additions.</li>
 *     <li><b>patch: </b>A patch number only incremented when a patch/bugfix is made.</li>
 * </ul>
 */
public record Version(int major, int minor, int patch) implements Comparable<Version> {

	@Override
	public String toString() {
		return this.major + "." + this.minor + "." + this.patch;
	}

	@Override
	public int compareTo(final Version o) {
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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Version)) return false;
		return compareTo((Version) o) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.major, this.minor, this.patch);
	}
}
