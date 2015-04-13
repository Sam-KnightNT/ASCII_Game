package version_7;

import java.awt.image.BufferedImage;
import java.util.*;

public class Item {

	private String name;
	private BufferedImage image;
	private String description;
	
	public Item() {}
	
	public Item(String name, BufferedImage image, String description) {
		this.name = name;
		this.image = image;
		this.description = description;
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
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Pair<Boolean, ItemTile> getTileAt(ArrayList<ItemTile> tiles, int x, int y) {
		for (ItemTile tile:tiles) {
			if (tile.getItem() == this && tile.getX() == x && tile.getY() == y) {
				return new Pair<Boolean, ItemTile>(true, tile);
			}
		}
		return new Pair<Boolean, ItemTile>(false, null);
	}
	
	public String toString() {
		return name+": "+description;
	}
}
