package version_7;

// Potion basically refers to anything that is a liquid in a bottle, drinkable or not. You should either drink or throw them, depending on the contents.
public class Potion extends Item {

	//Liquid may be a pure liquid, or a mixture.
	private Liquid liquid;
	
	private Bottle bottle;
	
	//The volume of liquid contained within.
	private int fullness;
	
	//TODO - find out if there's a better way to do the Bottle syntax. I could just get rid of the Bottle class altogether, since all Bottles are used for is Potions, but it looks nice and neat.
	public Potion(Liquid liquid, Bottle bottle) {
		//This is a horrible line and it's only here because Java doesn't allow superconstructors to be called beyond the first line.
		//It calls the superconstructor with the following:
		//	A string that is "Bottle" if there is no liquid present, or "Potion of <liquidname>" if there is.
		//	Null - this would be the image used to display it AND NEEDS TO BE DONE.
		//TODO - do ^that.
		//A formatted string that displays a capacity and a string.
		//The logic of the last string of the description is:
		//"Is the liquid volume 0? If so, display 'is empty'. If not, check the volume vs the capacity. If they are equal, return 'is full', otherwise display the volume and space remaining."
		super(liquid.getVolume()==0 ? "Bottle" : "Potion of "+liquid.getName(), null, 
				String.format("A bottle used for storing liquids. Has a capacity of %.4f litres%s.", bottle.getCapacity()/1000.0, 
						liquid.getVolume()==0 	? ""
												: " and "+ (liquid.getVolume()==bottle.getCapacity()
													? "is full"
													: String.format("contains %.4fl of liquid, leaving %.4fl free.",
																								liquid.getVolume()/1000.0, (bottle.getCapacity()-liquid.getVolume())/1000.0))));
		if (liquid.getVolume()<=bottle.getCapacity()) {
			this.liquid = liquid;
			this.bottle = bottle;
			fullness = liquid.getVolume();
		} else {
			GameClass.print("Invalid potion - liquid volume is greater than bottle capacity. Reducing liquid volume to compensate.");
			//TODO - change this to spill some of the liquid out into the world, after issuing a warning to the player.
			this.liquid = liquid;
			this.liquid.setVolume(bottle.getCapacity());
			this.bottle = bottle;
		}
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
	
	/**
	 * Fills this potion with liquid
	 * @param liquid The liquid to be added to be this potion.
	 * @param volume The amount of liquid to pour into this.
	 * @return true iff this action is successful (i.e. potion bottle has enough capacity remaining)
	 */
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
	
	/**
	 * Pours part of this potion into another.
	 * @param potion The potion to pour into.
	 * @param volume The volume being poured into this potion.
	 * @return true iff this action is successful (i.e. other potion has sufficient space left)
	 */
	public boolean pourInto(Potion potion, int volume) {
		boolean success = potion.getEmptiness()<=volume && this.pourOut(volume);
		if (success) {
			potion.fillWith(this.getLiquid(), volume); 
		} else if (!(potion.getEmptiness()<=volume)) {
			System.out.println("Cannot pour into "+potion.getName()+" - insufficient capacity");
		} else {
			System.out.println("Cannot pour into "+potion.getName()+" - not enough liquid in this container");
		}
		updateDescription();
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
	
	public int getFullness() {
		return fullness;
	}
	
	public int getEmptiness() {
		return bottle.getCapacity()-fullness;
	}
	
	public int getCapacity() {
		return bottle.getCapacity();
	}
	
	public void updateDescription() {
		this.setDescription(this.getDescString());
	}
	
	public String getDescString() {
		String descStr = String.format("A bottle used for storing liquids. Has a capacity of %.4f litres", bottle.getCapacity()/1000.0);
		if (fullness==bottle.getCapacity()) {
			descStr += " and is full.";
		} else if (fullness==0) {
			descStr += ".";
		} else {
			descStr += String.format(" and contains %.4fl of liquid, leaving %.4fl free.", fullness/1000.0, (bottle.getCapacity()-fullness)/1000.0);
		}
		return descStr;
	}
}
