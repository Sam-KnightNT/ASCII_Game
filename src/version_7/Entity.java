package version_7;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Entity {

	// The general entity name
	private String name;
	private BufferedImage image;
	private String[] features;
	private HashMap<String, Integer> baseStats = new HashMap<String, Integer>();

	// Work more on hashmaps

	public Entity() {

	}

	public Entity(String name, BufferedImage image, String[] features, HashMap<String, Integer> baseStats) {
		this.name = name;
		this.image = image;
		this.features = features;
		this.baseStats = baseStats;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public BufferedImage getImage() {
		return image;
	}
	
	public void setFeatures(String[] features) {
		this.features = features;
	}
	public String[] getFeatures() {
		return features;
	}

	public void setStat(String str, Integer num) {
		baseStats.put(str, num);
	}
	public int getBaseStat(String name) {
		if (baseStats.containsKey(name)) {
			return baseStats.get(name);
		} else {
			return -1;
		}
	}
	public HashMap<String, Integer> getBaseStats() {
		return baseStats;
	}
	public boolean hasStat(String name) {
		if (baseStats.containsKey(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "Entity "+name;
	}
}