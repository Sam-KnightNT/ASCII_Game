package version_7;

public class Entrance {

	private Direction direction;
	private Coord2D locationA;
	private Coord2D locationB;
	private Entrance linkedEntrance;
	private Location containedLocation;

	public Entrance() {
	}
	
	public Entrance(Direction direction, byte location, byte size) {
		this.direction = direction;
		this.locationInRoom = location;
		this.size = size;
	}
	
	public Entrance(Triplet<Direction, Byte, Byte> info) {
		direction = info.getLeft();
		locationInRoom = info.getMiddle();
		size = info.getRight();
	}
	
	public Entrance(Direction direction, byte location, byte size, Entrance linkedEntrance) {
		this(direction, location, size);
		this.linkedEntrance = linkedEntrance;
	}
	
	public Entrance(Triplet<Direction, Byte, Byte> info, Entrance linkedEntrance) {
		this(info);
		this.linkedEntrance = linkedEntrance;
	}
	
	public Entrance(Direction direction, byte location, byte size, Entrance linkedEntrance, Location containedLocation) {
		this(direction, location, size, linkedEntrance);
		this.containedLocation = containedLocation;
	}
	
	public Entrance(Triplet<Direction, Byte, Byte> info, Entrance linkedEntrance, Location containedLocation) {
		this(info, linkedEntrance);
		this.containedLocation = containedLocation;
	}

	public Direction getDirection() {
		return direction;
	}
	public byte getCoords() {
		return locationInRoom;
	}
	public Location getLocation() {
		return containedLocation;
	}
	public int getSize() {
		return size;
	}
	public Triplet<Direction, Byte, Byte> getDetails() {
		return new Triplet<Direction, Byte, Byte>(direction, locationInRoom, size);
	}
	public Entrance getLinkedEntrance() {
		return linkedEntrance;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public void setCoords(byte coords) {
		this.locationInRoom = coords;
	}
	public void setLocation(Location location) {
		containedLocation = location;
	}
	public void setSize(byte size) {
		this.size = size;
	}
	public void setInfo(Direction direction, byte location, byte size) {
		this.direction = direction;
		this.locationInRoom = location;
		this.size = size;
	}
	public void setInfo(Triplet<Direction, Byte, Byte> info) {
		direction = info.getLeft();
		locationInRoom = info.getMiddle();
		size = info.getRight();
	}

	
	public String toString() {
		return new String("Entrance:\n\tDirection: "+direction+"\n\tLocation: "+locationInRoom+"\n\tSize: "+size);
	}

	public void setLinkedEntrance(Entrance linkedEntrance) {
		this.linkedEntrance = linkedEntrance;
	}
}
