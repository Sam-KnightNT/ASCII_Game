package version_7;

import java.util.ArrayList;

public class TileSpace2D extends TileSpace {
	
	private Tile[][] tiles;
	
	//Contains the list of Tiles, along with the Entities and Items.
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Tile getTile(byte x, byte y) {
		return tiles[x][y];
	}
	//For byte values, modified by a direction.
	public Tile getTile(short xy) {
		byte x = (byte) (xy & 0xff);
		byte y = (byte) (xy >> 8);
		return tiles[x][y];
	}
	@Override
	public Tile getTile(int loc) throws ArrayIndexOutOfBoundsException {
		int x = loc % 256;
		int y = ((loc - x) >> 8) % 256;
		return tiles[x][y];
	}
	
	public void setTile(byte x, byte y, TileType tile) {
		try {
			tiles[x][y] = new Tile(tile);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println("Error: Tile at "+x+", "+y+" in tilespace "+this.getName()+" cannot be found.");
			//System.exit(1);
		}
	}
	public void setTile(int x, int y, TileType tile) {
		this.setTile((byte) x, (byte) y, tile);
	}
	public void setTile(byte x, byte y, TileType tile, ArrayList<String> restrictedEntities) {
		this.setTile(x, y, tile);
		tiles[x][y].setRestrictedEntities(restrictedEntities);
	}
	public void setTile(int x, int y, TileType tile, ArrayList<String> restrictedEntities) {
		this.setTile(x, y, tile);
		tiles[x][y].setRestrictedEntities(restrictedEntities);
	}
	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	@Override
	public void setTile(int loc, TileType tile, ArrayList<String> restrictedEntities) {
		setTile((byte) (loc % 256), (byte) (loc >> 8) % 256, tile, restrictedEntities);
	}
	
	public String toString() {
		return "2D tilespace, "+x+" by "+y;
	}
}
