package jUnit;

import static org.junit.Assert.*;

import org.junit.Test;

import version_7.*;

public class EntranceTesting {


	@Test
	public void roomConnectionTestNorth() {
		Room room1 = new Room(6, 6, 6, 6);
		Room room2 = new Room(8, 3, 4, 3);
		Entrance entrance = new Entrance(Direction.NORTH, new Coord2D(2, 0), new Coord2D(5, 0));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with a North entrance", room1.getAttached().keySet().contains(room2));
	}
	
	
	@Test
	public void roomConnectionTestSouth() {
		Room room1 = new Room(8, 3, 5, 3);
		Room room2 = new Room(6, 6, 6, 6);
		Entrance entrance = new Entrance(Direction.SOUTH, new Coord2D(2, 0), new Coord2D(5, 0));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with a South entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestEast() {
		Room room1 = new Room(6, 6, 6, 6);
		Room room2 = new Room(5, 6, 12, 7);
		Entrance entrance = new Entrance(Direction.EAST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with an East entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestWest() {
		Room room1 = new Room(8, 8, 12, 5);
		Room room2 = new Room(6, 6, 6, 6);
		Entrance entrance = new Entrance(Direction.WEST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertTrue("The rooms can be attached correctly with a West entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestWestFail() {
		Room room1 = new Room(8, 3, 12, 5);
		Room room2 = new Room(6, 6, 6, 6);
		Entrance entrance = new Entrance(Direction.WEST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertFalse("The first room is too thin for the Entrance", room1.getAttached().keySet().contains(room2));
	}

	@Test
	public void roomConnectionTestWestFail2() {
		Room room1 = new Room(8, 6, 12, 5);
		Room room2 = new Room(6, 6, 6, 6);
		Entrance entrance = new Entrance(Direction.WEST, new Coord2D(0, 2), new Coord2D(0, 5));
		GameClass.attachTwoLocations(room1, room2, entrance);
		assertFalse("The first room is just wide enough for the Entrance, but it is on a corner", room1.getAttached().keySet().contains(room2));
	}
	

	
}
