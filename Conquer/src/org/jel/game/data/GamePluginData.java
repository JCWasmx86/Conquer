package org.jel.game.data;

import java.util.List;
import java.util.Map;

import org.jel.game.plugins.AttackHook;
import org.jel.game.plugins.CityKeyHandler;
import org.jel.game.plugins.KeyHandler;
import org.jel.game.plugins.MoneyHook;
import org.jel.game.plugins.MoveHook;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.RecruitHook;
import org.jel.game.plugins.ResourceHook;

/**
 * A container for the callbacks registered by the plugins.
 *
 */
final class GamePluginData {
	private List<Plugin> plugins;
	private List<RecruitHook> recruitHooks;
	private List<AttackHook> attackHooks;
	private List<MoveHook> moveHooks;
	private List<String> extraMusic;
	private Map<String, KeyHandler> keybindings;
	private List<ResourceHook> resourceHooks;
	private Map<String, CityKeyHandler> cityKeyHandlers;
	private List<MoneyHook> moneyHooks;

	GamePluginData() {
		// Empty
	}

	List<AttackHook> getAttackHooks() {
		return this.attackHooks;
	}

	Map<String, CityKeyHandler> getCityKeyHandlers() {
		return this.cityKeyHandlers;
	}

	List<String> getExtraMusic() {
		return this.extraMusic;
	}

	Map<String, KeyHandler> getKeybindings() {
		return this.keybindings;
	}

	List<MoneyHook> getMoneyHooks() {
		return this.moneyHooks;
	}

	List<MoveHook> getMoveHooks() {
		return this.moveHooks;
	}

	List<Plugin> getPlugins() {
		return this.plugins;
	}

	List<RecruitHook> getRecruitHooks() {
		return this.recruitHooks;
	}

	List<ResourceHook> getResourceHooks() {
		return this.resourceHooks;
	}

	void setAttackHooks(final List<AttackHook> attackHooks) {
		this.attackHooks = attackHooks;
	}

	void setCityKeyHandlers(final Map<String, CityKeyHandler> cityKeyHandlers) {
		this.cityKeyHandlers = cityKeyHandlers;
	}

	void setExtraMusic(final List<String> extraMusic) {
		this.extraMusic = extraMusic;
	}

	void setKeybindings(final Map<String, KeyHandler> keybindings) {
		this.keybindings = keybindings;
	}

	void setMoneyHooks(final List<MoneyHook> moneyHooks) {
		this.moneyHooks = moneyHooks;
	}

	void setMoveHooks(final List<MoveHook> moveHooks) {
		this.moveHooks = moveHooks;
	}

	void setPlugins(final List<Plugin> plugins) {
		this.plugins = plugins;
	}

	void setRecruitHooks(final List<RecruitHook> recruitHooks) {
		this.recruitHooks = recruitHooks;
	}

	void setResourceHooks(final List<ResourceHook> resourceHooks) {
		this.resourceHooks = resourceHooks;
	}
}