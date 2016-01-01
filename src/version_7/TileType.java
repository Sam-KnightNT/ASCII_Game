package version_7;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TileType {

	private int[] actions;
	private String name;
	private BufferedImage image;
	private ArrayList<String> permittedEntities;
	
	public TileType(String name, BufferedImage image, ArrayList<String> permittedEntities, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image;
		this.permittedEntities = permittedEntities;
	}
	
	public TileType(String name, BufferedImage[] image, int imageNum, ArrayList<String> permittedEntities, int[] actions) {
		this.actions = actions;
		this.name = name;
		this.image = image[imageNum];
		this.permittedEntities = permittedEntities;
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
	
	public void setPermittedEntities(ArrayList<String> permittedEntities) {
		this.permittedEntities = permittedEntities;
	}
	public boolean isTraversable(EntityTile entity) {
		boolean containsEntity = permittedEntities.contains(entity.getName()) || permittedEntities.contains("All");
		//If permittedEntities contains a reverser, it instead becomes restrictedEntities.
		if (permittedEntities.contains("-")) {
			return !containsEntity;
		} else {
			return containsEntity;
		}
	}
	
	public int[] getActions() {
		return actions;
	}
	
	public String toString() {
		return "TileType: "+this.getName();
	}
}
