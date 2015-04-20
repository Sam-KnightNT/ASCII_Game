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
							//Northern entrances want the southernmost point. So, go for the second location's y value.
							int loc = otherEntrance.getLocB().getY();
							if (loc>maxLength) {
								maxLength = loc;
							}
						}
					}
					break;
				case SOUTH:
					byte lowPoint = this.getH();
					for (Entrance otherEntrance : entrances) {
						if (otherEntrance.getDirection() == Direction.WEST || otherEntrance.getDirection() == Direction.EAST) {
							//Want northernmost point
							int loc = otherEntrance.getLocA().getY();
							if (lowPoint-loc>maxLength) {
								maxLength = lowPoint-loc;
							}
						}
					}
					break;
				case EAST:
					byte sidePoint = this.getW();
					for (Entrance otherEntrance : entrances) {
						if (otherEntrance.getDirection() == Direction.SOUTH || otherEntrance.getDirection() == Direction.NORTH) {
							int loc = otherEntrance.getLocA().getX();
							if (sidePoint-loc>maxLength) {
								maxLength = sidePoint-loc;
							}
						}
					}
					break;
				case WEST:
					for (Entrance otherEntrance : entrances) {
						if (otherEntrance.getDirection() == Direction.SOUTH || otherEntrance.getDirection() == Direction.NORTH) {
							int loc = otherEntrance.getLocB().getX();
							if (loc>maxLength) {
								maxLength = loc;
							}
						}
					}
					break;
				}
				//Now, carve out a path that is that many units long.
				for (int l=0; l<maxLength; l++) {
					for (int r=entrance.getNearSide(); r<entrance.getFarSide(); r++) {
						switch (entrance.getDirection()) {
						case NORTH:
							setTile(r, l, floor);
							break;
						case SOUTH:
							setTile(r, getH()-(l+1), floor);
							break;
						case WEST:
							setTile(l, r, floor);
							break;
						case EAST:
							setTile(getW()-(l+1), r, floor);
							break;
						}
					}
				}
			}
		}
	}

	public String toString() {
		return "Corridor: "+getName()+", size ("+getW()+", "+getH()+"), location ("+getX()+", "+getY()+")";
	}
	
	public String stringEntrances() {
		String str = "";
		for (Entrance entrance : getAttached().values()) {
			str += entrance+"\n";
		}
		return str;
	}
}
