package version_7;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

public class ItemEquippable extends Item {
	
	
	private HashMap<String, Integer> stats;

	public ItemEquippable() {
		stats = new HashMap<String, Integer>();
	}

	public ItemEquippable(String name, BufferedImage image, String description, HashMap<String, Integer> stats) {
		super(name, image, description);
		this.stats = stats;
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
	
}
