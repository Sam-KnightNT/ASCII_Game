package version_7;

import java.awt.geom.Point2D;

public class Coord3D {

	Point2D point;
	private double x;
	private double y;
	private double z;
	
	public Coord3D() {
		
	}
	
	public double distance(Coord3D crd) {
		return Math.sqrt(distanceSq(crd));
	}
	
	public double distance(double px, double py, double pz) {
		return Math.sqrt(distanceSq(px, py, pz));
	}
	
	public double distanceSq(Coord3D crd) {
		double dx = crd.getX()-x;
		double dy = crd.getY()-y;
		double dz = crd.getZ()-z;
		return (dx*dx)+(dy*dy)+(dz*dz);
	}
	
	public double distanceSq(double px, double py, double pz) {
		double dx = px-x;
		double dy = py-y;
		double dz = pz-z;
		return (dx*dx)+(dy*dy)+(dz*dz);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setLocation(Coord3D crd) {
		x = crd.getX();
		y = crd.getY();
		z = crd.getZ();
	}
	
	public void setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
