package version_7;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Location2D extends Location {


	//A Location with 2 dimensions. It contains a 2D array of Tiles and
	//a list of Entities and Items.
	
	private TileSpace2D tilespace;
	private byte w;
	private byte h;
	
	public TileSpace2D getTiles() {
		return tilespace;
	}
	public Tile getTile(int x, int y) {
		return tilespace.getTile((byte) x, (byte) y);
	}
	public Tile getTile(int xy) {
		return tilespace.getTile(xy);
	}
	public void setTiles(TileSpace2D tilespace) {
		this.tilespace = tilespace;
	}
	public void setTile(int x, int y, TileType tile) {
		tilespace.setTile((byte) x, (byte) y, tile);
	}
	public void setTile(int x, int y, TileType tile, ArrayList<String> permittedEntities) {
		tilespace.setTile((byte) x, (byte) y, tile, permittedEntities);
	}
	public void setTile(int x, int y, TileType tileType, boolean isTraversable) {
		tilespace.setTile((byte) x, (byte) y, tileType, (isTraversable ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList("All"))));
	}
	
	public byte getW() {
		return w;
	}
	public void setW(byte w) {
		this.w = w;
	}
	
	public byte getH() {
		return h;
	}
	public void setH(byte h) {
		this.h = h;
	}
	
	public byte getD() {
		return (byte) 1;
	}
	
	public byte getP() {
		return (byte) 1;
	}

	public void fill(int x1, int y1, int x2, int y2, TileType tile) {
		fill(new Coord2D(x1, y1), new Coord2D(x2, y2), tile);
	}
	public void fill(Coord a, Coord b, TileType tile) {
		if (!(a instanceof Coord2D && b instanceof Coord2D)) {
			System.out.println("Warning: Tried to fill 2D location "+this.toString()+" with non-2D coordinates. The 3rd and beyond coordinates will be ignored.");
			fill(a.x, a.y, b.x, b.y, tile);
		} else {
			fill((Coord2D) a, (Coord2D) b, tile);
		}
	}
	public void fill(Coord2D a, Coord2D b, TileType tile) {
		for (int x = a.x; x <= b.x; x++) {
			for (int y = a.y; y <= b.y; y++) {
				setTile(x, y, tile);
			}
		}
	}
	
	public void draw(Coord2D a, Coord2D b, TileType tile) {
		for (int x = a.x; x <= b.x; x++) {
			setTile(x, a.y, tile);
			setTile(x, b.y, tile);
		}
		for (int y = a.y; y <= b.y; y++) {
			setTile(a.x, y, tile);
			setTile(b.x, y, tile);
		}
	}
	public void draw(int x1, int y1, int x2, int y2, TileType tile) {
		draw(new Coord2D(x1, y1), new Coord2D(x2, y2), tile);
	}
}