package version_7;

public class LiquidType {

	//Should have drink effects (e.g. restore health, restore mana, modify stats) and touch effects (burn, poison)
	
	private Pair<String, Integer> effect;
	private String name;
	
	public LiquidType(Pair<String, Integer> effect, String name) {
		this.effect = effect;
		this.name = name;
	}

	public Pair<String, Integer> getEffect() {
		return effect;
	}

	public void setEffect(Pair<String, Integer> effect) {
		this.effect = effect;
	}

	public String getModifiedStat() {
		return effect.getLeft();
	}
	
	public int getAmountModified() {
		return effect.getRight();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
