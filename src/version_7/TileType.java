package version_7;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TileType {

	private int[] actions;
	private String name;
	private BufferedImage image;
	private ArrayList<String> restrictedEntities;
	
	public TileType(String name, BufferedImage image, ArrayList<String> restrictedEntities, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image;
		this.restrictedEntities = restrictedEntities;
	}
	
	public TileType(String name, BufferedImage[] image, int imageNum, ArrayList<String> restrictedEntities, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image[imageNum];
		this.restrictedEntities = restrictedEntities;
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
	
	public void setRestrictedEntities(ArrayList<String> restrictedEntities) {
		this.restrictedEntities = restrictedEntities;
	}
	public boolean isTraversable(EntityTile entity) {
		if (restrictedEntities.isEmpty()) {
			return true;
		} else if (restrictedEntities.get(0).equals("All")) {
			return false;
		} else if (restrictedEntities.contains(entity.getName())) {
			return false;
		} else {
			return true;
		}
	}
	
	public int[] getActions() {
		return actions;
	}
	
	public String toString() {
		return "TileType: "+this.getName();
	}
}
