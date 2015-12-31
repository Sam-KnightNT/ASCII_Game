package jUnit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
//import org.junit.Rule;
//import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import version_7.*;

public class EntranceTesting {

	//@Rule
	//public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	
	@Test
	public void roomSetNewCorner() throws IOException {
		Room room1 = new Room(6, 6, 6, 6);
		assertEquals(room1.getX(), 6);
		assertEquals(room1.getY(), 6);
		room1.setCorner(new Coord2D(12, 14));
		assertEquals(12, room1.getX());
		assertEquals(14, room1.getY());
		room1.setCorner(new Coord2D(4, 9));
		assertEquals(4, room1.getX());
		assertEquals(9, room1.getY());
	}	
}
