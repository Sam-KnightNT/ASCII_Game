package version_7;

public class IntPair {
	
	private final int left;
	private final int right;
	
	public IntPair(int left, int right) {
		this.left = left;
		this.right = right;
	}
	
	public int getLeft() {
		return left;
	}
	
	public int getRight() {
		return right;
	}

	public boolean equals(IntPair pair) {
		return equals(pair, true);
	}
	
	public boolean equals(IntPair pair, boolean sameOrderReq) {
		if (this.getLeft()==pair.getLeft() &&
			this.getRight()==pair.getRight())
			return true;
		else
			return (!sameOrderReq && this.getLeft()==pair.getRight()
								  && this.getRight()==pair.getLeft());
	}
	
	public String toString() {
		return "("+left+":"+right+")";
	}
}