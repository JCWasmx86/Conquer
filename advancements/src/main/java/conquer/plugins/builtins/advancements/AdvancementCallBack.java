package conquer.plugins.builtins.advancements;

@FunctionalInterface
public interface AdvancementCallBack {
	void onAdvancementGained(Advancement advancement);
}
