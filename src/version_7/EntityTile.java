package version_7;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

public class EntityTile implements Comparable<EntityTile> {

	private Entity entity;
	private int x;
	private int y;
	private int z;
	private HashMap<String, Integer> stats = new HashMap<String, Integer>();
	private ArrayList<Item> inventory = new ArrayList<Item>();
	private int randomness = 0;
	private Random ran = new Random();
	private int ticksLeft;
	private int id;
	private Location location;
	private ArrayList<Location> path;
 
	
	public EntityTile(Entity entity, Location loc, int x, int y, int z) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
		this.location = loc;
		genStats();
	}
	
	public EntityTile(Entity entity, Location loc, int x, int y, int z, int randomness, int id) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
		this.location = loc;
		this.randomness = randomness;
		this.id = id;
		genStats();
	}
	
	public Entity getEntity() {
		return entity;
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
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
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
	public void printStats() {
		System.out.println("	"+entity.getName());
		for (Entry<String, Integer> entry : stats.entrySet()) {
			System.out.println("Stat "+entry.getKey()+" has value "+entry.getValue());
		}
	}
		
	public void setCoords(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void addToInventory(Item item) {
		inventory.add(item);
	}
	public ArrayList<Item> getInventory() {
		return inventory;
	}
	
	public void printInventory() {
		for (Item item : inventory) {
			System.out.println(item.getName()+". "+item.getDescription());
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
	
	public ArrayList<Location> getPath() {
		return path;
	}

	public void setPath(ArrayList<Location> path) {
		this.path = path;
	}

	public void moveToNewRoom(Location location) {

		//Check the second Location on its Path. If the passed Location is equivalent, delete the first. Otherwise, add this to the start.
		Location newRoom = path.get(1);
		if (newRoom == location) {
			path.remove(0);
		} else {
			path.add(0, location);
		}
	}

	public void alterPath(Location newDestination) {
		 //If passed room is in the Path ArrayList, truncate it to that. Otherwise, add it to the end.
		if (path.contains(newDestination)) {
			//Remove the sublist of the path beginning at the index of this location (+1, since it starts at 0).
			path.removeAll(path.subList(path.indexOf(newDestination), path.size()-1));
		} else {
			path.add(newDestination);
		}
	}
}
