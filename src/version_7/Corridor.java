package version_7;

import java.util.HashMap;
import java.util.Map.Entry;

public class Corridor extends Location {

	@Override
	public void moveCamera() {
		//Centre on the player
		
	}

	public Corridor(int w, int h, int x, int y, TileType floorTileType, TileType wallTileType, HashMap<Direction, IntPair> entrances) {
		//Extend the corridor out along the entrances, meeting each other at a right angle, and drawing everything outside of these as the wallTileType.
		//HashMap contains the Direction of the entrance, the topmost/leftmost point and its length.
		width = w;
		height = h;
		corner = new Coord2D(x, y);
		tiles = new Tile[w][h];
		//Fill it all in with wall tiles
		for (int yI=0; yI<h; yI++) {
			for (int xI=0; xI<w; xI++) {
				tiles[xI][yI] = new Tile(wallTileType);
			}
		}
		
		//Now go along the entrances, and progressively carve the paths out while there are entrances in the appropriate direction.
		boolean carving = true;
		int carvingLength = 0;
		//While there are still entrances to be carved
		while (carving) {
			//Set carving to be false, only reset it if one needs to be carved
			carving = false;
			//For each entrance
			for (Entry<Direction, IntPair> entrance : entrances.entrySet()) {
				//And each other entrance
				
				//Go through the list of other entrances to find the furthest-away entrance (that isn't opposite). Then draw to there.
				int maxLength = 0;
				switch (entrance.getKey()) {
				case NORTH:
					for (Entry<Direction, IntPair> otherEntrance : entrances.entrySet()) {
						if (otherEntrance.getKey() == Direction.WEST || otherEntrance.getKey() == Direction.EAST) {
							IntPair stats = otherEntrance.getValue();
							if (stats.getLeft()+stats.getRight()>maxLength) {
								maxLength = stats.getLeft()+stats.getRight();
							}
						}
					}
				case SOUTH:
					for (Entry<Direction, IntPair> otherEntrance : entrances.entrySet()) {
						if (otherEntrance.getKey() == Direction.WEST || otherEntrance.getKey() == Direction.EAST) {
							int stats = otherEntrance.getValue().getLeft();
							if (stats > maxLength) {
								maxLength = stats;
							}
						}
					}
				case EAST:
					for (Entry<Direction, IntPair> otherEntrance : entrances.entrySet()) {
						if (otherEntrance.getKey() == Direction.SOUTH || otherEntrance.getKey() == Direction.NORTH) {
							int stats = otherEntrance.getValue().getLeft();
							if (stats > maxLength) {
								maxLength = stats;
							}
						}
					}
				case WEST:
					for (Entry<Direction, IntPair> otherEntrance : entrances.entrySet()) {
						if (otherEntrance.getKey() == Direction.SOUTH || otherEntrance.getKey() == Direction.NORTH) {
							IntPair stats = otherEntrance.getValue();
							if (stats.getLeft()+stats.getRight()>maxLength) {
								maxLength = stats.getLeft()+stats.getRight();
							}
						}
					}
				}
				//Now, carve out a path that is that many units long.
				for (int l=0; l<maxLength; l++) {
					for (int r=0; r<entrance.getValue().getRight(); r++) {
						if (entrance.getKey() == Direction.NORTH) {
							tiles[entrance.getValue().getLeft()+r][l] = new Tile(floorTileType);
						} else if (entrance.getKey() == Direction.SOUTH) {
							System.out.println(l);
							tiles[entrance.getValue().getLeft()+r][height-(l+1)] = new Tile(floorTileType);
						} else if (entrance.getKey() == Direction.WEST) {
							tiles[l][entrance.getValue().getLeft()+r] = new Tile(floorTileType);
						} else if (entrance.getKey() == Direction.SOUTH) {
							tiles[width-(l+1)][entrance.getValue().getLeft()+r] = new Tile(floorTileType);
						}
					}
					
					/* These switch statements check for the following.
					 * 
					 * E's are the current "entrance" variable and its carved path.
					 * X's are the otherEntrance variable's entrance.
					 * .'s are uncarved paths.
					 *  
					 * .............
					 * E............
					 * E............
					 * .............
					 * .......XXXX..
					 * returns 10, as there are 10 steps before the E carved path gets to the rightmost part of the X's.
					 * 
					 * ......EEEEE...
					 * ..............
					 * XXX...........
					 * XXX...........
					 * ..............
					 * returns 3.
					 * 
					 * ..............
					 * X.............
					 * X.............
					 * X.............
					 * ..............
					 * ..............
					 * X.............
					 * X.............
					 * X.............
					 * .....EE.......
					 * returns 8, as it looks for the furthest one.
					 */
				}
			}
		}
	}
	//This must contain each block that is within it, relative to the bottom-left corner.
	
}
