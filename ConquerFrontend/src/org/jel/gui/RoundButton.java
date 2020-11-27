package org.jel.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;

final class RoundButton extends JButton {
	private static final long serialVersionUID = 3881854141204649327L;

	private Shape shape, base;

	public RoundButton() {
		this(null, null);
	}

	public RoundButton(Action a) {
		this();
		this.setAction(a);
	}

	public RoundButton(Icon icon) {
		this(null, icon);
	}

	public RoundButton(String text) {
		this(text, null);
	}

	public RoundButton(String text, Icon icon) {
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
	public boolean contains(int x, int y) {
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

	protected void initShape() {
		if (!this.getBounds().equals(this.base)) {
			final var s = this.getPreferredSize();
			this.base = this.getBounds();
			this.shape = new Ellipse2D.Float(0, 0, s.width - 1.0f, s.height - 1.0f);
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
		this.initShape();
		final var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(this.getBackground());
		g2.draw(this.shape);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
}
