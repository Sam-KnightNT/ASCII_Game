package version_7;

public class LiquidPure extends Liquid {

	private LiquidType type;
	
	public LiquidPure(int concentration, LiquidType type) {
		super(concentration);
		this.type = type;
	}

	public LiquidType getType() {
		return type;
	}

	public void setType(LiquidType type) {
		this.type = type;
	}

}
