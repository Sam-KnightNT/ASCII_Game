package version_7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Room extends Location2D {

	//Has width, height and location (lower-left point)

	/*
	 * Dimensions
	 * Only square/rectangular for now - later add other shapes
	 * First add relative coordinates of any additions/subtractions
	 * (e.g. for slightly rounded corners)
	 * Eventually have several thousand pregenerated rooms that can be placed together like a jigsaw
	 * Or, have templates for rooms
	 * e.g. "Throne room: 20x30:40x60, must be 2:3 size and even length (including walls). 2 entrances at far side, 100% chance of this.
	 * 2 entrances at sides, close to far side, 50% chance. 2 entrances at sides, close to near side, 50%.
	 * Secret entrance behind throne, must use <item> on throne. Leads to treasure room. 25% chance. If not, generate treasure room elsewhere, 80% chance.
	 * Pillars every alternating space, 25% distance from each side wall, starting at a distance of 3 from far wall, 4 from near wall.
	 * Throne 2 away from the near side wall, equidistant from side walls.
	 * Example room (with all optional doors, probably different size) 
	 * --------------o-----------------
	 * -                              -
	 * -             T                -
	 * -                              -
	 * -     P                P       -
	 * o                              o
	 * -     P                P       -
	 * -                              -
	 * -     P                P       -
	 * -                              -
	 * -     P                P       -
	 * -                              -
	 * -     P                P       -
	 * -                              -
	 * -     P                P       -
	 * o                              o
	 * -     P                P       -
	 * -                              -
	 * -     P                P       -
	 * -                              -
	 * -                              -
	 * -------o--------------o---------
	 */
		
	private Coord2D position;
	private byte x;
	private byte y;
	
	public Room(byte w, byte b, byte x, byte y) throws IOException {
		//Create dummy tile types, that are just the default ones.
		this(w, b, x, y, new TileType("Dummy Tile", ImageIO.read(new File("images/materials/default.png")), new ArrayList<String>(Arrays.asList("All")), null),
				new TileType("Dummy Tile", ImageIO.read(new File("images/materials/default.png")), new ArrayList<String>(), null));
	}
	
	public Room(byte w, byte h, byte x, byte y, TileType floorTileType, TileType wallTileType) {
		this.setW(w);
		this.setH(h);
		this.setX(x);
		this.setY(y);
		this.setPosition(x, y);
		TileSpace2D tilespace = new TileSpace2D();
		tilespace.setTiles(new Tile[w][h]);
		for (int yI = 1; yI < h-1; yI++) {
			for (int xI = 1; xI < w-1; xI++) {
				tilespace.setTile(xI, yI, floorTileType);
			}
		}
		for (int xI = 0; xI < w; xI++) {
			tilespace.setTile(xI, 0, wallTileType);
			tilespace.setTile(xI, h-1, wallTileType);
		}
		for (int yI = 0; yI < h; yI++) {
			tilespace.setTile(0, yI, wallTileType);
			tilespace.setTile(w-1, yI, wallTileType);
		}
		this.setTiles(tilespace);
	}
	
	public Room(byte w, byte h, byte x, byte y, String name) throws IOException {
		this(w, h, x, y);
		this.setName(name);
	}

	//Casting ints as bytes (using bytes saves a lot of space, as there will be a LOT of these coordinates flying around. It'll save 75% of the coordinate memory)
	public Room(int w, int h, int x, int y) throws IOException {
		this((byte) w, (byte) h, (byte) x, (byte) y);
	}
	public Room(int w, int h, int x, int y, TileType floorTileType, TileType wallTileType) {
		this((byte) w, (byte) h, (byte) x, (byte) y, floorTileType, wallTileType);
	}
	
	public Room(int w, int h, int x, int y, String name) throws IOException {
		this(w, h, x, y);
		this.setName(name);
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
		return 0;
	}
	
	public byte getV() {
		return 0;
	}
	
	public String toString() {
		return "Room: "+getName()+", size ("+getW()+", "+getH()+"), location ("+getX()+", "+getY()+")";
	}
	
	public void setPosition(byte x, byte y) {
		this.setX(x);
		this.setY(y);
		this.position = new Coord2D(x, y);
	}
	
	public void setPosition(int x, int y) {
		setPosition((byte) x, (byte) y);
	}
	
	public Coord2D getPosition() {
		return position;
	}
	
	public Coord2D getSize() {
		return new Coord2D(getW(), getH());
	}
	
	public void setCorner(Coord2D c) {
		this.setX(c.getX());
		this.setY(c.getY());
	}
}
