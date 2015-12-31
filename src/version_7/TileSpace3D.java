package version_7;

import java.util.ArrayList;

public abstract class TileSpace3D extends TileSpace {

	private Tile[][][] tiles;
	
	//Contains the list of Tiles, along with the Entities and Items.
	
	public Tile[][][] getTiles() {
		return tiles;
	}
	
	public Tile getTile(byte x, byte y, byte z) {
		return tiles[x][y][z];
	}
	//For byte values, modified by a direction.
	public Tile getTile(int xyz) {
		byte x = (byte) (xyz & 0xff);
		byte y = (byte) (xyz >> 8);
		byte z = (byte) (xyz >> 16);
		return tiles[x][y][z];
	}
	
	public void setTile(byte x, byte y, byte z, TileType tile) {
		this.setTile(x, y, z, tile, new ArrayList<String>());
	}
	public void setTile(int x, int y, int z, TileType tile) {
		this.setTile((byte) x, (byte) y, (byte) z, tile);
	}
	public void setTile(byte x, byte y, byte z, TileType tile, ArrayList<String> restrictedEntities) {
		try {
			tiles[x][y][z] = new Tile(tile);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println("Error: Tile at "+x+", "+y+" in tilespace "+this.getName()+" cannot be found.");
			//System.exit(1);
		}
		tiles[x][y][z].setRestrictedEntities(restrictedEntities);
	}
	public void setTile(int x, int y, int z, TileType tile, ArrayList<String> restrictedEntities) {
		this.setTile(x, y, z, tile, restrictedEntities);
	}
	
	public void setTiles(Tile[][][] tiles) {
		this.tiles = tiles;
	}
	@Override
	public void setTile(int loc, TileType tile, ArrayList<String> restrictedEntities) {
		setTile((byte) (loc % 256), (byte) (loc >> 8) % 256, (byte) (loc >> 16) % 256, tile, restrictedEntities);
	}
	
	public String toString() {
		return "2D tilespace, "+x+" by "+y;
	}
}