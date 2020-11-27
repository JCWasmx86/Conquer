package org.jel.game.data;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.jel.game.utils.Graph;

public class GameBuilder {
	private Game game;

	public GameBuilder(Image backgroundImage, byte numPlayers, List<Double> clanCoins, List<String> clanNames,
			List<Integer> flags, List<Color> colors) {
		this.game = new Game();
		if (numPlayers != clanCoins.size()) {
			throw new IllegalArgumentException("numPlayers!=clanCoins.size())");
		}
		if (numPlayers != clanNames.size()) {
			throw new IllegalArgumentException("numPlayers!=clanNames.size())");
		}
		if (numPlayers != flags.size()) {
			throw new IllegalArgumentException("numPlayers!=flags.size())");
		}
		if (numPlayers != colors.size()) {
			throw new IllegalArgumentException("numPlayers!=colors.size())");
		}
		this.game = new Game();
		final List<Clan> clans = new ArrayList<>();
		for (var i = 0; i < numPlayers; i++) {
			final var clan = new Clan();
			clan.setId(i);
			clan.setCoins(clanCoins.get(i));
			clan.setName(clanNames.get(i));
			clan.setFlags(flags.get(i));
			clan.setColor(colors.get(i));
			clans.add(clan);
		}
		this.game.setPlayers(numPlayers);
		final var relations = new Graph<Integer>(numPlayers);
		for (var i = 0; i < numPlayers; i++) {
			relations.add(i);
		}
		for (var i = 0; i < numPlayers; i++) {
			for (var j = 0; j < numPlayers; j++) {
				if (i != j) {
					relations.addDirectedEdge(i, j, 50, 50);
				} else {
					relations.addDirectedEdge(i, j, Double.MAX_VALUE, Double.MAX_VALUE);
				}
			}
		}
		this.game.setRelations(relations);
		this.game.setClans(clans);
	}

	public Game getGame() {
		return this.game;
	}
}
