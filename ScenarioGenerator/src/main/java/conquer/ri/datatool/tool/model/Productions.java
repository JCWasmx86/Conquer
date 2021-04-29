package conquer.ri.datatool.tool.model;

public record Productions(double wheat, double fish, double wood, double coal, double meat, double iron, double textile,
						  double leather, double stone) {
	void validate() {
		ValidatorUtils.throwIfBad(this.wheat, "Production for wheat mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.fish, "Production for fish mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.wood, "Production for wood mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.coal, "Production for coal mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.meat, "Production for meat mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.iron, "Production for iron mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.textile, "Production for textile mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.leather, "Production for leather mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.stone, "Production for stone mustn't be negative, infinite or NaN!");
	}
}
