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
		super(concentration);
		types = new HashMap<LiquidType, Integer>();
	}
	
	public LiquidMixture(HashMap<LiquidType, Integer> types, double concentration) {
		//Run the superconstructor on the concentration, and the sum of all volumes
		super(concentration, types.values().stream().mapToInt(Integer::intValue).sum(), types.keySet().toArray(new LiquidType[types.size()]));
		this.types = types;
	}
	
	public LiquidMixture(LiquidType[] liquids, int[] volumes, double concentration) {
		types = new HashMap<LiquidType, Integer>();
		int noTypes = liquids.length;
		int noVols = volumes.length;
		this.setConcentration(concentration);
		if (noTypes > noVols) {
			System.out.println("Liquid mixture created has more types than it does volumes for them. Here is a list of each: ");
			for (int i=0; i<noVols; i++) {
				System.out.println("Type "+liquids[i]+" has volume "+volumes[i]);
				this.addConstituent(liquids[i], volumes[i]);
			}
			for (int i=noVols; i<noTypes; i++) {
				System.out.println("Type without volume: "+liquids[i]);
			}
		} else if (noVols > noTypes) {
			System.out.println("Liquid mixture created has less types than it does volumes for them. Here is a list of each: ");
			for (int i=0; i<noVols; i++) {
				System.out.println("Type "+liquids[i]+" has volume "+volumes[i]);
				this.addConstituent(liquids[i], volumes[i]);
			}
			for (int i=noVols; i<noTypes; i++) {
				System.out.println("Volume without type: "+volumes[i]);
			}
		} else {
			for (int i=0; i<noTypes; i++) {
				this.addConstituent(liquids[i], volumes[i]);
			}
		}
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

	public void addConstituent(LiquidType constituent, int volume) {
		if (types.containsKey(constituent)) {
			types.put(constituent, volume+types.get(constituent));
		} else {
			types.put(constituent, volume);
		}
		//With this method, it is assumed that the constituent should have 100% concentration.
		//Change the volume of the liquid as a whole so it reflects what has been added.
		//New concentration = weighted average of current and the new (assumedly 100%-concentration) liquid
		this.setVolume((int) (this.getVolume()+(volume*(100.0/this.getConcentration()))));
	}
	
	//Add all of a pure liquid to this one
	public void addConstituent(LiquidPure constituent) {
		this.addConstituent(constituent.getType(), constituent.getVolume());
	}
	
	public void mixWith(LiquidMixture mixture) {
		super.mixWith(mixture);
		for (Entry<LiquidType, Integer> liquid : mixture.getConstituents().entrySet()) {
			//If it exists, add to it. If not, create it.
			if (types.containsKey(liquid.getKey())) {
				types.put(liquid.getKey(), liquid.getValue()+types.get(liquid.getKey()));
			} else {
				types.put(liquid.getKey(), liquid.getValue());
			}
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
