package conquer.data.ri;

import java.util.List;
import java.util.Map;

import conquer.plugins.AttackHook;
import conquer.plugins.CityKeyHandler;
import conquer.plugins.KeyHandler;
import conquer.plugins.MoneyHook;
import conquer.plugins.MoveHook;
import conquer.plugins.Plugin;
import conquer.plugins.RecruitHook;
import conquer.plugins.ResourceHook;

/**
 * A container for the callbacks registered by the plugins.
 */
final class GamePluginData {
	private List<Plugin> plugins;
	private List<RecruitHook> recruitHooks;
	private List<AttackHook> attackHooks;
	private List<MoveHook> moveHooks;
	private List<String> extraMusic;
	private Map<String, List<KeyHandler>> keybindings;
	private List<ResourceHook> resourceHooks;
	private Map<String, List<CityKeyHandler>> cityKeyHandlers;
	private List<MoneyHook> moneyHooks;

	List<AttackHook> getAttackHooks() {
		return this.attackHooks;
	}

	void setAttackHooks(final List<AttackHook> attackHooks) {
		this.attackHooks = attackHooks;
	}

	Map<String, List<CityKeyHandler>> getCityKeyHandlers() {
		return this.cityKeyHandlers;
	}

	void setCityKeyHandlers(final Map<String, List<CityKeyHandler>> cityKeyHandlers) {
		this.cityKeyHandlers = cityKeyHandlers;
	}

	List<String> getExtraMusic() {
		return this.extraMusic;
	}

	void setExtraMusic(final List<String> extraMusic) {
		this.extraMusic = extraMusic;
	}

	Map<String, List<KeyHandler>> getKeybindings() {
		return this.keybindings;
	}

	void setKeybindings(final Map<String, List<KeyHandler>> keybindings) {
		this.keybindings = keybindings;
	}

	List<MoneyHook> getMoneyHooks() {
		return this.moneyHooks;
	}

	void setMoneyHooks(final List<MoneyHook> moneyHooks) {
		this.moneyHooks = moneyHooks;
	}

	List<MoveHook> getMoveHooks() {
		return this.moveHooks;
	}

	void setMoveHooks(final List<MoveHook> moveHooks) {
		this.moveHooks = moveHooks;
	}

	List<Plugin> getPlugins() {
		return this.plugins;
	}

	void setPlugins(final List<Plugin> plugins) {
		this.plugins = plugins;
	}

	List<RecruitHook> getRecruitHooks() {
		return this.recruitHooks;
	}

	void setRecruitHooks(final List<RecruitHook> recruitHooks) {
		this.recruitHooks = recruitHooks;
	}

	List<ResourceHook> getResourceHooks() {
		return this.resourceHooks;
	}

	void setResourceHooks(final List<ResourceHook> resourceHooks) {
		this.resourceHooks = resourceHooks;
	}

}