import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class LoopSearch3 {
	private Board board;
	
	public LoopSearch3(Board board) {
		this.board = board;
	}
	
	/**
	 * Search for loops on the board
	 * Loop is a connected set of cells of the same colour with an empty cell/cell of different colour within the loop
	 * @param colour Colour of loop to search for
	 * @return true if a loop is found, false otherwise
	 */
	public boolean searchForLoop(String colour) {
		Comparator<ArrayList<Integer>> cellComparator = new CellComparator();
		PriorityQueue<ArrayList<Integer>> notColourCellsQueue = new PriorityQueue<ArrayList<Integer>>(10, cellComparator);
		
		ArrayList<ArrayList<Integer>> diffColourCells, currentCellNeighbours, diffColourCellsQueue;
		ArrayList<Integer> currentCell;
		
		/* Find all edge cells that aren't of the colour ring you are searching for.
		 * Each element of this arraylist has the format [i, j, notVisitedBySearch],
		 * where notVisited = 0 if it has been visited and notVisited = 1 if it has not been visited.
		 * E.g. [1,2,0] is the cell at position (1,2) and it has been visited
		 */
		diffColourCellsQueue = new ArrayList<ArrayList<Integer>>();
		diffColourCells = findAllCellsNotOfColour(colour);		
		// is there a better way to add the cells without sifting every time a new one is added?
		// Now create a minimum (smallest at head) priority queue
		for (ArrayList<Integer> oneCell : diffColourCells) {
			//notColourCellsQueue.add(oneCell);
			/* add visited nodes nodes to start (initially all edges are visited)
			 * and all unvisited nodes to end
			 */
			if (!board.getCell(oneCell.get(0), oneCell.get(1)).isVisited()) {
				diffColourCellsQueue.add(oneCell);
			} else {
				diffColourCellsQueue.add(0, oneCell);
			}
		}						
		/* Modified dijkstra's algorithm to find all reachable cells.
		 * For each element in the queue, we want to find it's unvisited neighbours (of not colour specified) and visit those.
		 * If you can't visit a cell that means it is completely surrounded by the colour, hence you've found a ring
		 */
		//while (!notColourCellsQueue.isEmpty()) {
		while (!diffColourCellsQueue.isEmpty()) {
			// remove head (first element)
			//currentCell = notColourCellsQueue.poll();
			currentCell = diffColourCellsQueue.remove(0);
			/* the current cell is not visited
			 *  means that it was added at the start and its visited value never changed
			 *  i.e. it is unreachable from the edges of the board
			 */
			if (!board.getCell(currentCell.get(0), currentCell.get(1)).isVisited()) {
				System.out.println("FOUND A LOOP OF COLOUR: " + colour);
				System.out.println("found a cell that is surrounded at: " + currentCell);
				return true;
			}		
			
			currentCellNeighbours = board.getNonSameColourNeighbours(currentCell.get(0), currentCell.get(1), colour);
			for (ArrayList<Integer> oneNeighbour : currentCellNeighbours) {
				if (!board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).isVisited()) {
					// we have found a new unvisited node, add it into the queue with value of 0 - visited
					//notColourCellsQueue.add(new ArrayList<Integer>(Arrays.asList(oneNeighbour.get(0), oneNeighbour.get(1), 0)));
					diffColourCellsQueue.add(0, new ArrayList<Integer>(Arrays.asList(oneNeighbour.get(0), oneNeighbour.get(1))));
					// set the cell to visited to mark that it has been added into the queue
					board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).setVisited(true);
				}
			}
		}		
		// every cell was visited which means there are no loops
		return false;
	}
	
	/**
	 * Finds and returns all edge cells of the specified colour
	 * @param colour String of what colour cell to search for
	 * @return 2D ArrayList of all cells of the specified colour. E.g. [[0,1],[0,2],[2,4]]
	 */
	private ArrayList<ArrayList<Integer>> findAllCellsNotOfColour(String colour) {
		ArrayList<ArrayList<Integer>> cellList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < board.getNumRows(); i++) {
			for (int j = 0; j <= board.getMaxColumn(i); j++) {
				if (!board.get(i,j).equals(colour)) {
					// initialize dist of edge/corners to 0 - visited
					if (board.isEdgeOrCornerNode(i, j)) {
						cellList.add(new ArrayList<Integer>(Arrays.asList(i,j)));
						board.getCell(i, j).setVisited(true);
					} else {
						// other nodes be set to 1 - not visited
						cellList.add(new ArrayList<Integer>(Arrays.asList(i,j)));
					}
				}
			}
		}
		return cellList;
	}
}
