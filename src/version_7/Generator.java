package version_7;

import java.awt.Color;
import java.util.*;

import javax.swing.JFrame;


public class Generator {

	/*
	 * Set number of cells to generate (say, 150)
	 * Set a random point in a radius small radius (30 for now, could be ~50)
	 * Set a random size 3<s<20, with a skewed normal distribution (Park-Miller)
	 * Modify the width and height by +-2, minimum should be extended to 1
	 * Draw stuff to screen.
	 * Use separation steering to separate cells
	 * Fill in gaps with 1x1 cells
	 * Any cells above a certain size are rooms (above, say, 6x6)
	 * Use Delaunay Triangulation to connect rooms
	 * Construct a Minimal Spanning Tree
	 * Add a few more edges that were deleted from the Tree (15%)
	 * For each line still existing, construct straight lines or L shapes
	 * Set any cells that intersect these (including larger cells) as corridor tiles
	 */
	
	/*
	 * Alternative method
	 * Create a number of very large cells, and separation steer them, but only while they are strictly overlapping.
	 * For each cell:
	 * -Get all cells that are directly connected to this one.
	 * -For each one of these:
	 * --Select a random position for a Corridor.
	 * --Progressively shave lines off each, leaving the Corridor intact, until it gets to some length
	 */
	
	/*
	 * Alternative method
	 * Do it like D&D does
	 * Have a large number of pre-set Room types, which are defined in terms of probabilities and put in Slots in a list.
	 * For example, Slot 8 could be a Throne Room, which is 9x12 and has pillars, with a Gold throne in the centre and 3 entrances at set locations.
	 * Once this is generated, roll to determine which Room or Corridor is beyond these, if any. For example, the Throne Room's first door could have attributes
	 * {0, 0, 0, 1, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 9, 9, 9, 9}
	 * Roll a die from 1 to size(thatarray).
	 * If it is 01 to 03, nothing is created - the door does not exist.
	 * If it is 04, put Room 1 behind it - this is a Treasure room, with an invisible door that needs to be searched carefully for.
	 * 05 or 06 -> Room 2, trap treasure room - identical to the other one, but with traps instead of treasure.
	 * 07 to 14 -> Room 4, a Corridor that is 5 long and 2 wide, with a door on the left on the 3rd space, and a space for another Corridor at the end.
	 * 15 to 21 -> Room 6, a Corridor that is 8 long and 3 wide, where the 3rd row is trapped.
	 * 22 to 25 -> Room 9, a trap-filled Corridor that is guaranteed to be a treasure room at the end of it.
	 * Define all entrances in this way, as well as any potential rewards. E.g. in the treasure room there are 3 treasure spots - {1, 4, 4, 4, 4, 5}, {4, 4, 4, 5, 6, 7, 7}, {4, 7, 7, 8, 9, 10, 11}.
	 * Each time one of these Rooms is generated it may alter its own chances in other Rooms.
	 * For example, if that entrance above generates any room, add another 0 chance to that entrance's list. If it's a treasure room, add 1 to all trap treasure options. If it's a trap treasure room, add 1 to all treasure rooms.
	 * After the first Throne Room, change slot 8 to have a 1/8 chance of being another Throne Room (possibly modified to be smaller), and 7/8 to be a more generic Room. If this 2nd Throne Room is genned, reduce that chance to 0.
	 * Make sure the new Room can be placed at each stage. If not, remove it from this particular entrance's pool and try again.
	 */
	static final int NUM_CELLS = 150;
	static final int RADIUS_LIMIT_X = 70;
	static final int RADIUS_LIMIT_Y = 50;
	static final int MIN_SIZE = 3;
	static final int MAX_SIZE = 20;
	static final int SKEW = 1;
	static final double VARIANCE = 3;
	static final int CENTREX = 500;
	static final int CENTREY = 375;
	static final int SIZE_MULT = 6;
	static final double TAU = Math.PI*2;
	static Random random = new Random();
	static GeneratorWindow window;
	
