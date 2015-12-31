package version_7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Map extends Location2D {

	/*
	 * Rewrite this to be a single floor of a dungeon. A Dungeon is a series of Maps, stitched together - it has Shorts telling each Map where it is in xyz coord space.
	 * Maps have their own xy coord space, in which Rooms lie. Corridors, maybe, but this is the main rewrite - Corridors should not be separate Locations, they should simply be parts of a Map that have been carved out.
	 * This solves the problem with dungeon generation entirely.
	 * So, a Dungeon is a series of Maps, say, no more than 50. It contains the location of each Map in its xyz space (shortXshortXbyte?), the entrance to the dungeon itself, and a method that, given a Map and a coord in that Map, returns the next Map to go to and where the player should be.
	 * A Map contains a short's worth of blocks (256x256), and a list of Rooms, along with their Entrances. Its job is to contain the data about each tile within. If a player reaches an Entrance, it should calculate which Room that corresponds to and stick the player there.
	 * A Room contains Entities, Items and so on. It doesn't need to know its location, that bit of the code should be replaced with putting them in the Map.
	 */
	
	/*
	 * Alternate idea for dungeon generation:
	 * A Map has several Rooms. Those Rooms are generated from a list of... some, a few at first.
	 * They are placed together haphazardly, like the old algorithm, but unlike that they are predefined.
	 * They are just placed in the world, hopefully fitting together.
	 * Whatever doesn't fit is regenerated. If this fails 10 times, the entire thing is regenerated.
	 * If this fails 1000 times, the Map is cancelled with an Exception, something is wrong.
	 * Test this at this point, and link it to the game. Once that's done, add corridors.
	 * These will be defined later.
	 */
	
	private ArrayList<Room> rooms = new ArrayList<Room>();
	private Coord3D position;
	private byte x;
	private byte y;
	private byte z;
	
	public Map(int w, int h, int x, int y, int z) throws IOException {
		this((byte) w, (byte) h, (byte) x, (byte) y, (byte) z);
	}

	public Map(int w, int h, int x, int y, int z, TileType tileType) {
		this((byte) w, (byte) h, (byte) x, (byte) y, (byte) z, tileType);
	}
	
	public Map(byte w, byte h, byte x, byte y, byte z) throws IOException {
		this(w, h, x, y, z, new TileType("Dummy Tile", ImageIO.read(new File("images/materials/default.png")), new ArrayList<String>(), null));
	}

	public Map(byte w, byte h, byte x, byte y, byte z, TileType tileType) {
		this.setW(w);
		this.setH(h);
		this.setPosition(x, y, z);
		TileSpace2D tilespace = new TileSpace2D();
		tilespace.setTiles(new Tile[w][h]);
		tilespace.x = w;
		tilespace.y = h;
		for (int hI = 0; hI < h; hI++) {
			for (int wI = 0; wI < w; wI++) {
				tilespace.setTile(wI, hI, tileType);
			}
		}
		this.setTiles(tilespace);
	}
	
	public byte getX() {
		return x;
	}
	public void setX(byte x) {
		this.x = x;
	}
	
	public byte getY() {
		return y;
	}
	public void setY(byte y) {
		this.y = y;
	}
	
	public byte getZ() {
		return z;
	}
	public void setZ(byte z) {
		this.z = z;
	}
	
	public Coord3D getPosition() {
		return position;
	}
	
	public void setPosition(Coord3D p) {
		position = p;
		setX(p.getX());
		setY(p.getY());
		setZ(p.getZ());
	}
	public void setPosition(byte x, byte y, byte z) {
		position = new Coord3D(x, y, z);
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	public void addRoom(Room room) {
		rooms.add(room);
		int rx = room.getX();
		int ry = room.getY();
		for (int y = 0; y < room.getH(); y++) {
			for (int x = 0; x < room.getW(); x++) {
				this.setTile(x+rx, y+ry, room.getTile(x, y).getType());
			}
		}
		for (ItemTile item : room.getItems()) {
			this.addItem(new ItemTile(item.getItem(), new Coord3D(item.getLocation().add(room.getPosition()))));
		}
		for (EntityTile entity : room.getEntities()) {
			this.addEntity(entity);
		}
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}
	
	
	public boolean containsPoint(Coord3D coords) {
		int dx = coords.getX()-getX();
		int dy = coords.getY()-getY();
		if (getZ() == coords.getZ() && (dx >= 0 && dx <= getW()) && (dy >= 0 && dy <= getH())) {
			return true;
		}
		return false;
	}
	
	public Coord2D getSize() {
		// TODO Auto-generated method stub
		return new Coord2D(getW(), getH());
	}
	
	public String toString() {
		return "Map: ("+getX()+", "+getY()+", "+getZ()+"), "+getW()+"x"+getH();
	}
	
	public void fill(Coord a, Coord b, TileType t) {
		// TODO Auto-generated method stub
		
	}
	
	public byte getV() {
		// TODO Auto-generated method stub
		return 0;
	}
}