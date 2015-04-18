package version_7;

public class LiquidPure extends Liquid {

	private LiquidType type;
	
	//Assumes 100% concentration if not given
	public LiquidPure(LiquidType type, int volume) {
		super(volume, type);
		this.type = type;
	}
	
	//Assumes 1 litre if not given volume
	public LiquidPure(LiquidType type, double concentration) {
		super(concentration, type);
		this.type = type;
	}
	
	//Main constructor
	public LiquidPure(LiquidType type, double concentration, int volume) {
		super(concentration, volume, type);
		this.type = type;
	}

	public LiquidType getType() {
		return type;
	}

	public void setType(LiquidType type) {
		this.type = type;
	}

}
