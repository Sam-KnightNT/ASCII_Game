package version_7;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameClass {
	//TODO ideas
	/*
	 * The moving castle has to carve a path through the landscape, destroying it.
	 * Pre-calculate this path, as the calculations to do it would be immense.
	 * Every time you change screens, replace a patch of the land with this destroyed landscape.
	 * The patch length is equivalent to the speed of the castle multiplied by the time count since it last updated.
	 * If you are close, it recalculates every step. The castle is a 3Dish object, moving like the final boss.
	 * I.e. it is "unnatural", much like the Martians in It's Walky.
	 * You can lift up entire towns to move them, but it takes a supreme amount of effort and power.
	 * Maybe others can help? Or, you might be the only one.
	 * You can create hovering things, a platform or something, to help carry them along.
	 * The intention is to move them out of the way of the castle.
	 * If you do this early enough, you can delay the castle enough to make it worth it - 
	 * once they get to where it SHOULD have been, scouts must be sent to find it.
	 * So, put it somewhere out-of-the-way.
	 * Also, he will think this town is particularly important, so he'll spend extra time picking through the rubble.
	 * The castle will also take a different route - for example, if you want to preserve an area that would be on the way.
	 * It'll recalculate the destructive path once it gets to the town, heading straight for the next one.
	 */
	static HashMap<String, Item> items = new HashMap<String, Item>();
	static HashMap<String, Entity> entities = new HashMap<String, Entity>();
	static HashMap<String, Location> locations = new HashMap<String, Location>();
	static HashMap<String, TileType> tiles = new HashMap<String, TileType>();
	static Random random = new Random();
	//TODO - Remove all static modifiers, since they should be unique to each GameClass
	private static GameImage mainImage;
	//private static InfoPanel infoPanel;
	private final static JFrame frame = new JFrame();
	private static final int PT_SIZE = 40;
	static int playerIndex;
	public static EntityTile self;
	private static Location cloc;
	private static boolean AIon = false;
	private static TreeMap<EntityTile, Integer> unfrozenEntities = new TreeMap<EntityTile, Integer>();
	
	//Where the player is initially
	static int px = 1;
	static int py = 3;
	static int pz = 0;
	
	//Dimensions of maps
	static int dx;
	static int dy;
	static int dz;
	
	public static void main(String[] args) throws Exception {
		String filename = "..\\ASCII_Game\\mazeInfo.txt";
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String contents = "";
		String line = reader.readLine();
		while (line != null) {
			contents += line.trim()+"\n";
			line = reader.readLine();
		}
		reader.close();
		//TODO: Add debug option, if first line is "debug", write out everything that goes on - 
		//E.g. "stats modifier not found, no stat changes will be applied to this item" etc
		//TODO: 2nd window for stats, attack direction and stuff
		String[] strs = contents.split("\n");
		String[] dimensions;
		String str;
		for (int i=0; i<strs.length; i++) {
			str = strs[i];
			if (str.contains("dimensions:")) {
				if (!(dx==0))
					throw new Exception("Dimensions are defined at least twice, remove one of the lines");
				else {
					dimensions = str.replace("dimensions: ", "").split(", ");
					print(dimensions[0]+"+"+dimensions[1]);
					dx = Integer.parseInt(dimensions[0]);
					dy = Integer.parseInt(dimensions[1]);
					dz = Integer.parseInt(dimensions[2]);
				}
			}
			else if (str.trim().equals("player:")) {
				i++;
				str = "";
				print(str);
				while(!strs[i].trim().equals("end player definition")) {
					str += strs[i]+"\n";
					i++;
				}
				generatePlayer(str);
			}
			else if (str.trim().equals("begin item definitions")) {
				i++;
				while(!strs[i].trim().equals("end item definitions")) {
					str += strs[i]+"\n";
					i++;
				}
				generateItems(str);
			}
			else if (str.trim().equals("begin entity definitions")) {
				i++;
				while(!strs[i].trim().equals("end entity definitions")) {
					str += strs[i]+"\n";
					i++;
				}
				generateEntities(str);
			}
			else if (str.trim().equals("begin material definitions")) {
				i++;
				while(!strs[i].trim().equals("end material definitions")) {
					str += strs[i]+"\n";
					i++;
				}
				generateMaterials(str);
			}
			/*else if (str.trim().equals("begin tile definitions")) {
				i++;
				while(!strs[i].trim().equals("end tile definitions")) {
					str += strs[i]+"\n";
					i++;
				}
				generateTiles(str);
			}*/
			else if (str.trim().equals("begin spell definitions")) {
				i++;
				while(!strs[i].trim().equals("end spell definitions")) {
					str += strs[i]+"\n";
					i++;
				}
				generateSpells(str);
			}/*
			else if (str.trim().equals("begin map definitions")) {
				i++;
				while(!strs[i].trim().equals("end map definitions")) {
					str += strs[i]+"\n";
					i++;
				}
				generateMaps(str);
			}*/
		}
		
		BufferedReader readIn = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		
		//Construct 2 rooms and a corridor to connect them
		Room room = new Room(9, 6, 11, 29, tiles.get("Gold 6 Floor"), tiles.get("Gold 6 Wall"));
		Room room2 = new Room(7, 8, 17, 21, tiles.get("Tin 6 Floor"), tiles.get("Tin 6 Wall"));
		Room room3 = new Room(12, 8, 7, 12, tiles.get("Bronze 6 Floor"), tiles.get("Bronze 6 Wall"));
		Room room4 = new Room(7, 8, 1, 20, tiles.get("Iron 6 Floor"), tiles.get("Iron 6 Wall"));
		Room room5 = new Room(24, 12, 0, 0, tiles.get("Grass 2 Floor"), tiles.get("Grass 2 Wall"));
		
		ArrayList<Entrance> entrances = new ArrayList<Entrance>();
		entrances.add(new Entrance(Direction.SOUTH, new Coord2D(4, 8), new Coord2D(7, 8)));
		entrances.add(new Entrance(Direction.WEST, new Coord2D(8, 6), new Coord2D(8, 8)));
		entrances.add(new Entrance(Direction.NORTH, new Coord2D(3, 0), new Coord2D(6, 0)));
		entrances.add(new Entrance(Direction.EAST, new Coord2D(0, 4), new Coord2D(0, 6)));

		Corridor corridor = new Corridor(9, 9, 8, 20, tiles.get("Marble Floor"), tiles.get("Marble Wall"));
		corridor.setName("Corridor 1");
		room.setName("Room 1");
		room2.setName("Room 2");
		room3.setName("Room 3");
		room4.setName("Room 4");
		room5.setName("Room 5");
		EntityTile mino = new EntityTile(entities.get("Minotaur"), room, (byte) 1, (byte) 1, (byte) 0);
		unfrozenEntities.put(mino, 100);
		room.addEntity(mino);
		attachTwoLocations(corridor, room, entrances.get(0));
		attachTwoLocations(corridor, room2, entrances.get(1));
		attachTwoLocations(corridor, room3, entrances.get(2));
		attachTwoLocations(corridor, room4, entrances.get(3));
		attachTwoLocations(room3, room5, new Entrance(Direction.NORTH, 2, 4, room3));
		
		corridor.extrudeWithCurrentAttachments(tiles.get("Marble Floor"));
		room.carveEntrancesWithCurrentAttachments(tiles.get("Marble Floor"));
		room2.carveEntrancesWithCurrentAttachments(tiles.get("Marble Floor"));
		room3.carveEntrancesWithCurrentAttachments(tiles.get("Bronze Floor"));
		room4.carveEntrancesWithCurrentAttachments(tiles.get("Grass Floor"));
		room5.carveEntrancesWithCurrentAttachments(tiles.get("Grass Floor"));
		room.pillarCorners(tiles.get("Marble Pillar"));
		locations.put("Room 1", room);
		locations.put("Room 2", room2);
		locations.put("Room 3", room3);
		locations.put("Room 4", room4);
		locations.put("Room 5", room5);
		locations.put("Corridor 1", corridor);
		
		cloc = locations.get("Corridor 1");
		self = new EntityTile(entities.get("Player"), cloc, (byte) 5, (byte) 7, (byte) 0);
		cloc.addEntity(self);
		mainImage = new GameImage(new ArrayList<Location>(locations.values()), cloc, PT_SIZE, dx, dy);
		mainImage.setSize(1040, 730);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(1040,730);
				frame.setTitle("Game Thing");
				frame.setVisible(true);
				frame.add(mainImage);
				//TODO - Add a more pretty graphical thing to infoPanel, that shows you your attack
				//Like a swipey thing with the 9 directions, that shows you what your wand is doing, how strong the attack is, etc
				//This is how it becomes a fusion of old-style graphics and new-style features
				//infoPanel.setSize(100, mainImage.getHeight());
				//frame.add(infoPanel);
				frame.setResizable(false);
			}
		});
		
