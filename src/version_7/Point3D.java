package version_7;

import java.awt.geom.Point2D;

public class Point3D {

	Point2D point;
	private int x;
	private int y;
	private int z;
	
	public Point3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double distance(Point3D pt) {
		return Math.sqrt(distanceSq(pt));
	}
	
	public double distance(int px, int py, int pz) {
		return Math.sqrt(distanceSq(px, py, pz));
	}
	
	public int distanceSq(Point3D pt) {
		int dx = pt.getX()-x;
		int dy = pt.getY()-y;
		int dz = pt.getZ()-z;
		return (dx*dx)+(dy*dy)+(dz*dz);
	}
	
	public int distanceSq(int px, int py, int pz) {
		int dx = px-x;
		int dy = py-y;
		int dz = pz-z;
		return (dx*dx)+(dy*dy)+(dz*dz);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void setLocation(Point3D pt) {
		x = pt.getX();
		y = pt.getY();
		z = pt.getZ();
	}
	
	public void setLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
