package version_7;

import java.util.ArrayList;

public class Path {
	
	private ArrayList<Location> path;
	
	public Path(ArrayList<Location> path) {
		this.path = path;
	}
	
	public ArrayList<Location> get() {
		return path;
	}
	
	public void set(ArrayList<Location> path) {
		this.path = path;
	}
	
}