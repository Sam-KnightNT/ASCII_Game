package version_7;

public class Effect {

	private String effect;
	//This class specifies a command to run every time the effect is triggered -
	//	if you use an item, hit an enemy, walk through a door, whatever. 
	public Effect(String effect) {
		this.effect = effect;
	}
	
	public String getEffect() {
		return effect;
	}
	
	public boolean runEffect() {
		//Send this effect's... effects to the command system - run the command, return true if successful
		return true;
	}
}
