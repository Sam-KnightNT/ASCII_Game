package version_7;

public class ItemTile {
	
	private Item item;
	private byte x;
	private byte y;
	private byte z;
	
	public ItemTile(Item item, byte x, byte y, byte z) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public ItemTile(Item item, int x, int y, int z) {
		this(item, (byte) x, (byte) y, (byte) z);
	}
	
	public ItemTile(Item item, Coord3D c) {
		this(item, c.getX(), c.getY(), c.getZ());
	}
	
	public Item getItem() {
		return item;
	}
	
	public byte getX() {
		return x;
	}
	
	public byte getY() {
		return y;
	}

	public byte getZ() {
		return z;
	}
	
	public String getName() {
		return item.getName();
	}

	public void setX(byte x) {
		this.x = x;
	}

	public void setY(byte y) {
		this.y = y;
	}

	public void setZ(byte z) {
		this.z = z;
	}

	public String toString() {
		return "Item "+item+" at ("+x+", "+y+", "+z+")";
	}

	public Coord3D getLocation() {
		return new Coord3D(x, y, z);
	}
}
