package version_7;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	public Room (int w, int h, int x, int y) {
		width = w;
		height = h;
		corner = new Coord2D(x, y);
		this.x = x;
		this.y = y;
		tiles = new Tile[w][h];
	}
	
	public Room(int w, int h, int x, int y, TileType floorTileType, TileType wallTileType) {
		width = w;
		height = h;
		corner = new Coord2D(x, y);
		this.x = x;
		this.y = y;
		tiles = new Tile[w][h];
		for (int yI = 1; yI < h-1; yI++) {
			for (int xI = 1; xI < w-1; xI++) {
				tiles[xI][yI] = new Tile(floorTileType);
			}
		}
		for (int xI = 0; xI < w; xI++) {
			tiles[xI][0] = new Tile(wallTileType);
			tiles[xI][h-1] = new Tile(wallTileType);
		}
		for (int yI = 0; yI < h; yI++) {
			tiles[0][yI] = new Tile(wallTileType);
			tiles[w-1][yI] = new Tile(wallTileType);
		}
	}

	public Room(int w, int h, int x, int y, TileType floorTileType, TileType wallTileType, HashMap<Location, Entrance> attachments) {
		this(w, h, x, y, floorTileType, wallTileType);
		attachedLocs = attachments;
	}
	
	public void carveEntrancesWithCurrentAttachments(TileType floor) {
		carveEntrances(new ArrayList<Entrance>(attachedLocs.values()), floor);
	}
	public void carveEntrances(ArrayList<Entrance> entrances, TileType floorTileType) {
		for (Entrance entrance : entrances) {
			for (int s=0; s<entrance.getSize(); s++) {
				switch (entrance.getDirection()) {
				case NORTH:
					tiles[entrance.getLocation()+s][0] = new Tile(floorTileType);
					break;
				case SOUTH:
					tiles[entrance.getLocation()+s][height-1] = new Tile(floorTileType);
					break;
				case WEST:
					tiles[0][entrance.getLocation()+s] = new Tile(floorTileType);
					break;
				case EAST:
					tiles[width-1][entrance.getLocation()+s] = new Tile(floorTileType);
					break;
				}
			}
		}
	}
	public int getW() {
		return width;
	}
	public void setW(int w) {
		width = w;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	
	
	public int getH() {
		return height;
	}
	public void setH(int h) {
		height = h;
	}
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void moveCamera() {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		return new String("\tRoom:\n\t\tWidth: "+width+"\n\t\tHeight: "+height+"\n\t\tX position: "+x+"\n\t\tY position: "+y);
	}
}
