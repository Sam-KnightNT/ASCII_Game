package version_7;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.swing.*;

public class GameImage extends JPanel {
	
	//private static String attackDir;
	//private static int swingDir;
	
	//I think this was used for hammer, sword etc
	//private static String attackType;
	private static boolean menuOnScreen = false;
	private static EntityTile targetedEntity = null;
	
	private static final long serialVersionUID = -2736624963222321827L;
	private BufferedImage mainPane;
	private BufferedImage ctrlPane;
	private BufferedImage locnPane;
	private BufferedImage cmbtPane;
	private BufferedImage behindMenu;
	private LinkedList<String> lines = null;

	private Graphics2D gMain;
	private Graphics2D gCtrl;
	private Graphics2D gLocn;
	private Graphics2D gCmbt;
	
	private int xDim, yDim;							//Overall size of the main screen
	protected final int X_UNIT = 20, Y_UNIT = 30;	//Size of a single tile
	private int xView, yView;						//Number of tiles in the whole view
	//private int z;								//Current slice of the map (unused)
	private Dungeon dungeon;
	private EntityTile player;
	
	private ArrayList<BufferedImage> dungeonSlices = new ArrayList<BufferedImage>();
	protected static final int INFO_WIDTH = 400;	//Width of the control pane
	private static final int CONTROL_GAP = 5;		//Distance between main pane and control pane
	private int locationPaneHeight = 20;
	private int combatPaneHeight = 20;
	
	public GameImage() {
		init();
	}
	
	public GameImage(Dungeon dungeon, int xArray, int yArray) {
		this.dungeon = dungeon;
		
		xDim = X_UNIT*xArray;
		yDim = Y_UNIT*yArray;
		setSize(xDim, yDim);
		setPreferredSize(new Dimension(xDim+INFO_WIDTH, yDim));
		xView = xArray;
		yView = yArray;
		
		mainPane = new BufferedImage(xDim, yDim, BufferedImage.TYPE_INT_RGB);											//mainPane is the screen where the action happens...
		ctrlPane = new BufferedImage(INFO_WIDTH, yDim, BufferedImage.TYPE_INT_RGB);										//controlPane is the side screen, featuring the turn order and so on...
		locnPane = new BufferedImage(xDim+INFO_WIDTH+CONTROL_GAP, locationPaneHeight, BufferedImage.TYPE_INT_RGB);		//locationPane is at the top, telling you your location and coordinates...
		cmbtPane = new BufferedImage(xDim+INFO_WIDTH+CONTROL_GAP, combatPaneHeight, BufferedImage.TYPE_INT_RGB);		//combatPane is at the bottom, showing your available moves.
		
		//Bake the dungeon tiles onto the dungeon BufferedImage.
		bakeDungeon();
		
		//boolean pressed = false;
		
		//this.setLayout(new FlowLayout());
		
		createInputMap();
		createActionMap();
		addListeners(); //There's a keyListener, a mouseListener, a mouseMotionListener and a componentListener for resizing.

		//So, hitting Q first will mean the thing's looking in the first 9 characters for what it needs.
		//What I need is:
		//If Shift and a direction are held, charge.
		//If Shift and a direction is pressed, set firstSwing to true, and add 9xn to the checkPosition, as well as putting up the swing preview.
		//If firstSwing is true and a direction is pressed, add n to the checkPosition and do the relevant attack.
		//TODO - what happens when you hold one button, then hold another button? Should you charge in the first direction?
		//Blocking mechanic works a bit differently in the full version.
		//Instead of being a strong defence, it reduces attack power. The amount reduced depends on your skill and chance.
		//A 100-skill blocker blocks 100% of damage 100% of the time. 50-skill blockers decrease attack power by an average of 8 points.
		//Enemies have a small amount of blocking directions usually - e.g. the Minotaur left-blocking blocks QAZ.
		//Players have a lot more - left-block blocks WQAZX. Guardians and bosses also have lots of block.
		//TODO - need to figure out how to balance that. I mean, if the blocker is unskilled, the entity shouldn't take full power attacks if they're in steel armour.
		//A neat idea - if you hold QE at the same time, maybe it could be basically charging towards W, while holding 2 weapons to QE, dealing damage to each of them?
		//I think the combat should be really bloody complex, so combos like this should be included.

		/* TODO allow charging attacks
		 * These will allow the player to get out of situations where they are surrounded, at the cost of a LOT of life (they'll be hit by most of the enemies).
		 * If you hold a direction for 2 seconds you can perform a charging attack, where you basically attempt to ram the enemy out of the way.
		 * If you fail, nothing happens. If you succeed barely (DIFF = 1) you push them 1 space back, deal minor damage and stun them a bit, allowing you to move diagonally out.
		 * If DIFF=2-5, you push them 2 spaces back, move one space forward, stun them for a longer time and deal increasing amounts of damage.
		 * DIFF=6-9 is 3 spaces back and longer stun.
		 * DIFF=10 (might be impossible) means you launch them 5 spaces back, move 2, stun for AGES and deal heavy damage.
		 * Since enemies will attempt to surround you, this is probably going to be used a fair bit.
		 * The cooldown is fairly large, however. Otherwise it'd be OP - just spam it.   
		 */
		
		//Now, finally, initialise the graphics and draw to the screen.
		init();
	}

