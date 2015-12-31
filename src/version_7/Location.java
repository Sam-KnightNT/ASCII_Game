package version_7;

import java.util.ArrayList;

public abstract class Location {
	
	private ArrayList<EntityTile> entities = new ArrayList<EntityTile>();
	private ArrayList<ItemTile> items = new ArrayList<ItemTile>();
	private String name;
	
	//Position in 4-D space. 4th-D will be 0 if there isn't one, etc.
	public abstract Coord getPosition();
	
	//Size in 4-D space. 4th-D will be 1 if there isn't one.
	public abstract Coord getSize();
	
	//Fill a certain area with a certain tile.
	public abstract void fill(Coord a, Coord b, TileType t);

	//These will probably be problematic. But they're here anyway.
	public abstract byte getX();
	public abstract byte getY();
	public abstract byte getZ();
	public abstract byte getV();
	public abstract byte getW();
	public abstract byte getH();
	public abstract byte getD();
	public abstract byte getP();
	public int getWH()   { return getW()   + getH() << 8 ; }
	public int getWHD()  { return getWH()  + getD() << 16; }
	public int getWHDP() { return getWHD() + getP() << 24; }
	
	public abstract TileSpace getTiles();
	
	public abstract Tile getTile(int val);
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Pair<Boolean, Entity> entityAt(byte x, byte y) {
		for (EntityTile entity:entities) {
			if (entity.getX()==x && entity.getY()==y) {
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
			System.out.println("ERROR. ERROR. Entity "+entity.getName()+" not found.");
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
					" with "+replacer.getEntity().getName()+" in location "+this.getName()+".");
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
	
	
	public Pair<Boolean, Item> itemAt(byte x, byte y) {
		for (ItemTile item:items) {
			if (item.getX()==x && item.getY()==y) {
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
}
