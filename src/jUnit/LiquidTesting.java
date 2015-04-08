package jUnit;

import static org.junit.Assert.*;

import org.junit.Test;

import version_7.Liquid;

public class LiquidTesting {

	@Test
	public void basicLiquidMixTest() {
		Liquid liquid1 = new Liquid(50.0, 500);
		Liquid liquid2 = new Liquid(30.0, 1500);
		liquid1.mixWith(liquid2);
		assertEquals("Mixed liquids have the correct volume", 2000, liquid1.getVolume());
		assertEquals("Mixed liquids have the correct concentration", 35.0, liquid1.getConcentration(), 0.0001);
	}
	
}
