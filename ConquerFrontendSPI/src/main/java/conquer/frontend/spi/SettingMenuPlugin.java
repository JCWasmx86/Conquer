package conquer.frontend.spi;

import java.awt.Component;

import java.util.Optional;

import javax.swing.Icon;

public interface SettingMenuPlugin {
	Component getComponent();

	String getTitle();

	Optional<Icon> getIcon();
}
