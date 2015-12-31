package version_7;

import java.util.ArrayList;

public class Spell {
	
	//Something that can be fired at an enemy, with a number of effects it can have on them.
	//Should be extended with target and stuff, so the Effects can literally contain the commands.
	private String name;
	private String description;
	private ArrayList<Effect> effects = new ArrayList<Effect>();
	
	public Spell() {
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	public String getDescription() {
		return description;
	}
	
	public void addEffects(Effect[] effects) {
		for (Effect effect : effects) {
			this.effects.add(effect);
		}
	}
}
