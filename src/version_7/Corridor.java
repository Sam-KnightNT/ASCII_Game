package version_7;

import java.util.ArrayList;
import java.util.HashMap;

public class Corridor extends Location {

	@Override
	public void moveCamera() {
		//Centre on the player
		
	}
	
	public Corridor(byte w, byte h, byte x, byte y, TileType floorTileType, TileType wallTileType) {
		//This already has all its entrances defined.
		//Extend the corridor out along the entrances, meeting each other at a right angle, and drawing everything outside of these as the wallTileType.
		//HashMap contains the Direction of the entrance, the topmost/leftmost point and its length.
		width = w;
		height = h;
		corner = new Coord2D(x, y);
		this.x = x;
		this.y = y;
		tiles = new Tile[w][h];
		//Fill it all in with wall tiles
		for (int yI=0; yI<h; yI++) {
			for (int xI=0; xI<w; xI++) {
				tiles[xI][yI] = new Tile(wallTileType);
			}
		}		
	}
	public Corridor(byte w, byte h, byte x, byte y, TileType floorTileType, TileType wallTileType, HashMap<Location, Entrance> attachments) {
		//This already has all its entrances defined.
		//Extend the corridor out along the entrances, meeting each other at a right angle, and drawing everything outside of these as the wallTileType.
		//HashMap contains the Direction of the entrance, the topmost/leftmost point and its length.
		this(w, h, x, y, floorTileType, wallTileType);
		attachedLocs = attachments;
		this.extrude(new ArrayList<Entrance>(attachedLocs.values()), floorTileType);
	}

	public Corridor(int w, int h, int x, int y, TileType floorTileType, TileType wallTileType) {
		//This already has all its entrances defined.
		//Extend the corridor out along the entrances, meeting each other at a right angle, and drawing everything outside of these as the wallTileType.
		//HashMap contains the Direction of the entrance, the topmost/leftmost point and its length.
		width = (byte) w;
		height = (byte) h;
		corner = new Coord2D((byte) x, (byte) y);
		this.x = (byte) x;
		this.y = (byte) y;
		tiles = new Tile[w][h];
		//Fill it all in with wall tiles
		for (int yI=0; yI<h; yI++) {
			for (int xI=0; xI<w; xI++) {
				tiles[xI][yI] = new Tile(wallTileType);
			}
		}		
	}
	public Corridor(int w, int h, int x, int y, TileType floorTileType, TileType wallTileType, HashMap<Location, Entrance> attachments) {
		//This already has all its entrances defined.
		//Extend the corridor out along the entrances, meeting each other at a right angle, and drawing everything outside of these as the wallTileType.
		//HashMap contains the Direction of the entrance, the topmost/leftmost point and its length.		this(w, h, x, y, floorTileType, wallTileType);
		this(w, h, x, y, floorTileType, wallTileType);
		attachedLocs = attachments;
		this.extrude(new ArrayList<Entrance>(attachedLocs.values()), floorTileType);
	}
	
	public void extrudeWithCurrentAttachments(TileType floor) {
		this.extrude(new ArrayList<Entrance>(attachedLocs.values()), floor);
	}
	
	private void extrude(ArrayList<Entrance> entrances, TileType floor) {

		//Now go along the entrances, and progressively carve the paths out while there are entrances in the appropriate direction.
		boolean carving = true;
		//While there are still entrances to be carved
		while (carving) {
			//Set carving to be false, only reset it if one needs to be carved
			carving = false;
			//For each entrance
			for (Entrance entrance : entrances) {
				//And each other entrance
				
				//Go through the list of other entrances to find the farthest-away entrance (that isn't opposite). Then draw to there.
				int maxLength = 0;
				switch (entrance.getDirection()) {
				case NORTH:
					for (Entrance otherEntrance : entrances) {
						if (otherEntrance.getDirection() == Direction.WEST || otherEntrance.getDirection() == Direction.EAST) {
							int loc = otherEntrance.getCoords();
							int size = otherEntrance.getSize();
							if (loc+size>maxLength) {
								maxLength = loc+size;
							}
						}
					}
					break;
				case SOUTH:
					for (Entrance otherEntrance : entrances) {
						if (otherEntrance.getDirection() == Direction.WEST || otherEntrance.getDirection() == Direction.EAST) {
							int loc = otherEntrance.getCoords();
							if (loc>maxLength) {
								maxLength = loc;
							}
						}
					}
					break;
				case EAST:
					for (Entrance otherEntrance : entrances) {
						if (otherEntrance.getDirection() == Direction.SOUTH || otherEntrance.getDirection() == Direction.NORTH) {
							int loc = otherEntrance.getCoords();
							if (loc>maxLength) {
								maxLength = loc;
							}
						}
					}
					break;
				case WEST:
					for (Entrance otherEntrance : entrances) {
						if (otherEntrance.getDirection() == Direction.SOUTH || otherEntrance.getDirection() == Direction.NORTH) {
							int loc = otherEntrance.getCoords();
							int size = otherEntrance.getSize();
							if (loc+size>maxLength) {
								maxLength = loc+size;
							}
						}
					}
					break;
				}
				//Now, carve out a path that is that many units long.
				for (int l=0; l<maxLength; l++) {
					for (int r=0; r<entrance.getSize(); r++) {
						switch (entrance.getDirection()) {
						case NORTH:
							tiles[entrance.getCoords()+r][l] = new Tile(floor);
							break;
						case SOUTH:
							tiles[entrance.getCoords()+r][height-(l+1)] = new Tile(floor);
							break;
						case WEST:
							tiles[l][entrance.getCoords()+r] = new Tile(floor);
							break;
						case EAST:
							tiles[width-(l+1)][entrance.getCoords()+r] = new Tile(floor);
							break;
						}
					}
				}
			}
		}
	}
	
	public String toString() {
		return new String("\tCorridor:\n\t\tWidth: "+width+"\n\t\tHeight: "+height+"\n\t\tX position: "+x+"\n\t\tY position: "+y);
	}
}
