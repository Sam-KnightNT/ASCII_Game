package version_7;
import java.util.ArrayList;


public class CellCoord2D implements Comparable<Object> {

	private int x;
	private int y;
	
	public CellCoord2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public CellCoord2D(float x, float y) {
		this((int) x, (int) y);
	}
	
	public CellCoord2D(double x, double y) {
		this((int) x, (int) y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void moveX(int dx) {
		this.x += dx;
	}

	public void moveY(int dy) {
		this.y += dy;
	}

	public CellCoord2D subtract(CellCoord2D c) {
		x-=c.x;
		y-=c.y;
		return this;
	}
	
	public CellCoord2D add(CellCoord2D c) {
		x+=c.x;
		y+=c.y;
		return this;
	}
	
	public static CellCoord2D sum(CellCoord2D a, CellCoord2D b) {
		return new CellCoord2D(a.x+b.x, a.y+b.y);
	}
	
	public static CellCoord2D difference(CellCoord2D a, CellCoord2D b) {
		return new CellCoord2D(a.x-b.x, a.y-b.y);
	}
	
	public CellCoord2D getFraction(double fraction) {
		return new CellCoord2D((int) (x*fraction), (int) (y*fraction));
	}
	
	public CellCoord2D shiftTo() {
		//If the x and y coords are equal, return signum(x), signum(y), i.e. the signs of x and y.
		//Otherwise, return signum(x), 0 if x>y, 0, signum(y) otherwise.
		return Math.abs(x)==Math.abs(y) ? new CellCoord2D(Math.signum(x), Math.signum(y)) :
					  Math.abs(x)>Math.abs(y) ? new CellCoord2D(Math.signum(x), 0) :
						    new CellCoord2D(0, Math.signum(y));
	}
	
	public boolean isZero() {
		return (x==0 && y==0);
	}
	
	public String toString() {
		return "("+x+", "+y+")";
	}

	public ArrayList<CellCoord2D> getOrthogonalNeighbours() {
		ArrayList<CellCoord2D> neighbours = new ArrayList<CellCoord2D>();
		neighbours.add(new CellCoord2D(x-1, y));
		neighbours.add(new CellCoord2D(x+1, y));
		neighbours.add(new CellCoord2D(x, y-1));
		neighbours.add(new CellCoord2D(x, y+1));
		return neighbours;
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof CellCoord2D)) {
			System.out.println("Wasn't instance of CellCoord2D");
			return Integer.MAX_VALUE;
		} else {
			CellCoord2D c = (CellCoord2D) o;
			System.out.println(c.x+", "+x+", "+(c.y << 16)+", "+(y << 16));
			return (c.x-x) + ((c.y << 16) - (y << 16));
		}
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof CellCoord2D)) {
			System.out.println("Wasn't instance of CellCoord2D");
			return false;
		} else {
			CellCoord2D c = (CellCoord2D) o;
			return c.x == x && c.y == y;
		}
	}
	
	public int hashCode() {
		return x + (y << 16);
	}
}
