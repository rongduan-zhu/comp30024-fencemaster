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
			/*if (!board.getCell(oneCell.get(0), oneCell.get(1)).isVisited()) {*/
				// mark current cell as visited i.e. do not try to check it again as a loop starting position
				/*board.getCell(oneCell.get(0), oneCell.get(1)).setVisited(true);*/
				
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
					if (board.getCell(currentCell.get(0), currentCell.get(1)).isVisited()) {
						continue;
					}
					board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
					visitedCells++;
					
					currentCellNeighbours = board.getAllNeighbours(currentCell.get(0), currentCell.get(1));
					System.out.println("i am at cell: " + currentCell + " and the neighbours are: " + currentCellNeighbours);
					// every cell in a loop must have at least two neighbours
					if (currentCellNeighbours.size() < 2) {
						// not enough neighbours, go to next cell in queue
						visitedCells = 0;
						continue;
					} else if (currentCellNeighbours.size() == 2) {
						// when you have two neighbours, they cannot be neighbours of each other 
						// (i.e. they must 'spread apart' from start)
						// otherwise it is not a loop
						ArrayList<Integer> neighbourOne = currentCellNeighbours.get(0);
						ArrayList<Integer> neighbourTwo = currentCellNeighbours.get(1);
						ArrayList<ArrayList<Integer>> neighbourOneNeighbours = board.getAllNeighbours(neighbourOne.get(0), neighbourOne.get(1));
						if (neighbourOneNeighbours.contains(neighbourTwo)) {
							visitedCells = 0;
							continue;
						}
					} else if (currentCellNeighbours.size() == 6) {
						// the only case of having six neighbours is a cell completely surrounded which is not a loop
						visitedCells = 0;
						continue;
					}
					for (ArrayList<Integer> oneNeighbour : currentCellNeighbours) {
						if (!board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).isVisited()) {
							// add cells to start of queue so that you get further away from start before coming back
							cellsToVisitQueue.add(0, oneNeighbour);														
						}
						// visited cells for 3 to ensure it doesn't go out one cell then come back, not sure about this yet
						if (oneNeighbour.equals(startCell) && !currentCell.equals(startCell) && visitedCells >= 3) {
							System.out.println("FOUND IT!!! at cell:" + currentCell + " and start cell is:" + startCell);
							return true;
						}
					}					
				}
			/*}*/		
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
	