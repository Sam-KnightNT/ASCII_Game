package version_7;

// Potion basically refers to anything that is a liquid in a bottle, drinkable or not. You should either drink or throw them, depending on the contents.
public class Potion extends Item {

	//Liquid may be a pure liquid, or a mixture.
	private Liquid liquid;
	
	private Bottle bottle;
	//The fullness of the bottle, in millilitres.
	private int fullness;
	
	
	public Potion(Liquid liquid, Bottle bottle, int fullness) {
		super("Potion of "+liquid.getName(), null, "A potion used for storing liquids. Has a capacity of %d4 litres and is empty");
		this.liquid = liquid;
		this.bottle = bottle;
		this.fullness = fullness;
	}
	
	public Liquid getLiquid() {
		return liquid;
	}

	public void setLiquid(Liquid liquid) {
		this.liquid = liquid;
	}

	public Bottle getBottle() {
		return bottle;
	}

	public void setBottle(Bottle bottle) {
		this.bottle = bottle;
	}
	
	public boolean fillWith(Liquid liquid, int volume) {
		if (volume+fullness>bottle.getCapacity()) {
			System.out.println("Insufficient space in bottle left");
			return false;
		} else {
			fullness += volume;
			this.updateDescription();
			return true;
		}
	}
	
	public boolean pourInto(Potion potion, int volume) {
		boolean success = potion.getEmptiness()<=volume && this.pourOut(volume);
		if (success) {
			potion.fillWith(this.getLiquid(), volume); 
		} else if (!(potion.getEmptiness()<=volume)) {
			System.out.println("Cannot pour into "+potion.getName()+" - insufficient capacity");
		} else {
			System.out.println("Cannot pour into "+potion.getName()+" - not enough liquid in this container");
		}
		return success;
	}
	
	public boolean pourOut(int volume) {
		if (fullness-volume<0) {
			System.out.println("Insufficient liquid to pour out that much.");
			return false;
		} else {
			fullness -= volume;
			this.updateDescription();
			return true;
		}
	}
	
	public int getEmptiness() {
		return bottle.getCapacity()-fullness;
	}
	
	public void updateDescription() {
		String descStr = String.format("A bottle used for storing liquids. Has a capacity of %d4 litres and ", bottle.getCapacity()/1000f);
		if (fullness==bottle.getCapacity()) {
			descStr += "is full.";
		} else if (fullness==0) {
			descStr += "is empty.";
		} else {
			descStr += String.format("contains %d4l of liquid, leaving %d4l free.", fullness/1000f, (bottle.getCapacity()-fullness)/1000f);
		}
		this.setDescription(descStr);
	}
}
