package conquer.plugins.builtins.advancements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import conquer.data.AttackResult;
import conquer.data.ICity;
import conquer.data.StreamUtils;

public class Advancements {
	static List<Advancement> gainedAdvancements = new ArrayList<>();
	static List<Advancement> availableAdvancements = new ArrayList<>() {
		{
			this.add(new FirstBlood());
			this.add(new Reconquista());
			this.add(new DwarfUprising());
		}
	};

	static boolean gained(final Advancement advancement) {
		return gainedAdvancements.contains(advancement);
	}

	static class FirstBlood implements Advancement {

		private transient  AdvancementCallBack callback;

		@Override
		public String title() {
			return "First Blood";
		}

		@Override
		public String extendedTitle() {
			return "Conquer your first city";
		}

		@Override
		public void init(AdvancementCallBack ifGained) {
			if (gained(this)) {
				return;
			}
			this.callback = ifGained;
		}

		@Override
		public void before(ICity src, ICity destination, long numberOfSoldiersMoved) {
			if ((!gained(this)) && src.isPlayerCity()) {
				gainedAdvancements.add(this);
				if (callback != null)
					callback.onAdvancementGained(this);
			}
		}
	}

	static class Reconquista implements Advancement {
		private transient AdvancementCallBack callback;
		private List<ICity> citiesConqueredByEnemyLastRound = new ArrayList<>();
		private List<ICity> citiesConqueredByEnemy = new ArrayList<>();
		private boolean playerCityWasAttacked = false;
		private ICity cityThatWasAttacked = null;

		@Override
		public String title() {
			return "Reconquista";
		}

		@Override
		public String extendedTitle() {
			return "Lose a city and conquer it in the next round";
		}

		@Override
		public void init(AdvancementCallBack ifGained) {
			if (gained(this)) {
				return;
			}
			this.callback = ifGained;
		}

		@Override
		public Optional<Class<? extends Advancement>> getDependency() {
			return Optional.of(FirstBlood.class);
		}

		@Override
		public void before(ICity src, ICity destination, long numberOfSoldiersMoved) {
			//If a player city is attacked, save it.
			if (gained(this)) {
				return;
			} else if (destination.isPlayerCity()) {
				playerCityWasAttacked = true;
				cityThatWasAttacked = destination;
			}
		}

		@Override
		public void nextRound(int round) {
			citiesConqueredByEnemyLastRound = new ArrayList<>(citiesConqueredByEnemy);
			citiesConqueredByEnemy = new ArrayList<>();
		}

		@Override
		public void after(ICity src, ICity destination, long survivingSoldiers, AttackResult result) {
			if (gained(this)) {
				return;
			}
			//If the city of the player was attacked, add it to the list of cities that were conquered
			if (playerCityWasAttacked && result == AttackResult.CITY_CONQUERED) {
				citiesConqueredByEnemy.add(cityThatWasAttacked);
				playerCityWasAttacked = false;
				cityThatWasAttacked = null;
				//Else if the player is attacking a city conquered in the last/current round, trigger the advancement
			} else if (src.isPlayerCity() &&
				(citiesConqueredByEnemy.contains(destination) || citiesConqueredByEnemyLastRound.contains(destination)) && result == AttackResult.CITY_CONQUERED) {
				gainedAdvancements.add(this);
				if (callback != null)
					callback.onAdvancementGained(this);
				citiesConqueredByEnemy.remove(destination);
				citiesConqueredByEnemyLastRound.remove(destination);
			}
		}
	}

	static class DwarfUprising implements Advancement {
		private AdvancementCallBack callback;

		@Override
		public String title() {
			return "Dwarf Uprising";
		}

		@Override
		public String extendedTitle() {
			return "Attack a much bigger enemy";
		}

		@Override
		public void init(AdvancementCallBack ifGained) {
			if (gained(this)) {
				return;
			}
			this.callback = ifGained;
		}

		@Override
		public void before(ICity src, ICity destination, long numberOfSoldiersMoved) {
			if (src.isPlayerCity() && !gained(this)) {
				//Get number of cities of player and the enemy
				final var cntPlayer = StreamUtils.getCitiesAsStream(src.getInfo().getCities(), src.getClan()).count();
				final var cntEnemy =
					StreamUtils.getCitiesAsStream(destination.getInfo().getCities(), src.getClan()).count();
				final var factor = cntEnemy / ((double) cntPlayer);
				//If the enemy has more than 2.5 as many cities as the player, grant the advancement.
				if (factor >= 2.5) {
					gainedAdvancements.add(this);
					if (callback != null)
						callback.onAdvancementGained(this);
				}
			}
		}
	}
}
