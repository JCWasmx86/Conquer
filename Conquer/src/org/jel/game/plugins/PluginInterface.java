package org.jel.game.plugins;

import org.jel.game.data.EventList;

public interface PluginInterface {
	void addAttackHook(AttackHook ah);

	void addCityKeyHandler(String key, CityKeyHandler ckh);

	void addKeyHandler(String key, KeyHandler handler);

	void addMoneyHook(MoneyHook mh);

	void addMoveHook(MoveHook mh);

	void addMusic(String fileName);

	void addRecruitHook(RecruitHook rh);

	void addResourceHook(ResourceHook rh);

	EventList getEventList();
}
