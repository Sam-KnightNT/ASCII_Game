package version_7;

import java.util.ArrayList;

public abstract class TileSpace {
		
	public String name;
	public int x;
	public int y;
	public int z;
	public int p; //Plane
	private int w; //Width (east-west)
	private int b; //Breadth (north-south)
	private int h; //Height (top-bottom)
	private int d; //Dimension
	public abstract Tile getTile(int loc);
	
	public abstract void setTile(int loc, TileType tile, ArrayList<String> permittedEntities);
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	
	public int getP() {
		return p;
	}
	public void setP(int p) {
		this.p = p;
	}
	
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	
	public int getD() {
		return d;
	}
	public void setD(int d) {
		this.d = d;
	}
}