	static ArrayList<Cell> cells = new ArrayList<Cell>();
	static HashMap<CellCoord2D, Pair<Cell, Cell>> corridors = new HashMap<CellCoord2D, Pair<Cell, Cell>>();
	static ArrayList<Connection> connections = new ArrayList<Connection>();
	static ArrayList<CellCoord2D> corrCells = new ArrayList<CellCoord2D>();
	
	public static void main(String[] args) throws InterruptedException {	
		int rng = random.nextInt();
		System.out.println(rng);
		run(rng);
		//Training one - 3000
		//4 main areas - -114036787
		//Long spiral - 2083514
	}

	@SuppressWarnings("unchecked")
	public static Pair<ArrayList<Cell>, ArrayList<CellCoord2D>> run(int rng) throws InterruptedException {

		//Create window
		System.out.println("Initialising...");
		
		random = new Random(rng);
		window = new GeneratorWindow();
		JFrame frame = new JFrame();
		frame.add(window);
		frame.setSize(CENTREX*2, CENTREY*2);
		frame.setVisible(true);
		window.setGraphics();
		
		//Generate cells
		System.out.print("Generating cells... ");
		
		for (int i = 0; i<NUM_CELLS; i++) {			
			int size = getRoomParameters();
			int x = random.nextInt(RADIUS_LIMIT_X*2) - RADIUS_LIMIT_X;
			int y = random.nextInt(RADIUS_LIMIT_Y*2) - RADIUS_LIMIT_Y;
			
			int x_size = random.nextInt(2)*2 - 2 + size;
			int y_size = random.nextInt(2)*2 - 2 + size;
			
			Cell cell = new Cell(new CellCoord2D(x, y), x_size, y_size);
			cells.add(cell);
			window.repaintCell(cell);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Done.");
		
		Thread.sleep(1000);
		
		//TODO - this:
		//Sort the cells by x-value. Check each pair in turn. If they overlap, move the furthest one from the origin 1 square outwards.
		//Sort the cells by y-value. Check each pair in turn. If they overlap, move the furthest one from the origin 1 square outwards.
		//Repeat until there are no overlaps.
		
		
		//Separation steering
		System.out.print("Applying separation steering... ");
		boolean overlaps = true;
		while (overlaps) {
			
			overlaps = false;
			Collections.sort(cells);
			for (Cell cell : cells) {
				for (int i=cells.indexOf(cell)+1; i<NUM_CELLS; i++) {
					if (cell.overlaps(cells.get(i))) {
						overlaps = true;
						window.clearCell(cell);
						window.clearCell(cells.get(i));
						cell.moveAwayFrom(cells.get(i));
						cells.get(i).moveAwayFrom(cell);
						window.repaintCell(cell);
						window.repaintCell(cells.get(i));
					}
				}
			}
			
			if (overlaps) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Done.");
		
		Thread.sleep(1000);
		
		System.out.print("Removing blue cells... ");
		//Delete any blue cells, i.e. ones which are less than 20 cells in size.
		ArrayList<Cell> newCells = new ArrayList<Cell>();
		for (int i=0; i<cells.size(); i++) {
			Cell cell = cells.get(i);
			if (cell.getH()*cell.getW()>20) {
				newCells.add(cell);
			} else {
				window.clearCell(cell);
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		cells = newCells;
		
		System.out.println("Done.");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.print("Recentring cells... ");
		//After all this is done, the cells will be massively skewed towards the negative - re-orient them so that the mean is 0 again
		int totalX = 0;
		int totalY = 0;
		for (Cell cell : cells) {
			//Add the x/y coords to the total sum of them 
			totalX += cell.getCentre().getX();
			totalY += cell.getCentre().getY();
		}
		
		//Get the average x/y position (to the nearest integer)
		totalX /= cells.size();
		totalY /= cells.size();
		
		CellCoord2D averagePosition = new CellCoord2D(totalX, totalY);
		
		window.slowTranspose(cells, averagePosition);
		
		System.out.println("Done.");
		
		Thread.sleep(1000);
		//Now, construct a Delaunay Path and a Minimum Spanning Tree, to get the corridors which should be drawn.
		//Delaunay Triangulation - http://en.wikipedia.org/wiki/Delaunay_triangulation
		//Euclidean MST - http://en.wikipedia.org/wiki/Euclidean_minimum_spanning_tree
		//Alternative - use A* to find paths to other cells.
		//Relative Neighbourhood Graph looks good - http://en.wikipedia.org/wiki/Relative_neighborhood_graph
		//Also see Gabriel Graph http://en.wikipedia.org/wiki/Gabriel_graph and Nearest Neighbour Graph http://en.wikipedia.org/wiki/Nearest_neighbor_graph
		//Then, stick the corridor tiles in the appropriate places.
		
		
		//For now, draw a line from each cell to its nearest neighbour.
		
		//Create a series of lines between the points, that cover each and every point. Go recursively - check each point.
		//If it doesn't have 2 points from it, find the closest point to it that doesn't have a line to it. Draw a line to that point, and pause for a bit.
		System.out.print("Generating connections... ");
		boolean finished = false;
		while (!finished) {
			//return true if all points have been iterated through and none without triangles are found.
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finished = triangulate(); 
		}
		System.out.println("Done.");
		
		Thread.sleep(500);
		
		System.out.print("Connecting remaining cells...");
		
		//Now check for disconnected loops - generate a list, A, of all cells. While this list is populated, pick the first cell on the list.
		//Generate a list of connected cells, B. Go through this in turn - draw a green line when a connection has been checked.
		//If the cell on the end is still in list A, delete it from A and put its connected cells on the end of list B. Then take the next from list B.
		//Once list B is exhausted, check list A. If it is empty, we are done. If not, check which is larger - A or ~A (i.e. deleted cells).
		//Iterate through the smaller of A and ~A, finding the cell which is the closest to a cell in ~A or A. Draw a line between these 2 cells at the end.
		//Pick the next cell on list A and repeat. If this one empties without returning to the start, it is done.

		finished = false;
		ArrayList<Cell> cellGen = (ArrayList<Cell>) cells.clone();
		//While cellGen still has cells in it...
		while (!cellGen.isEmpty()) {
			//Take the first one
			Cell cell = cellGen.remove(0);
			
			//Get all of its connections.
			ArrayList<Cell> connectedCells = cell.getConnections();
			
			//While there are connections in this list...
			while (!connectedCells.isEmpty()) {
				//Take the first one in the list.
				Cell cCell = connectedCells.remove(0);
				window.drawCellPart(Color.YELLOW, cCell, 7);
				//For each of its connections...
				for (Cell ccCell : cCell.getConnections()) {
					//If the full list contains the connected cell...
					if (cellGen.contains(ccCell)) {
						//Remove it.
						cellGen.remove(ccCell);
						//Draw a small square, to indicate the cell has been reached.
						window.drawCellPart(Color.CYAN, ccCell, 3);
						connectedCells.add(ccCell);
						Thread.sleep(50);
					}
				}
			}
			for (Cell cellP : cells) {
				window.repaintCell(cellP);
			}
			if (!cellGen.isEmpty()) {
				//Then there is a loop not connected, so go through both loops and find the pair with the smallest distance to each other.
				Cell closestCellA = null;
				Cell closestCellB = null;
				double closestDist = 100;
				Collections.sort(cells);
				Collections.sort(cellGen);
				cells.removeAll(cellGen);
				
				for (Cell loopCell : cells) {
					for (Cell otherCell : cellGen) {
						if (loopCell.getDistanceTo(otherCell)<closestDist) {
							closestCellA = loopCell;
							closestCellB = otherCell;
							closestDist = loopCell.getDistanceTo(otherCell);
						}
					}
				}
				cells.addAll(cellGen);
				//Then connect the two.
				closestCellA.addConnection(closestCellB);
				closestCellB.addConnection(closestCellA);
				connections.add(new Connection(closestCellA, closestCellB));
				
				//And draw the line.
				window.getGraphics().setColor(Color.WHITE);
				window.getGraphics().drawLine(CENTREX+(closestCellA.getCentre().getX()*SIZE_MULT), CENTREY+(closestCellA.getCentre().getY()*SIZE_MULT),
						CENTREX+(closestCellB.getCentre().getX()*SIZE_MULT), CENTREY+(closestCellB.getCentre().getY()*SIZE_MULT));

				//Finally, restart cellGen so that it tries again.
				cellGen = (ArrayList<Cell>) cells.clone();
			}
		}
		System.out.println("Done.");
		
		Thread.sleep(500);
		
		//Draw corridors using the A* algorithm.
		//Work out the start point by finding which point intersects the line drawn. Do the same with the end point.
		//Then, perform A* to connect the 2.

		System.out.print("Drawing corridors...");
		for (Connection connection : connections) {
			Cell A = connection.getCellA();
			Cell B = connection.getCellB();
			
			//Work out the angle, and which points intersect that line in each cell. Set those as the start/end points.
			double angle = (A.angleWith(B));
			
			double m = Math.tan(angle);
			
			int h = A.getH();
			int w = A.getW();
			
			CellCoord2D start = null;

			//Check if the width/height are even - if so, some distances will need to be adjusted by 1.
			boolean hEven = h % 2 == 0;
			boolean wEven = w % 2 == 0;
			
			//Need 4 angles, that represent the angle made by the 4 corners.
			//Angles that are between these 4 indicate which side is intersected first.
			double ang1 = Math.atan2(h, w);
			double ang2 = Math.atan2(h, -w);
			double ang3 = ang1+Math.PI;
			double ang4 = ang2+Math.PI;
			
			//Distance is measured from the centre, so half each value.
			h /= 2;
			w /= 2;
			
			//Now, get which side it intersects. We need to check for each quadrant.
			if (angle >= ang1 && angle < ang2) {
				//In this case, it will intersect the bottom side. We know y in this case, it's h. I know it seems upside-down, but it works.
				//If the height is even, it needs to be shifted one up.
				start = new CellCoord2D(h/m, (hEven ? h-1 : h));
			} else if (angle >= ang2 && angle < ang3) {
				//Intersects left line. x = -w.
				start = new CellCoord2D(-w, -m*w);
			} else if (angle >= ang3 && angle < ang4) {
				//Intersects top line. y = -h.
				start = new CellCoord2D(-h/m, -h);
			} else {
				//Intersects right line. x = w.
				start = new CellCoord2D((wEven ? w-1 : w), m*w);
			}
			
			//If it's on a corner, just shove it 1 space up/down.
			if (A.isCorner(CellCoord2D.sum(start, CellCoord2D.difference(A.getCentre(), A.getCorner())))) {
				start.moveY((int) -Math.signum(start.getY()));
			}
			
			//Now do the same for the end.
			angle = B.angleWith(A);
			m = Math.tan(angle);
			h = B.getH();
			w = B.getW();
			
			hEven = h % 2 == 0;
			wEven = w % 2 == 0;
			
			CellCoord2D end = null;
			
			//Need 4 angles, that represent the angle made by the 4 corners.
			//Angles that are between these 4 indicate which side is intersected first.
			ang1 = Math.atan2(h, w);
			ang2 = Math.atan2(h, -w);
			ang3 = ang1+Math.PI;
			ang4 = ang2+Math.PI;
			
			h /= 2;
			w /= 2;
			//Now, get which side it intersects. We need to check for each quadrant.
			if (angle >= ang1 && angle < ang2) {
				//Intersects bottom line. y = h.
				end = new CellCoord2D(h/m, (hEven ? h-1 : h));
			} else if (angle >= ang2 && angle < ang3) {
				//Intersects left line. x = -w.
				end = new CellCoord2D(-w, -m*w);
			} else if (angle >= ang3 && angle < ang4) {
				//Intersects bottom line. y = -h.
				end = new CellCoord2D(-h/m, -h);
			} else {
				//Intersects right line. x = w.
				end = new CellCoord2D((wEven ? w-1 : w), m*w);
			}
			
			if (B.isCorner(CellCoord2D.sum(end, CellCoord2D.difference(B.getCentre(), B.getCorner())))) {
				end.moveY((int) -Math.signum(end.getY()));
			}

			ArrayList<CellCoord2D> path = A_Star(CellCoord2D.sum(A.getCentre(), start), CellCoord2D.sum(B.getCentre(), end), B);
			
			//The last element is within cell B, so remove that
			path.remove(path.size()-1);
			
			//Remove another one? Apparently it needs it.
			path.remove(path.size()-1);
			
			//Once this has done, create a new Corridor that gets the path carved.
			//Need to have more of a check for Corridors - if the Corridor hit is heading to the same Cell the path is going to, connect to it.
			//For the purposes of this, the Corridors are a special type of Cell, which are nothing but the lines.
			//Or, just have an ever-expanding list of points that containedInCells calls, that includes all points in Cells, and all points in Corridors. Points in Corridors should have a HashMap entry or something, telling it where they go. If current is at one of them, join them up.
			//Then at the end, carve those points out in the Cells and Corridors generated alongside them.
			for (CellCoord2D coord : path) {
				corridors.put(coord, new Pair<Cell, Cell>(A, B));
				window.repaintPoint(coord, new Color(25, 180, 55));
				corrCells.add(coord);
			}
			
			//Finally, generate the corridor associated with this path.
			//generateCorridor(path);
		}
		System.out.println("Done.");
		return new Pair<ArrayList<Cell>, ArrayList<CellCoord2D>>(cells, corrCells);
	}
	
	private static int getRoomParameters() {
		double r = 0;
		while (r<MIN_SIZE || r>MAX_SIZE) {
			r = (random.nextGaussian()*VARIANCE)+SKEW;
		}
		int s = (int) Math.round(r);
		return s%2==0 ? s+1 : s;
	}
	
	private static boolean triangulate() {
		Collections.shuffle(cells, random);
		for (Cell cell : cells) {
			if (cell.getConnectionCount() < 2) {
				//Create a new Connection to the closest cell that doesn't already have one.
				double closestCellDist = 1000;
				Cell closestCell = null;
				cells.remove(cell);
				for (Cell otherCell : cells) {
					if (cell.getDistanceTo(otherCell)<closestCellDist && !cell.getConnections().contains(otherCell)) {
						closestCell = otherCell;
						closestCellDist = cell.getDistanceTo(otherCell);
					}
				}
				cells.add(cell);
				window.getGraphics().drawLine(CENTREX+(cell.getCentre().getX()*SIZE_MULT), CENTREY+(cell.getCentre().getY()*SIZE_MULT),
						CENTREX+(closestCell.getCentre().getX()*SIZE_MULT), CENTREY+(closestCell.getCentre().getY()*SIZE_MULT));
				
				cell.addConnection(closestCell);
				closestCell.addConnection(cell);
				connections.add(new Connection(cell, closestCell));
				return false;
			}
		}
		return true;
	}
	
	private static ArrayList<CellCoord2D> A_Star(CellCoord2D start, CellCoord2D end, Cell endCell) throws InterruptedException {
		
		Thread.sleep(50);
		ArrayList<CellCoord2D> closedSet = new ArrayList<CellCoord2D>();
		ArrayList<CellCoord2D> openSet = new ArrayList<CellCoord2D>();
		openSet.add(start);
		
		//The first one is the new node - it "came from" the second.
		HashMap<CellCoord2D, CellCoord2D> cameFrom = new HashMap<CellCoord2D, CellCoord2D>();
		
		HashMap<CellCoord2D, Double> gScore = new HashMap<CellCoord2D, Double>();
		HashMap<CellCoord2D, Double> fScore = new HashMap<CellCoord2D, Double>();
		gScore.put(start, 0.0);
		
		double gSc = gScore.get(start);
		
		
		int estimate = costEstimate(start, end);
		
		fScore.put(start, gSc+estimate);
		CellCoord2D current;
		cameFrom.put(start, new CellCoord2D(0, 0));
		while (!openSet.isEmpty()) {
			current = getLowestOf(openSet, fScore);
			if (current.equals(end)) {
				return reconstructPath(cameFrom, end);
			} else if (corridorHeadingTo(current, endCell)) {
				return reconstructPath(cameFrom, current);
			}
			openSet.remove(current);
			closedSet.add(current);
			for (CellCoord2D neighbour : current.getOrthogonalNeighbours()) {
				if (!containedInCells(neighbour) || neighbour.equals(end)) {
					if (!closedSet.contains(neighbour)) {
						//neighbour "came from" current
						cameFrom.put(neighbour, current);
						
						//Does the current cell already belong to a path? If so, the new addition to the score is 0.5. Otherwise,
						//	Has the path made a turn? (i.e. does current-cameFrom(current) = neighbour-current)? Score is 1 greater if so, otherwise 1.25.
						//TODO - add more behaviour here - if the path has already been traversed by another Corridor, the score is 0.5 to encourage re-using paths.
						double tentativeG = gScore.get(current) +
								(corridors.keySet().contains(current) ? 0.5 : 
									(CellCoord2D.difference(current, cameFrom.get(current)).equals(CellCoord2D.difference(neighbour, current)) ? 1 : 1.25));
						
						if (!openSet.contains(neighbour) || tentativeG < gScore.get(neighbour)) {
							cameFrom.put(neighbour, current);
							gScore.put(neighbour, tentativeG);
							fScore.put(neighbour, gScore.get(neighbour) + costEstimate(neighbour, end));
							if (!openSet.contains(neighbour)) {
								openSet.add(neighbour);
							}
						}
					}
				}
			}
		}
		//If it gets here, it has failed.
		System.out.println("A* failed. Bug the developer to put in more debugging features if you see this.");
		System.exit(2);
		return null;
	}
	
	private static ArrayList<CellCoord2D> reconstructPath(HashMap<CellCoord2D, CellCoord2D> cameFrom, CellCoord2D current) {
		ArrayList<CellCoord2D> totalPath = new ArrayList<CellCoord2D>();
		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			totalPath.add(current);
		}
		return totalPath;
	}
	
	private static boolean corridorHeadingTo(CellCoord2D point, Cell headingTo) {
		return (corridors.containsKey(point) && corridors.get(point).contains(headingTo));
	}
	
	private static int costEstimate(CellCoord2D node, CellCoord2D goal) {
		return Math.abs(goal.getX()-node.getX())+Math.abs(goal.getY()-node.getY());
	}
	
	private static CellCoord2D getLowestOf(ArrayList<CellCoord2D> set, HashMap<CellCoord2D, Double> scores) {
		CellCoord2D selected = set.get(0);
		double minScore = scores.get(selected);
		for (CellCoord2D coord : scores.keySet()) {
			if (set.contains(coord) && scores.get(coord)<minScore) {
				minScore = scores.get(coord);
				selected = coord;
			}
		}
		return selected;
	}
	
	@SuppressWarnings("unused")
	private static ArrayList<Cell> copyCells(ArrayList<Cell> cells) {
		ArrayList<Cell> newCells = new ArrayList<Cell>();
		for (Cell cell : cells) {
			newCells.add(new Cell(new CellCoord2D(cell.getCorner().getX(), cell.getCorner().getY()), cell.getW(), cell.getH()));
		}
		return newCells;
	}
	
	private static boolean containedInCells(CellCoord2D coord) {
		for (Cell cell : cells) {
			if (cell.contains(coord)) {
				return true;
			}
		}
		return false;
	}
}
