package version_7;

public class Liquid extends Item {

	//TODO - add some kind of flowing mechanic. Liquids on the ground flow at a rate of 1ml/tick 
	//(or dependent on their viscosity) across each adjacent square that have less volume than them.
	
	//All liquids are watered down with water - this is the percentage of the Liquid that is not Water (Water has 0% concentration)
	private double concentration;
	
	//Volume of liquid is in millilitres
	private int volume;
	
	//Create 1 litre of liquid
	public Liquid(double concentration) {
		this.concentration = concentration;
		this.volume = 1000;
	}
	
	public Liquid(double concentration, int volume) {
		this.concentration = concentration;
		this.volume = volume;
	}

	public double getConcentration() {
		return concentration;
	}

	public void setConcentration(double concentration) {
		this.concentration = concentration;
	}
	
	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public boolean isWater() {
		return (concentration==0);
	}
	
	public void mixWith(double concentration, int volume) {
		//The volumes are simply added to each other, but the concentrations are a bit harder to calculate.
		//They should be a weighted average of the concentrations, depending on the volumes. So...
		int newVolume = this.volume + volume;
		this.concentration = ((this.volume*this.concentration) + (volume*concentration)) / newVolume;
		this.volume = newVolume;
	}

	public void mixWith(Liquid liquid) {
		mixWith(liquid.getConcentration(), liquid.getVolume());
	}
}
