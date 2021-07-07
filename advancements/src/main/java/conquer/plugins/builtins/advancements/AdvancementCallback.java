package conquer.plugins.builtins.advancements;

@FunctionalInterface
public interface AdvancementCallback {
	void onAdvancementGained(Advancement advancement);
}
