package version_7;

import java.awt.image.BufferedImage;

public class Liquid extends Item {

	//TODO - add some kind of flowing mechanic. Liquids on the ground flow at a rate of 1ml/tick 
	//(or dependent on their viscosity) across each adjacent square that have less volume than them.
	
	//All liquids are watered down with water - this is the percentage of the Liquid that is not Water (Water has 0% concentration)
	private double concentration;
	
	//Volume of liquid is in millilitres
	private int volume;
	
	//Default values are always 1 litre (1000ml) volume, 100% concentration
	//This constructor passing is a bit messy, but it ensures I only have to define everything once. If I defined every parameter in every constructor these first ones would be massive.
	public Liquid() {
		this(100, 1000);
	}
	public Liquid(double concentration) {
		this(concentration, 1000);
	}
	public Liquid(int volume) {
		this(100, volume);
	}
	public Liquid(double concentration, int volume) {
		this(concentration, volume, new LiquidType("Generic Liquid"));
	}
	
	//The same constructors with a single type defined
	public Liquid(LiquidType type) {
		this(100, 1000, type);
	}
	public Liquid(double concentration, LiquidType type) {
		this(concentration, 1000, type);
	}
	public Liquid(int volume, LiquidType type) {
		this(100, volume, type);
	}
	public Liquid(double concentration, int volume, LiquidType type) {
		this(concentration, volume, type, null, "A liquid of type "+type.getName());
	}
	
	//Multiple types defined
	public Liquid(double concentration, int volume, LiquidType[] types) {
		this(concentration, volume, types, "A liquid consisting of: "+types.toString().replaceAll("[\\[\\]]", ""));
	}
	
	//Defined description
	public Liquid(double concentration, int volume, String description) {
		this(concentration, volume, new LiquidType("Generic Liquid"), null, description);
	}
	
	//Single type and description
	public Liquid(double concentration, int volume, LiquidType type, String description) {
		this(concentration, volume, type, null, description);
	}
	
	//Multiple types and a description
	public Liquid(double concentration, int volume, LiquidType[] types, String description) {
		this(concentration, volume, types, null, description);
	}
	
	
	
	//MAIN CONSTRUCTORS
	
	//Single type, image and description
	public Liquid(double concentration, int volume, LiquidType type, BufferedImage image, String description) {
		super(type.getName(), image, description);
		this.setConcentration(concentration);
		this.setVolume(volume);
	}
	
	
	//Multiple types, image and description
	public Liquid(double concentration, int volume, LiquidType[] types, BufferedImage image, String description) {
		super("Liquid Mixture", image, description);
		this.setConcentration(concentration);
		this.setVolume(volume);
	}
	
	
	
	
	
	
	
	public double getConcentration() {
		return concentration;
	}

	public void setConcentration(double concentration)  {
		//Concentration is a percentage, so clamp it to a maximum of 100. (Actually don't, for shenanigans)
		/*if (concentration>100) {
			System.out.println("Error: liquid concentration cannot be greater then 100%. Defaulting to 100%.");
			this.concentration = 100.0;
		} else {*/
			this.concentration = concentration;
		//}
	}
	
	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public boolean isWater() {
		return concentration<0.0000001;
	}
	
	public boolean isPure() {
		return 100-concentration<0.0000001;
	}
	
	public double getWaterContent() {
		return volume*(1-(concentration/100));
	}
	
	public void mixWith(double concentration, int volume) {
		//The volumes are simply added to each other
		//The concentrations are averaged, weighted by the volumes
		int newVolume = this.volume + volume;
		this.concentration = ((this.volume*this.concentration) + (volume*concentration)) / newVolume;
		this.volume = newVolume;
	}

	public void mixWith(Liquid liquid) {
		mixWith(liquid.getConcentration(), liquid.getVolume());
	}
}
