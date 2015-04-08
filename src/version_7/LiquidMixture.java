package version_7;

import java.util.HashMap;

public class LiquidMixture extends Liquid {

	private HashMap<LiquidType, Integer> types;
	
	public LiquidMixture(int concentration, HashMap<LiquidType, Integer> types) {
		super(concentration);
		this.types = types;
	}

	public HashMap<LiquidType, Integer> getConstituents() {
		return types;
	}

	public void setConstituents(HashMap<LiquidType, Integer> types) {
		this.types = types;
	}

}
