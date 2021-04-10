package conquer.gui;

import conquer.data.IClan;
import conquer.gui.utils.ImageResource;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public abstract class UpgradePanel extends JPanel implements ActionListener {
	protected final transient IClan clan;
	protected final JLabel infoLabel;
	protected final JButton upgradeOnce;
	protected final JButton upgradeMax;

	UpgradePanel(final IClan clan) {
		this.clan = clan;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.infoLabel = new JLabel();
		this.infoLabel.setText(this.getInfoText());
		this.add(this.infoLabel);
		this.upgradeOnce = new JButton("");
		this.initUpgradeOnce();
		this.add(this.upgradeOnce);
		this.upgradeMax = new JButton(new ImageResource("max.png"));
		this.initUpgradeMax();
		this.add(this.upgradeMax);
		this.repaint();
		final Timer timer = new ExtendedTimer(Utils.getRefreshRate(), this);
		timer.start();
	}

	abstract String getInfoText();

	abstract void initUpgradeOnce();

	abstract void initUpgradeMax();

	abstract String getOneLevelString();

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.infoLabel.setText(this.getInfoText());
		this.initUpgradeOnce();
		this.initUpgradeMax();
	}
}
