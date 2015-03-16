package version_7;

public class Entrance {

	private Direction direction;
	private int location;
	private int size;

	public Entrance() {
	}
	
	public Entrance(Direction direction, int location, int size) {
		this.direction = direction;
		this.location = location;
		this.size = size;
	}
	
	public Entrance(Triplet<Direction, Integer, Integer> details) {
		direction = details.getLeft();
		location = details.getMiddle();
		size = details.getRight();
	}

	public Direction getDirection() {
		return direction;
	}
	public int getLocation() {
		return location;
	}
	public int getSize() {
		return size;
	}
	public Triplet<Direction, Integer, Integer> getDetails() {
		return new Triplet<Direction, Integer, Integer>(direction, location, size);
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setDetails(Direction direction, int location, int size) {
		this.direction = direction;
		this.location = location;
		this.size = size;
	}
	public void setDetails(Triplet<Direction, Integer, Integer> details) {
		direction = details.getLeft();
		location = details.getMiddle();
		size = details.getRight();
	}

	
	public String toString() {
		return new String("\tEntrance:\n\t\tDirection: "+direction+"\n\t\tLocation: "+location+"\n\t\tSize: "+size);
	}
}
