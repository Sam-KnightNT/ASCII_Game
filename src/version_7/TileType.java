package version_7;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class TileType {

	private int[] actions;
	private String name;
	private BufferedImage image;
	private boolean walkable;
	private Color colour;
	private Color background;
	
	public TileType(String name, BufferedImage image, boolean walkable, Color colour, Color BGColour, int[] properties, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image;
		this.walkable = walkable;
		this.colour = colour;
		background = BGColour;
	}
	
	public TileType(String name, BufferedImage[] image, int imageNum, boolean walkable, Color colour, Color BGColour, int[] properties, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image[imageNum];
	}
	
	public TileType() {
		
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public BufferedImage getImage() {
		return image;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}
	public boolean isWalkable() {
		return walkable;
	}
	
	public int[] getActions() {
		return actions;
	}
	
	public Color getColour() {
		return colour;
	}
	public void setColour(Color colour) {
		this.colour = colour;
	}
	
	public Color getBG() {
		return background;
	}
	public void setBG(Color colour) {
		background = colour;
	}
	
}