//		SPOT FURTHER DOWN, BELOW COMMANDS

		//TODO - modify speed counter to account for things like
		//opening inventory (1/20th a move time), long wind-up moves and so on
		//Perhaps an action list of what to do next, i.e. "Minotaur move in 20 ticks, Player perform Great Strike in 35 ticks", etc?
		//Each action has wind-up time (usually 0) and cool=down time (default to speed)
		//E.g. for Great Strike
		//Wind-up .5, so it takes (0.5*10000)/speed ticks before the action is performed
		//Cool-down 1, so it takes 10000/speed ticks before you can perform another action
		while (true) {
			System.out.print("Please enter a command: ");
			try {
				command = readIn.readLine();
			} catch (IOException ex) {
				print(ex);
			}
			if (command.equals("stats")) {
				self.printStats();
			}
			else if (command.equals("change")) {
				command("cb Marble Floor "+self.getX()+" "+self.getY());
			}
			else if (command.equals("regen")) {
				self.setStat("mana", self.getStat("max mana"));
				print("You regen to "+self.getStat("mana")+" mana.");
			}
			else if (command.startsWith("magic")) {
				//Selector selector = new Selector()
				//selector.
			}
			else if (command.startsWith("damage ")) {
				if (self.hasStat("mana") && self.hasStat("magic power") && self.getStat("mana")>0) {
					String name = command.substring(7);
					EntityTile entity = null;
					if (name.matches(".+ [0-9]+")) {
						int damage = Integer.parseInt(name.substring(name.indexOf(' ')+1));
						name = name.substring(0, name.indexOf(" "));
						print(damage+" "+name);
						command("d "+name+" "+damage);
					}
					else if (cloc.getEntityCount(name)>0) {
						ArrayList<EntityTile> entityList = cloc.getEntityByName(name);
						if (entityList.size()==1) {
							entity = entityList.get(0);
						} else {
							print("More than one entity with name "+name+" found:\n" +
									"Please enter which one you want to attack.\n" +
									"They are numbered from top to bottom, if on the same height then left to right.");
							int num = Integer.parseInt(readIn.readLine());
							entity = entityList.get(num-1);
						}
						print("You do a weird magicky thing!");
						int rng = random.nextInt(6)+1;
						print("A mystical die rolls. It rolls a "+rng+"!");
						int damage;
						self.setStat("mana", self.getStat("mana")-1);
						switch (rng) {
						case 1:
							entities.remove(self.getName());
							break;
						case 2:
							damage = random.nextInt(20); 
							command("d "+self+" "+damage);
							break;
						case 3:
							command("d "+entity+" 1");
							break;
						case 4:
							damage = random.nextInt(20);
							command("d "+entity+" "+damage);
							break;
						case 5:
							damage = random.nextInt(30)+40;
							command("d "+entity+" "+damage);
	 					case 6:
	 						damage = random.nextInt(15);
	 						command("d "+self+" "+damage);
	 						cloc.removeEntity(entity);
	 						break;
	 					default:
	 						print(
	 							"I don't know what you just did, but something's broke.\n" +
	 							"Let me know what the die rolled and I'll try to fix it.\n");
	 					}
	 				} else {
	 					print("Entity not recognised.");
	 					for (EntityTile entityN:cloc.getEntities()) {
	 						print(entityN.getName());
	 					}
	 				}
				}
				else if (!self.hasStat("mana")) {print("Undefined mana");}
				else if(!self.hasStat("magic power")) {print("Undefined magic power");}
				else if(self.getStat("mana")<=0) {print("Out of mana!");}
			}
			
			//Heal thyself
			else if (command.equals("heal")) {
				int mana = self.getStat("mana");
				int power = self.getStat("magic power");
				if (power>0 && mana>0) {
					int health = self.getStat("health");
					print("You attempt to heal yourself.");
					int rng = random.nextInt(6)+1;
					print("You roll a "+rng);
					switch (rng) {
					case 2: case 3: case 4:
						self.setStat("health", health+power);
						break;
					case 5:
						self.setStat("health", health+(power*2));
						break;
					case 6:
						self.setStat("health", self.getStat("max health"));
						break;
					}
					print("You now have "+health+" health.");
					self.setStat("mana", mana-1);
				}
				else if(mana==0) {print("Out of mana!");}
				else if (mana<0) {print("Undefined mana");}
				else if(power<0) {print("Undefined magic power");}
			}
			
			//Control another entity
			else if (command.startsWith("control ")) {
				String entity = command.substring(8);
				if (entities.containsKey(entity)) {
					ArrayList<EntityTile> ent = cloc.getEntityByName(entity);
					if (!ent.isEmpty()) {
						if (ent.size()>1) {
							print("More than one entity has been found, please type\n" +
									"which one you want to control (it added them from top to bottom, then left to right)");
							int num = Integer.parseInt(readIn.readLine());
							self = ent.get(num-1);
						} else {
							self = ent.get(0);
						}
					} else {
						print("Entity not found, type \"entities\" for a list.");
					}
				} else {
					print("Invalid entity, type \"entities\" for a list of valid names");
				}
			}
			else if (command.contentEquals("all entities")) {
				for (String entName : entities.keySet()) {
					print(entName);
				}
			}
			else if (command.contentEquals("entities")) {
				for (EntityTile entity : cloc.getEntities()) {
					print(entity.getEntity().getName());
				}
			}
			else if (command.contentEquals("hostilities")) {
				if (AIon) {
					AIon = false;
				} else {
					AIon = true;
				}
			}
			else {
				print("Command not recognised, type \"show\" for a list");
			}
			
			//Change this to only repaint the small area being changed
			//frame.getContentPane().repaint();
		}
	}
	
	public static void command(String command) {
		//Move
		if (command.matches("m([swen])$")) {
			switch (command.charAt(1)) {
			case 's':
				cycle(Direction.SOUTH);
				break;
			case 'w':
				cycle(Direction.WEST);
				break;
			case 'n':
				cycle(Direction.NORTH);
				break;
			case 'e':
				cycle(Direction.EAST);
				break;
			default:
				print("I've apparently set the command system a bit wrong,\n" +
						"tell me what you pressed, and that \"the direction regex was passed\""
						+command+"\" as a parameter\"");
			}
		}
		
		//Pick up item
		else if (command.equals("p")) {
			Pair<Boolean, Item> itemHere = cloc.itemAt(self.getX(), self.getY(), self.getZ()); 
			if (itemHere.getLeft()) {
				Item item =	itemHere.getRight();
				Pair<Boolean, ItemTile> itemAt = 
					item.getTileAt(cloc.getItems(), self.getX(), self.getY());
				if (itemAt.getLeft()) {
					cloc.removeItem(itemAt.getRight());
					self.pickupItem(item);
					mainImage.setCurrentLocation(cloc);
					mainImage.redrawMap();
				}
				else {
					//Add new exception (ItemNotFound exception?) like ConcurrentModification
					print(
						"Something went seriously wrong here. I can't find an item at the place\n" +
						"you're at, but the only way to get here is if you picked one up.\n" +
						"Did it disappear before this thing deleted it? Exiting anyway, this will crash otherwise.");
					System.exit(0);
				}
			} else {
				print("No item here.");
			}
		}
		
		//Display inventory
		else if (command.equalsIgnoreCase("inventory")) {
			self.printInventory();
		}
		
		//Show entity list and timings
		else if (command.equals("ls")) {
			for (EntityTile entity : unfrozenEntities.keySet()) {
				print(entity.getTicks()+" "+entity.getName());
			}
		}
		
		//Show command list
		else if (command.equals("s")) {
			print("Todo");
		}
		
		//Damage entity - REWRITE
		else if (command.startsWith("d")) {
			Pattern pattern = Pattern.compile("d (.+) ([0-9]+)");
			Matcher matcher = pattern.matcher(command);
			if (matcher.matches()) {
				print(command);
				String entity = matcher.group(1);
				int damage = Integer.parseInt(matcher.group(2));
				int entCount = cloc.getEntityCount(entity);
				if (entCount==1) {
					dealDamage(cloc.getEntityByName(entity).get(0), damage);
				} else if (entCount==0) {
					print("Entity "+entity+" not found, no damage will be dealt.");
				} else {
					print("More than 1 entity of same name found - something is probably wrong with" +
							"the combat system, so report it to whoever made it. If you ARE that person, hi me!");
				}
			} else {
				print("Improper usage of \"d\" command.\n" +
						"Should be \"d <entityname> <amount>\", found "+command+".");
			}
		}
		
		else if (command.startsWith("magic ")) {
			
			//Here, check through the spell list to find a matching spell
			//If there is one, do the effect listed
			//Else say "There is no spell by this name" or something
		}
		
		//Change one block into another
		else if (command.startsWith("cb")) {
			print(command);
			Pattern pattern = Pattern.compile("cb (.+) ([0-9]+) ([0-9]+) ([0-9]+)");
			Matcher matcher = pattern.matcher(command);
			if (matcher.matches()) {
				//Must be valid, so get it
				String tileName = matcher.group(1);
				byte x = Byte.parseByte(matcher.group(2));
				byte y = (byte) (Byte.parseByte(matcher.group(3))+1);
				byte z = Byte.parseByte(matcher.group(4));
				//TODO - test this and damage, add other things that cause these to change (maze generator?)
				if (tiles.containsKey(tileName)) {
					TileType tile = tiles.get(tileName);
					print("Changing at "+x+" "+y+" "+z);
					cloc.setTile(x, y, tile);
				} else {
					print("Tile "+tileName+" not found, " +
							"tile at ("+x+", "+y+") will not be changed.");
				}
				
			} else {
				print("Improper usage of \"cb\" command.\n" +
						"Should be \"cb <tilename> <xcoord> <ycoord>, found "+command+".");
			}
			//TODO next time: Test this and damage systems
		}
		//TODO - deprecate this, it shouldn't need to have to update it in the image all the time
		mainImage.setCurrentLocation(cloc);
		//Change this to only repaint the small area being changed
		frame.getContentPane().repaint();
		//TODO - have pictorial representations of engravings, like the DF engravings. See jef's stream on 21/03/2015, about 2:10 in, for an example of engravings. Before the coffin guy gets possessed.
		//So, for example, "This is an image of "Yui8856" and bucklers. "Yui8856" is surrounded by the bucklers." would have an image of a red-haired, long-bearded dorf surrounded by bucklers.
		//You can have more control over the engravings - such as, plating the bucklers in iron, or making carved drawings/embossed drawings.
	}
	
	public static void cycle(Direction dir) {
		TreeMap<EntityTile, Integer> newList = new TreeMap<EntityTile, Integer>();
		
		//Reset your own tick count
		unfrozenEntities.remove(self);
		int ticks = self.getTicks();
		self.resetTicks();
		
		//Print out all the locatios and their entities
		//TODO - delete this
		/*for (Location loc : locations.values()) {
			System.out.println(loc.getName());
			for (EntityTile ent : loc.getEntities()) {
				System.out.println("\t"+ent.getName());
			}
		}*/
		
		//Move the player
		boolean leftRoom = move(dir, self, cloc);
		
		//If the player entered a new room, alter the path of every entity that needs it
		if (leftRoom) {
			for (EntityTile entity : unfrozenEntities.keySet()) {
				//alterPath pseudocode: if passed room is in the Path ArrayList, truncate it to that. Otherwise, add it to the end.
				entity.alterPathEnd(self.getLocation());
			}
		}
		
		//Put the player into the new list, as well as every other entity
		newList.put(self, self.getTicks());
		for (EntityTile other : unfrozenEntities.keySet()) {
			other.setTicks(other.getTicks()-ticks);
			newList.put(other, other.getTicks()-ticks);
		}

		//And set the unfrozen entity list to this new list
		unfrozenEntities = newList;
		
		//Cycle through unfrozenEntities while there are entities to move
		while (!unfrozenEntities.firstKey().equals(self)) {
			
			//Recreate the new list and pop the entity off
			newList = new TreeMap<EntityTile, Integer>();
			EntityTile entity = unfrozenEntities.pollFirstEntry().getKey();
			
			//Reset the entity's tick rate after getting its current remaining
			ticks = entity.getTicks();
			entity.resetTicks();
			
			//Attempt to pathfind to the player
			boolean leftRoomEnt = pathfindToPlayer(entity);
			if (leftRoomEnt) {
				//Check the second Location on its Path. If the passed Location is equivalent, delete the first. Otherwise, add this to the start.
				entity.alterPathBeginning(entity.getLocation());
			}
			newList.put(entity, entity.getTicks());
			for (EntityTile other : unfrozenEntities.keySet()) {
				other.setTicks(other.getTicks()-ticks);
				newList.put(other, other.getTicks()-ticks);
			}
			unfrozenEntities = newList;
		}
		/*for (EntityTile ent : unfrozenEntities.keySet()) {
			print(ent.getName());
		}*/
		mainImage.redrawMap();
	}
	
	public static boolean move(Direction dir, EntityTile entity, Location loc) {
		//TODO - replace the location parameter, replace it with this line
		//Location loc = entity.getLocation();
		byte x = (byte) entity.getX();
		byte y = (byte) entity.getY();
		short xy = (short) (x + (y << 8));
		byte z = entity.getZ();
		//First, check and see if it has an entrance listed.
		if (entity.getEntrance()!=null) {
			//If it does, compare directions.
			Entrance entrance = entity.getEntrance();
			if (entrance.getDirection()==dir) {
				//If they are equal, move through the entrance.
				cloc.removeEntity(entity);
				cloc = entrance.getLinkedEntrance().getLocation();
				cloc.addEntity(entity);
				mainImage.setCurrentLocation(cloc);
				print(entity.getName()+" has moved to a new location! From "+loc.getName()+" to "+cloc.getName());
				entity.setLocation(cloc);
				byte d;
				switch (entrance.getDirection()) {
				case NORTH:
					//Work out the difference in x positions
					d = (byte) (cloc.getX()-loc.getX());
					entity.setCoords((byte) (x-d), (byte) (cloc.getH()-1), z);
					break;
				case SOUTH:
					d = (byte) (cloc.getX()-loc.getX());
					entity.setCoords((byte) (x-d), (byte) 0, z);
					break;
				case WEST:
					d = (byte) (cloc.getY()-loc.getY());
					entity.setCoords((byte) 0, (byte) (y-d), z);
					break;
				case EAST:
					d = (byte) (cloc.getY()-loc.getY());
					entity.setCoords((byte) (cloc.getW()-1), (byte) (y-d), z);
					break;
				}
				entity.setNewEntrance(entrance.getLinkedEntrance());
				return true;
			} else if (entrance.getDirection().getOppositeDirection()==dir) {
				//If they are opposite, remove this entrance and carry on.
				entity.setNewEntrance(null);
			}
		}
		if (x>0 && x<loc.getW()-1 && y>0 && y<loc.getH()-1) {
			//If all of these are true, there is no way moving will lead to a separate location, so move there.
			
			//The direction values are selected so that adding them will change xy to the appropriate value
			short newxy = (short) (xy+dir.getNumVal());
			Tile newTile = loc.getTile(newxy);
			if (!newTile.getType().isWalkable()) {
				print("Cannot move there.");
			} else {
				Pair<Boolean, EntityTile> otherEntTile = existsAtLoc(loc, newxy);
				if (otherEntTile.getLeft()) {
					print("The "+entity.getName()+" attacks the "+otherEntTile.getRight().getName()+"!");
					fight(entity, otherEntTile.getRight());
				} else {
					entity.setCoords(newxy, z);
					if (entity==self) {
						print("You move "+dir+" to "+entity.getX()+", "+entity.getY());
					} else {
						print("The "+entity.getName()+" moves "+dir+" to "+entity.getX()+", "+entity.getY());
					}
				}
			}
			//drawMap(cmap);
			return false;
		} else {
			//Otherwise, it's possible that the entity's at an Entrance, so check against all of them to find the right one.
			//Once it's found, if the direction matches up, go straight through it.
			HashMap<Location, Entrance> attachments = loc.getAttached();
			boolean found = false;
			Entrance foundEntrance = null;
			search:
			for (Entry<Location, Entrance> entry : attachments.entrySet()) {
				Entrance entrance = entry.getValue();
				//Make sure the current location is within the entrance
				if (	   entrance.getLocB().getY()>=y && entrance.getLocA().getY()<=y
						&& entrance.getLocA().getX()<=x && entrance.getLocB().getX()>=x) {
					found = true;
					foundEntrance = entrance;
					break search;
				}
			}
			//If an entrance has been found, set the entity's current entrance to be the one found.
			if (found) {
				//If the direction happens to be the same, move to the new location immediately.
				if (foundEntrance.getDirection()==dir) {
					//If the entity moving is the player, change cloc
					if (entity.getName()=="Player") {
						cloc.removeEntity(entity);
						cloc = foundEntrance.getLinkedEntrance().getLocation();
						cloc.addEntity(entity);
						mainImage.setCurrentLocation(cloc);
					} else {
						//Otherwise remove the entity from its current location and move it to the new location
						Location newLoc = foundEntrance.getLinkedEntrance().getLocation();
						entity.getLocation().removeEntity(entity);
						newLoc.addEntity(entity);
					}
					print(entity.getName()+" has moved to a new location! From "+loc.getName()+" to "+cloc.getName());
					byte d;
					switch (foundEntrance.getDirection()) {
					case NORTH:
						d = (byte) (cloc.getX()-loc.getX());
						entity.setCoords((byte) (x-d), (byte) (cloc.getH()-1), z);
						break;
					case SOUTH:
						d = (byte) (cloc.getX()-loc.getX());
						entity.setCoords((byte) (x-d), (byte) 0, z);
						break;
					case WEST:
						d = (byte) (cloc.getY()-loc.getY());
						entity.setCoords((byte) 0, (byte) (y-d), z);
						break;
					case EAST:
						d = (byte) (cloc.getY()-loc.getY());
						entity.setCoords((byte) (cloc.getW()-1), (byte) (y-d), z);
						break;
					}
					return true;
				} else {
					//If the entity does not go through the door, just perform the movement.
					if (foundEntrance.getDirection().getOppositeDirection()!=dir) {
						//If the direction is not opposite, it is still in the entrance, so set that as the current entrance.
						entity.setNewEntrance(foundEntrance);
					}
					//Else just set the entity's current entrance as this one (as long as it's not opposite), to speed up finding it next time, as well as actually moving over there.
					short newxy = (short) (xy+dir.getNumVal());
					Tile newTile = loc.getTile(newxy);
					if (!newTile.getType().isWalkable()) {
						print("Cannot move there.");
					} else {
						Pair<Boolean, EntityTile> otherEntTile = existsAtLoc(loc, newxy);
						if (otherEntTile.getLeft()) {
							print("The "+entity.getName()+" attacks the "+otherEntTile.getRight().getName()+"!");
							fight(entity, otherEntTile.getRight());
						} else {
							entity.setCoords(newxy, z);
							if (entity==self) {
								print("You move "+dir+" to "+entity.getX()+", "+entity.getY());
							} else {
								print("The "+entity.getName()+" moves "+dir+" to "+entity.getX()+", "+entity.getY());
							}
						}
					}
					//drawMap(cmap);
					return false;
				}
			} else {
				return false;
			}
		}
		//I don't think it should ever get to this point - this could be a link to Betweenford if someone manages to get here. For now, return false.
		//TODO - add Betweenford entrance here
	}

	public static void moveToEntrance(EntityTile entity, Entrance entrance) {
		//If the entity isn't yet at the entrance, move towards it. If the entity is at the entrance, move right through it.
		
		//Check the orientation of the Entrance. If it is east/west, check the entity's y position. If it's above the entrance, moveTo the northernmost point.
		//If it's below the entrance (plus size), moveTo the southernmost. If it's between, move east/west.
		//Do the same with north/south and the x position.
		//Find the closest point in the entrance, then attempt to move to there.
		
		byte x = entity.getX();
		byte y = entity.getY();
		Coord2D ca = entrance.getLocA();
		Coord2D cb = entrance.getLocB();
		int minX;
		if (x<=ca.getX()) {
			minX = ca.getX();
		} else if (x>=cb.getX()) {
			minX = cb.getX();
		} else {
			minX = x;
		}
		
		int minY;
		if (y<ca.getY()) {
			minY = ca.getY();
		} else if (y>cb.getY()) {
			minY = cb.getY();
		} else {
			minY = y;
		}
		
		if (minX == x && minY == y) {
			//If the entity is at the entrance, move straight through it.
			move(entrance.getDirection(), entity, entity.getLocation());
		} else {
			//Otherwise move towards it
			moveTo(entity, new Coord3D(minX, minY, 0));
		}
	}
	public static void moveToPlayer(EntityTile entity) {
		moveTo(entity, self.getCoords());
	}
	
	public static void moveTo(EntityTile entity, Coord3D coords) {
		//Assuming it's all handled and the stuff is all valid
		
		//Get the relative differences. If abs(diffX)>=abs(diffY), try to move EAST or WEST depending on signum(diffX).
		int x = entity.getX()-coords.getX();
		int y = entity.getY()-coords.getY();
		
		if (Math.abs(x)>=Math.abs(y)) {
			if (x<0) {
				move(Direction.WEST, entity, entity.getLocation());
			} else {
				move(Direction.EAST, entity, entity.getLocation());
			}
		} else {
			if (y<0) {
				move(Direction.SOUTH, entity, entity.getLocation());
			} else {
				move(Direction.NORTH, entity, entity.getLocation());
			}
		}	
	}
	
	public static boolean pathfindToPlayer(EntityTile entity) {
		//If the entity is in the same room as the player, try to move towards the player.
		if (entity.getLocation() == self.getLocation()) {
			moveToPlayer(entity);
			return false;
		} else {
			//If the entity has a path to follow, follow it. Otherwise, create the path to the player.
			Location aimingFor = null;
			try {
				Path path = entity.getPath();
				aimingFor = path.get(0);
			} catch (NullPointerException e) {
				print("Path is null - creating it.");
				
				//Work out which linked location to aim for, and take the best route to there.
				Location currentLocation = entity.getLocation();
				
				//Depth-first search to get to the new room
				//Path is basically syntactic sugar for ArrayList<Location>, and a couple more methods
				LinkedList<Path> searchLocations = new LinkedList<Path>();
				for (Location loc : currentLocation.getAttached().keySet()) {
					searchLocations.add(new Path(loc));
				}
				//We have a list of paths to search - at first, it is a list of paths that are single-length starting at the entity's location.
				//For each path, we want to check the last index.
				Path path = searchLocations.pop();
				Location location = path.peekLast();
				Location target = self.getLocation();
				
				search:
				while (location != target) {
					//If it isn't, in a while loop, get all of the attachments of it, put them on the end of the list, and get the first one and do the same thing.
					//You already have the path to expand - expand it.
					for (Location attachedLoc : location.getAttached().keySet()) {
						if (attachedLoc.equals(self.getLocation())) {
							//If the location is found, break the loop.
							location = attachedLoc;
							path.add(attachedLoc);
							break search;
						} else {
							//Otherwise add the new path to the list of paths you are looking for.
							Path newPath = new Path(path);
							newPath.add(attachedLoc);
							searchLocations.addLast(newPath);
						}
					}
					//Then choose the next path on the list and end the while loop.
					path = searchLocations.pop();
					location = path.peekLast();
				}
				entity.setPath(path);
				aimingFor = path.get(0);
			}
			//Now pathfind to the entrance that takes you to that location.
			//Find the Entrance corresponding to that Location it wants to get to.
			Entrance entrance = entity.getLocation().findEntranceFor(aimingFor);
			if (entrance != null) {
				moveToEntrance(entity, entrance);
			} else {
				print("This should never be reached - here's a dump of all the relevant information.\nEntity:\n"+entity+"\nLocation:\n"+entity.getLocation()+"\nEntrance list:");
				for (Entrance entranceP : entity.getLocation().getAttached().values()) {
					print(entranceP);
				}
				print("\nLocation it is aiming for:\n"+aimingFor);
			}
			return false;
		}
	}
	
	public static Pair<Boolean, EntityTile> existsAtLoc(Location loc, byte xPos, byte yPos) {
		for (EntityTile entity : loc.getEntities()) {
			if (entity.getX()==xPos && entity.getY()==yPos) {
				return new Pair<Boolean, EntityTile>(true, entity);
			}
		}
		return new Pair<Boolean, EntityTile>(false, null);
	}
	private static Pair<Boolean, EntityTile> existsAtLoc(Location loc, short newxy) {
		return existsAtLoc(loc, (byte) (newxy & 0xff), (byte) (newxy >> 8));
	}
	
	public static Map initialiseMap(int x, int y, Map map, String type) {
		for (int j=1; j<(y-1); j++) {
			for (int i=1; i<(x-1); i++) {
				if (type.equals("empty")) {
					map.setTile(i, j, 0, tiles.get("Marble Floor"));					
				} else if (type.equals("full")) {
					map.setTile(i, j, 0, tiles.get("Marble Wall"));
				}
			}
		}
		for (int i=0; i<x; i++) {
			map.setTile(i, 0, 0, tiles.get("Marble Wall"));
			map.setTile(i, y-1, 0, tiles.get("Marble Wall"));
		}
		for (int j=0; j<y; j++) {
			map.setTile(0, j, 0, tiles.get("Marble Wall"));
			map.setTile(x-1, j, 0, tiles.get("Marble Wall"));
		}
		return map;
		
		//return mazeGen.generate(x, y);
	}
	
	public static void generateItems(String str) {
		System.out.print("Generating items... ");
		String[] strs = str.split("\n");
		for (int i=0; i<strs.length; i++) {
			while(i<strs.length && !strs[i].trim().equals("end")) {
				if (strs[i].trim().contains("item")) {
					Item item = new Item();
					while(!strs[i].trim().equals("end")) {
						strs[i] = strs[i].trim();
						if (strs[i].contains("name:")) {
							strs[i] = strs[i].replace("name: ", "");
							item.addName(strs[i]);
						}
						else if (strs[i].trim().contains("description:")) {
							strs[i] = strs[i].replace("description: ", "");
							item.addDescription(strs[i]);
						}
						else if (strs[i].contains("stats:")) {
							i++;
							while(!strs[i].trim().equals("end")) {
								strs[i] = strs[i].trim();
								String[] statStr = strs[i].split(": ");
								Pair<String, Integer> stat =
									new Pair<String, Integer>(statStr[0], Integer.parseInt(statStr[1]));
								item.addStat(stat);
								i++;
							}
						}
						i++;
					}/*
					try { entity.getColour().equals(null); } catch (NullPointerException e) {
						entity.setColour(Color.BLACK);
					}
					try { entity.getBGColour().equals(null); } catch (NullPointerException e) {
						entity.setBGColour(Color.WHITE);
						entity.setTransparency(true);
					}*/
					try {
						item.setImage(ImageIO.read(new File("images/items/"+item.getName()+".png")));
					} catch (IOException e) {
						e.printStackTrace();
						print("Cannot get image for "+item.getName());
					}
					items.put(item.getName(), item);
				}
				else if (strs[i].trim().equals("")) {
					i++;
				}
				else {
					print(
							"\"item\" tag not found, please add it before each new item. \n"
							+strs[i]);
					i++;
				}
			}
		}
		print("done.");
	}
	
	public static void generateEntities(String str) {
		System.out.print("Generating entities... ");
		String[] strs = str.split("\n");
		
		for (int i=0; i<strs.length; i++) {
			while(i<strs.length && !strs[i].trim().equals("end")) {
				if (strs[i].trim().contains("entity")) {
					Entity entity = new Entity();
					while(!strs[i].trim().equals("end")) {
						strs[i] = strs[i].trim();
						if (strs[i].contains("name:")) {
							strs[i] = strs[i].replace("name: ", "");
							entity.setName(strs[i]);
						}
						else if (strs[i].contains("stats:")) {
							i++;
							strs[i] = strs[i].trim();
							while(!strs[i].equals("end")) {
								String[] statSplit = strs[i].split(": ");
								String statName = statSplit[0];
								int stat = Integer.parseInt(statSplit[1]);
								entity.setStat(statName, stat);
								if (statName.equals("health")) {
									entity.setStat("max health", stat);
								}
								if (statName.equals("mana")) {
									entity.setStat("max mana", stat);
									print(entity.getName()+", "+entity.getBaseStat("max mana"));
								}
								i++;
							}
							if (!entity.hasStat("speed")) {
								entity.setStat("speed", 100);
							}
							if (!entity.hasStat("health")) {
								entity.setStat("health", 100);
								entity.setStat("max health", 100);
							}
							if (!entity.hasStat("mana")) {
								entity.setStat("mana", 0);
								entity.setStat("max mana", 0);
							}
						}
						i++;
					}
					try {
						entity.setImage(ImageIO.read(new File("images/entities/"+entity.getName()+".png")));
					} catch (IOException e) {
						e.printStackTrace();
						print(entity.getName());
					}
					entities.put(entity.getName(), entity);
				}
				else if (strs[i].trim().equals("")) {
					i++;
				}
				else {
					print(
						"\"entity\" tag not found, please add it before each new entity. \n"+strs[i]);
					i++;
				}
			}
		}
		print("done.");
	}
	
	public static void generateSpells(String str) {
		System.out.print("Generating spells... ");
		String[] strs = str.split("\n");
		for (int i=0; i<strs.length; i++) {
			while(i<strs.length && !strs[i].trim().equals("end")) {
				if (strs[i].trim().contains("spell")) {
					Spell spell = new Spell();
					while(!strs[i].trim().equals("end")) {
						i++;
						strs[i] = strs[i].trim();
						if (strs[i].contains("name:")) {
							strs[i] = strs[i].replace("name: ", "");
							spell.setName(strs[i]);
						}
						else if (strs[i].contains("description:")) {
							strs[i] = strs[i].replace("description: ", "");
							spell.setDescription(strs[i]);
						}
					}
				}
				i++;
			}
		}
		print("done.");
	}
	
	public static void generateMaterials(String str) {
		System.out.print("Generating materials... ");
		BufferedImage upStairs = null;
		BufferedImage downStairs = null;
		BufferedImage upDownStairs = null;
		BufferedImage pillar = null;
		try {
			upStairs = ImageIO.read(new File("images/masks/Up.png"));
			downStairs = ImageIO.read(new File("images/masks/Down.png"));
			upDownStairs = ImageIO.read(new File("images/masks/Stairs.png"));
			pillar = ImageIO.read(new File("images/masks/Pillar.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
			print("One or more masks not present; please check you have \"Up.png\", \"Down.png\", \"Stairs.png\" and \"Pillar.png\" in your images folder");
		}
		Pattern pattern = Pattern.compile("material:\n((?:.+\n)+)end");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String name;
			String mat = matcher.group(1);
			//One-liner equivalent to pattern/matcher above
			Matcher nMatch = Pattern.compile("name: (.+)").matcher(mat);
			if (nMatch.find()) {
				name = nMatch.group(1);
				print("Name: "+name);
				String filepath = "images/materials/"+name+".png";
				BufferedImage img = null;
				BufferedImage floorImg = null;
				BufferedImage upImg = null;
				BufferedImage downImg = null;
				BufferedImage upDownImg = null;
				BufferedImage pillarImg = null;
				try {
					img = ImageIO.read(new File(filepath));
					upImg = ImageIO.read(new File(filepath));
					downImg = ImageIO.read(new File(filepath));
					upDownImg = ImageIO.read(new File(filepath));
					pillarImg = ImageIO.read(new File(filepath));
					float scaleFactor = 0.7f;
					RescaleOp op = new RescaleOp(scaleFactor, 0, null);
					floorImg = op.filter(img, null);
					upImg = op.filter(img, null);
					downImg = op.filter(img, null);
					upDownImg = op.filter(img, null);
					//pillarImg = op.filter(img, null);
					Graphics2D upG = upImg.createGraphics();
					Graphics2D downG = downImg.createGraphics();
					Graphics2D upDownG = upDownImg.createGraphics();
					Graphics2D pillarG = pillarImg.createGraphics();
					upG.drawImage(upStairs, null, 0, 0);
					downG.drawImage(downStairs, null, 0, 0);
					upDownG.drawImage(upDownStairs, null, 0, 0);
					pillarG.drawImage(pillar, null, 0, 0);
				} catch (IOException e) {
					e.printStackTrace();
					print("Path of invalid material: "+filepath);
				}
				
				tiles.put(name+" Wall", new TileType(name+" Wall", img, false, null, null));
				tiles.put(name+" Floor", new TileType(name+" Floor", floorImg, true, null, null));
				tiles.put(name+" Upward Stairway", new TileType(name+" Upward Stairway", upImg, true, null, null));
				tiles.put(name+" Downward Stairway", new TileType(name+" Downward Stairway", downImg, true, null, null));
				tiles.put(name+" Up/Down Stairway", new TileType(name+" Up/Down Stairway", upDownImg, true, null, null));
				tiles.put(name+" Upward Slope", new TileType(name+" Upward Slope", img, true, null, null));
				tiles.put(name+" Downward Slope", new TileType(name+" Downward Slope", img, true, null, null));
				tiles.put(name+" Pillar", new TileType(name+" Pillar", pillarImg, true, null, null));
			} else {
				print("Material name not found, please check syntax");
			}
		}
	}
	
	/*public static void generateMaps(String str) {
		System.out.print("Generating maps... ");
		//TODO - make something like the TARDIS, specifically, the star in the middle.
		//Make it gigantic, but able to be walked around in an average room
		//Something like
		/* |||||||||||||||||||||
		 * |                   |
		 * |                   |
		 * |     |||-|||       |
		 * |     |     |       |
		 * |     -     -       |
		 * |     |     |       |
		 * |     |||-|||       |
		 * |                   |
		 * |||||||||||||||||||||
		 * Where each - is a door, and the four doors lead to different areas in a vast expanse
		 */
		//
		/*
		String[] strs = str.split("\n");
		for (int i=0; i<strs.length; i++) {
			strs[i] = strs[i].trim();
			if (strs[i].startsWith("map:")) {
				Map map = new Map(dx, dy, dz, tiles.get("Marble Wall"));
				while (!strs[i].trim().equals("end")) {
					if (strs[i].startsWith("name:")) {
						String name = strs[i].substring(6);
						map.setName(name);
					}
					else if (strs[i].startsWith("entities:")) {
						i++;
						while (!strs[i].trim().equals("end")) {
							String[] entInfo = strs[i].split(": ");
							Entity entity = entities.get(entInfo[0]);
							String[] coords = entInfo[1].split(", ");
							map.addEntity(new EntityTile(entity, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), 0));
							i++;
						}
					}
					else if (strs[i].startsWith("rooms:")) {
						i++;
						while (!strs[i].trim().equals("end")) {
							Pattern roomPattern = Pattern.compile("(.*, )*(.*)");
							Matcher roomMatcher = roomPattern.matcher(strs[i]);
							String[] roomInfo = null;
							boolean hasFlag = false;
							if (roomMatcher.matches()) {
								if (roomMatcher.group(2).matches("(?:\\d)*")) {
									roomInfo = roomMatcher.group().split(", ");
								} else {
									roomInfo = roomMatcher.group(1).split(", ");
									hasFlag = true;
								}
							} else {
								print("Error: Room matching failed. Should contain only numbers separated by\n" +
										"a comma and space, with an optional 'w' at the end. This will probably crash now.");
							}
							if (roomInfo.length==3) {
								Room room = new Room(Integer.parseInt(roomInfo[0]), Integer.parseInt(roomInfo[1]), Integer.parseInt(roomInfo[2]), Integer.parseInt(roomInfo[2]));
								if (hasFlag) {
									switch(roomMatcher.group(2)) {
									case "w":
										map.addRoom(room, true);
										break;
										default:
											map.addRoom(room);
									}
								} else {
									map.addRoom(room);
								}
							}
							else if (roomInfo.length==4) {
								Room room = new Room(Integer.parseInt(roomInfo[0]), Integer.parseInt(roomInfo[1]), Integer.parseInt(roomInfo[2]), Integer.parseInt(roomInfo[3]));
								if (hasFlag) {
									switch(roomMatcher.group(2)) {
									case "w":
										map.addRoom(room, true);
										break;
										default:
											map.addRoom(room);
									}
								} else {
									map.addRoom(room);
								}
							}
							i++;
						}
					}
					else if (strs[i].startsWith("walls:")) {
						i++;
						while (!strs[i].trim().equals("end")) {
							Pattern wallPattern = Pattern.compile("(.*, )*(.*)");
							Matcher wallMatcher = wallPattern.matcher(strs[i]);
							String[] wallInfo = strs[i].split(", ");
							if (!wallMatcher.matches()) {
								print("Error: Wall matching failed. Should contain only numbers separated by\n" +
										"a comma and space. This will probably crash now.");
							}
							if (wallInfo.length==3) {
								wallify(wallInfo[0], wallInfo[0], wallInfo[1], wallInfo[2], map);
							}
							else if (wallInfo.length==4) {
								wallify(wallInfo[0], wallInfo[1], wallInfo[2], wallInfo[3], map);
							}
							i++;
						}
					}
					else if (strs[i].startsWith("controlled entity:")) {
						String entityName = strs[i].trim().replace("controlled entity: ", "");
						for (EntityTile entity : map.getEntities()) {
							if (entity.getEntity().getName().equals(entityName) && self==null) {
								self = entity;
							}
							else if (entity.getEntity().getName().equals(entityName) && self!=null) {
								print("Only one \"controlled entity\" tag is allowed per file.\n" +
										"Please remove any others.");
							}
						}
						if (self==null) {
							print("Entity to control ("+entityName+")cannot be found, please check spelling.\n" +
									"If you can't find the problem, let me know and show me your thingy file.");
							//TODO - name file appropriately
						}
					}
					else if (strs[i].startsWith("items:")) {
						i++;
						while (!strs[i].trim().equals("end")) {
							String[] itemInfo = strs[i].split(": ");
							Item item = items.get(itemInfo[0]);
							String[] coords = itemInfo[1].split(", ");
							map.addItem(new ItemTile(item, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), 0));
							i++;
						}
					}
					else if (strs[i].startsWith("exits:")) {
						i++;
						while(!strs[i].trim().equals("end")) {
							String[] exitInfo = strs[i].split(" ");
							String[] coordData = exitInfo[0].split(",");
							String destMap = exitInfo[1];
							String destDir = exitInfo[2];
							String[] destCoordData = exitInfo[3].split(",");
							Point2D exitCoords = new Point2D.Double(Integer.parseInt(coordData[0]), Integer.parseInt(coordData[1]));
							Point2D destCoords = new Point2D.Double(Integer.parseInt(destCoordData[0]), Integer.parseInt(destCoordData[1]));
							//Link exit = new Link(map.getName(), destMap, destCoords, (char) destDir.charAt(0));
							//map.addExit(exit, exitCoords);
							//exit.getDestinationMap();
							//print(exit.toString());
							i++;
							//TODO Each map should have a list of other maps that it links to. It already has a list of Links I think? So just go through these Links once all Maps are generated,
							//and check if the destination Map is valid. If it is, stick it on a list inside the Map for faster access when it comes to going through the Link.
						}
					}
					i++;
				}
				maps.put(map.getName(), map);
			}
			else if (strs[i].startsWith("links:")) {
				Pattern linkPattern = Pattern.compile("\\[(\\d+)\\D*(\\d+)\\] ?(\\w+) (<>[<&&>]) \\[(\\d+)\\D*(\\d+)\\] ?(\\w+)");
				while(!strs[i].trim().equals("end")) {
					Matcher linkMatcher = linkPattern.matcher(strs[i]);
					if (linkMatcher.find()) {
						
					} else {
						System.out.println(strs[i]+" did not match the expected pattern. Should be of the form\n" +
								"[34,25]mapname1 < [45,26]mapname2\n" +
								"You can replace the commas with any non-digit character, but the square brackets must enclose the coordinates." +
								"There can be an optional space between the coords and the map name, but the space between the name and the link direction (and the link and coords) is essential." +
								"The link direction is either <, > or <>. < means you can travel 1-way to the first set, > is 1-way FROM the 1st set, <> is in either direction." +
								"Finally, the map names can only contain alphanumeric characters, i.e. a-z A-Z 0-9. No spaces, underscores, punctuation or anything.");
					}
					
				} 
			}
			else if (strs[i].startsWith("initial map:")) {
				cloc = maps.get(strs[i].replace("initial map: ", ""));
				for (EntityTile entity : cloc.getEntities()) {
					unfrozenEntities.put(entity, entity.getTicks());
				}
			}
		}
		for (Map map : maps.values()) {
			//initialiseMap(dx, dy, map, "full");
			for (Entry<Link, Point2D> exitInfo : map.getExits().entrySet()) {
				print(exitInfo.toString());
			}
		}
		print("done.");
	}*/
	
	/*public static void drawMap(Map map) {
		int xDim = map.getDimX();
		int yDim = map.getDimY();
		//TODO generalise
		int zDim = map.getDimZ();
		int z = zDim;
		
		
		char[][][] charmap = new char[xDim][yDim][zDim];
		for (int y=0; y<yDim; y++) { 
			for (int x=0; x<xDim; x++) {
				charmap[x][y][z] = map.getTile(x, y, z).getMarker();
			}
		}

		for (ItemTile item:map.getItems()) {
			charmap[item.getX()][item.getY()][item.getZ()] = item.getItem().getMarker();
		}
		
		for (EntityTile entity:map.getEntities()) {
			charmap[entity.getX()][entity.getY()][entity.getZ()] = entity.getEntity().getMarker();
		}
		
		if (map.getCount("Player")>0) {
			ArrayList<EntityTile> entities = map.getEntities();
			for (EntityTile entity:entities) {
				if (entity.getEntity().getName()=="Player") {
					charmap[entity.getX()][entity.getY()][entity.getZ()] = entity.getEntity().getMarker();			
				}
			}	
		}
		
		for (int y=0; y<yDim; y++) {
			for (int x=0; x<xDim; x++) { 
				System.out.print(charmap[x][y]);
			}
			print();
		}
		
//		g.update(map);
	}*/
	
	private static void generatePlayer(String str) {
		System.out.print("Generating player... ");
		
		String[] strs = str.split("\n");
		Entity player = new Entity();
		player.setName("Player");
		for (int i=0; i<strs.length; i++) {
			strs[i] = strs[i].trim();
			if (strs[i].startsWith("name:")) {
				strs[i] = strs[i].replace("name: ", "");
				player.setFeatures(new String[]{"Player name: ".concat(strs[i])});
			}
			else if (strs[i].startsWith("stats:")) {
				i++;
				while(!strs[i].trim().equals("end")) {
					strs[i] = strs[i].trim();
					String[] statSplit = strs[i].split(": ");
					String statName = statSplit[0];
					int stat = Integer.parseInt(statSplit[1]);
					player.setStat(statName, stat);
					i++;
				}
				player.setStat("speed", 100);
				player.setStat("max health", player.getBaseStat("health"));
				player.setStat("max mana", player.getBaseStat("mana"));
			}
		}
		try {
			player.setImage(ImageIO.read(new File("images/entities/Dwarf.png")));
		} catch (IOException e) {
			e.printStackTrace();
			print("Player");
		}
		entities.put("Player", player);
		print("done.");
	}
	
	private static void fight(EntityTile ent1, EntityTile ent2) {
		int str1 = ent1.getStrength()+random.nextInt(20)-10;
		int str2 = ent2.getStrength()+random.nextInt(20)-10;
		
		print(ent1.getName()+" strength: "+str1+"\n"+
				ent2.getName()+" strength: "+str2);
		if (str1>str2) {
			boolean kill = dealDamage(ent1, ent2, str1-str2);
			if (kill) {
				boolean killed = cloc.removeEntity(ent2);
				unfrozenEntities.remove(ent2);
				print(killed);
			}
		}
		else if (str2>str1) {
			boolean kill = dealDamage(ent2, ent1, str2-str1);
			if (kill) {
				boolean killed = cloc.removeEntity(ent1);
				unfrozenEntities.remove(ent1);
				print(killed);
			}
		}
		else {
			System.out.print("Nobody takes damage: ");
			if (ent1.equals(self)) {
				System.out.print("You have ");
			} else {
				System.out.print("The "+ent1.getName()+" has ");
			}
			print(ent1.getHealth()+" health left, and ");
			if (ent2.equals(self)) {
				System.out.print("you have ");
			} else {
				System.out.print("the "+ent2.getName()+" has ");
			}
			print(ent2.getHealth()+".\n");
		}
	}
	
	private static boolean dealDamage(EntityTile attacker, EntityTile defender, int diff) {

		int hp = defender.getHealth();
		hp-=diff;
		
		boolean isDef = defender.equals(self);
		boolean isAtt = attacker.equals(self);
		String nameDef = defender.getName();
		String nameAtt = attacker.getName();
		
		defender.setStat("health", hp);
		
		//Damage dealer
		if (isAtt)
			System.out.print("You overpower ");
		else
			System.out.print("The "+nameAtt+" overpowers ");
		
		//Damage taker
		if (isDef)
			print("you!");
		else
			print("the "+nameDef+"!");
		
		//Damage dealt
		if (isAtt)
			System.out.print("You deal ");
		else
			System.out.print("The "+nameAtt+" deals ");
		print(diff+" damage!");
		
		//If defender dies
		if (defender.getHealth()<=0) {
			if (isDef)
				System.out.print("You die!\n");
			else
				print("The "+nameDef+" dies!\n");
			return true;
		}
		else {
			if (isDef)
				System.out.print("You now have ");
			else
				System.out.print("The "+nameDef+" now has ");
			print(defender.getHealth()+" health.\n");
		}
		return false;
	}
	
	public static boolean dealDamage(EntityTile damaged, int amount) {
		int health = damaged.getHealth();
		health-=amount;
		damaged.setStat("health", health);
		if (health<=0) {
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("unused")
	private static Color parseColour(String str, Color colour) {
		switch (str.toLowerCase()) {
		case "blue":
			colour = Color.BLUE;
			break;
		case "black":
			colour = Color.BLACK;
			break;
		case "cyan":
			colour = Color.CYAN;
			break;
		case "darkgray": case "dark gray": case "dark_gray":
		case "darkgrey": case "dark grey": case "dark_grey":
			colour = Color.DARK_GRAY;
			break;
		case "gray": case "grey":
			colour = Color.GRAY;
			break;
		case "green":
			colour = Color.GREEN;
			break;
		case "lightgray": case "light gray": case "light_gray":
		case "lightgrey": case "light grey": case "light_grey":
			colour = Color.LIGHT_GRAY;
			break;
		case "magenta":
			colour = Color.MAGENTA;
			break;
		case "orange":
			colour = Color.ORANGE;
			break;
		case "pink":
			colour = Color.PINK;
			break;
		case "red":
			colour = Color.RED;
			break;
		case "white":
			colour = Color.WHITE;
			break;
		case "yellow":
			colour = Color.YELLOW;
			break;
		default:
			if (str.startsWith("dec")) {
				String[] rgb = str.replace("dec(", "").replace(")", "").split(", ");
				print("Found decimal string, trying to use colour. "+rgb[0]+" "+rgb[1]+" "+rgb[2]);
				colour = new Color(Integer.parseInt(rgb[0]),
						Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
			}
			else if (str.startsWith("hex")) {
				String[] rgb = str.replace("hex(", "").replace(")", "").split(", ");
				colour = new Color(Integer.parseInt(rgb[0], 16),
						Integer.parseInt(rgb[1], 16), Integer.parseInt(rgb[2], 16));
			} else if (str.startsWith("#")) {
				str = str.substring(1);
				if (!str.matches("[0-9a-fA-F]{6}")) {
					print(str+" is not valid hex code.");
				} else {
					String[] rgbS = str.split("(?<=\\G..)");
					int[] rgb = new int[3];
					for (int i=0; i<3; i++) {
						rgb[i] = Integer.parseInt(rgbS[i], 16);
					}
					colour = new Color(rgb[0], rgb[1], rgb[2]);
				}
			} else {
				print("Invalid colour "+str+", keeping default one.");
			}
		}
		return colour;
	}
	
	public static int getPX() {
		return self.getX();
	}
	
	public static int getPY() {
		return self.getY();
	}
	
	public static int getPZ() {
		return self.getZ();
	}
	
	public static void print(String string) {
		System.out.println(string);
	}
	
	public static void print(Boolean bool) {
		System.out.println(bool);
	}
	
	public static void print(Exception e) {
		System.out.println(e);
	}
	
	public static void print(char c){
		System.out.println(c);	
	}
	
	public static void print(Object o) {
		System.out.println(o);
	}
	
	//TODO - put this and other things in a GameUtilities class
	public static void floorify(int dimX, int dimY, int centreX, int centreY, Map map) {
		double radiusX = (dimX-1.0)/2.0;
		int lowRadX = (int) Math.floor(radiusX);
		int highRadX = (int) Math.ceil(radiusX);
		
		double radiusY = (dimY-1.0)/2.0;
		int lowRadY = (int) Math.floor(radiusY);
		int highRadY = (int) Math.ceil(radiusY);

		for (int j = centreY-lowRadY; j <= centreY+highRadY; j++) {
			for (int i = centreX-lowRadX; i<=centreX+highRadX; i++) {
				String name = map.getTile(i, j, 0).getType().getName().replaceAll("Floor|Wall|Up Stair|Down Stair|Up/Down Stair", "Floor");
				TileType tile = tiles.get(name);
				map.setTile(i, j, 0, tile);
			}
		}
	}
	
	public static void wallify(int dimX, int dimY, int centreX, int centreY, Map map) {
		double radiusX = (dimX-1.0)/2.0;
		int lowRadX = (int) Math.floor(radiusX);
		int highRadX = (int) Math.ceil(radiusX);
		
		double radiusY = (dimY-1.0)/2.0;
		int lowRadY = (int) Math.floor(radiusY);
		int highRadY = (int) Math.ceil(radiusY);

		for (String tileName : tiles.keySet()) {
			print(tileName);
		}
		for (int j = centreY-lowRadY; j <= centreY+highRadY; j++) {
			for (int i = centreX-lowRadX; i<=centreX+highRadX; i++) {
				String name = map.getTile(i, j, 0).getType().getName().replaceAll("Floor|Wall|Up Stair|Down Stair|Up/Down Stair", "Wall");
				name = "Marble Up/Down Stairway";
				TileType tile = tiles.get(name);
				map.setTile(i, j, 0, tile);
			}
		}
	}
	
	public static void wallify(String a, String b, String c, String d, Map map) {
		wallify(Integer.parseInt(a), Integer.parseInt(b), Integer.parseInt(c), Integer.parseInt(d), map);
	}
	
	public static void surround(int dimX, int dimY, int centreX, int centreY, Map map) {
		double radiusX = dimX/2;
		int lowRadX = (int) Math.floor(radiusX)+1;
		int highRadX = (int) Math.ceil(radiusX)+1;

		double radiusY = dimY/2;
		int lowRadY = (int) Math.floor(radiusY)+1;
		int highRadY = (int) Math.ceil(radiusY)+1;
		
		for (int j=centreY-lowRadY; j<=centreY+highRadY; j++) {
			int[] xArr = {centreX-lowRadX, centreX+highRadX};
			for (int i : xArr) {
				String name = map.getTile(i, j, 0).getType().getName().replace("Floor|Wall|Up Stairs|Down Stairs|Up/Down Stairs", "Wall");
				TileType tile = tiles.get(name);
				map.setTile(i, j, 0, tile);				
			}
		}
		for (int i = centreX-lowRadX; i<=centreX+highRadX; i++) {
			int[] yArr = {centreY-lowRadY, centreY+highRadY};
			for (int j : yArr) {
				String name = map.getTile(i, j, 0).getType().getName().replace("Floor|Wall|Up Stairs|Down Stairs|Up/Down Stairs", "Wall");
				TileType tile = tiles.get(name);
				map.setTile(i, j, 0, tile);				
			}
		}
	}
	
	public static void attachTwoLocations(Location loc1, Location loc2, Entrance entrance) {
		//First, check if they are allowed to have a connection (if they're aligned right). If so, create the connection.
		//Creating: Take the Direction, and place it onto loc1 in that direction, with the second in the Triplet as the location, third width.
		//Then, reverse the Direction, calculate the relative 2nd point on loc2 (relative to loc1) and put it in that too.
		
		//In order to be a valid connection, the other location must be in an appropriate place. take the relative x and y coords, and check the entrance's direction.
		int relX = loc2.getX()-loc1.getX();
		int relY = loc2.getY()-loc1.getY();
		Coord2D locA = entrance.getLocA();
		Coord2D locB = entrance.getLocB();
		Direction dir = entrance.getDirection();
		boolean cond1;
		boolean cond2;
		boolean cond3;
		//TODO - implement cond4, which is true iff the first location is wide enough for the entrance
		//TODO - generalise cond1, 2, 3 and 4 so I don't need different things for each direction
		boolean cond4;
		Coord2D entrA;
		Coord2D entrB;
		Entrance entrance2 = new Entrance();
		switch(dir) {
		case NORTH:
			cond1 = (-relY==loc2.getH());
			cond2 = (relX < locA.getX());
			cond3 = (relX+loc2.getW() > locB.getX());
			entrA = new Coord2D(entrance.getLocA().getX()-relX, loc2.getH()-1);
			entrB = new Coord2D(entrance.getLocB().getX()-relX, loc2.getH()-1);
			break;
		case SOUTH:
			cond1 = (relY==loc1.getH());
			cond2 = (relX < locA.getX());
			cond3 = (relX+loc2.getW() > locB.getX());
			entrA = new Coord2D(entrance.getLocA().getX()-relX, 0);
			entrB = new Coord2D(entrance.getLocB().getX()-relX, 0);
			break;
		case EAST:
			cond1 = (-relX==loc2.getW());
			cond2 = (relY < locA.getY());
			cond3 = (relY+loc2.getH() > locB.getY());
			entrA = new Coord2D(loc2.getW()-1, entrance.getLocA().getY()-relY);
			entrB = new Coord2D(loc2.getW()-1, entrance.getLocB().getY()-relY);
			break;
		case WEST:
			cond1 = (relX==loc1.getW());
			cond2 = (relY < locA.getY());
			cond3 = (relY+loc2.getH() > locB.getY());
			entrA = new Coord2D(0, entrance.getLocA().getY()-relY);
			entrB = new Coord2D(0, entrance.getLocB().getY()-relY);
			break;
		default:
			cond1 = false;
			cond2 = false;
			cond3 = false;
			entrA = null;
			entrB = null;
		}
		if (cond1 && cond2 && cond3) {
			entrance2.setInfo(entrance.getDirection().getOppositeDirection(), entrA, entrB);
			entrance.setLinkedEntrance(entrance2);
			entrance2.setLinkedEntrance(entrance);
			loc1.addAttachment(loc2, entrance);
			//Modify the Entrance here to contain the relative location
			loc2.addAttachment(loc1, entrance2);
			print("Valid connection between\n"+loc1+"\nand\n"+loc2);
			//TODO next time - figure out why all entrances created are valid, but only the one from one Room to another gets drawn
		} else {
			String printStr = "Invalid connection between\n"+loc1+"\nand\n"+loc2+"\nfrom entrance\n"+entrance+"\nbecause: ";
			char direc = dir.getDirectionality();
			char oppDirec = dir.getDirectionality()=='X' ? 'Y' : 'X';
			String sign = (dir==Direction.SOUTH || dir==Direction.WEST) ? "negative " : "positive ";
			String oppSign = (dir==Direction.SOUTH || dir==Direction.WEST) ? "positive " : "negative ";
			if (!cond1) {
				printStr += "the locations were not aligned in the "+direc+" direction.";
			} else if (!cond2) {
				printStr += "the first location is too far in the "+sign+oppDirec+" direction.";
			} else if (!cond3) {
				printStr += "the first location is too far in the "+oppSign+oppDirec+" direction.";
			} else {
				printStr += "something completely weird went on and I don't know what. The locations are as follows: "+loc1.toString()+", and "+loc2.toString()+". The entrance is "+entrance.toString();
			}
			print(printStr);
			System.exit(1);
		}
	}
}