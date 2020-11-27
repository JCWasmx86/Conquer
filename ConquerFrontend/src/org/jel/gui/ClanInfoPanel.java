package org.jel.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.ToLongFunction;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Game;
import org.jel.game.data.Resource;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;

final class ClanInfoPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3553281021198844773L;
	private final Clan clan;
	private final Game game;
	private JEditorPane jep;
	private Timer timer;

	ClanInfoPanel(Clan clan, Game game) {
		this.clan = clan;
		this.game = game;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.jep.setText(this.generateText());
	}

	private String coinsPerRound() {
		final var production = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId())
				.mapToDouble(c -> (c.getNumberOfPeople() * Shared.COINS_PER_PERSON_PER_ROUND)
						- (c.getNumberOfSoldiers() * Shared.COINS_PER_SOLDIER_PER_ROUND))
				.sum();
		return String.format("<br><font color='%s'>Coins per round: %.2f</font>", production <= 0 ? "red" : "green",
				production);
	}

	private String generateText() {
		final var sb = new StringBuilder("<html><body>");
		sb.append("Name: ").append(this.clan.getName()).append("<br>Number of soldiers: ")
				.append(this.getNumber(City::getNumberOfSoldiers)).append("<br>Number of people: ")
				.append(this.getNumber(City::getNumberOfPeople)).append("<br>Coins: ")
				.append(String.format("%.2f", this.clan.getCoins())).append(this.coinsPerRound());
		for (final var r : Resource.values()) {
			sb.append(this.resource(r));
		}
		return sb.append("</body></html>").toString();
	}

	private long getNumber(ToLongFunction<City> cc) {
		return StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId()).mapToLong(cc).sum();
	}

	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.jep = new JEditorPane("text/html", this.generateText());
		this.jep.setEditable(false);
		this.jep.setIgnoreRepaint(true);
		((DefaultCaret) this.jep.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		final var jps = new JPanel();
		jps.setLayout(new BoxLayout(jps, BoxLayout.Y_AXIS));
		jps.add(this.jep);
		jps.add(new UpgradeSoldiersPanel(this.clan, this.game));
		jps.add(new UpgradeSoldiersDefense(this.clan, this.game));
		jps.add(new UpgradeSoldiersOffense(this.clan, this.game));
		final var jsp = new JScrollPane(jps);
		jsp.setIgnoreRepaint(true);
		this.add(jsp);
		this.timer = new Timer(17, this);
		this.timer.start();
	}

	private String resource(Resource r) {
		final var sb = new StringBuilder("<br> ").append(r.getName()).append(": ")
				.append(String.format("%.2f", this.clan.getResources().get(r.getIndex()))).append("<br>");
		final var productions = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId())
				.mapToDouble(c -> (c.getNumberOfPeople() * c.getProductions().get(r.getIndex()))).sum();
		final var usage = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId()).mapToDouble(c -> {
			final var i = r.getIndex();
			final var va = Shared.getDataValues()[i];
			return ((c.getNumberOfSoldiers() * va[1]) + (c.getNumberOfPeople() * va[0]));
		}).sum();
		final var balance = productions - usage;
		if (balance <= 0) {
			sb.append("<font color='red'>");
		} else {
			sb.append("<font color='green'>");
		}
		return sb.append(r.getName()).append(" per round: ").append(String.format("%.2f", balance)).append("</font>")
				.toString();
	}

}
