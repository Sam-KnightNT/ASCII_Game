package version_7;

import java.util.ArrayList;
public class Path extends ArrayList<Location> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1978933604504823917L;

	public Path(Location loc) {
		this.add(loc);
	}

	public Path(Path path) {
		this.addAll(path);
	}
	
	public Path() {
	}

	public Location pop() {
		return this.remove(0);
	}
	
	public Location peek() {
		return this.get(0);
	}
	
	public Location popLast() {
		return this.remove(this.size()-1);
	}
	
	public Location peekLast() {
		return this.get(this.size()-1);
	}
	
	public Path get() {
		return this;
	}
	
	public String toString() {
		String str = this.get(0).getName();
		for (Location l : this.subList(1, this.size())) {
			str += ", "+l.getName();
		}
		return str;
	}
}