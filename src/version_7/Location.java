package version_7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class Location {

	private Tile[][] tiles;
	private HashMap<Location, Entrance> attachedLocs = new HashMap<Location, Entrance>();
	private ArrayList<EntityTile> entities = new ArrayList<EntityTile>();
	private ArrayList<ItemTile> items = new ArrayList<ItemTile>();
	private Coord2D corner;
	private String name;
	private byte width;
	private byte height;
	
	public HashMap<Location, Entrance> getAttached() {
		return attachedLocs;
	}
	
	public void addAttachment(Location attachment, Entrance entrance) {
		attachedLocs.put(attachment, entrance);
		entrance.setLocation(this);
		//TODO - this needs more information about the attachment. How wide is the connection, and where is it?
	}
	
	public void setAttachments(HashMap<Location, Entrance> attachments) {
		this.attachedLocs = attachments;
	}

	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Tile getTile(byte x, byte y) {
		return tiles[x][y];
	}
	//For byte values, modified by a direction.
	public Tile getTile(short xy) {
		byte x = (byte) (xy & 0xff);
		byte y = (byte) (xy >> 8);
		return tiles[x][y];
	}
	
	public byte getX() {
		return corner.getX();
	}
	public byte getY() {
		return corner.getY();
	}
	public byte getW() {
		return width;
	}
	public byte getH() {
		return height;
	}
	public Coord2D getCorner() {
		return corner;
	}
	
	public void setX(byte x) {
		corner = new Coord2D(x, corner.getY());
	}
	public void setY(byte y) {
		corner = new Coord2D(corner.getX(), y);
	}
	public void setW(byte w) {
		width = w;
	}
	public void setH(byte h) {
		height = h;
	}
	public void setCorner(Coord2D location) {
		this.corner = location;
	}
	public void setCorner(byte x, byte y) {
		corner = new Coord2D(x, y);
	}
	public void setTile(byte x, byte y, TileType tile) {
		try {
			tiles[x][y] = new Tile(tile);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println("Error: Tile at "+x+", "+y+" in location "+this.getName()+" cannot be found.");
			//System.exit(1);
		}
	}
	public void setTile(int x, int y, TileType tile) {
		this.setTile((byte) x, (byte) y, tile);
	}
	public void setTile(byte x, byte y, TileType tile, boolean isWalkable) {
		this.setTile(x, y, tile);
		tiles[x][y].setWalkable(isWalkable);
	}
	public void setTile(int x, int y, TileType tile, boolean isWalkable) {
		this.setTile(x, y, tile);
		tiles[x][y].setWalkable(isWalkable);
	}
	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	public Pair<Boolean, Entity> entityAt(byte x, byte y, byte z) {
		for (EntityTile entity:entities) {
			if (entity.getX()==x && entity.getY()==y && entity.getZ()==z) {
				return new Pair<Boolean, Entity>(true, entity.getEntity());
			}
		}
		return new Pair<Boolean, Entity>(false, null);
	}
	
	public void addEntity(EntityTile entity) {
		entities.add(entity);
		entity.setLocation(this);
	}
	public boolean removeEntity(EntityTile entity) {
		boolean removed = entities.remove(entity);
		if (!removed) {
			System.out.println("ERROR. ERROR.");
			return false;
		} else {
			return true;
		}
	}
	public ArrayList<EntityTile> getEntities() {
		return entities;
	}
	public ArrayList<EntityTile> getEntityByName (String name) {
		ArrayList<EntityTile> entReturned = new ArrayList<EntityTile>();
		for (EntityTile entity : entities) {
			String otherName = entity.getEntity().getName();
			if (otherName.equals(name) && entReturned.isEmpty()) {
				entReturned.add(entity);
			} else if (otherName.equals(name) && !entReturned.isEmpty()) {
				System.out.println("Another entity called "+name+" has been found.");
				entReturned.add(entity);
			}
		}
		if (!entReturned.isEmpty()) {
			return entReturned;
		} else {
			System.out.println("No entities found, this will go wrong soon.");
			return null;
		}
	}
	public EntityTile getEntity(EntityTile entity) {
		return entities.get(entities.indexOf(entity));
	}
	public boolean replaceEntity(EntityTile entityToReplace, EntityTile replacer) {
		boolean replace = entities.remove(entityToReplace);
		boolean add = entities.add(replacer);
		if (add && replace) {
			return true;
		} else {
			System.out.println("Error while replacing entity "+entityToReplace.getEntity().getName()+
					" with "+replacer.getEntity().getName()+" in location "+this.name+".");
			if (!add) {
				System.out.println("The entity to add was not found.");
			} else if (!replace) {
				System.out.println("The entity to remove was not found.");
			} else {
				System.out.println("I have no idea how you got here, apparently (add && replace) is false,\n" +
						"but so are (!add) and (!replace). Please tell me how this happened.");
			}
			return false;
		}
	}

	/**
	 * @param entName: name of the entity to check for
	 * @return number of entities found
	 */
	public int getEntityCount(String entName) {
		int count = 0;
		for (EntityTile entity:entities) {
			if (entity.getName().equals(entName)) {
				count++;
			}
		}
		return count;
	}
	
	
	public Pair<Boolean, Item> itemAt(byte x, byte y, byte z) {
		for (ItemTile item:items) {
			if (item.getX()==x && item.getY()==y && item.getZ()==z) {
				return new Pair<Boolean, Item>(true, item.getItem());
			}
		}
		return new Pair<Boolean, Item>(false, null);
	}
	
	public void addItem(ItemTile item) {
		items.add(item);
	}
	public void removeItem(ItemTile item) {
		items.remove(item);
	}
	public ArrayList<ItemTile> getItems() {
		return items;
	}
	public int getItemCount(String itemName) {
		int count = 0;
		for (ItemTile item : items) {
			if (item.getName().equals(itemName)) {
				count++;
			}
		}
		return count;
	}

	public Entrance findEntranceFor(Location location) {
		for (Entry<Location, Entrance> entry : attachedLocs.entrySet()) {
			if (entry.getKey()==location) {
				return entry.getValue();
			}
		}
		System.out.println("No entrance found between "+this.getName()+" and "+location.getName());
		return null;
	}
	public void update(ArrayList<EntityTile> entities, ArrayList<ItemTile> items) {
		this.entities = entities;
		this.items = items;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract void moveCamera();
	
}
