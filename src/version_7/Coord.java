package version_7;

public abstract class Coord {
	
	protected byte x;
	protected byte y;
	
    public byte getX() {
        return x;
    }
    
    public void setX(byte x) {
        this.x = x;
    }
    
    public byte getY() {
        return y;
    }
    
    public void setY(byte y) {
        this.y = y;
    }
    
    public abstract byte getZ();
    public abstract byte getV();
    
    public abstract Coord add(Coord c);
	public abstract double distance(Coord c);
	public abstract int toSingleVal();
	public static double sumD(Coord c1, Coord c2) {
		return c1.toSingleVal() + c2.toSingleVal();
	}
}
