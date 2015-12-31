package version_7;

import java.util.HashMap;

public class Dungeon extends Location3D {

	//A series of Maps, in 4D space. Contains methods for checking which Map to move to, 
	//finding the number of Entities/Items in the dungeon, and eventually
	//the Theme and other things that dictate how the Dungeon is formed.
	
	Coord4D position;
	byte x;
	byte y;
	byte z;
	byte v;
	HashMap<Map, Coord3D> maps = new HashMap<Map, Coord3D>();
	
	public Dungeon() {
		
	}
	public Dungeon(HashMap<Map, Coord3D> maps) {
		this.maps = maps;
	}

	public HashMap<Map, Coord3D> getMaps() {
		return maps;
	}

	public void setMaps(HashMap<Map, Coord3D> maps) {
		this.maps = maps;
	}
	
	public void addMap(Map map, Coord3D location) {
		maps.put(map, location);
		map.setPosition(location);
		if (map.getW()+location.getX() > this.getW()) {
			this.setW(map.getW()+location.getX());
		}
		if (map.getH()+location.getY() > this.getH()) {
			this.setH(map.getH()+location.getY());
		}
		if (map.getD()+location.getZ() > this.getD()) {
			this.setH(map.getD()+location.getZ());
		}
	}
	
	public Map getMapRel(Location cloc, Coord3D coords) {
		if (cloc instanceof Map) {
			cloc = (Map) cloc;
			//First, get the absolute coordinates of the point you want to check.
			//This needs the absolute coordinates of the corner of the map, added to whatever point the player's at in that map.
			
			Coord3D mapCorner = maps.get(cloc);
			coords.add(mapCorner);
			
			Map betweenford = null;
			
			//Go through each Map - if the z-level matches, and the new coord is contained within the new map, return it.
			for (Map map : maps.keySet()) {
				if (map.getPosition().getZ() == coords.getZ() && map.containsPoint(coords)) {
					GameClass.print("New map name: "+map.getName());
					return map;
				}
				if (map.getName()=="Between Ford") {
					betweenford = map;
				}
			}
			
			//If no location matches, then send the player to the Between Ford, and teleport them.
			GameClass.print("New map name: Between Ford");
			coords.setLocation(0, 0, 0);
			return betweenford;
		} else if (cloc instanceof Room) {
			//TODO: Find the location of the Room in the Map, add this to the relative position 
		}
		//TODO - replace this with The Between Ford
		return null;
	}
	
	public Coord3D getLocInDungeon(Location cloc, Coord3D coords) {
		if (cloc instanceof Map) {
			return new Coord3D(Coord3D.c3sum(coords, ((Map) cloc).getPosition()));
		} else if (cloc instanceof Room) {
			//TODO - Find location of room again
			return null;
		} return coords;
	}
	@Override
	public Coord4D getPosition() {
		// TODO Auto-generated method stub
		return new Coord4D(getX(), getY(), getZ(), getP());
	}
	@Override
	public Coord3D getSize() {
		return new Coord3D(getW(), getH(), getD());
	}
	
	public String toString() {
		return "Dungeon "+this.getName()+", containing "+this.getMaps().size()+" maps.";
	}
	@Override
	public void fill(Coord a, Coord b, TileType t) {
		// TODO Auto-generated method stub
		
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
	public byte getV() {
		return v;
	}
	
	public void fill(Coord3D a, Coord3D b, TileType tile) {
		// TODO Auto-generated method stub
		
	}
	
	public void draw(Coord3D a, Coord3D d, TileType tile) {
		// TODO Auto-generated method stub
		
	}
	
	public byte getP() {
		//Dungeons only contain a single Plane, so this is always 1.
		return 1;
	}
}
