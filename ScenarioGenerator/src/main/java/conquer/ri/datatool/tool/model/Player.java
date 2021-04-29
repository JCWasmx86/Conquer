package conquer.ri.datatool.tool.model;

import java.awt.Color;

public record Player(String name, Color clanColor, double initialCoins, int flags) {
	void validate() {
		ValidatorUtils.throwIfNull(this.name, "Playername is missing!");
		ValidatorUtils.throwIfNull(this.clanColor, "Color of " + this.name + " is missing!");
		ValidatorUtils.throwIfBad(this.initialCoins, "Initial coins of " + this.name + " mustn't be negative, " +
			"infinite" +
			" or NaN!");
	}
}
