package version_7;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Map {

	/*
	 * Redo this. A Map has several Rooms. Those Rooms are generated from a list of... some, a few at first.
	 * They are placed together haphazardly, like the old algorithm, but unlike that they are predefined.
	 * They are just placed in the world, hopefully fitting together.
	 * Whatever doesn't fit is regenerated. If this fails 10 times, the entire thing is regenerated.
	 * If this fails 1000 times, the Map is cancelled with an Exception, something is wrong.
	 * Test this at this point, and link it to the game. Once that's done, add corridors.
	 * These will be defined later.
	 */
	
	private Tile[][][] tiles;
	private ArrayList<Room> rooms = new ArrayList<Room>();
	private HashMap<Link, Point2D> exits = new HashMap<Link, Point2D>();
	private ArrayList<EntityTile> entities = new ArrayList<EntityTile>();
	private ArrayList<ItemTile> items = new ArrayList<ItemTile>();
	private String name;
	
	public Map(int x, int y, int z) {
		tiles = new Tile[x][y][z];
	}

	public Map(int x, int y, int z, TileType tileType) {
		tiles = new Tile[x][y][z];
		for (int zI = 0; zI < z; zI++) {
			for (int yI = 0; yI < y; yI++) {
				for (int xI = 0; xI < x; xI++) {
					tiles[xI][yI][zI] = new Tile(tileType);
				}
			}
		}
	}
	
	public Map(Tile[][][] tiles, ArrayList<EntityTile> entities, ArrayList<ItemTile> items, String name) {
		this.tiles = tiles;
		this.entities = entities;
		this.items = items;
		this.name = name;
	}
	
	public void setTile(int x, int y, int z, TileType newTile) {
		tiles[x][y][z] = new Tile(newTile);
	}
	
	public Map changeTile(int x, int y, int z, TileType newTile) {
		tiles[x][y][z] = new Tile(newTile);
		return new Map(tiles, entities, items, name);
	}
	
	public int getDimX() {
		return tiles.length;
	}
	
	public int getDimY() {
		return tiles[0].length;
	}
	
	public int getDimZ() {
		return tiles[0][0].length;
	}
	
	public Tile getTile(int x, int y, int z) {
		return tiles[x][y][z];
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Pair<Boolean, Entity> entityAt(int x, int y, int z) {
		for (EntityTile entity:entities) {
			if (entity.getX()==x && entity.getY()==y && entity.getZ()==z) {
				return new Pair<Boolean, Entity>(true, entity.getEntity());
			}
		}
		return new Pair<Boolean, Entity>(false, null);
	}
	
	public Pair<Boolean, Item> itemAt(int x, int y, int z) {
		for (ItemTile item:items) {
			if (item.getX()==x && item.getY()==y && item.getZ()==z) {
				return new Pair<Boolean, Item>(true, item.getItem());
			}
		}
		return new Pair<Boolean, Item>(false, null);
	}
	
	public void addEntity(EntityTile entity) {
		entities.add(entity);
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
					" with "+replacer.getEntity().getName()+" to map "+this.name+".");
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
	
	public void addItem(ItemTile item) {
		items.add(item);
	}
	public void removeItem(ItemTile item) {
		items.remove(item);
	}
	public ArrayList<ItemTile> getItems() {
		return items;
	}
		
	/**
	 * @param entName: name of the entity to check for
	 * @return number of entities found
	 */
	public int getCount(String entName) {
		int count = 0;
		for (EntityTile entity:entities) {
			if (entity.getName().equals(entName)) {
				count++;
			}
		}
		return count;
	}
	
	public void update(ArrayList<EntityTile> entities, ArrayList<ItemTile> items) {
		this.entities = entities;
		this.items = items;
		System.out.println(items);
	}
	
	public void addRoom(Room room, boolean hasWall) {
		rooms.add(room);
		GameClass.floorify(room.getW(), room.getH(), room.getX(), room.getY(), this);
		if (hasWall) {
			GameClass.surround(room.getW(), room.getH(), room.getX(), room.getY(), this);
		}
	}
	
	public void addRoom(Room room) {
		addRoom(room, false);
	}
	
	public void addExit(Link exit, Point2D coords) {
		exits.put(exit, coords);
	}
	public HashMap<Link, Point2D> getExits() {
		return exits;
	}
}