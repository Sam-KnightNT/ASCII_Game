package version_7;

public class Coord4D extends Coord {
	
	private byte z;
	private byte v;
	
	public Coord4D(byte x, byte y, byte z, byte v) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.v = v;
	}
	
	public Coord4D(int x, int y, int z, int v) {
		this((byte) x, (byte) y, (byte) z, (byte) v);
	}
	
	public Coord4D(Coord3D c, int v) {
		this(c.getX(), c.getY(), c.getZ(), (byte) v);
	}
	
	public Coord4D(Coord2D c, int z, int v) {
		this(c.getX(), c.getY(), (byte) z, (byte) v);
	}

	public byte getZ() {
		return z;
	}
	public void setZ(byte z) {
		this.z = z;
	}
	
	public byte getV() {
		return v;
	}
	public void setV(byte v) {
		this.v = v;
	}

	public void setLocation(Coord4D crd) {
		x = crd.getX();
		y = crd.getY();
		z = crd.getZ();
		v = crd.getV();
	}
	
	public void setLocation(byte x, byte y, byte z, byte v) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.v = v;
	}
	
	public String toString() {
		return ("("+x+", "+y+", "+z+", "+v+")");
	}
	
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		} else if (!(obj instanceof Coord4D)) {
			return false;
		} else {
			Coord4D coords = (Coord4D) obj;
			return (x==coords.x && y==coords.y && z==coords.z && v==coords.v);
		}
	}

	public Coord add(Coord4D c) {
		x += c.x;
		y += c.y;
		z += c.z;
		v += c.v;
		return this;
	}
	public Coord add(Coord3D c) {
		return add(new Coord4D(c, 0));
	}
	public Coord add(Coord2D c) {
		return add(new Coord4D(c, 0, 0));
	}
	public Coord add(Coord c) {
		if (c instanceof Coord2D) {
			return add((Coord2D) c);
		} else if (c instanceof Coord3D) {
			return add((Coord3D) c);
		} else if (c instanceof Coord4D) {
			return add((Coord4D) c);
		} else {
			System.out.println("No add defined for "+c.getClass());
			return null;
		}
	}
	
	public double distanceTo(Coord2D c) {
		return distanceTo(new Coord4D(c, 0, 0));
	}
	public double distanceTo(Coord3D c) {
		return distanceTo(new Coord4D(c, 0));
	}
	public double distanceTo(Coord4D c) {
		int dx = c.x-x;
		int dy = c.y-y;
		int dz = c.z-z;
		int dv = c.v-v;
		return Math.sqrt(dx*dx + dy*dy + dz*dz + dv*dv);
	}
	
	public double distanceTo(Coord c) {
		return c.distanceTo(this);
	}
	
	public int toSingleVal() {
		return x + (y << 8) + (z << 16) + (v << 24);
	}
}