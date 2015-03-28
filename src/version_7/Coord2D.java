package version_7;
 
public class Coord2D {
 
    private byte x;
    private byte y;
    
    public Coord2D(byte x, byte y) {
        this.x = x;
        this.y = y;
    }
    
    public Coord2D(int x, int y) {
        this.x = (byte) x;
        this.y = (byte) y;
    }
 
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
    
    public String toString() {
    	return "("+x+", "+y+")";
    }
     
}