	public void init() {
		gMain = mainPane.createGraphics();
		gCtrl = ctrlPane.createGraphics();
		gLocn = locnPane.createGraphics();
		gCmbt = cmbtPane.createGraphics();
		setPlayer(GameClass.self);
		lines = new LinkedList<String>();
		/*try {
			//gSide.drawImage(ImageIO.read(new File("images/Menu Background.png")), 0, 0, null);
		} catch (IOException e) {
			gSide.drawString("Warning: Menu background not found.", 0, 0);
		}*/
		this.setVisible(true);
		redrawMap();
	}
	
	public void createInputMap() {
		
		//Movement
		this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
		this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
		this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
		this.getInputMap().put(KeyStroke.getKeyStroke("HOME"), "up-left");
		this.getInputMap().put(KeyStroke.getKeyStroke("PAGE_UP"), "up-right");
		this.getInputMap().put(KeyStroke.getKeyStroke("END"), "down-left");
		this.getInputMap().put(KeyStroke.getKeyStroke("PAGE_DOWN"), "down-right");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD8"), "up");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD2"), "down");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD4"), "left");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD6"), "right");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD7"), "up-left");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD9"), "up-right");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD1"), "down-left");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD3"), "down-right");
		this.getInputMap().put(KeyStroke.getKeyStroke("NUMPAD5"), "wait");
		
		//Used for examples of the old combat system, if I ever want to go back to that
		//this.getInputMap().put(KeyStroke.getKeyStroke("shift Q"), "attack ul");
		

		this.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "space"); //Will eventually have various functions, possibly
		this.getInputMap().put(KeyStroke.getKeyStroke('p'), "pick up");
		this.getInputMap().put(KeyStroke.getKeyStroke("shift C"), "change"); //Debug - change one tile into another
		this.getInputMap().put(KeyStroke.getKeyStroke('c'), "controls");
		this.getInputMap().put(KeyStroke.getKeyStroke('z'), "prime"); //Debug - currently does nothing, will be used to reproduce situations that lead to bugs (e.g. combination of Entities that break pathfinding)
		this.getInputMap().put(KeyStroke.getKeyStroke('i'), "inventory");
		this.getInputMap().put(KeyStroke.getKeyStroke('g'), "getInv");
		this.getInputMap().put(KeyStroke.getKeyStroke('m'), "mix"); //Potion mixing - currently not in the game.
		this.getInputMap().put(KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE), "exit");

	}
	
	public void bakeDungeon() {
		//Find the maximum depth this dungeon goes to. Currently always 1 because z-levels aren't in the game yet.
		int maxDepth = 0;
		for (Map map : dungeon.getMaps().keySet()) {
			if (map.getZ() > maxDepth) {
				maxDepth = map.getZ();
			}
		}
		//Create n dungeonSlices, where n is the lowest z-level in the dungeon.
		for (int i = 0; i <= maxDepth; i++) {
			dungeonSlices.add(new BufferedImage(10000, 10000, BufferedImage.TYPE_INT_RGB));
		}
		
		//Bake the dungeon onto the dungeon image.
		for (Map map : dungeon.getMaps().keySet()) {
			Graphics2D g = dungeonSlices.get(map.getZ()).createGraphics();
			for (int y=0; y<map.getH(); y++) {
				for (int x=0; x<map.getW(); x++) {
					TileType tile = map.getTile(x, y).getType();
					
					int y2 = y+map.getY();
					int x2 = x+map.getX();
					//TODO - check which is more efficient. This current method, or
					//BufferedImage img = tile.getImage();
					//g.drawImage(img.getSubimage(20*x % img.getWidth(), 30*y % img.getHeight(), 20, 30), (x*xUnit)+offsetX+offsetX2, (y*yUnit)+offsetY+offsetY2, null);
					
					int wImg = tile.getImage().getWidth();
					int hImg = tile.getImage().getHeight();
					//For the maps, draw a small portion of the thing in each map. So, take the map's position, take it away from the player map's position. Then take the player's position off THAT.
					//Then draw whatever stays in the frame - based on the resolution.
					
					//If the textures are not a multiple of 20x30, complain about them.
					//TODO - allow repeated textures that aren't a multiple of 20x30? Dunno if I want that to be allowed, since it'll look weird if it's a regular pattern.
					try {
					BufferedImage img = tile.getImage().getSubimage(20*x2 % wImg, 30*y2 % hImg, 20, 30);
					g.drawImage(img, x2*X_UNIT, y2*Y_UNIT, null);
					} catch (RasterFormatException e) {
						System.err.print("Tile "+tile.getName()+" is not in the required format; ");
						if (wImg % 20 != 0) {
							System.err.print("the width needs to be a multiple of 20 (currently "+wImg+")");
							if (hImg % 30 != 0) {
								System.err.print(" and ");
							}
						}
						if (hImg % 30 != 0) {
							System.err.print("the height needs to be a multiple of 30 (currently "+hImg+")");
						}
						System.err.println(".");
						System.exit(1);
					}
				}
			}
		}
	}
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	public void setPlayer(EntityTile player) {
		this.player = player;
	}
	
	public void createActionMap() {
		
		this.getActionMap().put("controls", new AbstractAction() {
			private static final long serialVersionUID = -2009680800825896652L;

			public void actionPerformed(ActionEvent e) {
				if (!menuOnScreen) {
					displayControls();
				}
			}
		});
		
		this.getActionMap().put("pick up", new AbstractAction() {
			private static final long serialVersionUID = -7471445489039297912L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("p");
			}
		});
		
		this.getActionMap().put("up", new AbstractAction() {
			private static final long serialVersionUID = -7471445489039297912L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mn");
			}
		});

		this.getActionMap().put("down", new AbstractAction() {
			private static final long serialVersionUID = -7850831045227052125L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("ms");
			}
		});

		this.getActionMap().put("left", new AbstractAction() {
			private static final long serialVersionUID = -7391545913153485894L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mw");
			}
		});

		this.getActionMap().put("right", new AbstractAction() {
			private static final long serialVersionUID = -3837103569154536260L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("me");
			}
		});
		
		this.getActionMap().put("up-left", new AbstractAction() {
			private static final long serialVersionUID = -3164380163562077592L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mnw");
			}
		});
		
		this.getActionMap().put("up-right", new AbstractAction() {
			private static final long serialVersionUID = -3132645763777895288L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mne");
			}
		});

		this.getActionMap().put("down-left", new AbstractAction() {
			private static final long serialVersionUID = 6230524046884637525L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("msw");
			}
		});

		this.getActionMap().put("down-right", new AbstractAction() {
			private static final long serialVersionUID = -4038166830328845870L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mse");
			}
		});

		this.getActionMap().put("wait", new AbstractAction() {
			private static final long serialVersionUID = -1079775521788366681L;

			public void actionPerformed(ActionEvent e) {
				GameClass.command("wait");
			}
		});
		
		this.getActionMap().put("change", new AbstractAction() {
			private static final long serialVersionUID = -2549505830577275314L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("cb Marble Wall "+(player.getX()+1)+" "+player.getY());
			}
		});

		this.getActionMap().put("exit", new AbstractAction() {
			private static final long serialVersionUID = -2549505830577275314L;
			
			public void actionPerformed(ActionEvent e) {
				if (menuOnScreen) {
					hideControls();
				} else {
					System.exit(1);
				}
			}
		});

		this.getActionMap().put("getInv", new AbstractAction() {
			private static final long serialVersionUID = -2549505830577275314L;
			
			public void actionPerformed(ActionEvent e) {
				dispInventory();
				/*TODO - have a temporary KeyboardListener here.
				frame.addKeyListener(new KeyListener() {
					public void keyTyped(KeyEvent e) {
						if (!firstSwing) {
							switch (e.getKeyChar()) {*/
			}
		});

		this.getActionMap().put("inventory", new AbstractAction() {
			private static final long serialVersionUID = 6230524046884637525L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("inventory");
				dispInventory();
			}
		});

		this.getActionMap().put("mix", new AbstractAction() {
			private static final long serialVersionUID = -4038166830328845870L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mix");
			}
		});
		
		/*
		this.getActionMap().put("change", new AbstractAction() {
			private static final long serialVersionUID = -4015903698462993768L;

			public void actionPerformed(ActionEvent e) {
				pressed = true;
			}
		});*/
	}
	
	public void addListeners() {
		
		this.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				//GameClass.print("Typed: "+e+"\n"+e.getKeyCode()+", "+e.getKeyChar());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				//GameClass.print("Pressed: "+e+"\n"+e.getKeyCode()+", "+e.getKeyChar()+", "+e.getKeyLocation());
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==16) {
					GameClass.print("Released Shift");
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Click at "+e.getPoint().toString()+", "+xView+", "+yView);
				Coord pLoc = player.getCoords();
				int x = Math.floorDiv(e.getPoint().x, X_UNIT)+pLoc.x-(xView/2);
				int y = Math.floorDiv(e.getPoint().y, Y_UNIT)+pLoc.y-(yView/2);
				int val = x + (y << 8);
				Pair<Boolean, EntityTile> entity = player.getLocation().entityAt(x, y); 
				if (entity.getLeft()) {
					GameClass.print("Clicked on "+x+", "+y+" which is a "+player.getLocation().getTile(val)+". There is a "+entity.getRight()+" here.");
				} else {
					GameClass.print("Clicked on "+x+", "+y+" which is a "+player.getLocation().getTile(val));
				}
			}
			
		});
		
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				System.out.println("Dragged!");
			}
			public void mouseMoved(MouseEvent e) {
				System.out.println("Moved to "+e.getPoint().toString()+", "+xView+", "+yView);
				Coord pLoc = player.getCoords();
				int x = Math.floorDiv(e.getPoint().x, X_UNIT)+pLoc.x-(xView/2);
				int y = Math.floorDiv(e.getPoint().y, Y_UNIT)+pLoc.y-(yView/2);
				int val = x + (y << 8);
				Pair<Boolean, EntityTile> entity = player.getLocation().entityAt(x, y); 
				if (entity.getLeft()) {
					GameClass.print("This is a "+player.getLocation().getTile(val)+". There is a "+entity.getRight()+" here.");
				} else {
					GameClass.print("Moved to "+x+", "+y+" which is a "+player.getLocation().getTile(val));
				}
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(mainPane, 0, locationPaneHeight, null);
		g.drawImage(ctrlPane, mainPane.getWidth()+CONTROL_GAP, 0, null);
		g.drawImage(locnPane, 0, 0, null);
		g.drawImage(cmbtPane, 0, yDim+locationPaneHeight, null);
	}
	
	public void drawInfo(String info) {
		//Draw up to 30 lines of information in the status log.
		gCtrl.setColor(Color.WHITE);
		if (lines.size()>=30) {
			lines.pop();
			lines.add(info);
			gCtrl.clearRect(0, 300, INFO_WIDTH, 455);
		} else {
			lines.add(info);
		}
		for (int i=0; i<lines.size(); i++) {
			gCtrl.drawString(lines.get(i), 0, 315+(15*i));
		}
	}
	
	public void drawLocation(int x, int y) {
		//Draws the specified location to the top of the screen.
		gLocn.setColor(new Color(150, 150, 255));
		gLocn.clearRect((int) (INFO_WIDTH*.4), 0, 100, 15);
		gLocn.drawString("("+x+", "+y+")", (int) (INFO_WIDTH*0.4), 10);
	}
	
	public void drawEquipment(EntityTile player) {
		//Draws the player's equipment below the status info.
		int l = 0;
		for (Item item : player.getInventory()) {
			gCtrl.drawString(item.getName(), 15, 30+(l*15));
		}
	}
	
	public void redrawMap() {
		gMain.setColor(Color.BLACK);
		gMain.clearRect(0, 0, mainPane.getWidth(), mainPane.getHeight());
		
		int offsetX = (int) ((mainPane.getWidth()/2.0f)-(2+player.getX())*X_UNIT);
		int offsetY = (int) ((mainPane.getHeight()/2.0f)-(2+player.getY())*Y_UNIT);
		
		//Draw dungeon slice first, draw entities and items on top
		byte h = (byte) Math.min(dungeon.getH(), player.getY()+(yView/2)+2);
		byte w = (byte) Math.min(dungeon.getW(), player.getX()+(xView/2)+2);
		
		//Is this thing greater than 0? If so, set it equal. Otherwise, set it equal to 0.
		//"This thing" is the location of the player in the map, plus the location of the map in the dungeon, multipled by the number of pixels each unit takes up,
		//then shifted as if that point described is in the centre of the screen - i.e. moving half a screen left/up.
		int x = X_UNIT*(player.getX()+player.getLocation().getX()-(xView/2));
		int y = Y_UNIT*(player.getY()+player.getLocation().getY()-(yView/2));
		int xDraw = (x > 0 ? x : 0);
		int yDraw = (y > 0 ? y : 0);
		
		gMain.drawImage(dungeonSlices.get(0).getSubimage(xDraw, yDraw, mainPane.getWidth(), mainPane.getHeight()), (x < 0 ? -x : 0), (y < 0 ? -y : 0), null);
		//TODO - try storing the maps as a series of pixels, each one representing a different Tile, which is then expanded when the slice is loaded, if this takes too much memory.
		//This is stored in memory, and the appropriate part is drawn on screen in one big chunk, followed by entities/items.
		//Every time the player modifies the dungeon, the small part of the image is remade.
		
		//Alternatively, TODO - once it's all drawn, store it in g, then only delete part of it, shift it in a direction and draw the new column or row that's revealed.
		
		for (ItemTile itemT : dungeon.getItems()) {
			Item item = itemT.getItem();
			gMain.drawImage(item.getImage(), (itemT.getX()*X_UNIT)+offsetX, (itemT.getY()*Y_UNIT)+offsetY, null);
		}
		for (EntityTile entityT : dungeon.getEntities()) {
			Entity entity = entityT.getEntity();
			gMain.drawImage(entity.getImage(), (entityT.getX()*X_UNIT)+offsetX, (entityT.getY()*Y_UNIT)+offsetY, null);
		}
		for (Location location : dungeon.getMaps().keySet()) {
			int offsetX2 = 30;
			int offsetY2 = 45;
			h = (byte) Math.min(location.getH(), player.getY()+(yView/2)+2);
			w = (byte) Math.min(location.getW(), player.getX()+(xView/2)+2);
			for (ItemTile itemT : location.getItems()) {
				Item item = itemT.getItem();
				gMain.drawImage(item.getImage(), (itemT.getX()*X_UNIT)+offsetX+offsetX2, (itemT.getY()*Y_UNIT)+offsetY+offsetY2, null);
			}
			for (EntityTile entityT : location.getEntities()) {
				Entity entity = entityT.getEntity();
				gMain.drawImage(entity.getImage(), (entityT.getX()*X_UNIT)+offsetX+offsetX2, (entityT.getY()*Y_UNIT)+offsetY+offsetY2, null);
			}
		}
		
		//This is useful for reference.
		/*try {
			g.drawImage(ImageIO.read(new File("images/masks/bigmask.png")), 400, 300, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		drawLocation(player.getX(), player.getY());
	}
	//TODO add dynamic textures
	//2 coordinates - old pair, EntityTile
	public void redrawMap(HashMap<Triplet<Integer, Tile, Integer>, EntityTile> changes) {
		for (Entry<Triplet<Integer, Tile, Integer>, EntityTile> entry : changes.entrySet()) {
			EntityTile entity = entry.getValue();
			Triplet<Integer, Tile, Integer> coords = entry.getKey();
		
			int x = entity.getX();
			int y = entity.getY();
			
			//Draw the map's tile thingy in the old location
			gMain.drawImage(coords.getMiddle().getImage(), null, coords.getLeft(), coords.getRight());
			gMain.drawImage(entity.getImage(), null, x*X_UNIT, y*Y_UNIT);
		}
	}
	
/*	public void zLevel(int z) {
		this.z = z;
	}*/
	
	public void setInfo(String info) {
		gCtrl.setColor(Color.WHITE);
		//TODO - add to the control pane, a double- or triple-size display which is either the 3x3 or 5x5 around the player.
		//This should display stuff like the direction you are facing, any swipes that take place and things that are closely relevant to melee combat.
		gCtrl.drawString(info, 20, 20);
	}
	
	public void drawTurnOrder(TreeSet<EntityAssociation> entities) {
		//For each entity in the set, draw it.
		gCtrl.clearRect(20, 50, INFO_WIDTH-50, entities.size()*26);
		int entNo = 0;
		for (EntityAssociation eStat : entities) {
			EntityTile entity = eStat.getEntity();
			
			//Draw the background shape with a gradient - same colour as the entity, but fading from left to right.
			Color colour = entity.getColour();
			Color a = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 150);
			Color b = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 50);
			GradientPaint grad = new GradientPaint(0, 0, a, INFO_WIDTH-50, 0, b);
	        gCtrl.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			gCtrl.setPaint(grad);
			gCtrl.fillRect(20+entNo*10, 50+entNo*26, INFO_WIDTH-50-entNo*10, 25);
			
			//Write the entity name in this shape, with a black border.
			Font font = new Font("Calibri", Font.BOLD, 22);
			GlyphVector gve = font.createGlyphVector(gMain.getFontRenderContext(), entity.getName());
			GlyphVector gvt = font.createGlyphVector(gMain.getFontRenderContext(), "" + entity.getTicks());
			Shape entityTextShape = gve.getOutline();
			Shape tickTextShape = gvt.getOutline();
			
			gCtrl.translate(25+entNo*10, 69+entNo*26);
			gCtrl.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
			gCtrl.setPaint(Color.BLACK);
			gCtrl.draw(entityTextShape);
			gCtrl.setPaint(Color.WHITE);
			gCtrl.fill(entityTextShape);
			gCtrl.translate(305-(entNo*10), 0);
			gCtrl.setPaint(Color.BLACK);
			gCtrl.draw(tickTextShape);
			gCtrl.setPaint(Color.WHITE);
			gCtrl.fill(tickTextShape);
			gCtrl.translate(-330, -(69+entNo*26));
			entNo++;
		}
		//TODONEXT
		//Add a line saying "Press Q to view controls", and pressing Q opens a box in the centre of the screen saying the controls.
		//Add a bar at the top (or bottom) which shows the attacks you can do with your weapons. First version should only have 1 attack - "Push".
		//Push should move the enemy you select back 2 tiles and reset their move.
		//The bar should have a list, arrayed horizontally - like this.
		//"1. Push		2. Swipe		3. Lunge"
		//And if you hit 1, it should display the next menu with the list of enemies (with their colours) replacing the list of attacks. This should happen even if there's only one enemy.
		//It should show the battle preview in real-time, i.e. show your next move moving up and the enemy turn moving down as soon as you select the attack.
		//It will preview the first enemy - so you select "Push", it moves yours and the first enemy's turns around, but you can also mouseover the enemy names to highlight the enemy, and change the preview to be "attack that enemy".
		//The "first" enemy is the one that's moving next, so that players can see before they strike what order the enemies will be in on the menu.
		//Once you select that move, obviously it'll take place.
		//After that, implement the ability to hold shift to select different weapons. There are only 2 for the preview - Sword and Hammer.
		//The Sword's moves are Swipe, Lunge, Parry and Vault.
		//Swipe simply deals a good amount of damage (which isn't implemented yet).
		//Lunge moves you one square forward, hitting anything up to 2 squares away. If there's an enemy right in front of you, you push them and still move but you have a longer recovery time.
		//Parry waits a certain amount of time, and if an enemy should attack in that time, you dodge out of the way and hit them instead.
		//Vault sends you over the enemy, knocking them to the position you were at and moving you to the square behind where they were. I imagine the sort of vault-kick-diving-board move from... Spiderman 2 I think? Or Batman?
		//The Hammer only has 2 attacks: Smash and Swing. Push is still available.
		//Smash hits directly in front, stunning the target and leaving them unable to move for about 300 ticks.
		//Swing hits the 3 tiles in front, knocking every enemy in those 3 tiles to the left or right, depending on the direction. If you're swiping right and there's an enemy at W, it'll be pushed to R.
		//If there's one at Q and another at E, they'll be moved to E and R respectively.
		//Once all this is done, another update is in order. Show off the menus and updated UI, basically. Ask Squiggs how he thinks I should advertise. Maybe set up a subreddit and chart my progress.
	}
	
	public void dispInventory() {
		gCtrl.setColor(new Color(250, 120, 150));
		ArrayList<Item> inv = player.getInventory();
		gCtrl.clearRect(0, 490, INFO_WIDTH, inv.size()*15);
		for (int i=0; i<inv.size(); i++) {
			gCtrl.drawString(inv.get(i).getName(), 0, 490+(i*15));
		}
	}
	
	private void displayControls() {
		menuOnScreen = true;
		GameClass.print("Displaying menu.");
		int x = 450;
		int y = 500;
		int w = 100;
		int h = 100;
		//TODO NEXT TIME
		//Get the menu from the images, put it on the thing and add the controls.
		//Also see what can be done about variable sized windows.
		behindMenu = new BufferedImage(mainPane.getColorModel(), mainPane.copyData(null), mainPane.isAlphaPremultiplied(), null);
		behindMenu = behindMenu.getSubimage(x, y, w, h);
		gMain.drawImage(behindMenu, x, y-250, w, h, null);
		gMain.setColor(Color.BLACK);
		gMain.fillRect(x, y, w, h);
		gMain.setColor(Color.WHITE);
		gMain.drawString("MENU HERE", x+10, y+(h/2));
		this.repaint(x, y, w+1, h+1);
		this.repaint(x, y-250, w+1, h+1);
	}
	
	private void hideControls() {
		menuOnScreen = false;
		GameClass.print("Hiding menu.");
		gMain.drawImage(behindMenu, 100, 100, 100, 100, null);
		this.repaint(100, 100, 101, 101);
	}

	public void debugDraw(BufferedImage img) {
		int xSize = img.getWidth();
		int ySize = img.getHeight();
		//Scale the image so that it fits properly into the space
		AffineTransform t = AffineTransform.getScaleInstance(200/xSize, 300/ySize);
		BufferedImage i = new BufferedImage(200, 300, BufferedImage.TYPE_INT_ARGB);
		i.createGraphics().drawRenderedImage(img, t);
		
		//TODO once it changes to a 60FPS clock - make the background constantly change, with options to set it at any RGB value
		
		//Put the 6 backgrounds in - white, blue, black, green, red, yellow
		gCtrl.setColor(Color.WHITE);
		gCtrl.fillRect(0, 0, 200, 300);
		gCtrl.setColor(Color.BLUE);
		gCtrl.fillRect(200, 0, 200, 300);
		gCtrl.setColor(Color.BLACK);
		gCtrl.fillRect(0, 300, 200, 300);
		gCtrl.setColor(new Color(10, 150, 10));
		gCtrl.fillRect(200, 300, 200, 300);
		gCtrl.setColor(new Color(160, 10, 10));
		gCtrl.fillRect(0, 600, 200, 300);
		gCtrl.setColor(new Color(150, 150, 10));
		gCtrl.fillRect(200, 600, 200, 300);
		
		gCtrl.drawImage(i, 0, 0, null);
		gCtrl.drawImage(i, 200, 0, null);
		gCtrl.drawImage(i, 0, 300, null);
		gCtrl.drawImage(i, 200, 300, null);
		gCtrl.drawImage(i, 0, 600, null);
		gCtrl.drawImage(i, 200, 600, null);
	}

	public void setCombatPortrait(BufferedImage portrait) {
		gCtrl.drawImage(portrait, 0, 600, null);
	}
	
	public EntityTile getTargetedEntity() {
		return targetedEntity;
	}
}
