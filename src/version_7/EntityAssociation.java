package version_7;

public class EntityAssociation implements Comparable<EntityAssociation> {

	private EntityTile entity;
	private int value;
	
	public EntityAssociation(int val, EntityTile entity) {
		this.setValue(val);
		this.setEntity(entity);
	}

	public EntityTile getEntity() {
		return entity;
	}

	public void setEntity(EntityTile entity) {
		this.entity = entity;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public void decreaseValue(int dec) {
		value -= dec;
	}

	@Override
	public int compareTo(EntityAssociation other) {
		int comp = this.getValue() - other.getValue();
		//Does comp equal 0? If so return -1 or 1, depending on which Entity's ID is greater. Otherwise return Comp.
		//Note that sometimes, the Value in this will be ID anyway - in this case the check's redundant, but not in all cases.
		return (comp != 0 ? comp : signum(this.getEntity().getID(), other.getEntity().getID()));
	}
	
	private int signum(int a, int b) {
		//Is a greater than or equal to b? If so, is it greater than? If so, return 1. If equal, return 0 and if less than, return -1.
		if (a > b) {
			return 1;
		} else if (a==b) {
			return 0;
		} else {
			return -1;
		}
	}
	
	public String toString() {
		return "Val "+value+" for entity "+entity.getName()+"#"+entity.getID();
	}
}
