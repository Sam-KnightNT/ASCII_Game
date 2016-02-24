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
	static HashMap<String, Entity> entityTypes = new HashMap<String, Entity>();
	static int entityCount = 0;
	static Dungeon dungeon;
	static int attackValue = 0;
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

	private static HashSet<EntityTile> entityInstances = new HashSet<EntityTile>();
	private static TreeSet<EntityAssociation> entityByTicks = new TreeSet<EntityAssociation>();
	private static HashMap<Integer, EntityTile> entityByID = new HashMap<Integer, EntityTile>();
	
	private static BufferedImage bottle;
	private static BufferedImage liquid;
	private static BufferedImage shine;

	private static Map arena = new Map(120, 120, 0, 60, 0, tiles.get("Test Floor"));
	
	static boolean debug = true;
	static boolean systemPrint = true;
	
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
	 * A point on optimisation and multithreading - I forgot about the fact that, basically, each thread can work on different bytes simultaneously.
	 * So, what I propose, is making sure each thing fits neatly into bytes - each entity takes up exactly n bytes, each tile, etc.
	 * Then, I can spawn multiple threads, each of which deals with the movement of a single entity at a time.
	 * It should hopefully be possible to update entities simultaneously - or at least have it so that ones who require other entities' info can be put into a sequence, so those that don't can update earlier.
	 * That's quite a way down the line, but it'll happen eventually.
	 * 
	 * 
	 * Roadmap for To3: Arena
	 * v0.01: Drawing player on map, in ASCII
	 * v0.02: Drawn on Swing graphics
	 * v0.03: Changed into non-ASCII, instead using map tiles
	 * v0.04: Created other entities
	 * v0.05: Added basic combat system and pathfinding
	 * v0.06: Created info panel, with info on combat and such (where I am now)
	 * v0.07: Working simple combat system with multiple entities, simple AI behaviour and an arena (possibly untextured)
	 * v0.08: Completed complex combat system
	 * v0.09: Working combat panel, flavour text and swipe previews
	 * v0.10: Completed tutorial and finalised arena
	 * v0.11: (Optional) More advanced AI, featuring rangers and such
	 * v0.12: All extraneous things removed - "Generating" statements, printouts, most commands, potion system, all that guff.
	 * 			Make it ready for public release
	 * v1.00: Progression, scores 
	 * 
	 * Details on v0.08
	 * There should be new controls. If more than one enemy is standing next to you, 1-9 points at them.
	 * Shift+QWEDCXZA does the already-mentioned swipey things, which compares the enemy's defence against that particular attack to the player's attack strength.
	 * Hitting some button ('s' for stance probably) opens a panel in front of the main view (this'll be a third pane) that can be dismissed with Esc.
	 * It shows a menu - several numbers with stances written beside, such as "two-handed overhead". One (your current stance) is highlighted.
	 * Pressing numbers highlights that stance, and whichever stance is highlighted has flavour text beside it.
	 * For example, two-handed is "Allows devastating downward smashes, but little in the way of other attacks and weak defences."
	 * There could be different stances for different weapon combos later down the line - sword and shield, for example, or polearm.
	 * Also, don't forget the possibility of using, say, WW as a stab, or S- as a stab to the middle then a slice outwards.
	 * 
	 * To make it more engaging, I can add different timings to everything. Changing stance requires 50% of the time it would have taken to move.
	 * Attacking requires a 15% waiting period before you actually hit your opponent, and a 135% waiting period afterwards - in total it takes 1.5 times as long as a movement.
	 * Once you begin your attack, you may or may not telegraph to your opponent what you are about to do. Not sure which would be better balanced.
	 * But the waiting periods are meant to make it possible to anticipate opponent's attacks most of the time, and prepare for them. But not all the time - you have to attack occasionally.
	 * Blocking directions should be lessened to compensate - possibly even have an entirey separate blocking system, where the entity can block attacks from 1 direction or deflect attacks from 3 for half/quarter damage.
	 * That would lead to a lot more tactics in combat - do I keep changing stances to look for an opening, or attack now and risk my defence being open?
	 * There'll have to be a healing system in place, at least a basic heal-to-full-after-wave. You might be dealt a lot of damage in each combat with this system.
	 * Also, difficulties are easy to cater for - easier ones lower the enemy speed or tendency to block attacks, harder ones increase their speed. Or, even cheat by letting them see what attack you're making while winding up.
	 * 
	 * I need to rethink this mechanic. It's not that fun now that I consider it, unlike the proposed research mechanic.
	 * There's just one way to do things, and before you learn that way it's hard, once you learn it it's easy. There should be some challenge involved.
	 * But, it can't just be RNG, can't be one-of-n-type (e.g. Minotaurs have different blocking states to figure out each time you play) etc.
	 * Maybe I could get inspiration from Blade Mode in Metal Gear V: Revengeance?
	 * Or work in a skill tree like Final Fantasy X, or Path of Exile? www.gamesradar.com/coolest-game-mechanics-2013/, slides 3 and 16.
	 * 
	 * Perhaps it could be more of a puzzly kinda combat, more like DROD than fighting.
	 * The focus should be more on crowd control than one-on-one duels, so it should be possible to do a lot of fancy moves to that end - similar to the rapier in Crypt of the Necrodancer.
	 * So, how about different weapons in your arsenal that do different things?
	 * The sword moves could have a focus on moving you and the hammer on stunning or knocking back your foes? Maybe have, say, a mace that can keep enemies away and slowly bleed them, or an axe that can chop limbs off and permanently slow enemies and such.
	 * Yeah, so the combat becomes focused around seeing enemy telegraphy, fending off side attackers and weakening foes so you have a chance to finish some of them off!
	 * Also, different enemies react to different weapons and situations in different ways! Minotaurs can charge at you and are heavy, so you're much better off using the sword to dodge, and the axe to debilitate them.
	 * However slimes are unhampered by the axe - in fact, hitting them will split them in two! They are very light though - you can easily hammer them into a wall whereupon they'll split, but it makes it easier to get at the nuclei with a cutting weapon. Or you can ground pound them, if they're small enough or if you're strong enough you'll hit the nucleus outright.
	 * Kobolds could be fast enough that you'll have to plan ahead when dealing with them, but weak enough to die easily when attacked. So you need to take care to only let one or two near at once.
	 * Other enemies... 
	 * Controls:
	 * Shift+1-4 select weapons, or 1-6 or whatever.
	 * 1-9 selects the type of strike to use, this is dependent on the weapon. For example, the Axe has 1: Cleave Arm (weakens opponents' attack), 2: Cleave Leg (lowers enemy movement), 3: Cleave Head (unlikely to work, but insta-kills), etc.
	 * Hammer has, e.g., 1: Launching Blow (sends enemies flying depending on their weight), 2: Ground Pound (if the tile in front is clear, smashes the ground around it, stunning enemies. Otherwise hits the enemy in the tile ahead for massive damage), 3: Debilitating Whack (hits the enemy in the head, stunning them and knocking them a bit away).
	 * Sword: 1: Lunge (closes the distance towards an enemy, but deals a lot of damage - particularly to slimes), 2: Dodge (Hit the enemy in front and move a tile in any direction), 3: Vault (hits an enemy in front and launches you over them, landing on the other side), 4: Charge (like Debilitating Whack but more damage, less knockback/stun time and moves you alongside them - use if you NEED to get away from a group sharpish)  
	 * QWEDCXZA only selects an attack direction, not what attack to use. I.e. which enemy/tile to hit.
	 * Numpad 1-9 still moves you as normal.
	 * 
	 * This system is also conducive to upgrades - you can get better weapons but most people will prefer one type - you can just about manage with one, and in fact a good challenge could be to use only one type, but it's far more prudent to use all of them.
	 * 
	 * 
	 * More information on the dungeons: Instead of being just "dungeons", they should be more... alive. Like you're storming fortresses.
	 * Imagine Dwarf Fortresses. How would they make them in order to fend off attackers? Build the generator to make stuff like that.
	 * Do what Will said - have preset corridors and rooms and stuff, plugged into each other with certain rules.
	 * E.g. a particularly violent fortress will have the armory close to the entrance.
	 * Some particularly sneaky forts will have hidden areas for their gear, so that attackers can't steal them...
	 * The free version should have 5 premade fortresses, it should be more of a demo than anything. It should also have limited abilities - levels 1-3 or so.
	 * The full version should have the Dungen, along with much more abilities and stuff. It should cost... something. Not sure how much.
	 */
	public static void main(String[] args) throws Exception {
		readFromFile();
		
		BufferedReader readIn = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		
		/*createBetweenFord();
		
		Map map1 = new Map(50, 50, 0, 0, 0, tiles.get("Sandstone Brick 2 Floor"));
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
		
		maps.put("Map 1", map1);
		maps.put("Map 2", map2);*/
		

		//Create arena map
		arena.setName("Arena");
		maps.put("Arena", arena);

		cloc = arena;

		//Put walls around arena
		arena.fill(new Coord2D(0, 0), new Coord2D(119, 119), tiles.get("Limestone 2 Wall"));
		arena.fill(new Coord2D(30, 50), new Coord2D(94, 90), tiles.get("Limestone 2 Floor"));
		
		fillLine(30, 64);
		fillLine(31, 60);
		fillLine(32, 58);
		fillLine(33, 56);
		fillLine(34, 55);
		fillLine(35, 54);
		fillLine(36, 53);
		fillLine(37, 52);
		fillLine(38, 52);
		fillLine(39, 51);
		fillLine(40, 51);
		fillLine(41, 51);
		fillLine(42, 51);
		fillLine(43, 51);
		fillLine(44, 50);
		fillLine(45, 50);
		fillLine(46, 50);
		fillLine(47, 50);
		fillLine(48, 50);
		fillLine(49, 50);
		fillLine(50, 50);
		fillLine(51, 50);
		fillLine(52, 50);

		TileType floor = tiles.get("Limestone 2 Floor");
		TileType gate = tiles.get("Limestone Gate");
		
		arena.fill(new Coord2D(60, 30), new Coord2D(63, 49), floor);
		arena.fill(new Coord2D(30, 27), new Coord2D(93, 30), floor);
		arena.fill(new Coord2D(30, 20), new Coord2D(37, 27), floor);
		arena.fill(new Coord2D(86, 20), new Coord2D(93, 27), floor);
		arena.fill(new Coord2D(60, 49), new Coord2D(63, 49), gate);
		
		//Create the player
		self = createPlayer(42, 62);
		
		
		//Add enemies
		createEntity("Minotaur", arena, 45, 65);
		createEntity("Minotaur", arena, 46, 66);
		createEntity("Slime", arena, 40, 64);
		createEntity("Slime", arena, 48, 62);
		createEntity("Slime", arena, 50, 65);
		createEntity("Slime", arena, 50, 62);
		createEntity("Slime", arena, 49, 62);
		
		//Create dungeon (currently doesn't matter, but if you remove it it goes haywire so don't)
		dungeon = new Dungeon();
		dungeon.setName("Default dungeon");
		//dungeon.addMap(maps.get("Between Ford"), maps.get("Between Ford").getPosition());
		for (Map map : maps.values()) {
			dungeon.addMap(map, map.getPosition());
		}
		
		initialiseMainImage();
//		SPOT FURTHER DOWN, BELOW COMMANDS
		
		//TODO - modify speed counter to account for things like
		//opening inventory (1/20th a move time), long wind-up moves and so on
		//Perhaps an action list of what to do next, i.e. "Minotaur move in 20 ticks, Player perform Great Strike in 35 ticks", etc?
		//Each action has wind-up time (usually 0) and cool=down time (default to speed)
		//E.g. for Great Strike
		//Wind-up .5, so it takes (0.5*10000)/speed ticks before the action is performed
		//Cool-down 1, so it takes 10000/speed ticks before you can perform another action
		
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
		//The scale goes from 0-f, 8 being about the level of steel armour and f being magically-resistant carbon nanotube plate. There is also '!', which indicates a complete block of the attack or an OHKO for attacks (attack takes precedence), and a '.' for misses.
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
				System.out.println(entityByTicks);
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
				print("I've apparently set the command system a bit wrong.");
				print("This part of the code deals with orthogonal movement,");
				print("but none of the directions were valid. The param was "+command+".");
				print("Please show the developer this message.");
			}
			//print(cloc.getTile(self.getXY()));
			//print(self.getXY()+", "+self.getX()+", "+self.getY());
		}
		else if (command.matches("m([ns])([we])$")) {
			switch (command.substring(1)) {
			case "ne":
				cycle(Direction.NORTHEAST);
				break;
			case "nw":
				cycle(Direction.NORTHWEST);
				break;
			case "se":
				cycle(Direction.SOUTHEAST);
				break;
			case "sw":
				cycle(Direction.SOUTHWEST);
				break;
			default:
				print("I've apparently set the command system a bit wrong.");
				print("This part of the code deals with diagonal movement,");
				print("but none of the directions were valid. The param was "+command+".");
				print("Please show the developer this message.");
			}
		}
		
		//Wait a cycle
		else if (command.equals("wait")) {
			cycle(false, null);
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
					System.out.println(
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
			for (EntityTile entity : entityInstances) {
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
		
		//Let one entity attack another
		else if (command.startsWith("att ")) {
			//TODO - check if there's a problem with entities with spaces in their names (Bronze Colossus) - this will probably confuse the parser
			Pattern pattern = Pattern.compile("att (.+) (.+) ([0-9]{1,2})");
			Matcher matcher = pattern.matcher(command);
			if (matcher.matches()) {
				print(command+" attack!");
				//This should get the stances of the attacker and defender, then get the A-SPC/D-SPC from the strings provided in the entity stats.
				//Then work out how much damage to deal based on that.
				EntityTile attacker = entityByID.get(Integer.parseInt(matcher.group(1)));
				EntityTile defender = entityByID.get(Integer.parseInt(matcher.group(2)));
				//String aStance = attacker.getStance();
			} else {
				print("Improper usage of attack command. "+command);
			}
		}
		
		//Cast a spell
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
						print(cloc.getClass()+" is not either a 2D or 3D location. It's probably 4D, i.e. it changes over time - I haven't added behaviour for that yet, nag me.");
					}
				} else {
					print("Tile "+tileName+" not found, " +
							"tile at ("+x+", "+y+") will not be changed.");
				}
				
			} else {
				print("Improper usage of \"cb\" command.\n" +
						"Should be \"cb <tilename> <xcoord> <ycoord>, found "+command+".");
			}
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
						int num = 2;
						entity = entityList.get(num-1);
					}
					print("You do a weird magicky thing!");
					int rng = random.nextInt(6)+1;
					print("A mystical die rolls. It rolls a "+rng+"!");
					int damage;
					self.setStat("mana", self.getStat("mana")-1);
					switch (rng) {
					case 1:
						entityTypes.remove(self.getName());
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
			if (entityTypes.containsKey(entity)) {
				ArrayList<EntityTile> ent = cloc.getEntityByName(entity);
				if (!ent.isEmpty()) {
					if (ent.size()>1) {
						print("More than one entity has been found, please type\n" +
								"which one you want to control (it added them from top to bottom, then left to right)");
						int num = 2;
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
			for (String entName : entityTypes.keySet()) {
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
		//Change this to only repaint the small area being changed
		frame.getContentPane().repaint();
		//TODO - have pictorial representations of engravings, like the DF engravings. See jef's stream on 21/03/2015 (http://www.twitch.tv/jefmajor/b/639760684)
		//about 1:10 in, for an example of engravings. Before the coffin guy gets possessed.
		//So, for example, "This is an image of "Yui8856" and bucklers. "Yui8856" is surrounded by the bucklers." would have an image of a red-haired, long-bearded dorf surrounded by bucklers.
		//Yui is at 1:15:15
		//You can have more control over the engravings - such as, plating the bucklers in iron, or making carved drawings/embossed drawings.
	}
	
	public static void cycle(Direction dir) {
		cycle(true, dir);
	}
	
	public static void cycle(boolean move, Direction dir) {
		
		//First, check to see if you are the first entity in the list.
		//If not, something weird's gone wrong and the game should probably stop before it breaks even more.
		//TODO - remove this check for the full RPG - it could lead to interesting stuffs.
		/*if (entityByTicks.first().getEntity() != self) {
			System.out.println("Something is severely wrong, the player is not at the front of the move list despite only just having moved.");
			System.out.println("I'm exiting the program now since bad stuff would be almost certain.");
			System.out.println(entityByTicks);
			System.exit(-2);
		}*/
		
		//After this, move the player and reset the tick count.
		TurnStatus moveStatus = playerMove(dir);
		
		switch (moveStatus) {
		case FOUGHT:
			print("Fought.");
			break;
		case MOVED:
			print("Moved.");
			break;
		case BLOCKED:
			print("Blocked.");
		}
		
		//Left of this pair is whether the player actually moved. If so, reset tick count, otherwise do it dynamically (e.g. if the player fought, have smaller cooldown)
		//TODO - for now it's just resetting anyway - do it dynamically.
		entityByTicks.pollFirst();
		
		//Get the current ticks left (to subtract it from every other Entity) and reset the player's ticks.
		int ticks = self.getTicks();
		self.resetTicks();
		
		/*
		boolean leftRoom = false;
		//Move the player
		if (move) {
			Pair<Boolean, Boolean> didMove = playerMove(dir);
			leftRoom = didMove.getRight();
		}
		
		//TODO - remove for arena release, there is only one room.
		//If the player entered a new room, alter the path of every entity that needs it
		if (leftRoom) {
			for (EntityTile entity : entityInstances) {
				//alterPath pseudocode: if passed room is in the Path ArrayList, truncate it to that. Otherwise, add it to the end.
				entity.alterPathEnd(self.getLocation());
				print("Path: "+entity.getPath().toString());
			}
		}
		*/
		
		//Now cycle through each Entity and decrease their ticks left by this amount.
		for (EntityAssociation assoc : entityByTicks) {
			assoc.decreaseValue(ticks);
			assoc.getEntity().decreaseTicks(ticks);
		}
		
		//And re-add the player in the appropriate place (which TreeSet does for us)
		EntityAssociation selfA = new EntityAssociation(self.getTicks(), self);
		entityByTicks.add(selfA);
		
		//TreeSet allows us to simply grab the chunk of Entities that are to move before the player.
		//Get this chunk, then find out how many ticks the last one needs to move.
		//Move each Entity on this list, then change the tick value to be their current tick value minus this value, added to their speed if they make a move.
		SortedSet<EntityAssociation> movers = entityByTicks.headSet(selfA);
		SortedSet<EntityAssociation> nonmovers = entityByTicks.tailSet(selfA, true);
		
		TreeSet<EntityAssociation> newList = new TreeSet<EntityAssociation>();
		int lastTicks = 0;
		if (!movers.isEmpty()) {
			lastTicks = movers.last().getValue();
		} else {
			System.out.println("Movers is empty");
		}
		for (EntityAssociation assoc : movers) {
			//Get the entity to move and move it
			EntityTile entity = assoc.getEntity();
			
			//Change the entity's ticks remaining based on the state it WILL be at after all others have moved.
			//TODO - this will be problematic if it moves twice before the player does. Fix it somehow.
			ticks = entity.getTicks();
			entity.resetTicks();
			entity.decreaseTicks(lastTicks-ticks);
			
			//Attempt to pathfind to the player
			//TODO - no need for leftRoomEnt, delete the thing in pathfind.
			boolean leftRoomEnt = pathfind(entity);
			
			if (leftRoomEnt) {
				//Check the second Location on its Path. If the passed Location is equivalent, delete the first. Otherwise, add this to the start.
				entity.alterPathBeginning(entity.getLocation());
			}
			
			//If and only if the entity is alive, add it to the new list. Otherwise, remove it from both other lists.
			if (entity.getHealth()>0) {
				newList.add(new EntityAssociation(entity.getTicks(), entity));
			} else {
				boolean didRemove = entityInstances.remove(entity);
				if (debug) {
					print(didRemove ? "Successfully removed "+entity+"." : "ERROR: Entity "+entity+" not found.");
				}
				didRemove = entityByID.remove(entity.getID()).equals(entity);
				if (debug) {
					print(didRemove ? "Successfully removed "+entity+" from ID list." : "ERROR: Entity "+entity+" not found in ID list.");
				}
			}
		}
		
		//Now decrease everything that wasn't in that list by the appropriate amount.
		for (EntityAssociation assoc : nonmovers) {
			assoc.decreaseValue(lastTicks);
			assoc.getEntity().decreaseTicks(lastTicks);
			newList.add(assoc);
		}
		
		//Finally, replace the previous ticklist with the new one.
		entityByTicks = newList;
		
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
	
	public static TurnStatus move(Direction dir, EntityTile entity) {
		//TODO - remember that, before a big cleanup, this and playerMove returned a Pair<Boolean, Boolean>, which was whether the player moved and whether the player moved to a new location.
		//This enum now returns whether the player moved, fought or hit a wall. Later I should add to it MOVED_LOC, which means the player changed locations.
		Location loc = entity.getLocation();
		
		//TODO - this is an absolute mess, clean it up! (Put most things into separate functions)
		//TODO - remove extraneous stuff, no need to check if the player's in a different room because there aren't any.
		
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
				return TurnStatus.MOVED;
			} else {
				print(entity.getName()+" cannot move to "+nx+", "+((newxy - nx) >> 8)+" in location "+loc.getName());
				return TurnStatus.BLOCKED;
			}
		} else {
			//Otherwise, it's outside the current Location and should go to whatever Location is there.
			print ("Moving to new location");
			Location newloc = dungeon.getMapRel(loc, new Coord3D(nx, ny, loc.getZ()));
			entity.setX(nx);
			entity.setY(ny);
			loc.removeEntity(entity);
			try {
				newloc.addEntity(entity);
				print ("New map info: "+newloc.toString());
			} catch (NullPointerException e) {
				//In this case, the player has made an illegal move - shunt them to the Between Ford.
				between = true;
				return TurnStatus.MOVED_BETWEEN;
			}
			return TurnStatus.MOVED_LOC;
		}
	}
	
	private static int mod(int i) {
		//Is i > 0? If so, return i % 256. Else return i % 256 + 256
		return i > 0 ? i % 256 : i % 256 + 256;
	}
	
	public static TurnStatus playerMove(Direction dir) {
		if (canMove(dir, self)) {
			return move(dir, self);
		} else if (cloc.getTile(self.getCoords().shift(dir).toSingleVal()).isTraversable(self)) {
			fight (self, cloc.entityAt(self.getCoords().shift(dir)).getRight());
			return TurnStatus.FOUGHT;
		}
		return TurnStatus.BLOCKED;
	}
	
	public static void moveToPlayer(EntityTile entity) {
		//Move into a position around the player, respecting other enemy positions.
		if (entity.getCoords().distanceTo(self.getCoords())>=1.5) {
			//Move towards the player.
			//TODO - respect other entities, attempt to move to a location where they aren't in the way so you block the player from moving.
			//In other words, make the target not equal to the player.
			
			//Find which of the 8 directions surrounding the player are already taken, and move to one which isn't.
			//TODO - see whether this is fairer or all 8 directions. This is probably better since it allows players to get breathing room more easily.
			
			Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH};//Direction.class.getEnumConstants();
			Coord2D playerCoords = self.getCoords();
			double minDist = Integer.MAX_VALUE;
			Coord2D target = null;
			
			for (Direction dir : directions) {
				Coord2D shiftedCoords = playerCoords.shift(dir);
				//If there is no entity at this particular location (next to the player), and it is closer than the current one...
				if (!cloc.entityAt(shiftedCoords).getLeft() && entity.getCoords().distanceTo(shiftedCoords)<minDist) {
					//Set it as the new target.
					target = playerCoords.shift(dir);
					minDist = entity.getCoords().distanceTo(shiftedCoords);
				}
			}
			
			moveTo(entity, target);
		} else {
			//If within range, attack the player.
			fight(entity, self);
		}
	}
	
	public static boolean moveTo(EntityTile entity, Coord target) {
		//If the coast is clear, move in a particular direction. If not, use A* to find your way.
		//TODO - once awkward mazes are possible, rewrite this to include A*.
		
		//Finds where it needs to go, checks the distance to that place.
		//Tries to head straight for it.
		//If that is blocked for whatever reason, tries to head diagonally or whatever towards that location - basically, aims to minimise the distance.
		
		//Check if the distance to the target is (basically) 0 - it shouldn't ever be, so throw an error if it is.
		boolean isTooClose = entity.getCoords().distanceTo(target) < 0.001;
		
		if (isTooClose) {
			if (debug) {
				print("The entity "+entity.getName()+" is at its target, and that means \"moveTo\" shouldn't have been called.");
				print("Check the distance BEFORE this method. Exiting in 10 seconds.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				// 	TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(12);
			} else {
				print ("Error: \"moveTo\" called when it shouldn't have been, run in debug mode for more info.");
			}
		}
		
		//Get the distance when moving in all 8 directions.
		int coords = entity.getCoords().toSingleVal();
		Direction[] directions = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST,
				Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
		double minDist = Integer.MAX_VALUE;
		
		Direction direction = null;
		for (int i=0; i<8; i++) {
			double distance = Coord2D.fromSingleVal(coords+directions[i].getNumVal()).distanceTo(target);
			if (canMove(directions[i], entity) && distance <= minDist) {
				if (distance < minDist) {
					minDist = distance;
					direction = directions[i];
				} else {
					//If the 2 distances are equidistant, randomly select one of them.
					Random rand = new Random();
					if (rand.nextBoolean()) {
						direction = directions[i];
					}
				}
			}
		}
		if (direction != null) {
			move(direction, entity);
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean canMove(Direction direction, EntityTile entity) {
		//True iff there are no entities in the way and the tile is traversable.
		//TODO - this'll be called a lot, so I'll probably need to optimise it to shit.
		//TODO - there's a check in the move method for this, too. See if both are necessary.
		int newLoc = entity.getCoords().toSingleVal()+direction.getNumVal();
		for (EntityTile entityO : cloc.getEntities()) {
			if (newLoc == entityO.getCoords().toSingleVal()) {
				return false;
			}
		}
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
						System.out.println("Entity not created: "+entity.getName());
					}
					entityTypes.put(entity.getName(), entity);
				}
				else if (strs[i].trim().equals("")) {
					i++;
				}
				else {
					System.out.println(
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
				if(name.endsWith("Gate")) {
					float scaleFactor = 0.7f;
					RescaleOp op = new RescaleOp(scaleFactor, 0, null);	
					try {
						BufferedImage img = ImageIO.read(new File(filepath));
						BufferedImage gateImg = op.filter(img, null);
						ArrayList<String> playerRestricted = new ArrayList<String>();
						playerRestricted.add("-");
						playerRestricted.add("Player");
						tiles.put(name, new TileType(name, gateImg, playerRestricted, null));
						
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Path of invalid material: "+filepath);
					}
				} else {
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
					ArrayList<String> allRestricted = new ArrayList<String>();
					allPermitted.add("All");
					tiles.put(name+" Wall", new TileType(name+" Wall", img, allRestricted, null));
					tiles.put(name+" Floor", new TileType(name+" Floor", floorImg, allPermitted, null));
					tiles.put(name+" Upward Stairway", new TileType(name+" Upward Stairway", upImg, allPermitted, null));
					tiles.put(name+" Downward Stairway", new TileType(name+" Downward Stairway", downImg, allPermitted, null));
					tiles.put(name+" Up/Down Stairway", new TileType(name+" Up/Down Stairway", upDownImg, allPermitted, null));
					tiles.put(name+" Upward Slope", new TileType(name+" Upward Slope", img, allPermitted, null));
					tiles.put(name+" Downward Slope", new TileType(name+" Downward Slope", img, allPermitted, null));
					tiles.put(name+" Pillar", new TileType(name+" Pillar", pillarImg, allRestricted, null));
				}
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
		entityTypes.put("Player", player);
		System.out.println("done.");
	}
	
	static void attack(int val, boolean firstDir) {
		if (firstDir) {
			attackValue = val*9;
			//And put the swipe previews up.
		} else {
			attackValue += val;
			//Actually perform the attack now that you have both inputs.
			//Command: get the attackValue'th value from the defence of a given defender, and the attack of you. This is so other entities can also attack without going through this method.
			if (mainImage.getTargetedEntity() == null) {
				print("No entity is attackable from here!");
			} else {
				command("att "+self.getID()+" "+mainImage.getTargetedEntity().getID()+" "+attackValue);
				attackValue = 0;
				//And remove the swipe previews.
			}
		}
	}
	
	private static void fight(EntityTile ent1, EntityTile ent2) {
		
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
			printStr += "you, ";
		else
			printStr += "the "+nameDef+", ";
		
		printStr += "dealing ";
		
		printStr += diff+" damage! ";
		
		//If defender dies
		if (defender.getHealth()<=0) {
			if (isDef)
				printStr += "You die!";
			else
				printStr += "The "+nameDef+" dies!";
			return true;
		}
		else {
			if (isDef)
				printStr += "You have ";
			else
				printStr += "The "+nameDef+" has ";
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
	
	public static void print(String s, boolean system) {
		if (system) {
			System.out.println(s);
		}
		//Try to draw to the main image. If it doesn't exist (such as, for testing purposes), print to the console.
		try {
			mainImage.drawInfo(s);
		} catch (NullPointerException e) {
			System.out.println("Screen has not yet been created. This may be intentional.");
		}
	}
	
	public static void print(String s) {
		print(s, systemPrint);
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
	
	private static boolean createEntity(String name, Location loc, int x, int y) {
		if (entityTypes.containsKey(name)) {
			EntityTile entity = new EntityTile(entityTypes.get(name), loc, (byte) x, (byte) y, null);
			boolean v = entityByTicks.add(new EntityAssociation(entity.getTicks(), entity));
			entityInstances.add(entity);
			entityByID.put(entity.getID(), entity);
			
			if (debug) {
				if (v) {
					System.out.println("New entity created: "+entity+" with ticks "+entity.getTicks());
				} else {
					System.out.println("WARNING: Entity just created is identical to one already on the board. Entity is "+entity+"");
				}
			}
			return true;
		} else {
			System.out.println("Entity with name "+name+" not found.\n"
					+ "Please note that names in the database may be different from the actual entity name; the database name should be used.\n"
					+ "Run with debug mode on to view the entire list of available entities.");
			if (debug) {
				System.out.println("List of entities:");
				for (String entity : entityTypes.keySet()) {
					System.out.println(entity+", where the entity's name is "+entityTypes.get(entity));
				}
			}
			return false;
		}
	}
	
	private static EntityTile createPlayer(int x, int y) {
		EntityTile player = new EntityTile(entityTypes.get("Player"), cloc, x, y, null);
		entityByTicks.add(new EntityAssociation(0, player));
		entityInstances.add(player);
		entityByID.put(player.getID(),  player);
		return player;
	}
	
	private static void fillLine(int x, int y) {
		TileType wall = tiles.get("Limestone 2 Wall");
		ArrayList<Integer> tiles = new ArrayList<Integer>();
		for (int i=50; i<=y; i++) {
			tiles.add(x);
			tiles.add(i);
		}
		for (int i=0; i<tiles.size(); i+=2) {
			arena.setTile(tiles.get(i), tiles.get(i+1), wall);
			arena.setTile(123-tiles.get(i), tiles.get(i+1), wall);
			arena.setTile(tiles.get(i), 140-tiles.get(i+1), wall);
			arena.setTile(123-tiles.get(i), 140-tiles.get(i+1), wall);
		}
	}

}