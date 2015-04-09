package version_7;

public class LiquidType {

	//Should have drink effects (e.g. restore health, restore mana, modify stats) and touch effects (burn, poison)
	//TODO - change drinkStat and touchStat to Stat objects - give the ability to either affect a stat or cause an effect (e.g. paralysis)
	
	private String drinkEffect;
	private String touchEffect;
	private String name;
	
	//If there is just one stat given, assume it is intended to affect an entity on drinking.
	public LiquidType(String drinkEffect, String name) {
		this.drinkEffect = drinkEffect;
		this.name = name;
	}
	
	public LiquidType(String drinkEffect, String touchEffect, String name) {
		this.drinkEffect = drinkEffect;
		this.touchEffect = touchEffect;
		this.name = name;
	}

	public String getDrinkEffect() {
		return drinkEffect;
	}

	public void setDrinkEffect(String effect) {
		this.drinkEffect = effect;
	}
	
	public String getTouchEffect() {
		return touchEffect;
	}

	public void setTouchEffect(String effect) {
		this.touchEffect = effect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name+": drinkEffect = "+drinkEffect+", touchEffect="+touchEffect+" on touch.";
	}
}
