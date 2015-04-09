package version_7;

import java.util.HashMap;
import java.util.Map.Entry;

public class LiquidMixture extends Liquid {

	//The type of liquid, and its volume in millilitres.
	private HashMap<LiquidType, Integer> types;
	
	public LiquidMixture() {
		super();
		types = new HashMap<LiquidType, Integer>();
	}
	
	//Constructor with only concentration - assumed that the types will be added later. Give no volume, as this will be added with each liquid
	public LiquidMixture(double concentration) {
		super(concentration, 0);
		types = new HashMap<LiquidType, Integer>();
	}
	
	public LiquidMixture(HashMap<LiquidType, Integer> types, double concentration) {
		//Run the superconstructor on the concentration, and the sum of all volumes
		super(concentration, types.values().stream().mapToInt(Integer::intValue).sum());
		this.types = types;
	}

	public HashMap<LiquidType, Integer> getConstituents() {
		return types;
	}
	
	public int getVolumeOf(LiquidType constituent) {
		return types.get(constituent);
	}

	public void setConstituents(HashMap<LiquidType, Integer> types) {
		this.types = types;
	}

	public void addConstituent(LiquidType liquid, int volume) {
		if (types.containsKey(liquid)) {
			types.put(liquid, volume+types.get(liquid));
		} else {
			types.put(liquid, volume);
		}
		//With this method, it is assumed that the constituent should have 100% concentration.
		//Change the volume of the liquid as a whole so it reflects what has been added.
		//New concentration = weighted average of current and the new (assumedly 100%-concentration) liquid
		this.setVolume((int) (this.getVolume()+(volume*(100.0/this.getConcentration()))));
	}
	
	public void addConstituent(LiquidPure liquid) {
		//Add this to the mixture
	}
	
	public void mixWith(LiquidMixture mixture) {
		super.mixWith(mixture);
		for (Entry<LiquidType, Integer> liquid : mixture.getConstituents().entrySet()) {
			//You have the volume of each of these
		}
	}
	
	public String toString() {
		String rtnStr = "Liquid mixture, consisting of: ";
		for (Entry<LiquidType, Integer> liquid : types.entrySet()) {
			rtnStr += String.format("/n/t%d4 litres of "+liquid.getKey().getName(), liquid.getValue()/1000f);
		}
		return rtnStr;
	}
	
	
	
}
