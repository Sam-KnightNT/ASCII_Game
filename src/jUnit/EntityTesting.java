package jUnit;

import org.junit.*;

import version_7.*;

public class EntityTesting {

	/*Tests:
	 * Test Entity creation, that all the parameters are created and can be retreived
	 * Call "move", move it one space NORTH, check its x and y before and after
	 * Call "move", move it EAST, check x and y
	 * Call "move", move NORTH, EAST, NORTH, NORTH, NORTH, WEST, SOUTH, WEST, WEST, check x and y
	 * Create player, call "pathfindToPlayer" when it is in the same room
	 * Create player, call "pathfindToPlayer" from a different room
	 * Create player, repeatedly call "pathfindToPlayer" and check it gets into a fight once they meet
	 * Create player, call "cycle" with player moving NORTH, check pathfindToPlayer doesn't mess up once player goes into new Room
	 */
	
	@Before
	public void setUp() {
		//Create testing area - 3 Rooms, one on top of the other
		Room room1 = new Room(0, -10, 10, 10);
		Room room2 = new Room(0, 0, 10, 10);
		Room room3 = new Room(0, 10, 10, 10);
		Entrance ent1 = new Entrance(Direction.NORTH, new Coord2D(1, 0), new Coord2D(9, 0));
		Entrance ent2 = new Entrance(Direction.NORTH, new Coord2D(1, 0), new Coord2D(9, 0));
		GameClass.attachTwoLocations(room1, room2, ent1);
		GameClass.attachTwoLocations(room2, room3, ent2);
	}
	
}
