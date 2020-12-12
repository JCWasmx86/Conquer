package org.jel.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.ToLongFunction;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Game;
import org.jel.game.data.Resource;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;

/**
 * Shows information about the clan itself. This panel is divided into 4
 * components. The first one shows information about the clan (name, number of
 * resources, number of soldiers,...). The three other components are:
 * {@code UpgradeSoldiersPanel, UpgradeSoldiersDefense and UpgradeSoldiersOffense}.
 */
final class ClanInfoPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3553281021198844773L;
	private final transient Clan clan;
	private final transient Game game;
	private JEditorPane jep;

	/**
	 * Constructs a new ClanInfoPanel for the specified clan.
	 *
	 * @param clan The clan.
	 * @param game A reference to the game object.
	 */
	ClanInfoPanel(Clan clan, Game game) {
		this.clan = clan;
		this.game = game;
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.game.isDead(Shared.PLAYER_CLAN)) {
			this.jep.setText("<html><body><font color='red'>" + Messages.getString("ClanInfoPanel.youAreDead") //$NON-NLS-1$ //$NON-NLS-2$
					+ "</font></body></html>"); //$NON-NLS-1$
		} else {
			this.jep.setText(this.generateText());
		}
	}

	private String coinsPerRound() {
		final var production = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId())
				.mapToDouble(c -> (c.getNumberOfPeople() * Shared.COINS_PER_PERSON_PER_ROUND)
						- (c.getNumberOfSoldiers() * Shared.COINS_PER_SOLDIER_PER_ROUND))
				.sum();
		return String.format("<br><font color='%s'>%s: %.2f</font>", production <= 0 ? "red" : "green", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				Messages.getString("ClanInfoPanel.coinsPerRound"), production); //$NON-NLS-1$
	}

	private String generateText() {
		final var sb = new StringBuilder("<html><body>"); //$NON-NLS-1$
		sb.append(Messages.getString("ClanInfoPanel.name")).append(": ").append(this.clan.getName()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				.append(Messages.getString("ClanInfoPanel.numberOfSoldiers")) //$NON-NLS-1$
				.append(": ").append(this.getNumber(City::getNumberOfSoldiers)).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$
				.append(Messages.getString("ClanInfoPanel.numberOfPeople")).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
				.append(this.getNumber(City::getNumberOfPeople)).append("<br>") //$NON-NLS-1$
				.append(Messages.getString("ClanInfoPanel.coins")).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
				.append(String.format("%.2f", this.clan.getCoins())) //$NON-NLS-1$
				.append(this.coinsPerRound());
		for (final var r : Resource.values()) {
			sb.append(this.resource(r));
		}
		return sb.append("</body></html>").toString(); //$NON-NLS-1$
	}

	private long getNumber(ToLongFunction<City> cc) {
		return StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId()).mapToLong(cc).sum();
	}

	/**
	 * Initialises this component.
	 */
	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.jep = new JEditorPane("text/html", this.generateText()); //$NON-NLS-1$
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
		final var timer = new ExtendedTimer(17, this);
		timer.start();
	}

	private String resource(Resource r) {
		final var sb = new StringBuilder("<br> ").append(r.getName()).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
				.append(String.format("%.2f", this.clan.getResources().get(r.getIndex()))).append("<br>"); //$NON-NLS-1$ //$NON-NLS-2$
		final var productions = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId())
				.mapToDouble(c -> (c.getNumberOfPeople() * c.getProductions().get(r.getIndex()))).sum();
		final var usage = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan.getId()).mapToDouble(c -> {
			final var i = r.getIndex();
			final var va = Shared.getDataValues()[i];
			return ((c.getNumberOfSoldiers() * va[1]) + (c.getNumberOfPeople() * va[0]));
		}).sum();
		final var balance = productions - usage;
		if (balance <= 0) {
			sb.append("<font color='red'>"); //$NON-NLS-1$
		} else {
			sb.append("<font color='green'>"); //$NON-NLS-1$
		}
		return sb.append(r.getName()).append(Messages.getString("ClanInfoPanel.perRound")).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
				.append(String.format("%.2f", balance)) //$NON-NLS-1$
				.append("</font>").toString(); //$NON-NLS-1$
	}

}
