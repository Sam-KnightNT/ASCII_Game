package version_7;

public class Triplet<L, M, R>{
	
	private final L left;
	private final M middle;
	private final R right;
	
	public Triplet(L left, M middle, R right) {
		this.left = left;
		this.middle = middle;
		this.right = right;
	}
	
	public L getLeft() {
		return left;
	}
	
	public M getMiddle() {
		return middle;
	}
	
	public R getRight() {
		return right;
	}
	
	public String toString() {
		return "Left: "+left+"\nMiddle: "+middle+"\nRight: "+right+"\n";
	}
}