package version_7;

public class Coord3D {

	private byte x;
	private byte y;
	private byte z;
	
	public Coord3D(byte x, byte y, byte z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double distance(Coord3D crd) {
		return Math.sqrt(distanceSq(crd));
	}
	
	public double distance(byte px, byte py, byte pz) {
		return Math.sqrt(distanceSq(px, py, pz));
	}
	
	public double distanceSq(Coord3D crd) {
		byte dx = (byte) (crd.getX()-x);
		byte dy = (byte) (crd.getY()-y);
		byte dz = (byte) (crd.getZ()-z);
		return (dx*dx)+(dy*dy)+(dz*dz);
	}
	
	public double distanceSq(byte px, byte py, byte pz) {
		byte dx = (byte) (px-x);
		byte dy = (byte) (py-y);
		byte dz = (byte) (pz-z);
		return (dx*dx)+(dy*dy)+(dz*dz);
	}
	
	public byte getX() {
		return x;
	}
	
	public byte getY() {
		return y;
	}
	
	public byte getZ() {
		return z;
	}
	
	public void setLocation(Coord3D crd) {
		x = crd.getX();
		y = crd.getY();
		z = crd.getZ();
	}
	
	public void setLocation(byte x, byte y, byte z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
