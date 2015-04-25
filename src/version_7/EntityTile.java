package version_7;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

public class EntityTile implements Comparable<EntityTile> {

	private Entity entity;
	private byte x;
	private byte y;
	private byte z;
	private Coord3D coords;
	private HashMap<String, Integer> stats = new HashMap<String, Integer>();
	private ArrayList<Item> inventory = new ArrayList<Item>();
	private int randomness = 0;
	private Random ran = new Random();
	private int ticksLeft;
	private int id;
	private Location location;
	private Path path;
	private Entrance entrance;
 
	public EntityTile(Entity entity, byte x, byte y, byte z) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
		this.coords = new Coord3D(x, y, z);
		this.path = new Path();
		genStats();
	}
	
	public EntityTile(Entity entity, Location loc, byte x, byte y, byte z) {
		this(entity, x, y, z);
		this.location = loc;
		loc.addEntity(this);
		genStats();
	}
	
	public EntityTile(Entity entity, Location loc, byte x, byte y, byte z, int randomness, int id) {
		this(entity, loc, x, y, z);
		this.randomness = randomness;
		this.id = id;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public byte getX() {
		return x;
	}
	
	public byte getY() {
		return y;
	}

	public byte getXY() {
		return (byte) (x + (y << 8));
	}
	
	public byte getZ() {
		return z;
	}
	
	public Coord3D getCoords() {
		return coords;
	}
	
	public void setX(byte x) {
		this.x = x;
		this.coords.setX(x);
	}
	
	public void setY(byte y) {
		this.y = y;
		this.coords.setY(y);
	}
	
	public void setZ(byte z) {
		this.z = z;
		this.coords.setZ(z);
	}
	
	public void setCoords(Coord3D coords) {
		this.x = coords.getX();
		this.y = coords.getY();
		this.z = coords.getZ();
		this.coords = coords;
	}

	public void setCoords(byte x, byte y, byte z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.coords = new Coord3D(x, y, z);
	}

	public void setCoords(short xy, byte z) {
		this.setCoords((byte) (xy & 0xff), (byte) (xy >> 8), z); 
	}
	
	public int getStrength() {
		if (stats.containsKey("strength")) {
			return stats.get("strength");
		}
		return 0;
	}
	
	public int getHealth() {
		if (stats.containsKey("health")) {
			return stats.get("health");
		}
		return 0;
	}
	
	public String getName() {
		return entity.getName();
	}
	
	public int getStat(String stat) {
		if (stats.containsKey(stat)) {
			return stats.get(stat);
		} else {
			return -1;
		}
	}
	public HashMap<String, Integer> getAllStats() {
		return stats;
	}
	public boolean hasStat(String stat) {
		return entity.hasStat(stat);
	}
	
	public void setStat(String stat, int amount) {
		stats.put(stat, amount);
	}
	public void genStats() {
		if (entity.getBaseStats()!=null) {
			for (Entry<String, Integer> entry : entity.getBaseStats().entrySet()) {
				String statName = entry.getKey();
				int statNum = entry.getValue();
				if (randomness>0) {
					statNum+=ran.nextInt(randomness)-(randomness/2);
				}
				this.setStat(statName, statNum);
			}
			if (!this.hasStat("speed")) {
				System.out.println("Something went very wrong here, the entity "+entity.getName()+" does not have a speed value.\n" +
						"Even if one isn't defined, it should default to 100. Send me a bug report and your file\n" +
						"and I'll see what I can do. Also, prepare for crashing soon.");
			} else {
				ticksLeft = (int) (100*(100/(float) this.getStat("speed")));
			}
		}
	}
	public void printStats() {
		System.out.println("	"+entity.getName());
		for (Entry<String, Integer> entry : stats.entrySet()) {
			System.out.println("Stat "+entry.getKey()+" has value "+entry.getValue());
		}
	}

	public void addToInventory(Item item) {
		inventory.add(item);
	}
	public ArrayList<Item> getInventory() {
		return inventory;
	}
	
	public void printInventory() {
		if (this.getInventory().size()>0) {
			GameClass.print("Your inventory contains:");
			for (Item item : inventory) {
				GameClass.print("\t"+item.getName()+": "+item.getDescription());
			}
		} else {
			GameClass.print("Your inventory is empty.");
		}
	}
	
	public void setTicks(int tick) {
		ticksLeft = tick;
	}
	public int getTicks() {
		return ticksLeft;
	}
	public void resetTicks() {
		ticksLeft = (int) (100*(100/(float) this.getStat("speed")));
	}
	
	public void pickupItem(Item item) {
		System.out.println("Picked up unequippable item");
		this.addToInventory(item);
		System.out.println(entity.getName()+" picked up "+item.getName());
	}
	
	public void pickupItem(ItemEquippable item) {
		System.out.println("Picked up equippable item");
		this.addToInventory(item);
		System.out.println(entity.getName()+" picked up "+item.getName());
		for (Entry<String, Integer> stat:item.getAllStats().entrySet()) {
			String name = stat.getKey();
			int val = stat.getValue();
			System.out.println("Attempting to increase stat "+name);
			if (this.stats.containsKey(name)) {
				int base = this.stats.get(name);
				System.out.println("Stat found, base is "+base+", incrementing by "+val);
				this.stats.put(name, base+val);
				System.out.println("New stat is "+(base+val));
			}
			else {
				System.out.println("Stat not found, creating new stat: "+name);
				this.stats.put(name, val);
			}
		}
	}

	public int getID() {
		return id;
	}
	
	@Override
	public int compareTo(EntityTile e) {
		if (this.equals(e)) {
			return 0;
		} else if (this.ticksLeft==e.ticksLeft) {
			//TODO: Change this to ID later on, once you implement global entity saving 
			return this.getName().compareTo(e.getName());
		} else {
			return this.ticksLeft-e.ticksLeft;
		}
		/*if (this.equals(e)) {
			return 0;
		} else if (this.entity.equals(e.entity)) {
			return 1;
		}
		return -1;*/
	}

	public void setImage(BufferedImage image) {
		entity.setImage(image);
	}
	public BufferedImage getImage() {
		return entity.getImage();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Path getPath() {
		return path;
	}

	public void setPath(Path locationPath) {
		this.path = locationPath.get();
	}

	public Entrance getEntrance() {
		return entrance;
	}
	
	public void setNewEntrance(Entrance entrance) {
		this.entrance = entrance;
	}
	public void moveThroughEntrance(Entrance entrance) {
		//Set the new location as whatever's on the other side of this
		location = entrance.getLinkedEntrance().getLocation();
		//Give it the new Entrance
		setNewEntrance(entrance.getLinkedEntrance());
	}

	public void alterPathBeginning(Location newLocation) {
		//Check the second Location on its Path. If the passed Location is equivalent, delete the first. Otherwise, add this to the start.
		Location newRoom = path.get(1);
		if (newRoom == location) {
			path.remove(0);
		} else {
			path.add(0, location);
		}
	}
	public void alterPathEnd(Location newDestination) {
		 //If passed room is in the Path ArrayList, truncate it to that. Otherwise, add it to the end.
		if (path.contains(newDestination)) {
			//Remove the sublist of the path beginning at the index of this location (+1, since it starts at 0).
			path.removeAll(path.subList(path.indexOf(newDestination), path.size()-1));
		} else {
			path.add(newDestination);
		}
	}
	
	public void updatePath() {
		//Occurs when the entity moves through an entrance. Remove the first location in the path - it's got to there.
		path.remove(0);
	}
	
	public String toString() {
		return (getName()+" in "+location.getName()+" at coordinates "+coords.toString());
	}

	public void setCoords(int x, int y, int z) {
		setCoords((byte) x, (byte) y, (byte) z);
	}
}
