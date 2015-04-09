package version_7;

public class Liquid extends Item {

	//TODO - add some kind of flowing mechanic. Liquids on the ground flow at a rate of 1ml/tick 
	//(or dependent on their viscosity) across each adjacent square that have less volume than them.
	
	//All liquids are watered down with water - this is the percentage of the Liquid that is not Water (Water has 0% concentration)
	private double concentration;
	
	//Volume of liquid is in millilitres
	private int volume;
	
	//Default constructor - 0 volume, 100% concentration
	public Liquid() {
		this.volume = 0;
		this.concentration = 100;
	}
	
	//Create 1 litre of liquid
	public Liquid(double concentration) {
		if (concentration>100) {
			System.out.println("Error: liquid concentration cannot be greater then 100%. Defaulting to 100%.");
			this.concentration = 100.0;
		} else {
			this.concentration = concentration;
			this.volume = 1000;
		}
	}
	
	public Liquid(double concentration, int volume) {
		if (concentration>100) {
			System.out.println("Error: liquid concentration cannot be greater then 100%. Defaulting to 100%.");
			this.concentration = 100.0;
		} else {
			this.concentration = concentration;
			this.volume = volume;
		}
	}
	
	//Create 100% pure liquid of this volume
	public Liquid(int volume) {
		this.volume = volume;
		this.concentration = 100;
	}

	public double getConcentration() {
		return concentration;
	}

	public void setConcentration(double concentration)  {
		if (concentration>100) {
			System.out.println("Error: liquid concentration cannot be greater then 100%. Defaulting to 100%.");
			this.concentration = 100.0;
		} else {
			this.concentration = concentration;
		}
	}
	
	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public boolean isWater() {
		return Math.abs(concentration)<0.0000001;
	}
	
	public boolean isPure() {
		return Math.abs(100-concentration)<0.0000001;
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
