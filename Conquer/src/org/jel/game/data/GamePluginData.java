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
public final class GamePluginData {
	private List<Plugin> plugins;
	private List<RecruitHook> recruitHooks;
	private List<AttackHook> attackHooks;
	private List<MoveHook> moveHooks;
	private List<String> extraMusic;
	private Map<String, KeyHandler> keybindings;
	private List<ResourceHook> resourceHooks;
	private Map<String, CityKeyHandler> cityKeyHandlers;
	private List<MoneyHook> moneyHooks;

	public GamePluginData() {
		// Empty
	}

	public List<AttackHook> getAttackHooks() {
		return this.attackHooks;
	}

	public Map<String, CityKeyHandler> getCityKeyHandlers() {
		return this.cityKeyHandlers;
	}

	public List<String> getExtraMusic() {
		return this.extraMusic;
	}

	public Map<String, KeyHandler> getKeybindings() {
		return this.keybindings;
	}

	public List<MoneyHook> getMoneyHooks() {
		return this.moneyHooks;
	}

	public List<MoveHook> getMoveHooks() {
		return this.moveHooks;
	}

	public List<Plugin> getPlugins() {
		return this.plugins;
	}

	public List<RecruitHook> getRecruitHooks() {
		return this.recruitHooks;
	}

	public List<ResourceHook> getResourceHooks() {
		return this.resourceHooks;
	}

	public void setAttackHooks(final List<AttackHook> attackHooks) {
		this.attackHooks = attackHooks;
	}

	public void setCityKeyHandlers(final Map<String, CityKeyHandler> cityKeyHandlers) {
		this.cityKeyHandlers = cityKeyHandlers;
	}

	public void setExtraMusic(final List<String> extraMusic) {
		this.extraMusic = extraMusic;
	}

	public void setKeybindings(final Map<String, KeyHandler> keybindings) {
		this.keybindings = keybindings;
	}

	public void setMoneyHooks(final List<MoneyHook> moneyHooks) {
		this.moneyHooks = moneyHooks;
	}

	public void setMoveHooks(final List<MoveHook> moveHooks) {
		this.moveHooks = moveHooks;
	}

	public void setPlugins(final List<Plugin> plugins) {
		this.plugins = plugins;
	}

	public void setRecruitHooks(final List<RecruitHook> recruitHooks) {
		this.recruitHooks = recruitHooks;
	}

	public void setResourceHooks(final List<ResourceHook> resourceHooks) {
		this.resourceHooks = resourceHooks;
	}
}