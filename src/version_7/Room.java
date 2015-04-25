package version_7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Room extends Location{

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
	
	public Room(byte w, byte h, byte x, byte y) throws IOException {
		//Create dummy tile types, that are just the default ones.
		this(w, h, x, y, new TileType("Dummy Tile", ImageIO.read(new File("images/materials/default.png")), true, null, null),
				new TileType("Dummy Tile", ImageIO.read(new File("images/materials/default.png")), false, null, null));
	}
	
	public Room(byte w, byte h, byte x, byte y, TileType floorTileType, TileType wallTileType) {
		this.setW(w);
		this.setH(h);
		this.setCorner(x, y);
		setTiles(new Tile[w][h]);
		for (int yI = 1; yI < h-1; yI++) {
			for (int xI = 1; xI < w-1; xI++) {
				setTile(xI, yI, floorTileType);
			}
		}
		for (int xI = 0; xI < w; xI++) {
			setTile(xI, 0, wallTileType);
			setTile(xI, h-1, wallTileType);
		}
		for (int yI = 0; yI < h; yI++) {
			setTile(0, yI, wallTileType);
			setTile(w-1, yI, wallTileType);
		}
	}

	public Room(byte w, byte h, byte x, byte y, TileType floorTileType, TileType wallTileType, HashMap<Location, Entrance> attachments) {
		this(w, h, x, y, floorTileType, wallTileType);
		setAttachments(attachments);
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
		for (int yI = 1; yI < h-1; yI++) {
			for (int xI = 1; xI < w-1; xI++) {
				setTile(xI, yI, floorTileType);
			}
		}
		for (int xI = 0; xI < w; xI++) {
			setTile(xI, 0, wallTileType);
			setTile(xI, h-1, wallTileType);
		}
		for (int yI = 0; yI < h; yI++) {
			setTile(0, yI, wallTileType);
			setTile(w-1, yI, wallTileType);
		}
	}
	public Room(int w, int h, int x, int y, TileType floorTileType, TileType wallTileType, HashMap<Location, Entrance> attachments) {
		this(w, h, x, y, floorTileType, wallTileType);
		this.setAttachments(attachments);
	}
	
	
	public Room(int w, int h, int x, int y, String name) throws IOException {
		this(w, h, x, y);
		this.setName(name);
	}

	public void carveEntrancesWithCurrentAttachments(TileType floor) {
		carveEntrances(new ArrayList<Entrance>(getAttached().values()), floor);
	}
	public void carveEntrances(ArrayList<Entrance> entrances, TileType floorTileType) {
		for (Entrance entrance : entrances) {
			for (int s=entrance.getNearSide(); s<entrance.getFarSide(); s++) {
				switch (entrance.getDirection()) {
				case NORTH:
					setTile(s, 0, floorTileType);
					break;
				case SOUTH:
					setTile(s, getH()-1, floorTileType);
					break;
				case WEST:
					setTile(0, s, floorTileType);
					break;
				case EAST:
					setTile(getW()-1, s, floorTileType);
					break;
				}
			}
		}
	}
	
	public void pillarCorners(TileType type) {
		//TODO - once this gets large, see what happens if you use bitmath, i.e. getH() & 0x1. Use two's complement.
		byte h = (byte) (getH()-1);
		byte w = (byte) (getW()-1);
		setTile(0, 0, type);
		setTile(0, h, type);
		setTile(w, 0, type);
		setTile(w, h, type);
	}

	@Override
	public void moveCamera() {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		return "Room: "+getName()+", size ("+getW()+", "+getH()+"), location ("+getX()+", "+getY()+")";
	}
	
	public void setCorner(int x, int y) {
		setCorner((byte) x, (byte) y);
	}
}
