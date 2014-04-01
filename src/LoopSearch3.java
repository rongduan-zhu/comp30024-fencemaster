import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class LoopSearch3 {
	private Board board;
	private static final int INFINITY = 100;

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
		
		ArrayList<ArrayList<Integer>> diffColourCells, currentCellNeighbours;
		ArrayList<Integer> currentCell;
		
		diffColourCells = findAllCellsNotOfColour(colour);		
		// is there a better way to add the cells without sifting every time a new one is added?
		for (ArrayList<Integer> oneCell : diffColourCells) {
			notColourCellsQueue.add(oneCell);
		}				
		
		/*System.out.println(board.getNonSameColourNeighbours(4,4, colour));*/
		
		
		while (!notColourCellsQueue.isEmpty()) {
			// remove head (first element)
			currentCell = notColourCellsQueue.poll();
			
			board.getCell(currentCell.get(0), currentCell.get(1)).timesVisited++;
			//System.out.println("my current cell is: " + currentCell);
			if (board.getCell(currentCell.get(0), currentCell.get(1)).getDist() == INFINITY) {
				System.out.println("FOUND A LOOP OF COLOUR: " + colour);
				System.out.println("found a cell that is surrounded at: " + currentCell);
				break;
			}
			/*
			 * no decreasekey operation in java priority queue so instead just skip elements
			 * that are marked as visited
		 	 */ 			 
			/*if (!board.getCell(currentCell.get(0), currentCell.get(1)).isVisited()) {
				board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
			} else {
				continue;
			}*/			
			board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
			currentCellNeighbours = board.getNonSameColourNeighbours(currentCell.get(0), currentCell.get(1), colour);
			for (ArrayList<Integer> oneNeighbour : currentCellNeighbours) {
				if (!board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).isVisited()) {
					// set the cell to visited to mark that it has been added into the queue
					board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).setVisited(true);
					// we have found a new unvisited node, add it into the queue with distance of 1
					notColourCellsQueue.add(new ArrayList<Integer>(Arrays.asList(oneNeighbour.get(0), oneNeighbour.get(1), 1)));
					board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).setDist(1);
				}
			}
		}
		for (int i=0; i < board.getNumRows(); i++) {
			for (int j = 0; j <= board.getMaxColumn(i); j++) {
				System.out.println("cell: " + "(" + i + ", " + j + ") has been visited: " + board.getCell(i,j).timesVisited + " times");
				board.getCell(i, j).timesVisited = 0;
			}
		}	
		
		
		// now check for any cells that still have a dist of infinity
		for (int i=0; i < board.getNumRows(); i++) {
			for (int j = 0; j <= board.getMaxColumn(i); j++) {
				if (!board.get(i,j).equals(colour)) {
					if (board.getCell(i,j).getDist() == INFINITY) {
						System.out.println("FOUND A LOOP OF COLOUR: " + colour);
						System.out.println("found a cell (end) that is surrounded at: " + i + ", " + j);
						return true;
					}
				}
			}
		}		
		
		return false;
	}
	
	/**
	 * Finds and returns all cells of the specified colour
	 * @param colour String of what colour cell to search for
	 * @return 2D ArrayList of all cells of the specified colour. E.g. [[0,1],[0,2],[2,4]]
	 */
	private ArrayList<ArrayList<Integer>> findAllCellsNotOfColour(String colour) {
		ArrayList<ArrayList<Integer>> cellList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < board.getNumRows(); i++) {
			for (int j = 0; j <= board.getMaxColumn(i); j++) {
				if (!board.get(i,j).equals(colour)) {
					// initialize dist of edge/corners to 0 - i.e. they are sources
					if (board.isEdgeOrCornerNode(i, j)) {
						cellList.add(new ArrayList<Integer>(Arrays.asList(i,j,0)));
						board.getCell(i, j).setDist(0);
						board.getCell(i, j).setVisited(true);
					} else {
						// other nodes be set to 'infinity' - 100
						cellList.add(new ArrayList<Integer>(Arrays.asList(i,j,INFINITY)));
						board.getCell(i, j).setDist(100);
					}
				}
			}
		}
		return cellList;
	}
}
