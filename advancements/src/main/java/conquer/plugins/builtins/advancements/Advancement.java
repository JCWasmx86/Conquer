package conquer.plugins.builtins.advancements;

import java.util.Optional;

public interface Advancement {
	default Optional<Advancement> getDependency() {
		return Optional.empty();
	}
}
