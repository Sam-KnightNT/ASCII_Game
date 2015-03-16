package version_7;

import java.util.ArrayList;

public abstract class Location {

	public Tile[][] tiles;
	private ArrayList<Location> attachedLocs;
	public ArrayList<EntityTile> entities = new ArrayList<EntityTile>();
	public ArrayList<ItemTile> items = new ArrayList<ItemTile>();
	Coord2D corner;
	private String name;
	protected int width;
	protected int height;
	
	public ArrayList<Location> getAttached() {
		return attachedLocs;
	}
	
	public void addAttachment(Location attachment) {
		attachedLocs.add(attachment);
	}

	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}
	
	public int getX() {
		return corner.getY();
	}
	public int getY() {
		return corner.getY();
	}
	public int getW() {
		return width;
	}
	public int getH() {
		return height;
	}
	public Coord2D getCorner() {
		return corner;
	}
	
	public void setCorner(Coord2D location) {
		this.corner = location;
	}
	
	public void setCorner(int x, int y) {
		corner = new Coord2D(x, y);
	}

	public void setTile(int x, int y, TileType tile) {
		tiles[x][y] = new Tile(tile);
	}
	
	public Pair<Boolean, Entity> entityAt(int x, int y, int z) {
		for (EntityTile entity:entities) {
			if (entity.getX()==x && entity.getY()==y && entity.getZ()==z) {
				return new Pair<Boolean, Entity>(true, entity.getEntity());
			}
		}
		return new Pair<Boolean, Entity>(false, null);
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
	
	
	public Pair<Boolean, Item> itemAt(int x, int y, int z) {
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
