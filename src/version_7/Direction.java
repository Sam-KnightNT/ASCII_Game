package version_7;

public enum Direction {
	//location is 2 Bytes or a Short - xxxxxxxx, yyyyyyyy. Each one is 0 to 127 in value, 0 is the corner.
	//Any locations with more than 128 x or y values should overflow, so it's quite likely this is the thing that'll result in Shenanigans.
	//North should increment x by 1. South should decrement x by 1. East should increment the total thing by 0x100. West should decrement the thing by 0x100.
	NORTH((short) 1), SOUTH((short) -1), EAST((short) 127), WEST((short) -127);
	
	private Direction opposite;
	private short numVal;

    static {
        NORTH.opposite = SOUTH;
        SOUTH.opposite = NORTH;
        EAST.opposite = WEST;
        WEST.opposite = EAST;
    }

    public Direction getOppositeDirection() {
        return opposite;
    }
    
    Direction(short numVal) {
        this.numVal = numVal;
    }

    public short getNumVal() {
        return numVal;
    }

}