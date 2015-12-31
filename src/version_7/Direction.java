package version_7;

public enum Direction {
	//location is 4 Bytes or an Int - xxxxxxxx, yyyyyyyy, zzzzzzzz, dddddddd. Each one is 0 to 127 in value, 0 is the corner.
	//Any locations with more than 128 x, y, z or d values should overflow, so it's quite likely this is the thing that'll result in Shenanigans.
	//North should decrement y by 1. South should increment y by 1.
	//East should increment x by 1. West should decrement x by 1.
	//Up should increment z by 1. Down should decrement z by 1.
	NORTH(-256), SOUTH(256), EAST(1), WEST(-1), UP(65536), DOWN(-65536), OUT(65536*256), IN(-65536*256);
	
	private Direction opposite;
	private boolean isVertical;
	private int numVal;

    static {
        NORTH.opposite = SOUTH;
        SOUTH.opposite = NORTH;
        EAST.opposite = WEST;
        WEST.opposite = EAST;
    }
    
    static {
    	NORTH.isVertical = true;
    	SOUTH.isVertical = true;
    	EAST.isVertical = false;
    	WEST.isVertical = false;
    }

    public Direction getOppositeDirection() {
        return opposite;
    }
    
    public boolean verticality() {
    	return isVertical;
    }
    
    public boolean horizontality() {
    	return !isVertical;
    }
    
    public char getDirectionality() {
    	return isVertical ? 'Y' : 'X';
    }
    public char getOppositeDirectionality() {
    	return isVertical ? 'X' : 'Y';
    }
    
    Direction(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }

}