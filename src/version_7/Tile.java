package version_7;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Tile {

	TileType tile;
	
	public Tile(TileType type, int state) {
		tile = type;
	}
	
	public Tile(TileType type) {
		tile = type;
	}

	
	//TODO deprecate this, add dynamic textures
	public void setImage(BufferedImage image) {
		tile.setImage(image);
	}	
	public BufferedImage getImage() {
		return tile.getImage();
	}

	
	public TileType getType() {
		return tile;
	}

	public void performAction(int action) {
		boolean isValid = false;
		try {
			isValid = tile.getActions().toString().contains(Integer.toString(action));
		} catch (NullPointerException e) {
			System.out.println("Error: action to perform was null.");
			e.printStackTrace();
		}
		if (!isValid) {
			throw new NullPointerException("Action not found in ActionList for this object.");
		}
		System.out.println(action+" was performed on "+tile.getName());
	}
	
	public boolean isWalkable(EntityTile entity) {
		return tile.isTraversable(entity);
	}
	public void setRestrictedEntities(ArrayList<String> restrictedEntities) {
		tile.setRestrictedEntities(restrictedEntities);
	}
	
	public String toString() {
		return tile.getName();
	}
	
}
