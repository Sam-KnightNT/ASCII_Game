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

    public double distance(Coord2D c) {
    	int dx = c.x-x;
    	int dy = c.y-y;
    	return Math.sqrt(dx*dx + dy*dy);
    }
    
    public double distance(Coord3D c) {
    	int dx = c.getX()-x;
    	int dy = c.getY()-y;
    	int dz = c.getZ();
    	return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
	@Override
	public double distance(Coord c) {
		return c.distance(this);
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

	public static int singleVal(byte x, byte y) {
		return x + (y << 8);
	}

	public static int singleVal(int x, int y) {
		return singleVal((byte) x, (byte) y);
	}
     
}