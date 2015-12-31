package version_7;

public class Terrain extends Location {

	@Override
	public Coord getPosition() {
		// TODO Auto-generated method stub
		return new Coord3D(0, 0, 0);
	}

	@Override
	public Coord getSize() {
		// TODO Auto-generated method stub
		return new Coord3D(0, 0, 0);
	}

	@Override
	public void fill(Coord a, Coord b, TileType t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getV() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getW() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getH() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getD() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getP() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TileSpace getTiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tile getTile(int val) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//This must be a huge, open area, with methods to generate it instead of definitions of each tile, to save memory. Changes made by the player should be stored, however. 
	
}
