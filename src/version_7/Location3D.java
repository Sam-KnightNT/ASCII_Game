package version_7;


public abstract class Location3D extends Location {
	
	//A Location with 3 dimensions. It contains a 3D array of Tiles,
	//a list of Entrances to smaller Locations, and possibly
	//a list of Entities and Tiles that are not contained within these.
	
	private TileSpace3D tilespace;
	private byte w;
	private byte h;
	private byte d;
	
	public TileSpace3D getTiles() {
		return tilespace;
	}
	public Tile getTile(int x, int y, int z) {
		return tilespace.getTile((byte) x, (byte) y, (byte) z);
	}
	public Tile getTile(int xyz) {
		return tilespace.getTile(xyz);
	}
	public void setTiles(TileSpace3D tilespace) {
		this.tilespace = tilespace;
	}
	public void setTile(int x, int y, int z, TileType tile) {
		tilespace.setTile((byte) x, (byte) y, (byte) z, tile);
	}
	
	public byte getW() {
		return w;
	}
	public void setW(byte w) {
		this.w = w;
	}
	public void setW(int w) {
		this.w = (byte) w;
	}
	
	public byte getH() {
		return h;
	}
	public void setH(byte h) {
		this.h = h;
	}
	public void setH(int h) {
		this.h = (byte) h;
	}
	
	public byte getD() {
		return d;
	}
	public void setD(byte d) {
		this.d = d;
	}
	public void setD(int d) {
		this.d = (byte) d;
	}

	public void fill(int x1, int y1, int z1, int x2, int y2, int z2, TileType tile) {
		fill(new Coord3D(x1, y1, z1), new Coord3D(x2, y2, z2), tile);
	}
	public void fill(Coord2D a, Coord2D b, TileType tile) {
		for (int x = a.x; x <= b.x; x++) {
			for (int y = a.y; y <= b.y; y++) {
				for (int z = a.getZ(); z <= b.getZ(); z++) {
					setTile(x, y, z, tile);
				}
			}
		}
	}
	
	//Draws a cuboid between specified points.
	public void draw(int x1, int y1, int z1, int x2, int y2, int z2, TileType tile) {
		draw(new Coord3D(x1, y1, z1), new Coord3D(x2, y2, z2), tile);
	}
	public void draw(Coord3D a, Coord3D b, TileType tile) {
		for (byte x = a.x; x <= b.x; x++) {
			for (byte y = a.y; y <= b.y; y++) {
				setTile(x, y, a.getZ(), tile);
				setTile(x, y, b.getZ(), tile);
			}
			for (byte z = a.getZ(); z <= b.getZ(); z++) {
				setTile(x, a.y, z, tile);
				setTile(x, b.y, z, tile);
			}
		}
		for (byte y = a.y; y <= b.y; y++) {
			for (byte z = a.getZ(); z <= b.getZ(); z++) {
				setTile(a.x, y, z, tile);
				setTile(b.x, y, z, tile);
			}
		}
	}
	
	//Draws a wireframe cuboid around specified points.
	public void frame(Coord3D a, Coord3D b, TileType tile) {
		//TODO - not done
	}
}