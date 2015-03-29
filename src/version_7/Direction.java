package version_7;

public enum Direction {
	//location is 2 Bytes or a Short - xxxxxxxx, yyyyyyyy. Each one is 0 to 127 in value, 0 is the corner.
	//Any locations with more than 128 x or y values should overflow, so it's quite likely this is the thing that'll result in Shenanigans.
	//North should increment x by 1. South should decrement x by 1. East should increment the total thing by 0x100. West should decrement the thing by 0x100.
	NORTH((short) -256), SOUTH((short) 256), EAST((short) 1), WEST((short) -1);
	
	private Direction opposite;
	private char directionality;
	private short numVal;

    static {
        NORTH.opposite = SOUTH;
        SOUTH.opposite = NORTH;
        EAST.opposite = WEST;
        WEST.opposite = EAST;
    }
    
    static {
    	NORTH.directionality = 'Y';
    	SOUTH.directionality = 'Y';
    	EAST.directionality = 'X';
    	WEST.directionality = 'X';
    }

    public Direction getOppositeDirection() {
        return opposite;
    }
    
    public char getDirectionality() {
    	return directionality;
    }
    
    Direction(short numVal) {
        this.numVal = numVal;
    }

    public short getNumVal() {
        return numVal;
    }

}