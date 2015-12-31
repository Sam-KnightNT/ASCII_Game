package jUnit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import version_7.Room;

public class LocationTesting {


	@Test
	public void roomChangeSize() throws IOException {
		//This does not work - the tilespace does not change size when the location does.
		Room room = new Room(20, 20, 1, 10);
		room.setW(10);
		assertEquals(10, room.getTileSpace().getW());
	}
	
}
