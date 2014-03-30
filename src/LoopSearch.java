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
			System.out.println(oneCell);
			// check if cell is visited before to prevent checking cells multiple times
			if (!board.getCell(oneCell.get(0), oneCell.get(1)).isVisited()) {
				ArrayList<ArrayList<Integer>> cellsToVisitQueue = new ArrayList<ArrayList<Integer>>();
				cellsToVisitQueue.add(oneCell);
				//System.out.println(cellsToVisitQueue);
			}
			
		}
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
			for (int j = 0; j < board.getMaxColumn(i); j++) {
				System.out.println("row: " + i + " and column: " + j);
				if (board.get(i,j).equals(colour)) {
					colourCells.add(new ArrayList<Integer>(Arrays.asList(i,j)));
				}
			}
		}
		return colourCells;
	}
}
	