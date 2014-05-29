package mlobanov;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Maxim Lobanov (mlobanov) and Rongduan Zhu (rz)
 *
 */

public class LoopSearch {
	private Board board;

	public LoopSearch(Board board) {
		this.board = board;
	}

	/**
	 * Search for loops on the board
	 * Loop is a connected set of cells of the same colour with an empty cell/cell of different colour within the loop
	 * @param colour Colour of loop to search for
	 * @return true if a loop is found, false otherwise
	 */
	public boolean searchForLoop(String colour) {
		ArrayList<ArrayList<Integer>> diffColourCells, currentCellNeighbours, diffColourCellsQueue;
		ArrayList<Integer> currentCell;

		/* reset visited status of each cell in the board as loopsearch changes this
		 * (previous loop searches or tripod search may have set these values)
		 */
		board.resetVisited();
		/* Find all edge cells that aren't of the colour ring you are searching for.
		 * Each element of this arraylist has the format [i, j, notVisitedBySearch],
		 * where notVisited = 0 if it has been visited and notVisited = 1 if it has not been visited.
		 * E.g. [1,2,0] is the cell at position (1,2) and it has been visited
		 */
		diffColourCellsQueue = new ArrayList<ArrayList<Integer>>();
		diffColourCells = findAllCellsNotOfColour(colour);

		// Now create a minimum (smallest at head) priority queue
		for (ArrayList<Integer> oneCell : diffColourCells) {
			/* add visited nodes nodes to start (initially all edges are visited)
			 * and all unvisited nodes to end
			 */
			if (!board.isEdgeOrCornerNode(oneCell.get(0), oneCell.get(1))) {
				diffColourCellsQueue.add(oneCell);
			} else {
				board.getCell(oneCell.get(0), oneCell.get(1)).setVisited(true);
				diffColourCellsQueue.add(0, oneCell);
			}
		}
		/* Modified dijkstra's algorithm to find all reachable cells.
		 * For each element in the queue, we want to find it's unvisited neighbours (of not colour specified) and visit those.
		 * If you can't visit a cell that means it is completely surrounded by the colour, hence you've found a ring
		 */
		while (!diffColourCellsQueue.isEmpty()) {
			// remove head (first element)
			currentCell = diffColourCellsQueue.remove(0);
			/* the current cell is not visited
			 *  means that it was added at the start and its visited value never changed
			 *  i.e. it is unreachable from the edges of the board
			 */
			if (!board.getCell(currentCell.get(0), currentCell.get(1)).isVisited()) {
				return true;
			}

			currentCellNeighbours = board.getNonSameColourNeighbours(currentCell.get(0), currentCell.get(1), colour);
			for (ArrayList<Integer> oneNeighbour : currentCellNeighbours) {
				if (!board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).isVisited()) {
					// we have found a new unvisited node, add it into the queue with value of 0 - visited
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
			for (int j = 0; j <= board.getNumRows(); j++) {
				if (board.isValidPosition(i, j) && !board.get(i,j).equals(colour)) {
					cellList.add(new ArrayList<Integer>(Arrays.asList(i,j)));
				}
			}
		}
		return cellList;
	}
}
