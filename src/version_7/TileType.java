package version_7;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class TileType {

	private int[] actions;
	private String name;
	private BufferedImage image;
	private boolean walkable;
	
	public TileType(String name, BufferedImage image, boolean walkable, int[] properties, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image;
		this.walkable = walkable;
	}
	
	public TileType(String name, BufferedImage[] image, int imageNum, boolean walkable, int[] properties, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image[imageNum];
		this.walkable = walkable;
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
}
