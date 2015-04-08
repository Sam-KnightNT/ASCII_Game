package version_7;

public class Bottle extends Item {

	//The capacity in millilitres.
	private int capacity;
	
	public Bottle(int capacity) {
		super("Bottle", null, String.format("A bottle used for storing liquids. Has a capacity of %d4 litres and is empty.", capacity/1000f));
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}
}
