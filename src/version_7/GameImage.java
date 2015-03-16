package version_7;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.*;

public class GameImage extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2736624963222321827L;
	private BufferedImage mainPane;
	private BufferedImage controlPane;

	private Graphics2D g;
	private Graphics2D gSide;
	private int xDim, yDim;			//Overall size of the image
	private int xUnit = 20, yUnit = 30;		//Size of a single character
	private int xArray, yArray;		//Size of the grid
	private int z;					//Current slice of the map
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
		this.xArray = xArray;
		this.yArray = yArray;
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
	}
	
	public void init() {
		g = mainPane.createGraphics();
		gSide = controlPane.createGraphics();
		this.setVisible(true);
		redrawMap();
	}
	
	public void redrawMap() {
		for (Location location : locations) {
			int dx = cloc.getX()-location.getX();
			int dy = cloc.getY()-location.getY();
			int h = location.getH();
			int w = location.getW();
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					TileType tile = location.getTile(x, y).getType();
					g.setColor(tile.getColour());
					g.setBackground(tile.getBG());
					//TODO next: Draw it with an offset that's intelligently devised - i.e. that places the centre of the room in the centre of the view, rather than the corner
					g.drawImage(tile.getImage(), ((x+dx)*xUnit)+(getWidth()/2), ((y+dy)*yUnit)+(getHeight()/2), null);
				}
			}
		}
		
		for (ItemTile itemT : cloc.getItems()) {
			Item item = itemT.getItem();
			g.setColor(item.getColour());
			if (item.getTransparency()) {
				g.setBackground(cloc.getTile(itemT.getX(), itemT.getY()).getType().getBG());
			} else {
				g.setBackground(item.getBGColour());
			}
			g.drawImage(item.getImage(), itemT.getX()*xUnit+(getWidth()/2), itemT.getY()*yUnit+(getHeight()/2), null);
		}
		
		for (EntityTile entityT : cloc.getEntities()) {
			Entity entity = entityT.getEntity();
			g.setColor(entity.getColour());
			if (entity.getTransparency()) {
				g.setBackground(cloc.getTile(entityT.getX(), entityT.getY()).getType().getBG());
			} else {
				g.setBackground(entity.getBGColour());
			}
			g.drawImage(entity.getImage(), entityT.getX()*xUnit+(getWidth()/2), entityT.getY()*yUnit+(getHeight()/2), null);
		}
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
	
	public void zLevel(int z) {
		this.z = z;
	}
	
	public void setInfo(String info) {
		gSide.drawString(info, 20, 20);
	}
}
