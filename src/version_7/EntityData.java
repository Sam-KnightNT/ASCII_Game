package version_7;

import java.util.ArrayList;

public class EntityData {

	String name = new String();
	ArrayList<String> features = new ArrayList<String>();
	ArrayList<Stat> stats = new ArrayList<Stat>();
	ArrayList<Item> inventory = new ArrayList<Item>();
	
	
	public EntityData() {
		
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void addStat(Stat stat) {
		stats.add(stat);
	}
	public void addStat(String name, int value) {
		addStat(new Stat(name, value));
	}
	
	public void addFeature(String feature) {
		features.add(feature);
	}
	
	public void addItem(Item item) {
		inventory.add(item);
	}
	
}
