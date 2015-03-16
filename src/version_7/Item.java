package version_7;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

public class Item {

	private String name;
	private BufferedImage image;
	private Color colour;
	private Color backColour;
	private String description;
	private HashMap<String, Integer> stats;
	private boolean transparentBackground = false;
	
	public Item() {
		stats = new HashMap<String, Integer>();
	}
	
	public Item(String name, BufferedImage image, String description, HashMap<String, Integer> stats) {
		this.name = name;
		this.image = image;
		this.description = description;
		this.stats = stats;
	}
	
	//Setter and getter for the name
	public void addName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	
	//Setter and getter for the marker
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	
	//Setter and getter for the description
	public void addDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	
	
	//Setters and getter for the stats
	public void addStat(String str, int num) {
		this.stats.put(str, num);
	}
	
	public void addStat(Pair<String, Integer> pair) {
		this.stats.put(pair.getLeft(), pair.getRight());
	}
	
	public void addStats(HashMap<String, Integer> pairs) {
		for (Entry<String, Integer> pair : pairs.entrySet()) {
			this.stats.put(pair.getKey(), pair.getValue());
		}
	}
	
	public HashMap<String, Integer> getAllStats() {
		return stats;
	}
	
	public int getStat(String name) {
		return stats.get(name);
	}
	
	public void setColour(Color colour) {
		this.colour =colour;
	}
	public Color getColour() {
		return colour;
	}
	
	public void setBGColour(Color colour) {
		backColour = colour;
	}
	public Color getBGColour() {
		return backColour;
	}
	
	public void setTransparency(boolean transparent) {
		transparentBackground = transparent;
	}
	public boolean getTransparency() {
		return transparentBackground;
	}
	
	public Pair<Boolean, ItemTile> getTileAt(ArrayList<ItemTile> tiles, int x, int y) {
		for (ItemTile tile:tiles) {
			if (tile.getItem() == this && tile.getX() == x && tile.getY() == y) {
				return new Pair<Boolean, ItemTile>(true, tile);
			}
		}
		return new Pair<Boolean, ItemTile>(false, null);
	}
}
