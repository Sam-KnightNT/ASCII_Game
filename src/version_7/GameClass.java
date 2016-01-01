package version_7;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.*;
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
	static Dungeon dungeon;
	static HashMap<String, Map> maps = new HashMap<String, Map>();
	//static HashMap<String, Location> rooms = new HashMap<String, Location>();
	static HashMap<String, TileType> tiles = new HashMap<String, TileType>();
	static Random random = new Random();
	//TODO - Remove all static modifiers, since they should be unique to each GameClass
	private static GameImage mainImage;
	//private static InfoPanel infoPanel;
	private final static JFrame frame = new JFrame();
	static int playerIndex;
	public static EntityTile self;
	private static Location cloc;
	private static boolean AIon = false;
	private static TreeMap<EntityTile, Integer> unfrozenEntities = new TreeMap<EntityTile, Integer>();
	private static BufferedImage bottle;
	private static BufferedImage liquid;
	private static BufferedImage shine;
	
	private static boolean debug = true;
	
	//Where the player is initially
	static int px = 1;
	static int py = 3;
	static int pz = 0;
	
	//Dimensions of maps
	static int dx;
	static int dy;
	static int dz;
	
	//Have you lost your way?
	static boolean between = false;
	/*
	 * This series of comments marks what should be done in v.03.07. 
	 * Currently, Potions seem to be working fine, at least, single ones do. I'm sick of them though, so I'm going to leave multi-fluid Potions until later.
	 * I've decided to reimplement Maps as a 256x256 chunk of Tiles, which contain the list of Rooms that are currently the entire world. More details are in the Map class.
	 * v.03.06 is done - the numbers are a bit arbitrary, but I know this is the third major revision, and there have been about 6 minor ones in this.
	 * Once v.03.07 is done, v.03.08 should begin - all this will do is allow Maps to be side-by-side and make multi-fluid Potions work. Then, that should be it for v.03, it should then be v.04.
	 * 
	 * The end of this version should contain:
	 * 	A Dungeon prototype, which contains at least 2 fully-working Maps.
	 * 	These Maps should have been generated with Dungen Alpha, should be fully traversible and should be connected by some arbitrary Room, with up/down stairs.
	 * 
	 * v.04 should work towards:
	 * 	Populating these Maps with Entities and Items
	 * 	Adding menus and stats
	 * 	Possible adding saving/loading
	 * 	Basically, actually building a game round all this stuff.
	 * 
	 * v.05 should work on combat and abilities, so the game is more interesting and properly playable. 
	 * 
	 * After v.05 is done, there might be a public release. Either v.04 or v.05 should deal with making the Dungen make a proper dungeon-like thing and adding a story and such.
	 * v.06 or v.07 onwards should probably be bugfix and balance tweaking. The main game should be done by then. After that, I can release Dungeon of the Three, Version 1 - AKA Tales of Three I: Dot3. After that (or after some more content updates, depending on how much I get done), begin work on Tales of Three II: Godfall or whatever it'll be called.
	 * 
	 * 
	 * TODO - redo this. Probably v0.something should have a test map, v0.s+1 implements the combat system, s+2 adds enemies, items and a progression, s+3 is the full map with events, s+4 has points and a menu, and s+5 is the full game (with different weapon progressions).
	 * 
	 * 
	 * 
	 * Roadmap for To3: Arena
	 * v0.01: Drawing player on map, in ASCII
	 * v0.02: Drawn on Swing graphics
	 * v0.03: Changed into non-ASCII, instead using map tiles
	 * v0.04: Created other entities
	 * v0.05: Added basic combat system and pathfinding
	 * v0.06: Created info panel, with info on combat and such (where I am now)
	 * v0.07: Completed complex combat system
	 * v0.08: Working combat panel, flavour text and swipe previews
	 * v0.09: Completed tutorial
	 * v0.10: Completed arena/textures
	 * v1.00: Progression, scores 
	 * 
	 * Details on v0.07
	 * There should be new controls. If more than one enemy is standing next to you, 1-9 points at them.
	 * Shift+QWEDCXZA does the already-mentioned swipey things, which compares the enemy's defence against that particular attack to the player's attack strength.
	 * Hitting some button ('s' for stance probably) opens a panel in front of the main view (this'll be a third pane) that can be dismissed with Esc.
	 * It shows a menu - several numbers with stances written beside, such as "two-handed overhead". One (your current stance) is highlighted.
	 * Pressing numbers highlights that stance, and whichever stance is highlighted has flavour text beside it.
	 * For example, two-handed is "Allows devastating downward smashes, but little in the way of other attacks and weak defences."
	 * There could be different stances for different weapon combos later down the line - sword and shield, for example, or polearm.
	 * Also, don't forget the possibility of using, say, WW as a stab, or S- as a stab to the middle then a slice outwards.
	 */
	public static void main(String[] args) throws Exception {
		readFromFile();
		
		BufferedReader readIn = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		
		createBetweenFord();
		
		Map map1 = new Map(54, 54, 0, 0, 0, tiles.get("Sandstone Brick 2 Floor"));
		Map map2 = new Map(100, 100, 43, -50, 0, tiles.get("Marble Floor"));
		
		//Construct 2 rooms and a corridor to connect them
		Room room1 = new Room(9, 11, 11, 20, tiles.get("biggold Floor"), tiles.get("biggold Wall"));
		Room room2 = new Room(7, 8, 1, 21, tiles.get("biggold Floor"), tiles.get("biggold Wall"));
		Room room3 = new Room(12, 8, 9, 14, tiles.get("biggold Floor"), tiles.get("biggold Wall"));
		Room room4 = new Room(7, 8, 20, 22, tiles.get("Iron 6 Floor"), tiles.get("Iron 6 Wall"));
		Room room5 = new Room(24, 12, 2, 2, tiles.get("biggrass Floor"), tiles.get("biggrass Wall"));
		
		room1.setName("Room 1");
		room2.setName("Room 2");
		room3.setName("Room 3");
		room4.setName("Room 4");
		room5.setName("Room 5");
		
		//Add Minotaur
		createEntity("Minotaur", map1, 1, 1, 0, 100);
		
		
		room2.addItem(new ItemTile(items.get("Pick of Destiny"), (byte) 3, (byte) 5, (byte) 0));
		
		//room.addItem(new ItemTile(items.get("Boots of Want to Get Over There Right Now"), 4, 5, 0));
		bottle = ImageIO.read(new File("images/items/Potion Bottle.png"));
		liquid = ImageIO.read(new File("images/items/Potion Liquid.png"));
		shine = ImageIO.read(new File("images/items/Potion Shine.png"));
		
		BufferedImage mPot = createPotionGraphics(new Color(10, 120, 255));
		
		BufferedImage hPot = createPotionGraphics(new Color(255, 15, 20));
		
		Potion healthPot = new Potion(new LiquidPure(new LiquidType("health", "Health Fluid"), 35.0, 500), new Bottle(1000));
		healthPot.setImage(hPot);
		Potion manaPot = new Potion(new LiquidPure(new LiquidType("mana", "Mana Fluid"), 35.0, 500), new Bottle(1000));
		manaPot.setImage(mPot);
		room1.addItem(new ItemTile(healthPot, 4, 5, 0));
		room1.addItem(new ItemTile(manaPot, 6, 5, 0));
		
		room5.addItem(new ItemTile(healthPot, 3, 3, 0));
		
		room3.fill(2, 0, 4, 0, tiles.get("biggold Floor"));
		room3.fill(11, 3, 11, 5, tiles.get("biggold Floor"));
		room3.fill(4, 7, 5, 7, tiles.get("biggold Floor"));
		room5.fill(9, 11, 11, 11, tiles.get("biggrass Floor"));
		room5.fill(23, 5, 23, 8, tiles.get("biggrass Floor"));
		room5.fill(4, 0, 7, 0, tiles.get("biggrass Floor"));
		room5.fill(0, 4, 0, 6, tiles.get("biggrass Floor"));
		room4.fill(1, 0, 4, 0, tiles.get("Iron 6 Floor"));
		room4.fill(0, 4, 0, 6, tiles.get("Iron 6 Floor"));
		room2.fill(6, 3, 6, 4, tiles.get("biggold Floor"));
		room1.fill(0, 4, 0, 5, tiles.get("biggold Floor"));
		room1.fill(8, 6, 8, 8, tiles.get("Iron 6 Floor"));
		

		map1.draw(7, 23, 10, 26, tiles.get("biggold Wall"));
		map1.fill(7, 24, 10, 25, tiles.get("biggold Floor"));
		
		room1.setTile(2, 2, tiles.get("biggold Pillar"), false);
		room1.setTile(2, 8, tiles.get("biggold Pillar"), false);
		room1.setTile(6, 2, tiles.get("biggold Pillar"), false);
		room1.setTile(6, 8, tiles.get("biggold Pillar"), false);
		room1.setTile(1, 3, tiles.get("biggold Downward Stairway"));
		room1.setTile(1, 5, tiles.get("biggold Upward Stairway"));
		room1.setTile(1, 7, tiles.get("biggold Up/Down Stairway"));
		map1.addRoom(room1);
		map1.addRoom(room2);
		map1.addRoom(room3);
		map1.addRoom(room4);
		map1.addRoom(room5);
		
		map1.setName("Map 1");
		
		cloc = map1;
		maps.put("Map 1", map1);
		maps.put("Map 2", map2);
		dungeon = new Dungeon();
		dungeon.setName("Default dungeon");
		//dungeon.addMap(maps.get("Between Ford"), maps.get("Between Ford").getPosition());
		dungeon.addMap(map1, map1.getPosition());
		dungeon.addMap(map2, map2.getPosition());
		
		/*
		rooms.put("Room 1", room);
		rooms.put("Room 2", room2);
		rooms.put("Room 3", room3);
		rooms.put("Room 4", room4);
		rooms.put("Room 5", room5);
		rooms.put("Corridor 1", corridor1);
		rooms.put("Corridor 2", corridor2);
		
		cloc = rooms.get("Room 1");*/
		self = new EntityTile(entities.get("Player"), cloc, (byte) 5, (byte) 5, (byte) 0, null);
		
		initialiseMainImage();
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
	 					for (EntityTile entityN : cloc.getEntities()) {
	 						print(entityN.getName());
	 					}
	 				}
				}
				else if (!self.hasStat("mana")) 		{print("Undefined mana");}
				else if (!self.hasStat("magic power")) 	{print("Undefined magic power");}
				else if (self.getStat("mana")<=0) 		{print("Out of mana!");}
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
	
	private static void createBetweenFord() {
		//A very dark, very large map with little in it.
		//TODO - redo it into a non-space that is basically an exception handler.
		//TODO - make the graphics draw programatically, so no hint of it exists in the graphics folders. Should be an animation, since the game should be converted to a framerate-based one before this is relevant. 
		//Something like, "when the player first enters, select 3 random points on the floor. Set them to be RGB 0.5/0.5/0.5. Add these pixels to a list.
		//Then, each frame, have all pixels above RGB 0.03 total check their 4 neighbours. Against each neighbour, roll 3 rands.
		//If the first rand > 0.5, add a random amount of up to 80% of your own colour to this neighbour, and decrease said colour by 1/10th of that. Repeat for each rand, and each neighbour, and each pixel.
		//Add this new pixel to the list of checked pixels, and redo. Once there are no pixels above RGB 0.05, delete them all and start again.
		//The intention is to create little staticy "explosions" in the background, that look like... I dunno, something weird and cool.
		/*Map betweenford = new Map(50, 50, 0, 0, 0, tiles.get("Dark Floor"));
		betweenford.setName("Between Ford");
		maps.put("Between Ford", betweenford);*/
	}

	public static void initialiseMainImage() {
		mainImage = new GameImage(dungeon, dx, dy);
		mainImage.setPlayer(self);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(Math.min(dx*mainImage.X_UNIT+mainImage.infoWidth+5, 1920), Math.min(dy*mainImage.Y_UNIT, 1200));
				frame.setTitle("Dungeon Game What Has No Name");
				frame.setVisible(true);
				frame.add(mainImage);
				//TODO - Add a more pretty graphical thing to infoPanel, that shows you your attack
				//Like a swipey thing with the 9 directions, that shows you what your wand is doing, how strong the attack is, etc
				//This is how it becomes a fusion of old-style graphics and new-style features
				frame.setResizable(false);
				//frame.pack();
			}
		});
	}
	
	public static void readFromFile() throws Exception {
		//TODO - make loading screen a spirograph
		String filename = "mazeInfo.txt";
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
					dx = Integer.parseInt(dimensions[0]);
					dy = Integer.parseInt(dimensions[1]);
					dz = Integer.parseInt(dimensions[2]);
				}
			}
			else if (str.trim().equals("player:")) {
				i++;
				str = "";
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
	}
	
	public static void command(String command) {
		//Move
		//TODO - add that new combat system.
		//TODO later on: Make a "hardcore"-type system, which is far more complex.
		//It takes into account your facing direction, and you can use both hands in various situations.
		//First - make both hands do things. For example, hammers must be 2-handed, and it doesn't matter. But you can wield 2 swords, and hit Ctrl+Stuff to use the right hand.
		//Or, wield a shield and hit Ctrl+Dir to block in that direction for a bit.
		//Next, add a system that depends on your facing direction.
		//You can change direction with... something. It takes time, and if you're swinging in another direction it takes more time than if you're already facing there.
		//Then, add bonuses to different attacks depending on your direction. For example, a left-swipe deals more damage if you are facing in the start direction (i.e. QE gives you a bonus if you're facing Q)
		//Then make them shift which direction you're facing.
		//Then make following attacks do better depending on previous attacks. E.g. CA works best with left-handed, then WZ makes you backslash, which is faster than CA -> QE.
		//EQ with left-hand sword into W with right-hand shield is also much faster, and a good, strong attack.
		//My main thought is having things like Keening and Sunder. That would be AWESOME. (and by that I mean dual swords, apparently Sunder is a hammer. Sod that. Keening deals large unarmoured damage, Sunder breaks armour))
		
		//Enemies have a number of stances, which are associated with a sprite on the combat screen and a series of resistances.
		//Resistances are a string of numbers, like "104244304224030420", which represent how much damage reduction is applied to each swing in each direction.
		//Their stance also determines the same thing for their attack strength.
		//For example, the start of the string being "123456789" means QW is resisted by 1, QE by 2 etc. 0 means there is no resistance (bare skin tends to be 1 or 2, fur 3-5, 0 would be reserved for ethereal beings and such).
		//The scale goes from 0-f, f being something like impact-resistant steel armour. There is also '!', which indicates a complete block of the attack or an OHKO for attacks (attack takes precedence), and a '.' for misses.
		//You can add breaks in between each set of attacks. So you could just have "435627544543945678...", or "436636536, 454636346, 554463445...".
		//On the sprite attack indicator, once you press a button, the possible attacks come up in a fan-like thing, with the easier attacks being greener, !s (blocks) being black and .s (misses) white.
		
		//TODO - change typing system to mouse-based system.
		//Holding left click will bring up a sort of wheel where you can see your character holding their weapon.
		//Swinging the mouse will swing the weapon - faster makes it do more damage, but might not be possible with heavier weapons.
		//Normally, you won't turn to match it, but holding right click will let you do that.
		//Swinging round and round will either whirl it round your head (great for flails) but holding right click lets you swing it for a very powerful whirlwind attack.
		//Also, holding WSAD while holding right click will keep you stuck in that direction, good if you want to quickly turn or something.
		
		//TODO - more exposition on arena fights
		//You have about 10-20 levels, each of which sees you fighting in a greater area.
		//Maybe the first can be an arena - you get points for things (maybe more points the less you take damage) and after 10,000 points a boss comes out of a gate.
		//Beat this boss and the gate remains open, so you can move out into a wider area. The second level has you doing something else, maybe clearing a bunch of narrowish corridors.
		//Once you get to a certain area, another boss battle and you reach level 3. And so on.
		//You have a choice in level 1 - what weapon do you take? You have sword, mace, flail, hammer, etc.
		//Every level you complete makes your weapon a bit more powerful - maybe 10 gives you a small chance to give an elemental strike after a 3-hit combo. Level 5 gives double damage after a 5th hit, etc.
		//Once you get to level 3 3 times with a certain weapon, you unlock the ability to start at level 2 with that weapon. This allows players to progress without having to do the same stuff again, as well as increasing replayability, without letting you power through the game.
		//After the final level and final boss, the entire game is available to you, maybe one level shuts off earlier parts, but you can get back to them after the finale. Here, you can fight ever-more-powerful (just more HP, defence, attack, attack speed) enemies.
		//The final enemies should be allocated "points" to spend in attributes, and these will be static at first but based on how well you do against them as time goes on.
		//If you beat a super-attack-speed enemy much more easily ("ease": a function of damage taken, time taken and number/strength of attacks used (stronger attacks mean a harder enemy)), attack speed will cost less but be less weighted to be spent on.
		//This ensures the player will always be forced to fight the more challenging variants of enemies - ones that THEY in particular find more challenging.
		//There might have to be some way to detect cheesing (deliberately taking damage to encourage attributes they find easier), but this might hamper the Any% max-score runs.
		//It should be such that you can do a speedrun, a max points run and a min points run - plus other things like a run to get the max points in the game (minus the final level - i.e. all enemies killed in level 2 etc) as fast as possible. You'd need setup for getting max points - less setup = faster time. Once max score is reached, this should be a speedrun category.
		//TL;DR: Arena, expands with each of 10-20 levels, with different expansion conditions. Different weapons encourage replayability, as does letting you continue from later areas. Make it speedrun-friendly and have intelligently-attributed ever-stronger enemies after the final boss for high-score competition.
		
		//TODO - simple combat system, maybe that could be the "easy" mode for players.
		//Basically, hold shift and tap QWEDCXZAS for any one of 9 attacks.
		//Q/E do a left/right slash. W is a downward strike. A/D are left/right swipes. Z/C are left/right upward chops. X is upward slice. S is a simple stab.
		//Have 2 options - relative controls and absolute controls. Absolute are those at all times, relative is based on the direction you're facing.
		//This allows you to do much better combos - you can have more fighting-game-style combat then. For example, QCW is a powerful slam. You slice the enemy, chop them up into the air, then slam them down, stunning them.
		//That's for hammers, the stun. You only gain the ability to do this around level 10. It can be seen as an air attack, or earth. With swords, you get a fire attack. This shoots a fire wave (think Air Cutter but with fire) forwards, or does extra damage if you whap them with the firey blade.
		
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
			//print(cloc.getTile(self.getXY()));
			//print(self.getXY()+", "+self.getX()+", "+self.getY());
		}
		
		//Pick up item
		else if (command.equals("p")) {
			Pair<Boolean, Item> itemHere = cloc.itemAt(self.getX(), self.getY()); 
			if (itemHere.getLeft()) {
				Item item =	itemHere.getRight();
				Pair<Boolean, ItemTile> itemAt = 
					item.getTileAt(cloc.getItems(), self.getX(), self.getY());
				if (itemAt.getLeft()) {
					cloc.removeItem(itemAt.getRight());
					self.pickupItem(item);
					print("Picked up "+itemAt.getRight().getName());
					mainImage.redrawMap();
					mainImage.dispInventory();
				}
				else {
					//Add new exception (ItemNotFound exception?) like ConcurrentModification
					print(
						"Something went seriously wrong here. I can't find an item at the place\n" +
						"you're at, but the only way to get here is if you picked one up.\n" +
						"Did it disappear before this thing deleted it? Exiting anyway, this will crash otherwise.");
					System.exit(-3);
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
							"the combat system, so report it to the developer.");
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
				byte y = Byte.parseByte(matcher.group(3));
				byte z = Byte.parseByte(matcher.group(4));
				//TODO - test this and damage, add other things that cause these to change (maze generator?)
				if (tiles.containsKey(tileName)) {
					TileType tile = tiles.get(tileName);
					print("Changing at "+x+" "+y+" "+z);
					if (cloc instanceof Location2D) {
						((Location2D) cloc).setTile(x, y, tile);
					} else if (cloc instanceof Location3D) {
						((Location3D) cloc).setTile(x, y, z, tile);
					} else {
						print(cloc.getClass()+" is not either a 2D or 3D location. It's probably 4D - I haven't added behaviour for that yet, nag me.");
					}
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
		else if (command.contentEquals("mix")) {
			Potion A = null;
			Potion B = null;
			Potion p = null;
			for (Item item : self.getInventory()) {
				if (item.getClass().equals(Potion.class)) {
					if (A != null) {
						B = (Potion) item;
						p = Potion.mix(A, B, new Bottle(5000));
						break;
					} else {
						A = (Potion) item;
					}
				}
			}
			if (p != null) {
				self.addToInventory(p);
				print("Mixed!");
			} else {
				print("Mixing failed, less than 2 Potions in inventory");
			}
		}
		//Change this to only repaint the small area being changed
		frame.getContentPane().repaint();
		//TODO - have pictorial representations of engravings, like the DF engravings. See jef's stream on 21/03/2015 (http://www.twitch.tv/jefmajor/b/639760684)
		//about 1:10 in, for an example of engravings. Before the coffin guy gets possessed.
		//So, for example, "This is an image of "Yui8856" and bucklers. "Yui8856" is surrounded by the bucklers." would have an image of a red-haired, long-bearded dorf surrounded by bucklers.
		//Yui is at 1:15:15
		//You can have more control over the engravings - such as, plating the bucklers in iron, or making carved drawings/embossed drawings.
	}
	
	public static void cycle(Direction dir) {
		TreeMap<EntityTile, Integer> newList = new TreeMap<EntityTile, Integer>();
		
		//Reset your own tick count
		unfrozenEntities.remove(self);
		int ticks = self.getTicks();
		self.resetTicks();
		
		//Print out all the locations and their entities
		//TODO - delete this
		/*for (Location loc : rooms.values()) {
			System.out.println(loc.getName());
			for (EntityTile ent : loc.getEntities()) {
				System.out.println("\t"+ent.getName());
			}
		}*/
		
		//Move the player
		Pair<Boolean, Boolean> didMove = move(dir, self);
		boolean leftRoom = didMove.getRight();
				
		//If the player entered a new room, alter the path of every entity that needs it
		if (leftRoom) {
			for (EntityTile entity : unfrozenEntities.keySet()) {
				//alterPath pseudocode: if passed room is in the Path ArrayList, truncate it to that. Otherwise, add it to the end.
				entity.alterPathEnd(self.getLocation());
				print("Path: "+entity.getPath().toString());
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
			boolean leftRoomEnt = pathfind(entity);
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
		//Check the 8 tiles around the player, and display the entity/ies that are there
		ArrayList<EntityTile> adjacents = new ArrayList<EntityTile>();
		
		for (int x = self.getX()-1; x <= self.getX()+1; x++) {
			for (int y = self.getY()-1; y <= self.getY()+1; y++) {
				if (x != self.getX() || y != self.getY()) {
					if (cloc.entityAt(x, y).getLeft()) {
						adjacents.add(cloc.entityAt(x, y).getRight());
					}
				}
			}
		}
		if (adjacents.size() > 0) {
			mainImage.setCombatPortrait(adjacents.get(0).getPortrait());
		}
		mainImage.redrawMap();
	}
	
	public static Pair<Boolean, Boolean> move(Direction dir, EntityTile entity) {
		Location loc = entity.getLocation();
		//NOTE: If moving is being weird, this used to have an extra parameter, location, instead of assuming it's the same as the entity's location.
		//Also, there was a z-position defined for the entity, but it didn't do anything as far as I saw.
		
		//TODO - this is an absolute mess, clean it up! (Put most things into separate functions)
		
		byte x = (byte) entity.getX();
		byte y = (byte) entity.getY();
		short xy = (short) (x + (y << 8));
		//First, check and see if the new position is outside of the current location.
		int newxy = xy+dir.getNumVal();
		int wh = loc.getWH();
		int w = loc.getW();
		int h = loc.getH();
		int nx = mod(newxy);
		int ny = (newxy - nx) >> 8;
		
		if (newxy >= 0 && newxy < wh && nx >= 0 && nx < w) {
			//If it is inside the current Location, just try to perform the movement. Note that some tiles might only let certain entities through.
			if (loc.getTile(newxy).isTraversable(entity)) {
				entity.setXY(newxy);
				System.out.println(entity.getLocation().getTile(newxy));
				return new Pair<Boolean, Boolean>(true, false);
			} else {
				print(entity.getName()+" cannot move to "+nx+", "+((newxy - nx) >> 8)+" in location "+loc.getName());
				return new Pair<Boolean, Boolean>(false, false);
			}
		} else {
			//Otherwise, it's outside the current Location and should go to whatever Location is there.
			print ("Moving to new location");
			Location newloc = dungeon.getMapRel(loc, new Coord3D(nx, ny, entity.getZ()));
			entity.setX(nx);
			entity.setY(ny);
			loc.removeEntity(entity);
			try {
				newloc.addEntity(entity);
				print ("New map info: "+newloc.toString());
			} catch (NullPointerException e) {
				//In this case, the player has made an illegal move - shunt them to the Between Ford.
				between = true;
				return new Pair<Boolean, Boolean>(true, true);
			}
			//if (newloc.equals(maps.get("Between Ford"))) {
			//	entity.setCoords(0, 0, 0);
			//}
			return new Pair<Boolean, Boolean>(true, true);
		}
	}
	
	private static int mod(int i) {
		//Is i > 0? If so, return i % 256. Else return i % 256 + 256
		return i > 0 ? i % 256 : i % 256 + 256;
	}

	public static void moveToPlayer(EntityTile entity) {
		//Move into a position around the player, respecting other enemy positions.
		if (entity.getCoords().distanceTo(self.getCoords())<1.5) {
			//Then the entity is next to the player - do nothing.
			print("Next to player: "+entity.getCoords().distanceTo(self.getCoords()));
		} else {
			print("Moving to player: "+entity.getCoords().distanceTo(self.getCoords()));
			//Otherwise move towards the player.
			//TODO - respect other entities, move to a location where they aren't in the way.
			moveTo(entity, self.getCoords());
		}
	}
	
	public static void moveTo(EntityTile entity, Coord target) {
		//If the coast is clear, move in a particular direction. If not, use A* to find your way.
		//TODO - once awkward mazes are possible, rewrite this to include A*.
		
		//Finds where it needs to go, checks the distance to that place.
		//Tries to head straight for it.
		//If that is blocked for whatever reason, tries to head diagonally or whatever towards that location - basically, aims to minimise the distance.
		
		//Get the distance to the target.
		double distance = entity.getCoords().distanceTo(target);
		
		//Make sure it's not 0 - it should never call this code if it is, but it might do.
		if (distance < 0.001) {
			GameClass.print("The entity "+entity.getName()+" is at its target, and that means \"moveTo\" shouldn't have been called. Check the distance BEFORE this method. Exiting.");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(12);
		}
		
		//Get the distance when moving in all 8 directions.
		int coords = entity.getCoords().toSingleVal();
		int[] directions = {-257, -256, -255, -1, 1, 255, 256, 257};
		double minDist = Integer.MAX_VALUE;
		double direction = 0;
		Coord2D c2d = new Coord2D(5, 5);
		Coord3D c3d = new Coord3D(6, 4, 2);
		int c2ds = c2d.toSingleVal();
		int c3ds = c3d.toSingleVal();
		for (int i=0; i<8; i++) {
			print("Number:" +directions[i]+".\n"
					+ "2D translation: "+c2d+", "+c2ds+", "+(c2ds+directions[i])+", "+Coord2D.fromSingleVal(c2ds+directions[i]) + "\n"
					+ "3D translation: "+c3d+", "+c3ds+", "+(c3ds+directions[i])+", "+Coord3D.fromSingleVal(c3ds+directions[i]));
			//if (entity.getCoords().add())
		}
	}
	
	private static boolean canMove(Direction direction, EntityTile entity) {
		//True iff there are no entities in the way and the tile is traversable.
		return entity.getLocation().getTile(entity.getXY()+direction.getNumVal()).isTraversable(entity);
	}

	public static boolean pathfind(EntityTile entity) {
		//If the entity is in the same room as the player, try to move towards the player.
		if (entity.getLocation() == self.getLocation()) {
			if (entity.getAI() == "SimpleMelee") {
				moveToPlayer(entity);
			} else if (entity.getAI() == "Ranged") {
				moveTo(entity, getDesiredLocation(entity));
			}
			return false;
		} else {
			//TODO - change this to A* search, and do it every 5 steps or so.
			/*//If the entity has a path to follow, follow it. Otherwise, create the path to the player.
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
				Loc2DSp2D location = path.peekLast();
				Loc2DSp2D target = self.getLocation();
				
				search:
				while (location != target) {
					//If it isn't, in a while loop, get all of the attachments of it, put them on the end of the list, and get the first one and do the same thing.
					//You already have the path to expand - expand it.
					for (Loc2DSp2D attachedLoc : location.getAttached().keySet()) {
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
					print(entranceP.toString());
				}
				print("\nLocation it is aiming for:\n"+aimingFor);
			}*/
			return false;
		}
	}
	
	private static Coord getDesiredLocation(EntityTile entity) {
		return null;
	}

	public static Pair<Boolean, EntityTile> existsAtLoc(Location loc, byte xPos, byte yPos) {
		for (EntityTile entity : loc.getEntities()) {
			if (entity.getX()==xPos && entity.getY()==yPos) {
				return new Pair<Boolean, EntityTile>(true, entity);
			}
		}
		return new Pair<Boolean, EntityTile>(false, null);
	}
	
	public static Map initialiseMap(int x, int y, Map map, String type) {
		for (int j=1; j<(y-1); j++) {
			for (int i=1; i<(x-1); i++) {
				if (type.equals("empty")) {
					map.setTile(i, j, tiles.get("Marble Floor"));					
				} else if (type.equals("full")) {
					map.setTile(i, j, tiles.get("Marble Wall"));
				}
			}
		}
		for (int i=0; i<x; i++) {
			map.setTile(i, 0, tiles.get("Marble Wall"));
			map.setTile(i, y-1, tiles.get("Marble Wall"));
		}
		for (int j=0; j<y; j++) {
			map.setTile(0, j, tiles.get("Marble Wall"));
			map.setTile(x-1, j, tiles.get("Marble Wall"));
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
					ItemEquippable item = new ItemEquippable();
					while(!strs[i].trim().equals("end")) {
						strs[i] = strs[i].trim();
						if (strs[i].contains("name:")) {
							strs[i] = strs[i].replace("name: ", "");
							item.setName(strs[i]);
						}
						else if (strs[i].trim().contains("description:")) {
							strs[i] = strs[i].replace("description: ", "");
							item.setDescription(strs[i]);
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
					System.out.println(
							"\"item\" tag not found, please add it before each new item. \n"
							+strs[i]);
					i++;
				}
			}
		}
		System.out.println("done.");
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
									System.out.println(entity.getName()+", "+entity.getBaseStat("max mana"));
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
		System.out.println("done.");
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
		System.out.println("done.");
	}
	
	public static void generateMaterials(String str) {
		System.out.print("Generating materials... ");
		BufferedImage upStairs = null;
		BufferedImage downStairs = null;
		BufferedImage upDownStairs = null;
		//TODO - make pillars destructible, and possibly load-bearing
		BufferedImage pillar = null;
		try {
			upStairs = ImageIO.read(new File("images/masks/Up.png"));
			downStairs = ImageIO.read(new File("images/masks/Down.png"));
			upDownStairs = ImageIO.read(new File("images/masks/Stairs.png"));
			pillar = ImageIO.read(new File("images/masks/Pillar.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("One or more masks not present; please check you have \"Up.png\", \"Down.png\", \"Stairs.png\" and \"Pillar.png\" in your images folder");
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
				System.out.println("Name: "+name);
				String filepath = "images/materials/"+name+".png";
				BufferedImage img = null;
				BufferedImage floorImg = null;
				BufferedImage upImg = null;
				BufferedImage downImg = null;
				BufferedImage upDownImg = null;
				BufferedImage pillarImg = null;
				float scaleFactor = 0.7f;
				RescaleOp op = new RescaleOp(scaleFactor, 0, null);	
				try {
					img = ImageIO.read(new File(filepath));
					upImg = ImageIO.read(new File(filepath));
					downImg = ImageIO.read(new File(filepath));
					upDownImg = ImageIO.read(new File(filepath));
					pillarImg = ImageIO.read(new File(filepath));
					floorImg = op.filter(img, null);
					upImg = op.filter(img, null);
					downImg = op.filter(img, null);
					upDownImg = op.filter(img, null);
					pillarImg = op.filter(img, null);
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
					System.out.println("Path of invalid material: "+filepath);
				}

				ArrayList<String> allPermitted = new ArrayList<String>();
				ArrayList<String> playerRestricted = new ArrayList<String>();
				ArrayList<String> playerPermitted = new ArrayList<String>();
				ArrayList<String> allRestricted = new ArrayList<String>();
				allPermitted.add("All");
				playerPermitted.add("Player");
				playerRestricted.add("-");
				playerRestricted.add("Player");
				tiles.put(name+" Wall", new TileType(name+" Wall", img, allRestricted, null));
				tiles.put(name+" Floor", new TileType(name+" Floor", floorImg, allPermitted, null));
				tiles.put(name+" Upward Stairway", new TileType(name+" Upward Stairway", upImg, allPermitted, null));
				tiles.put(name+" Downward Stairway", new TileType(name+" Downward Stairway", downImg, allPermitted, null));
				tiles.put(name+" Up/Down Stairway", new TileType(name+" Up/Down Stairway", upDownImg, allPermitted, null));
				tiles.put(name+" Upward Slope", new TileType(name+" Upward Slope", img, allPermitted, null));
				tiles.put(name+" Downward Slope", new TileType(name+" Downward Slope", img, allPermitted, null));
				tiles.put(name+" Pillar", new TileType(name+" Pillar", pillarImg, allRestricted, null));
			} else {
				System.out.println("Material name not found, please check syntax");
			}
		}
	}
	
	/*public static void generateMaps(String str) {
		System.out.print("Generating maps... ");
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
		System.out.println("done.");
	}
	
	@SuppressWarnings("unused")
	private static void fight(EntityTile ent1, EntityTile ent2) {
		int str1 = ent1.getStrength()+random.nextInt(20)-10;
		int str2 = ent2.getStrength()+random.nextInt(20)-10;
		
		print(ent1.getName()+" strength: "+str1);
		print(ent2.getName()+" strength: "+str2);

		String printStr = "";
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
			printStr += "Nobody takes damage: ";
			if (ent1.equals(self)) {
				printStr += "You have ";
			} else {
				printStr += "The "+ent1.getName()+" has ";
			}
			printStr += ent1.getHealth()+" health left, and ";
			print(printStr);
			printStr = "";
			if (ent2.equals(self)) {
				printStr += "you have ";
			} else {
				printStr += "the "+ent2.getName()+" has ";
			}
			printStr += ent2.getHealth()+".\n";
			print(printStr);
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
		
		String printStr = "";
		//Damage dealer
		if (isAtt)
			printStr += "You overpower ";
		else
			printStr += "The "+nameAtt+" overpowers ";
		
		//Damage taker
		if (isDef)
			printStr += "you!";
		else
			printStr += "the "+nameDef+"!";
		
		print(printStr);
		printStr = "";
		
		//Damage dealt
		if (isAtt)
			printStr += "You deal ";
		else
			printStr += "The "+nameAtt+" deals ";
		
		printStr += diff+" damage!";
		print(printStr);
		printStr = "";
		
		//If defender dies
		if (defender.getHealth()<=0) {
			if (isDef)
				print("You die!\n");
			else
				print("The "+nameDef+" dies!\n");
			return true;
		}
		else {
			if (isDef)
				printStr += "You now have ";
			else
				printStr += "The "+nameDef+" now has ";
			printStr += defender.getHealth()+" health.\n";
			print(printStr);
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
	
	public static void print(String s) {
		System.out.println(s);
		//Try to draw to the main image. If it doesn't exist (such as, for testing purposes), print to the console.
		try {
			mainImage.drawInfo(s);
		} catch (NullPointerException e) {
			System.out.println("Screen has not yet been created. This may be intentional.");
		}
	}
	
	public static void print(Boolean b) {
		print(b.toString());
	}
	
	public static void print(Exception e) {
		print(e.toString());
	}
	
	public static void print(char c){
		print(Character.toString(c));
	}
	
	public static void print(Object o) {
		print(o.toString());
	}
		
	public static BufferedImage colorImage(BufferedImage loadImg, Color colour) {
	    BufferedImage img = new BufferedImage(loadImg.getWidth(), loadImg.getHeight(), BufferedImage.TRANSLUCENT);
	    final float tintOpacity = 0.45f;
	    Graphics2D g2d = img.createGraphics(); 

	    //Draw the base image
	    g2d.drawImage(loadImg, null, 0, 0);
	    //Set the color to a transparent version of the input color
	    g2d.setColor(new Color(colour.getRed() / 255f, colour.getGreen() / 255f, 
	        colour.getBlue() / 255f, tintOpacity));

	    //Iterate over every pixel, if it isn't transparent paint over it
	    Raster data = loadImg.getData();
	    for(int x = data.getMinX(); x < data.getWidth(); x++){
	        for(int y = data.getMinY(); y < data.getHeight(); y++){
	            int[] pixel = data.getPixel(x, y, new int[4]);
	            if(pixel[3] > 0){ //If pixel isn't full alpha. Could also be pixel[3]==255
	                g2d.fillRect(x, y, 1, 1);
	            }
	        }
	    }
	    g2d.dispose();
	    return img;
	}

	private static BufferedImage createPotionGraphics(Color colour) {
		BufferedImage pot = new BufferedImage(20, 30, BufferedImage.TYPE_INT_ARGB);
		
		BufferedImage liquid = colorImage(GameClass.liquid, colour);
		
		pot.getGraphics().drawImage(liquid, 0, 0, 20, 30, null);
		pot.getGraphics().drawImage(bottle, 0, 0, 20, 30, null);
		pot.getGraphics().drawImage(shine, 0, 0, 20, 30, null);
		
		return pot;
	}
	
	private static boolean createEntity(String name, Location loc, int x, int y, int z, int speed) {
		if (entities.containsKey(name)) {
			EntityTile entity = new EntityTile(entities.get(name), loc, (byte) x, (byte) y, (byte) z, null);
			unfrozenEntities.put(entity, speed);
			return true;
		} else {
			System.out.println("Entity with name "+name+" not found.\n"
					+ "Please note that names in the database may be different from the actual entity name; the database name should be used.\n"
					+ "Run with debug mode on to view the entire list of available entities.");
			if (debug) {
				System.out.println("List of entities:");
				for (String entity : entities.keySet()) {
					System.out.println(entity+", where the entity's name is "+entities.get(entity));
				}
			}
			return false;
		}
	}

}