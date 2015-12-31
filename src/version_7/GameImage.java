package version_7;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.*;

public class GameImage extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2736624963222321827L;
	private BufferedImage mainPane;
	private BufferedImage controlPane;
	private LinkedList<String> lines = null;

	private Graphics2D g;
	private Graphics2D gSide;
	private int xDim, yDim;					//Overall size of the image
	protected final int X_UNIT = 20, Y_UNIT = 30;	//Size of a single character
	private int xView, yView;
	//private int z;						//Current slice of the map
	private Dungeon dungeon;
	private EntityTile player;
	//Rename these?
	
	private ArrayList<BufferedImage> dungeonSlices = new ArrayList<BufferedImage>();
	protected final int infoWidth = 300;
	
	public GameImage() {
		init();
	}
	
	public GameImage(Dungeon dungeon, int xArray, int yArray) {
		this.dungeon = dungeon;
		
		xDim = X_UNIT*xArray;
		yDim = Y_UNIT*yArray;
		setSize(xDim, yDim);
		xView = xArray;
		yView = yArray;
		
		mainPane = new BufferedImage(xDim, yDim, BufferedImage.TYPE_INT_RGB);
		controlPane = new BufferedImage(infoWidth, yDim, BufferedImage.TYPE_INT_RGB);
		
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
		
		
		//boolean pressed = false;
		
		this.setLayout(new FlowLayout());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "space");
		this.getInputMap().put(KeyStroke.getKeyStroke('p'), "pick up");
		this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
		this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
		this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
		this.getInputMap().put(KeyStroke.getKeyStroke('c'), "change");
		this.getInputMap().put(KeyStroke.getKeyStroke('z'), "prime");
		this.getInputMap().put(KeyStroke.getKeyStroke('i'), "inventory");
		this.getInputMap().put(KeyStroke.getKeyStroke('g'), "getInv");
		this.getInputMap().put(KeyStroke.getKeyStroke('m'), "mix");
		this.getInputMap().put(KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE), "exit");

		this.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.getActionMap().put("pick up", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7471445489039297912L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("p");
			}
		});
		
		this.getActionMap().put("up", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7471445489039297912L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mn");
			}
		});

		this.getActionMap().put("down", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7850831045227052125L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("ms");
			}
		});

		this.getActionMap().put("left", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7391545913153485894L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mw");
			}
		});
		
		this.getActionMap().put("right", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3837103569154536260L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("me");
			}
		});
		
		this.getActionMap().put("change", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2549505830577275314L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("cb Marble Wall "+(GameClass.getPX()+1)+" "+GameClass.getPY()+" "+GameClass.getPZ());
			}
		});

		this.getActionMap().put("exit", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2549505830577275314L;
			
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});

		this.getActionMap().put("getInv", new AbstractAction() {
			/**
			 * 
			 */
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
			/**
			 * 
			 */
			private static final long serialVersionUID = 6230524046884637525L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("inventory");
				dispInventory();
			}
		});

		this.getActionMap().put("mix", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4038166830328845870L;
			
			public void actionPerformed(ActionEvent e) {
				GameClass.command("mix");
			}
		});
		
		/*
		this.getActionMap().put("change", new AbstractAction() {

			/**
			 * 
			 *//*
			private static final long serialVersionUID = -4015903698462993768L;

			public void actionPerformed(ActionEvent e) {
				pressed = true;
			}
		});*/
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Click at "+e.getPoint().toString());
				int x = Math.floorDiv(e.getPoint().x, X_UNIT);
				int y = Math.floorDiv(e.getPoint().y, Y_UNIT);
				int val = x + (y >> 8);
				System.out.println("Clicked on "+x+", "+y+" which is a "+player.getLocation().getTile(val));
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				System.out.println("Dragged!");
			}
		});
		
		init();
	}
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	public void setPlayer(EntityTile player) {
		this.player = player;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(mainPane, 0, 0, null);
		g.drawImage(controlPane, mainPane.getWidth()+2, 0, null);
	}
	
	public void init() {
		g = mainPane.createGraphics();
		gSide = controlPane.createGraphics();
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
	
	public void drawInfo(String info) {
		//Draw up to 30 lines of information in the status log.
		gSide.setColor(Color.WHITE);
		if (lines.size()>=30) {
			lines.pop();
			lines.add(info);
			gSide.clearRect(0, 15, infoWidth, 455);
		} else {
			lines.add(info);
		}
		for (int i=0; i<lines.size(); i++) {
			gSide.drawString(lines.get(i), 0, 30+(15*i));
		}
	}
	
	public void drawLocation(int x, int y) {
		//Draws the specified location to the top of the screen.
		gSide.setColor(new Color(150, 150, 255));
		gSide.clearRect((int) (infoWidth*.4), 0, 100, 15);
		gSide.drawString("("+x+", "+y+")", (int) (infoWidth*0.4), 10);
	}
	
	public void drawEquipment(EntityTile player) {
		//Draws the player's equipment below the status info.
		int l = 0;
		for (Item item : player.getInventory()) {
			gSide.drawString(item.getName(), 15, 30+(l*15));
		}
	}
	
	public void redrawMap() {
		g.setColor(Color.BLACK);
		g.clearRect(0, 0, mainPane.getWidth(), mainPane.getHeight());
		
		int offsetX = (int) ((mainPane.getWidth()/2.0f)-player.getX()*X_UNIT);
		int offsetY = (int) ((mainPane.getHeight()/2.0f)-player.getY()*Y_UNIT);
		
		//Draw dungeon slice first, draw entities and items on top
		byte h = (byte) Math.min(dungeon.getH(), player.getY()+(yView/2)+2);
		byte w = (byte) Math.min(dungeon.getW(), player.getX()+(xView/2)+2);
		
		System.out.println("Drawing map: "+(20*(player.getX()+player.getLocation().getX()-(xView/2)-2))+", "+(30*(player.getY()+player.getLocation().getY()-(yView/2)-2)));
		System.out.println(dungeonSlices.get(0).getHeight());
		
		//Is this thing greater than 0? If so, set it equal. Otherwise, set it equal to 0.
		//"This thing" is the location of the player in the map, plus the location of the map in the dungeon, multipled by the number of pixels each unit takes up,
		//then shifted as if that point described is in the centre of the screen - i.e. moving half a screen left/up.
		int x = X_UNIT*(player.getX()+player.getLocation().getX()-(xView/2)-2);
		int y = Y_UNIT*(player.getY()+player.getLocation().getY()-(yView/2)-2);
		int xDraw = (x > 0 ? x : 0);
		int yDraw = (y > 0 ? y : 0);
		
		g.drawImage(dungeonSlices.get(0).getSubimage(xDraw, yDraw, mainPane.getWidth(), mainPane.getHeight()), (x < 0 ? -x : 0), (y < 0 ? -y : 0), null);
		//TODO - replace all this with a static image that is pre-calculated - i.e. the entire Dungeon. Each slice is stored as a big BufferedImage, 
		//OR, as a series of pixels, each one representing a different Tile, which is then expanded when the slice is loaded, if that takes too much memory.
		//This is stored in memory, and the appropriate part is drawn on screen in one big chunk, followed by entities/items.
		//Every time the player modifies the dungeon, the small part of the image is recalculated.
		
		//Alternatively, TODO - once it's all drawn, store it in g, then only delete part of it, shift it in a direction and draw the new column or row that's revealed.
		
		for (ItemTile itemT : dungeon.getItems()) {
			Item item = itemT.getItem();
			g.drawImage(item.getImage(), (itemT.getX()*X_UNIT)+offsetX, (itemT.getY()*Y_UNIT)+offsetY, null);
		}
		for (EntityTile entityT : dungeon.getEntities()) {
			Entity entity = entityT.getEntity();
			g.drawImage(entity.getImage(), (entityT.getX()*X_UNIT)+offsetX, (entityT.getY()*Y_UNIT)+offsetY, null);
		}
		for (Location location : dungeon.getMaps().keySet()) {
			int offsetX2 = 30;
			int offsetY2 = 45;
			h = (byte) Math.min(location.getH(), player.getY()+(yView/2)+2);
			w = (byte) Math.min(location.getW(), player.getX()+(xView/2)+2);
			for (ItemTile itemT : location.getItems()) {
				Item item = itemT.getItem();
				g.drawImage(item.getImage(), (itemT.getX()*X_UNIT)+offsetX+offsetX2, (itemT.getY()*Y_UNIT)+offsetY+offsetY2, null);
			}
			for (EntityTile entityT : location.getEntities()) {
				Entity entity = entityT.getEntity();
				g.drawImage(entity.getImage(), (entityT.getX()*X_UNIT)+offsetX+offsetX2, (entityT.getY()*Y_UNIT)+offsetY+offsetY2, null);
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
		this.paintComponent(g);
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
			g.drawImage(coords.getMiddle().getImage(), null, coords.getLeft(), coords.getRight());
			g.drawImage(entity.getImage(), null, x*X_UNIT, y*Y_UNIT);
		}
	}
	
	public Image getImage() {
		return mainPane;
	}
	
/*	public void zLevel(int z) {
		this.z = z;
	}*/
	
	public void setInfo(String info) {
		gSide.setColor(Color.WHITE);
		//TODO - add to the control pane, a double- or triple-size display which is either the 3x3 or 5x5 around the player.
		//This should display stuff like the direction you are facing, any swipes that take place and things that are closely relevant to melee combat.
		gSide.drawString(info, 20, 20);
	}
	
	public void dispInventory() {
		gSide.setColor(new Color(250, 120, 150));
		ArrayList<Item> inv = player.getInventory();
		gSide.clearRect(0, 490, infoWidth, inv.size()*15);
		for (int i=0; i<inv.size(); i++) {
			gSide.drawString(inv.get(i).getName(), 0, 490+(i*15));
		}
	}

	public void debugDraw(BufferedImage img) {
		//Make the image 10 times the size
		AffineTransform t = AffineTransform.getScaleInstance(10, 10);
		BufferedImage i = new BufferedImage(200, 300, BufferedImage.TYPE_INT_ARGB);
		i.createGraphics().drawRenderedImage(img, t);
		
		//TODO once it changes to a 60FPS clock - make the background constantly change, with options to set it at any RGB value
		
		//Put the 6 backgrounds in - white, blue, black, green, red, yellow
		gSide.setColor(Color.WHITE);
		gSide.fillRect(0, 0, 200, 300);
		gSide.setColor(Color.BLUE);
		gSide.fillRect(200, 0, 200, 300);
		gSide.setColor(Color.BLACK);
		gSide.fillRect(0, 300, 200, 300);
		gSide.setColor(new Color(10, 150, 10));
		gSide.fillRect(200, 300, 200, 300);
		gSide.setColor(new Color(160, 10, 10));
		gSide.fillRect(0, 600, 200, 300);
		gSide.setColor(new Color(150, 150, 10));
		gSide.fillRect(200, 600, 200, 300);
		
		gSide.drawImage(i, 0, 0, null);
		gSide.drawImage(i, 200, 0, null);
		gSide.drawImage(i, 0, 300, null);
		gSide.drawImage(i, 200, 300, null);
		gSide.drawImage(i, 0, 600, null);
		gSide.drawImage(i, 200, 600, null);
	}
}
