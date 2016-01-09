package version_7;

public class Coord3D extends Coord {
	
	private byte z;
	
	public Coord3D(byte x, byte y, byte z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Coord3D(int x, int y, int z) {
		this((byte) x, (byte) y, (byte) z);
	}
	
	public Coord3D(Coord2D c, int z) {
		this(c.getX(), c.getY(), (byte) z);
	}
	
	public Coord3D(Coord3D c) {
		this(c.getX(), c.getY(), c.getZ());
	}
	
	public byte getZ() {
		return z;
	}
	
	public void setZ(byte z) {
		this.z = z;
	}
	
	public byte getV() {
		//This has no planar value, so return 0
		return 0;
	}
	
	public void setLocation(Coord3D crd) {
		x = crd.getX();
		y = crd.getY();
		z = crd.getZ();
	}
	
	public void setLocation(int x, int y, int z) {
		this.x = (byte) x;
		this.y = (byte) y;
		this.z = (byte) z;
	}
	
	public String toString() {
		return ("("+x+", "+y+", "+z+")");
	}
	
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		} else if (!(obj instanceof Coord3D)) {
			return false;
		} else {
			Coord3D coords = (Coord3D) obj;
			return (x==coords.x && y==coords.y && z==coords.z);
		}
	}

	private Coord3D add(Coord3D c) {
		x += c.x;
		y += c.y;
		z += c.z;
		return this;
	}
	
	public Coord3D add(Coord2D c) {
		return add(new Coord3D(c, 0));
	}
	
	public Coord3D add(Coord c) {
		if (c instanceof Coord2D) {
			return add((Coord2D) c);
		} else if (c instanceof Coord3D) {
			return add((Coord3D) c);
		} else {
			System.out.println("No add defined for "+c.getClass());
			return null;
		}
	}
	
	public double distanceTo(Coord2D c) {
		return distanceTo(new Coord3D(c, 0));
	}
	
	public double distanceTo(Coord3D c) {
		int dx = c.x-x;
		int dy = c.y-y;
		int dz = c.z-z;
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	@Override
	public double distanceTo(Coord c) {
		if (c instanceof Coord3D) {
			return distanceTo((Coord3D) c);
		} else if (c instanceof Coord2D) {
			return distanceTo((Coord2D) c);
		} else if (c instanceof Coord4D) {
			return ((Coord4D) c).distanceTo(this);
		} else {
			GameClass.print("distanceTo not defined for Coord3D and "+c.getClass()+", please bug the developer to fix this.");
			System.exit(1);
			return 0;
		}
	}

	@Override
	public int toSingleVal() {
		return x + (y << 8) + (z << 16);
	}

	public static Coord3D fromSingleVal(int val) {
		int x = val % 256;
		int y = ((val - x) >> 8) % 256;
		int z = (val - x - y) >> 16;
		return new Coord3D(x, y, z);
	}
	
	public static Coord3D c3sum(Coord3D c1, Coord3D c2) {
		return new Coord3D(c1.x+c2.x, c1.y+c2.y, c1.z+c2.z);
	}

	public Coord2D shift(Direction dir) {
		// TODO Auto-generated method stub
		return null;
	}
}