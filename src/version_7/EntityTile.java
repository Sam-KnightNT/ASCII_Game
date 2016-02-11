package version_7;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

public class EntityTile implements Comparable<EntityTile> {

	private Entity entity;
	private byte x;
	private byte y;
	private Coord2D coords;
	private HashMap<String, Integer> stats = new HashMap<String, Integer>();
	private HashMap<String, Triplet<BufferedImage, String, String>> patterns;
	private ArrayList<Item> inventory = new ArrayList<Item>();
	
	//Each stat is modified by a random amount from -r to +r. TODO - change this, it's a bit crap.
	private int randomness = 0;
	private Random ran = new Random();
	private int ticksLeft;
	private int id;
	private Location location;
	private Path path;
	private Entrance entrance;
	private BufferedImage portrait;
	private String AI;
	
	public EntityTile(Entity entity, Location cloc, byte x, byte y, BufferedImage portrait, String AI) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.coords = new Coord2D(x, y);
		this.path = new Path();
		this.location = cloc;
		this.portrait = portrait;
		cloc.addEntity(this);
		this.setAI(AI);
		this.id = GameClass.entityCount+1;
		GameClass.entityCount++;
		genStats();
		this.resetTicks();
	}
	
	public EntityTile(Entity entity, Location cloc, byte x, byte y, BufferedImage portrait) {
		this(entity, cloc, x, y, portrait, "SimpleMelee");
	}
	
	public EntityTile(Entity entity, Location cloc, int x, int y, BufferedImage portrait) {
		this(entity, cloc, (byte) x, (byte) y, portrait);
	}
	
	public EntityTile(Entity entity, Location loc, byte x, byte y, BufferedImage portrait, int randomness) {
		this(entity, loc, x, y, portrait);
		this.randomness = randomness;
	}
	
	public EntityTile(Entity entity, Location cloc, int x, int y, BufferedImage portrait, int randomness) {
		this(entity, cloc, (byte) x, (byte) y, portrait, randomness);
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

	public short getXY() {
		return (short) (x + (y << 8));
	}
	
	public Coord2D getCoords() {
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
	
	public void setX(int x) {
		setX((byte) x);
	}
	
	public void setY(int y) {
		setY((byte) y);
	}
	
	public void setXY(int newxy) {
		setX((byte) (newxy % 256));
		setY((byte) (newxy >> 8));
	}
	
	public void setCoords(Coord2D coords) {
		this.x = coords.getX();
		this.y = coords.getY();
		this.coords = coords;
	}
	public void setCoords(int x, int y) {
		setCoords((byte) x, (byte) y);
	}
	
	public void setCoords(byte x, byte y) {
		this.x = x;
		this.y = y;
		this.coords = new Coord2D(x, y);
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
	
	public void setTicks(int ticks) {
		ticksLeft = ticks;
	}
	public int getTicks() {
		return ticksLeft;
	}
	public void resetTicks() {
		ticksLeft = (int) (100*(100/(float) this.getStat("speed")));
	}
	public void decreaseTicks(int ticks) {
		ticksLeft -= ticks;
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
	
	public void pickupItem(Potion item) {
		System.out.println("Picked up potion");
		this.addToInventory(item);
		System.out.println(entity.getName()+" picked up "+item.getName());
	}

	public void pickupItem(Item item) {
		if (item instanceof Potion) {
			pickupItem((Potion) item);
		} else if (item instanceof ItemEquippable) {
			pickupItem((ItemEquippable) item);
		} else {
			System.out.println("Picked up unequippable item");
			this.addToInventory(item);
			System.out.println(entity.getName()+" picked up "+item.getName());
		}
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public int compareTo(EntityTile e) {
		return this.id-e.id;
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
		if (GameClass.debug) {
			return (getName()+"#"+id+" in "+location.getName()+" at coordinates "+coords.toString());
		} else {
			return (getName()+"#"+id);
		}
	}
	
	public BufferedImage getPortrait() {
		return portrait;
	}
	
	public String getAI() {
		return AI;
	}
	
	public void setAI(String AI) {
		this.AI = AI;
	}
}
