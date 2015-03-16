package version_7;

public class Stat {

	String name;
	int value;
	
	public Stat() {
	}
	public Stat(String name) {
		setName(name);
	}
	public Stat(int value) {
		setValue(value);
	}
	public Stat(String name, int value) {
		setName(name);
		setValue(value);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
}
