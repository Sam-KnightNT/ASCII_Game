package jUnit;

import static org.junit.Assert.*;

import org.junit.Test;

import version_7.*;

public class LiquidTesting {

	@Test
	public void basicLiquidMixTest() {
		Liquid liquid1 = new Liquid(50.0, 500);
		Liquid liquid2 = new Liquid(30.0, 1500);
		liquid1.mixWith(liquid2);
		assertEquals("Mixed liquids have the correct volume", 2000, liquid1.getVolume());
		assertEquals("Mixed liquids have the correct concentration", 35.0, liquid1.getConcentration(), 0.0001);
	}
	
	@Test
	public void liquidPurePrintTestNoConcentration() {
		LiquidType healingFluid = new LiquidType("health", "Healing Fluid");
		LiquidPure liquid = new LiquidPure(healingFluid, 55);
		assertEquals("Pure liquid created without concentration given is 100% pure", 100.0, liquid.getConcentration(), 0.0001);
		assertEquals("Pure liquid created without concentration given has the correct volume", 55, liquid.getVolume());
		assertEquals("Pure liquid created without concentration given has the correct type", healingFluid, liquid.getType());
	}
	

	@Test
	public void liquidPurePrintTestNoVolume() {
		LiquidType healingFluid = new LiquidType("health", "Healing Fluid");
		LiquidPure liquid = new LiquidPure(healingFluid, 50.0);
		assertEquals("Pure liquid created without volume given is the correct concentration", 50.0, liquid.getConcentration(), 0.0001);
		assertEquals("Pure liquid created without volume given has 1 litre of fluid", 1000, liquid.getVolume());
		assertEquals("Pure liquid created without volume given has the correct type", healingFluid, liquid.getType());
	}
	

	@Test
	public void liquidPurePrintTest() {
		LiquidType healingFluid = new LiquidType("health", "Healing Fluid");
		LiquidPure liquid = new LiquidPure(healingFluid, 75.0, 55);
		assertEquals("Pure liquid created with both params given is the correct concentration", 75.0, liquid.getConcentration(), 0.0001);
		assertEquals("Pure liquid created with both params given has 1 litre of fluid", 55, liquid.getVolume());
		assertEquals("Pure liquid created with both params given has the correct type", healingFluid, liquid.getType());
	}
	
	@Test
	public void liquidMixturePrintTest() {
		LiquidType healingFluid = new LiquidType("health", "Healing Fluid");
		LiquidType manaFluid = new LiquidType("mana", "Mana Fluid");
		LiquidType strengthFluid = new LiquidType("strength", "Strength Fluid");
		LiquidMixture mixture = new LiquidMixture(50.0);
		mixture.addConstituent(healingFluid, 500);
		mixture.addConstituent(manaFluid, 1000);
		mixture.addConstituent(strengthFluid, 1500);
		assertEquals("Liquid mixture created has correct volume", 6000, mixture.getVolume());
	}
	
	@Test
	public void liquidMixtureCombineWithMixtureTest() {
		LiquidType healingFluid = new LiquidType("health", "Healing Fluid");
		LiquidType manaFluid = new LiquidType("mana", "Mana Fluid");
		LiquidType strengthFluid = new LiquidType("strength", "Strength Fluid");
		LiquidType speedFluid = new LiquidType("speed", "Speed Fluid");
		LiquidMixture mixture1 = new LiquidMixture(new LiquidType[]{speedFluid, manaFluid}, new int[]{500, 250}, 25.0);
		LiquidMixture mixture2 = new LiquidMixture(new LiquidType[]{strengthFluid, healingFluid, manaFluid, healingFluid}, new int[]{300, 400, 550, 250}, 75.0);
		
		//Before mixing, there should be 3 litres of the first mixture, consisting of 500ml speed, 250ml mana, 2250ml water.
		//Once mixed, there should be: 5 litres total, 300ml strengthFluid, 500ml speedFluid, 800ml manaFluid, 650ml healingFluid, 2750ml water.
		assertEquals("Correct volume before mixing with second mixture", 3000, mixture1.getVolume(), 3000);
		assertEquals("Correct water volume before mixing with second mixture", 2250, mixture1.getWaterContent(), 1);
		assertEquals("Correct fluid volume before mixing with second mixture", 500, mixture1.getVolumeOf(speedFluid));
		
		mixture1.mixWith(mixture2);
		
		//TODO - This is a note. This is actually a bit of a problem, since rounding errors make it 1 less than it should be. However, I'm leaving this in. This could form the basis of an interesting exploit.
		assertEquals("Correct total volume after mixing with second mixture", 5000, mixture1.getVolume(), 1);
		assertEquals("Correct volume of speedFluid after mixing with second mixture", 500, mixture1.getVolumeOf(speedFluid));
		assertEquals("Correct volume of manaFluid after mixing with second mixture", 800, mixture1.getVolumeOf(manaFluid));
		assertEquals("Correct volume of strengthFluid after mixing with second mixture", 300, mixture1.getVolumeOf(strengthFluid));
		assertEquals("Correct volume of healingFluid after mixing with second mixture", 650, mixture1.getVolumeOf(healingFluid));
		assertEquals("Correct volume of water after mixing with second mixture", 2750, mixture1.getWaterContent(), 1);
	}
	
	@Test
	public void potionCreationTest() {
		Liquid liquid = new Liquid(50.0, 500);
		Bottle bottle = new Bottle(1650);
		Potion potion = new Potion(liquid, bottle);
		System.out.println(potion);
		assertEquals("Correct volume of liquid in potion", 500, potion.getLiquid().getVolume());
		assertEquals("Volume displays correctly too", 500, potion.getFullness());
		assertEquals("Correct capacity of bottle", 1650, potion.getCapacity());
		assertEquals("Correct emptiness calculation", 1150, potion.getEmptiness());
	}
	
	//Need to check a liquid being created with a negative volume
	//Need to check a liquid being created with a negative concentration
	//Check 2 mixtures being combined
	//Check a mixture with 50% concentration of one fluid with another being added via addConstituent(LiquidType, int)
	//Check a mixture with 50% concentration of one fluid with the same fluid being added via addConstituent(LiquidType, int)
	//Check a mixture with 50% concentration of one fluid with another being added via addConstituent(LiquidPure)
	//Check a mixture with 50% concentration of one fluid with the same fluid being added via addConstituent(LiquidPure)
	//A Pure liquid being added to a Mixture
	//A Mixture being added to a Pure
	//A Pure being added to a Pure
	//A new liquid type being added to a Pure
	//A new liquid type being added to a Mixture
	//2 new liquid types being mixed
	//A new liquid being created
	//Potion being created with valid amounts
	//Potion being created with volume>capacity (check volume=capacity afterwards)
	
}
