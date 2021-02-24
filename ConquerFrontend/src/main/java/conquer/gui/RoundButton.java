package conquer.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * This code is from <a href=
 * "https://stackoverflow.com/a/6748509/13912132">https://stackoverflow.com/a/6748509/13912132</a>
 * with minor changes.
 */
final class RoundButton extends JButton {
	private static final long serialVersionUID = 3881854141204649327L;

	private transient Shape shape;
	private transient Shape base;

	public RoundButton(final Icon icon) {
		this(null, icon);
	}

	private RoundButton(final String text, final Icon icon) {
		this.setModel(new DefaultButtonModel());
		this.init(text, icon);
		if (icon == null) {
			return;
		}
		this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		this.setBackground(Color.BLACK);
		this.setContentAreaFilled(false);
		this.setFocusPainted(false);
		this.setAlignmentY(Component.TOP_ALIGNMENT);
		this.initShape();
	}

	@Override
	public boolean contains(final int x, final int y) {
		this.initShape();
		return this.shape.contains(x, y);
	}

	@Override
	public Dimension getPreferredSize() {
		final var icon = this.getIcon();
		final var i = this.getInsets();
		final var iw = Math.max(icon.getIconWidth(), icon.getIconHeight());
		return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
	}

	private void initShape() {
		if (!this.getBounds().equals(this.base)) {
			final var s = this.getPreferredSize();
			this.base = this.getBounds();
			this.shape = new Ellipse2D.Float(0, 0, s.width - 1.0f, s.height - 1.0f);
		}
	}

	@Override
	protected void paintBorder(final Graphics g) {
		this.initShape();
		final var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(this.getBackground());
		g2.draw(this.shape);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
}
