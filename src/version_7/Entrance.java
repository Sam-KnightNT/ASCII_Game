package version_7;

public class Entrance {

	private Direction direction;
	private int location;
	private int size;
	private Entrance linkedEntrance;
	private Location containedLocation;

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
	
	public Entrance(Direction direction, int location, int size, Entrance linkedEntrance) {
		this(direction, location, size);
		this.linkedEntrance = linkedEntrance;
	}
	
	public Entrance(Triplet<Direction, Integer, Integer> details, Entrance linkedEntrance) {
		this(details);
		this.linkedEntrance = linkedEntrance;
	}
	
	public Entrance(Direction direction, int location, int size, Entrance linkedEntrance, Location containedLocation) {
		this(direction, location, size, linkedEntrance);
		this.containedLocation = containedLocation;
	}
	
	public Entrance(Triplet<Direction, Integer, Integer> details, Entrance linkedEntrance, Location containedLocation) {
		this(details, linkedEntrance);
		this.containedLocation = containedLocation;
	}

	public Direction getDirection() {
		return direction;
	}
	public int getCoords() {
		return location;
	}
	public int getSize() {
		return size;
	}
	public Triplet<Direction, Integer, Integer> getDetails() {
		return new Triplet<Direction, Integer, Integer>(direction, location, size);
	}
	public Entrance getLinkedEntrance() {
		return linkedEntrance;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public void setCoords(int location) {
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
		return new String("Entrance:\n\tDirection: "+direction+"\n\tLocation: "+location+"\n\tSize: "+size);
	}
}
