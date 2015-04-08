package jUnit;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import version_7.*;

public class EntranceTesting {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	
	@Test
	public void roomSetNewCorner() {
		Room room1 = new Room(6, 6, 6, 6);
		assertEquals(room1.getX(), 6);
		assertEquals(room1.getY(), 6);
		room1.setCorner(12, 14);
		assertEquals(12, room1.getX());
		assertEquals(14, room1.getY());
		room1.setCorner(new Coord2D(4, 9));
		assertEquals(4, room1.getX());
		assertEquals(9, room1.getY());
	}
	
	@Test
	public void roomConnectionTestNorth() {
		Room room1 = new Room(6, 6, 6, 6, "Room 1");
		Room room2 = new Room(8, 3, 4, 3, "Room 2");
		Entrance entrance = new Entrance(Direction.NORTH, new Coord2D(2, 0), new Coord2D(5, 0));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with a North entrance", room1.getAttached().keySet().contains(room2));
	}
	
	
	@Test
	public void roomConnectionTestSouth() {
		Room room1 = new Room(8, 3, 5, 3, "Room 1");
		Room room2 = new Room(6, 6, 6, 6, "Room 2");
		Entrance entrance = new Entrance(Direction.SOUTH, new Coord2D(2, 0), new Coord2D(5, 0));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with a South entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestEast() {
		Room room1 = new Room(6, 6, 6, 6, "Room 1");
		Room room2 = new Room(5, 6, 12, 7, "Room 2");
		Entrance entrance = new Entrance(Direction.EAST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with an East entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestWest() {
		Room room1 = new Room(8, 8, 12, 5, "Room 1");
		Room room2 = new Room(6, 6, 6, 6, "Room 2");
		Entrance entrance = new Entrance(Direction.WEST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with a West entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestWestFail() {
		exit.expectSystemExitWithStatus(1);
		Room room1 = new Room(8, 2, 12, 5, "Room 1");
		Room room2 = new Room(6, 6, 6, 6, "Room 2");
		Entrance entrance = new Entrance(Direction.WEST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertFalse("The first room is too thin for the Entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestWestFail2() {
		exit.expectSystemExitWithStatus(1);
		Room room1 = new Room(8, 6, 12, 5, "Room 1");
		Room room2 = new Room(6, 6, 6, 6, "Room 2");
		Entrance entrance = new Entrance(Direction.WEST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		fail("Not sure whether this should pass or fail: The first room is just wide enough for the Entrance, but it is on the edge");
		assertFalse("The first room is just wide enough for the Entrance, but it is on a corner", room1.getAttached().keySet().contains(room2));
	}
	

	
}
