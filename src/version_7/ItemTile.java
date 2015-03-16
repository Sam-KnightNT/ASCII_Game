package version_7;

public class ItemTile {
	
	private Item item;
	private int x;
	private int y;
	private int z;
	
	public ItemTile(Item item, int x, int y, int z) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Item getItem() {
		return item;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
	
	public String getName() {
		return item.getName();
	}

}
