package jUnit;

import static org.junit.Assert.*;

import java.io.IOException;

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
	
	private Room room1;
	private Room room2;
	private Room room3;
	private Entity entity;
	private EntityTile entityTile;
	
	@Before
	public void setUp() throws IOException {
		//Create testing area - 3 Rooms, one on top of the other
		room1 = new Room(10, 10, 0, 10, "Room 1");
		room2 = new Room(10, 10, 0, 0, "Room 2");
		room3 = new Room(10, 10, 0, -10, "Room 3");
		entity = new Entity("Test Entity", null, null, null);
		entityTile = new EntityTile(entity, room1, (byte) 3, (byte) 4, (byte) 0, null);
		room1.addEntity(entityTile);
	}
	
	@Test
	public void createEntity() {
		assertEquals("Entity has a name", "Test Entity", entity.getName());
		assertEquals("Entity's location has been set correctly", room1, entityTile.getLocation());
		assertEquals("Entity's position has been set correctly", new Coord3D(3, 4, 0), entityTile.getCoords());
		assertEquals("Entity's x position has been initialised", 3, entityTile.getX());
		assertEquals("Entity's y position has been initialised", 4, entityTile.getY());
		assertEquals("Entity's z position has been initialised", 0, entityTile.getZ());
	}

	@Test
	public void moveEntityNorth() {
		GameClass.move(Direction.NORTH, entityTile);
		assertEquals("Entity's new position is correct", new Coord3D(3, 3, 0), entityTile.getCoords());
		assertEquals("Entity's x position has been changed", 3, entityTile.getX());
		assertEquals("Entity's y position has been changed", 3, entityTile.getY());
		assertEquals("Entity's z position has been changed", 0, entityTile.getZ());
	}

	@Test
	public void moveEntityEast() {
		GameClass.move(Direction.EAST, entityTile);
		assertEquals("Entity's new position is correct", new Coord3D(4, 4, 0), entityTile.getCoords());
		assertEquals("Entity's x position has been changed", 4, entityTile.getX());
		assertEquals("Entity's y position has been changed", 4, entityTile.getY());
		assertEquals("Entity's z position has been changed", 0, entityTile.getZ());
	}
	
	//Need to check moving an entity from one room into the next
	//Although since it uses a map system, probably need to check that instead
	
}
