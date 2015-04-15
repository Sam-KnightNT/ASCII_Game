package version_7;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
	private int xDim, yDim;			//Overall size of the image
	private int xUnit = 20, yUnit = 30;		//Size of a single character
	//private int z;					//Current slice of the map
	//Instead of a Map, have a list of Locations, one of these being the centralLocation.
	//On drawing, each of these Locations are drawn, relative to the centralLocation.
	private ArrayList<Location> locations;
	private Location cloc;
	//Rename these?
	
	public GameImage() {
		init();
	}
	
	public GameImage(ArrayList<Location> locations, Location location, int ptSize, int xArray, int yArray) {
		this.locations = locations;
		this.cloc = location;
		xDim = xUnit*xArray;
		yDim = yUnit*yArray;
		setSize(1040, 730);
		
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

			/**
			 * 
			 */

			public void actionPerformed(ActionEvent e) {
				GameClass.command("ms");
			}
		});

		this.getActionMap().put("left", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7391545913153485894L;

			/**
			 * 
			 */

			public void actionPerformed(ActionEvent e) {
				GameClass.command("mw");
			}
		});
		
		this.getActionMap().put("right", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3837103569154536260L;

			/**
			 * 
			 */

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
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				System.out.println("Dragged!");
			}
		});
		
		mainPane = new BufferedImage(xDim, yDim, BufferedImage.TYPE_INT_RGB);
		controlPane = new BufferedImage(xDim/2, yDim, BufferedImage.TYPE_INT_RGB);
		init();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(mainPane, 0, 0, null);
		g.drawImage(controlPane, 1050, 0, null);
	}
	
	public void init() {
		g = mainPane.createGraphics();
		gSide = controlPane.createGraphics();
		lines = new LinkedList<String>();
		this.setVisible(true);
		redrawMap();
	}
	
	public void drawInfo(String info) {
		if (lines.size()>=30) {
			lines.pop();
			lines.add(info);
			gSide.clearRect(0, 15, 300, 500);
		} else {
			lines.add(info);
		}
		for (int i=0; i<lines.size(); i++) {
			gSide.drawString(lines.get(i), 0, 30+(15*i));
		}
	}
	
	public void drawLocation(int x, int y) {
		gSide.clearRect(100, 0, 45, 15);
		gSide.drawString("("+x+", "+y+")", 100, 10);
	}
	
	public void drawEquipment(EntityTile player) {
		int l = 0;
		for (Item item : player.getInventory()) {
			gSide.drawString(item.getName(), 15, 30+(l*15));
		}
	}
	
	public void redrawMap() {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 1400, 1400);
		
		//If the current location is a Room, have it centre on the centre of the room. If it's a Corridor, have it centre on the player.
		int offsetX = (int) ((getWidth()/2.0f)-(cloc.getX()+(cloc.getW()/2.0f)*xUnit));
		int offsetY = (int) ((getHeight()/2.0f)-(cloc.getY()+(cloc.getH()/2.0f)*yUnit));
		
		//if (cloc instanceof version_7.Corridor) {
			offsetX = (int) ((getWidth()/2.0f)-(cloc.getEntityByName("Player").get(0).getX()*xUnit));
			offsetY = (int) ((getHeight()/2.0f)-(cloc.getEntityByName("Player").get(0).getY()*yUnit));
		//}
		for (Location location : locations) {
			int offsetX2 = (location.getX()-cloc.getX())*xUnit;
			int offsetY2 = (location.getY()-cloc.getY())*yUnit;
			byte h = location.getH();
			byte w = location.getW();
			for (byte y=0; y<h; y++) {
				for (byte x=0; x<w; x++) {
					TileType tile = location.getTile(x, y).getType();
					int xDraw = (x*xUnit)+offsetX+offsetX2;
					int yDraw = (y*yUnit)+offsetY+offsetY2;
					System.out.println(xDraw%20+", "+yDraw%30);
					//TODO - for tiled images, replace the 1st parameter with
					//tile.getImage().getSubimage(xDraw%20, yDraw%30, 20, 30)
					g.drawImage(tile.getImage(), (x*xUnit)+offsetX+offsetX2, (y*yUnit)+offsetY+offsetY2, null);
				}
			}
			for (ItemTile itemT : location.getItems()) {
				Item item = itemT.getItem();
				g.drawImage(item.getImage(), (itemT.getX()*xUnit)+offsetX+offsetX2, (itemT.getY()*yUnit)+offsetY+offsetY2, null);
			}
			for (EntityTile entityT : location.getEntities()) {
				Entity entity = entityT.getEntity();
				g.drawImage(entity.getImage(), (entityT.getX()*xUnit)+offsetX+offsetX2, (entityT.getY()*yUnit)+offsetY+offsetY2, null);
			}
		}
		
		
		this.paintComponent(g);
	}
	//TODO add dynamic textures, or large textures that take up (20n, 30n) and are wrappable
	//2 coordinates - old pair, EntityTile
	public void redrawMap(HashMap<Triplet<Integer, Tile, Integer>, EntityTile> changes) {
		for (Entry<Triplet<Integer, Tile, Integer>, EntityTile> entry : changes.entrySet()) {
			EntityTile entity = entry.getValue();
			Triplet<Integer, Tile, Integer> coords = entry.getKey();
		
			int x = entity.getX();
			int y = entity.getY();
			
			//Draw the map's tile thingy in the old location
			g.drawImage(coords.getMiddle().getImage(), null, coords.getLeft(), coords.getRight());
			g.drawImage(entity.getImage(), null, x*xUnit, y*yUnit);
		}
	}
	
	public void setCurrentLocation(Location location) {
		this.cloc = location;
	}
	
	public Image getImage() {
		return mainPane;
	}
	
	public IntPair getUnits() {
		return new IntPair(xUnit, yUnit);
	}
	
	public IntPair getDims() {
		return new IntPair(xDim, yDim);
	}
	
/*	public void zLevel(int z) {
		this.z = z;
	}*/
	
	public void setInfo(String info) {
		gSide.drawString(info, 20, 20);
	}
}
