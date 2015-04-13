package version_7;

public class Bottle extends Item {

	//The capacity in millilitres.
	private int capacity;
	
	public Bottle(int capacity) {
		super("Bottle", null, String.format("An empty bottle used for storing liquids. Has a capacity of %.4f litres.", Double.valueOf((double) capacity)/1000.0));
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}
}
