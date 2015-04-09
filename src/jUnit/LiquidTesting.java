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
	
	//Need to check 2 mixtures being combined
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
	
}
