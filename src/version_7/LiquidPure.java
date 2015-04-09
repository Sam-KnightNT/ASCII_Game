package version_7;

public class LiquidPure extends Liquid {

	private LiquidType type;
	
	//If not given a concentration, assume 100%
	public LiquidPure(LiquidType type, int volume) {
		super(volume);
		this.type = type;
	}
	
	//If not given a colume, assume 1 litre
	public LiquidPure(LiquidType type, double concentration) {
		super(concentration);
		this.type = type;
	}
	
	//Main constructor
	public LiquidPure(LiquidType type, double concentration, int volume) {
		super(concentration, volume);
		this.type = type;
	}

	public LiquidType getType() {
		return type;
	}

	public void setType(LiquidType type) {
		this.type = type;
	}

}
