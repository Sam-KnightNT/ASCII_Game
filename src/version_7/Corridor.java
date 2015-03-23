package version_7;

import java.util.ArrayList;
import java.util.HashMap;

public class Corridor extends Location {

	@Override
	public void moveCamera() {
		//Centre on the player
		
	}
	
	public Corridor(byte w, byte h, byte x, byte y, TileType floor, TileType wall) {
		this.setW(w);
		this.setH(h);
		this.setX(x);
		this.setY(y);
		this.setCorner(x, y);
		setTiles(new Tile[w][h]);
		//Fill it all in with wall tiles
		for (int yI=0; yI<h; yI++) {
			for (int xI=0; xI<w; xI++) {
				setTile(xI, yI, wall);
			}
		}		
	}
	public Corridor(byte w, byte h, byte x, byte y, TileType floor, TileType wall, HashMap<Location, Entrance> attachments) {
		this(w, h, x, y, floor, wall);
		this.setAttachments(attachments);
		this.extrudeWithCurrentAttachments(floor);
	}

	public Corridor(int w, int h, int x, int y, TileType floor, TileType wall) {
		this((byte) w, (byte) h, (byte) x, (byte) y, floor, wall);
	}
	public Corridor(int w, int h, int x, int y, TileType floor, TileType wall, HashMap<Location, Entrance> attachments) {
		this(w, h, x, y, floor, wall);
		this.setAttachments(attachments);
		this.extrudeWithCurrentAttachments(floor);
	}
	
	public void extrudeWithCurrentAttachments(TileType floor) {
		this.extrude(new ArrayList<Entrance>(this.getAttached().values()), floor);
	}
	
	private void extrude(ArrayList<Entrance> entrances, TileType floor) {
		//Go along the entrances, and progressively carve the paths out while there are entrances in the appropriate direction.
		boolean carving = true;
		//While there are still entrances to be carved
		while (carving) {
			//Set carving to be false, only reset it if one needs to be carved
			carving = false;
			//For each entrance
			for (Entrance entrance : entrances) {
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
						int s = entrance.getCoords()+r;
						switch (entrance.getDirection()) {
						case NORTH:
							setTile(s, l, floor);
							break;
						case SOUTH:
							setTile(s, getH()-(l+1), floor);
							break;
						case WEST:
							setTile(l, s, floor);
							break;
						case EAST:
							setTile(getW()-(l+1), s, floor);
							break;
						}
					}
				}
			}
		}
	}

	public String toString() {
		return new String("Corridor:\n\tWidth: "+this.getW()+"\n\tHeight: "+this.getH()+"\n\tX position: "+this.getX()+"\n\tY position: "+this.getY());
	}
	
	//This method give an extra t tabs to the string - if, for example, you have a nested hierarchy in which this is an element. This method is used to convey it is within another structure.
	public String toString(int t) {
		String tabs = "";
		//Create the desired number of tabs.
		for (int i=0; i<t; i++) {
			tabs += "\t";
		}
		return new String("Corridor:\n"+tabs+"Width: "+this.getW()+"\n"+tabs+"\tHeight: "+this.getH()+"\n"+tabs+"\tX position: "+this.getX()+"\n"+tabs+"\tY position: "+this.getY());
	}
}
