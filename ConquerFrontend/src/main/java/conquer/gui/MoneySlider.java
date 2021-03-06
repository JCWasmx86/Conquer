package conquer.gui;

import conquer.data.ConquerInfo;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * Shows a slider that determines the amount of coins to be send as a gift
 */
final class MoneySlider extends JPanel {
    private static final long serialVersionUID = 794966448509855336L;
    private final transient ConquerInfo game;
    private JSlider slider;

    /**
     * Construct a new slider
     *
     * @param game The source of the data.
     */
    MoneySlider(final ConquerInfo game) {
        this.game = game;
    }

    /**
     * Returns the amount of coins to be gifted.
     *
     * @return Amount of coins
     */
    double getMoney() {
        return (0.01 * this.slider.getValue()) * this.game.getPlayerClan().getCoins();
    }

    private String getText() {
        return Messages.getString("Shared.coins") + ": " //$NON-NLS-1$ //$NON-NLS-2$
                + String.format("%.2f", (0.01 * this.slider.getValue()) * this.game.getPlayerClan().getCoins()); //$NON-NLS-1$
    }

    /**
     * Initialises this component.
     */
    void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.slider = new JSlider(0, 100);
        final var textfield = new JTextField(this.getText());
        textfield.setEditable(false);
        this.slider.addChangeListener(a -> textfield.setText(this.getText()));
        this.add(this.slider);
        this.add(textfield);
        new ExtendedTimer(Utils.getRefreshRate(), a -> textfield.setText(this.getText())).start();
    }
}
