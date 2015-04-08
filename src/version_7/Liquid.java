package version_7;

public class Liquid extends Item {

	//All liquids are watered down with water - this is the percentage of the Liquid that is not Water (Water has 0% concentration)
	private double concentration;
	
	public Liquid(int concentration) {
		this.concentration = concentration;
	}

	public double getConcentration() {
		return concentration;
	}

	public void setConcentration(double concentration) {
		this.concentration = concentration;
	}
	
	public boolean isWater() {
		return (concentration==0);
	}
	
}
