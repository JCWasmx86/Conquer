package conquer.gui;

import conquer.data.*;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.ToLongFunction;

/**
 * Shows information about the clan itself. This panel is divided into 4
 * components. The first one shows information about the clan (name, number of
 * resources, number of soldiers,...). The three other components are:
 * {@code UpgradeSoldiersPanel, UpgradeSoldiersDefense and UpgradeSoldiersOffense}.
 */
final class ClanInfoPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 3553281021198844773L;
    private final transient IClan clan;
    private final transient ConquerInfo game;
    private JEditorPane jep;

    /**
     * Constructs a new ClanInfoPanel for the specified clan.
     *
     * @param clan The clan.
     * @param game A reference to the game object.
     */
    ClanInfoPanel(final IClan clan, final ConquerInfo game) {
        this.clan = clan;
        this.game = game;
    }

    /**
     * Shouldn't be used.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (this.game.isDead(this.game.getPlayerClan())) {
            this.jep.setText("<html><body><font color='red'>" + Messages.getString("ClanInfoPanel.youAreDead") //$NON-NLS-1$ //$NON-NLS-2$
                    + "</font></body></html>"); //$NON-NLS-1$
        } else {
            this.jep.setText(this.generateText());
        }
    }

    private String coinsPerRound() {
        final var production = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan).mapToDouble(
                c -> (c.getNumberOfPeople() * this.game.getResourceUsage(this.clan).getCoinsPerRoundPerPerson())
                        - (c.getNumberOfSoldiers() * this.game.getSoldierCosts(this.clan).coinsPerSoldierPerRound()))
                .sum();
        return String.format("<br><font color='%s'>%s: %.2f</font>", production <= 0 ? "red" : "green", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                Messages.getString("ClanInfoPanel.coinsPerRound"), production); //$NON-NLS-1$
    }

    private String generateText() {
        final var sb = new StringBuilder("<html><body>"); //$NON-NLS-1$
        sb.append(Messages.getString("Shared.name")).append(": ").append(this.clan.getName()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .append(Messages.getString("ClanInfoPanel.numberOfSoldiers")) //$NON-NLS-1$
                .append(": ").append(this.getNumber(ICity::getNumberOfSoldiers)).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$
                .append(Messages.getString("ClanInfoPanel.numberOfPeople")).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
                .append(this.getNumber(ICity::getNumberOfPeople)).append("<br>") //$NON-NLS-1$
                .append(Messages.getString("Shared.coins")).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
                .append(String.format("%.2f", this.clan.getCoins())) //$NON-NLS-1$
                .append(this.coinsPerRound());
        for (final var r : Resource.values()) {
            sb.append(this.resource(r));
        }
        return sb.append("</body></html>").toString(); //$NON-NLS-1$
    }

    private long getNumber(final ToLongFunction<ICity> cc) {
        return StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan).mapToLong(cc).sum();
    }

    /**
     * Initializes this component.
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
        jps.add(new UpgradeSoldiersPanel(this.clan));
        jps.add(new UpgradeSoldiersDefense(this.clan));
        jps.add(new UpgradeSoldiersOffense(this.clan));
        final var jsp = new JScrollPane(jps);
        jsp.setIgnoreRepaint(true);
        this.add(jsp);
        final var timer = new ExtendedTimer(Utils.getRefreshRate(), this);
        timer.start();
    }

    private String resource(final Resource r) {
        final var index = r.getIndex();
        final var sb = new StringBuilder("<br> ").append(r.getName()).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
                .append(String.format("%.2f", this.clan.getResources().get(index))).append("<br>"); //$NON-NLS-1$ //$NON-NLS-2$
        final var productions = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan)
                .mapToDouble(c -> (c.getNumberOfPeople() * c.getProductions().get(index))).sum();
        final var usage = StreamUtils.getCitiesAsStream(this.game.getCities(), this.clan).mapToDouble(c -> {
            final var va = this.game.getResourceUsage(this.clan).get(index);
            return ((c.getNumberOfSoldiers() * va[1]) + (c.getNumberOfPeople() * va[0]));
        }).sum();
        final var balance = productions - usage;
        if (balance <= 0) {
            sb.append("<font color='red'>"); //$NON-NLS-1$
        } else {
            sb.append("<font color='green'>"); //$NON-NLS-1$
        }
        return sb.append(r.getName()).append(" ").append(Messages.getString("ClanInfoPanel.perRound")).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
                .append(String.format("%.2f", balance)) //$NON-NLS-1$
                .append("</font>").toString(); //$NON-NLS-1$
    }

}
