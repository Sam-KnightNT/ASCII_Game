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

	public String toString() {
		return "Item "+item+" at ("+x+", "+y+", "+z+")";
	}
}
