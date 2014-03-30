import java.util.ArrayList;
import java.util.Arrays;


public class LoopSearch {
	private Board board;

	public LoopSearch(Board board) {
		this.board = board;
	}
	
	public Boolean searchForLoop(String colour) {
		ArrayList<ArrayList<Integer>> colourCells = findAllCellsOfColour(colour);
		/*for (ArrayList<Integer> oneCell : colourCells) {
			System.out.println(oneCell.get(0) + " " + oneCell.get(1));
		}*/
		
		for (ArrayList<Integer> oneCell : colourCells) {
			// check if cell is visited before to prevent checking cells multiple times
			if (!board.getCell(oneCell.get(0), oneCell.get(1)).isVisited()) {
				// create a queue of cells to visit and add this cell as the intial cell to visit
				ArrayList<ArrayList<Integer>> cellsToVisitQueue = new ArrayList<ArrayList<Integer>>();
				cellsToVisitQueue.add(oneCell);
				// cell that the loop search began from
				ArrayList<Integer> startCell = oneCell;
				// current cell that loop search is in
				ArrayList<Integer> currentCell;
				// 2D array list of neighbour cells to current cell
				ArrayList<ArrayList<Integer>> currentCellNeighbours;
				int visitedCells = 0;
				while (cellsToVisitQueue.size() > 0) {
					currentCell = cellsToVisitQueue.remove(0);
					visitedCells++;
					// mark current cell as visited
					board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
					System.out.println("i am at cell: " + currentCell);
					currentCellNeighbours = board.getAllNeighbours(currentCell.get(0), currentCell.get(1));
					
					// every cell in a loop must have at least two neighbours
					if (currentCellNeighbours.size() < 2) {
						// this node isn't part of loop so go to next cell in the queue
						continue;
					}
					for (ArrayList<Integer> oneNeighbour : currentCellNeighbours) {
						if (!board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).isVisited()) {
							// commented this out for now as have to get third neighbours not second
							/*// get neighbours of this neighbour, i.e. second neighbours
							ArrayList<ArrayList<Integer>> secondNeighbours = board.getAllNeighbours(oneNeighbour.get(0), oneNeighbour.get(1));
							// check that these neighbours aren't neighbours with current cell
							if (secondNeighbours.contains(currentCell)) {
								System.out.println("second neighbours are neighbours with current cell at: " + currentCell);
								break;
							}*/
							// check that neighbours aren't neighbours of each other, otherwise not a loop
							for (ArrayList<Integer> otherNeighbour: currentCellNeighbours) {
								// make sure not checking same cell against itself
								if (!oneNeighbour.equals(otherNeighbour)) {
									ArrayList<ArrayList<Integer>> otherNeighbours = board.getAllNeighbours(otherNeighbour.get(0), otherNeighbour.get(1));
									if (otherNeighbours.contains(oneNeighbour)) {
										// this node isn't part of loop so go to next cell in the queue
										continue;
									}
								}
							}
							// add cells to start of queue so that you get further away from start before coming back
							cellsToVisitQueue.add(0, oneNeighbour);
						}
						if (oneNeighbour.equals(startCell) && !currentCell.equals(startCell) && visitedCells >= 5) {
							System.out.println("FOUND IT!!! at cell:" + currentCell + " and start cell is:" + startCell);
							return true;
						}
					}					
				}
			}			
		}
		System.out.println("failed, returning false");
		return false;
	}
	
	/**
	 * Finds and returns all cells of the specified colour
	 * @param colour String of what colour cell to search for
	 * @return 2D ArrayList of all cells of the specified colour. E.g. [[0,1],[0,2],[2,4]]
	 */
	private ArrayList<ArrayList<Integer>> findAllCellsOfColour(String colour) {
		ArrayList<ArrayList<Integer>> colourCells = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < board.getNumRows(); i++) {
			for (int j = 0; j <= board.getMaxColumn(i); j++) {
				if (board.get(i,j).equals(colour)) {
					colourCells.add(new ArrayList<Integer>(Arrays.asList(i,j)));
				}
			}
		}
		return colourCells;
	}
}
	