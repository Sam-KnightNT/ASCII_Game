package version_7;

public class Entrance {

	private Direction direction;
	private Coord2D locationA;
	private Coord2D locationB;
	private Entrance linkedEntrance;
	private Location containedLocation;

	public Entrance() {
	}
	
	public Entrance(Direction direction, Coord2D locationA, Coord2D locationB) {
		this.direction = direction;
		this.locationA = locationA;
		this.locationB = locationB;
	}
	
	public Entrance(Triplet<Direction, Coord2D, Coord2D> info) {
		direction = info.getLeft();
		locationA = info.getMiddle();
		locationB = info.getRight();
	}
	
	public Entrance(Direction direction, int location, int size, Location containedLocation) {
		this.direction = direction;
		this.containedLocation = containedLocation;
		switch (direction) {
		case NORTH:
			locationA = new Coord2D(containedLocation.getH()-1, location);
			locationB = new Coord2D(containedLocation.getH()-1, location+size);
		case SOUTH:
			locationA = new Coord2D(0, location);
			locationB = new Coord2D(0, location+size);
		case WEST:
			locationA = new Coord2D(location, containedLocation.getW()-1);
			locationB = new Coord2D(location+size, containedLocation.getW()-1);
		case EAST:
			locationA = new Coord2D(location, 0);
			locationB = new Coord2D(location+size, 0);
		}
	}
	public Entrance(Direction direction, Coord2D locationA, Coord2D locationB, Entrance linkedEntrance) {
		this(direction, locationA, locationB);
		this.linkedEntrance = linkedEntrance;
	}
	
	public Entrance(Triplet<Direction, Coord2D, Coord2D> info, Entrance linkedEntrance) {
		this(info);
		this.linkedEntrance = linkedEntrance;
	}
	
	public Entrance(Direction direction, Coord2D locationA, Coord2D locationB, Entrance linkedEntrance, Location containedLocation) {
		this(direction, locationA, locationB, linkedEntrance);
		this.containedLocation = containedLocation;
	}
	
	public Entrance(Triplet<Direction, Coord2D, Coord2D> info, Entrance linkedEntrance, Location containedLocation) {
		this(info, linkedEntrance);
		this.containedLocation = containedLocation;
	}

	public Direction getDirection() {
		return direction;
	}
	public Location getLocation() {
		return containedLocation;
	}
	public Coord2D getLocA() {
		return locationA;
	}
	public Coord2D getLocB() {
		return locationB;
	}
	public Triplet<Direction, Coord2D, Coord2D> getDetails() {
		return new Triplet<Direction, Coord2D, Coord2D>(direction, locationA, locationB);
	}
	public Entrance getLinkedEntrance() {
		return linkedEntrance;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public void setLocation(Location location) {
		containedLocation = location;
	}
	public void setLocA(Coord2D coords) {
		this.locationA = coords;
	}
	public void setLocB(Coord2D coords) {
		this.locationB = coords;
	}
	public void setInfo(Direction direction, Coord2D locationA, Coord2D locationB) {
		this.direction = direction;
		this.locationA = locationA;
		this.locationB = locationB;
	}
	public void setInfo(Triplet<Direction, Coord2D, Coord2D> info) {
		direction = info.getLeft();
		locationA = info.getMiddle();
		locationB = info.getRight();
	}

	
	public String toString() {
		return new String("Entrance:\n\tDirection: "+direction+"\n\tLocation A: "+locationA+"\n\tLocation B: "+locationB);
	}

	public void setLinkedEntrance(Entrance linkedEntrance) {
		this.linkedEntrance = linkedEntrance;
	}
}
