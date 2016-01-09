package version_7;
 
public class Coord2D extends Coord {
	
    public Coord2D(byte x, byte y) {
        this.x = x;
        this.y = y;
    }
    
    public Coord2D(int x, int y) {
        this.x = (byte) x;
        this.y = (byte) y;
    }
    
    public Coord2D(Coord c) {
    	this.x = c.x;
    	this.y = c.y;
    }

	public byte getZ() {
		//This has no z-levels, so return 0
		return 0;
	}
	
	public byte getV() {
		//This has no planar value, so return 0
		return 0;
	}
	
    public String toString() {
    	return "("+x+", "+y+")";
    }

    public double distanceTo(Coord2D c) {
    	int dx = c.x-x;
    	int dy = c.y-y;
    	return Math.sqrt(dx*dx + dy*dy);
    }
    
    public double distanceTo(Coord3D c) {
    	int dx = c.getX()-x;
    	int dy = c.getY()-y;
    	int dz = c.getZ();
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

	public void add(Coord2D c) {
		x += c.x;
		y += c.y;
	}
	
	@Override
	public Coord2D add(Coord c) {
		if (c instanceof Coord2D) {
			add((Coord2D) c);
		} else {
			System.out.println("Cannot add a coordinate to one with smaller dimensionality - reverse the order");
		}
		return this;
	}

	@Override
	public int toSingleVal() {
		return x + (y << 8);
	}

	public static Coord2D fromSingleVal(int val) {
		int x = val % 256;
		int y = (val - x) >> 8;
		return new Coord2D(x, y);
	}

	public Coord2D shift(Direction dir) {
		return Coord2D.fromSingleVal(this.toSingleVal() + dir.getNumVal());
	}
}