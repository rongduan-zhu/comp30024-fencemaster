import java.util.ArrayList;
import java.util.Arrays;

public class Board {
	private ArrayList<ArrayList<Cell> > cellArray;
	private Integer numRows;

	public Board(int numRows) {
		/* Specifying capacity at start will save the time of dynamically reallocating
		 * more memory */
		this.numRows = numRows * 2 - 1;
		// a 2D ArrayList
		cellArray = new ArrayList<ArrayList<Cell>>(this.numRows);
		for (int i = 0; i < this.numRows; i++) {
			cellArray.add(new ArrayList<Cell>());
			for (int j = 0; j < this.numRows; j++) {
				if (isValidPosition(i, j)) {
					cellArray.get(i).add(new Cell());
				}
			}
		}
	}

	public boolean isValidPosition(int row, int column) {
		if (row < 0 || row > this.numRows - 1 ||
				column < 0 || column > this.numRows - 1) {
			return false;
		}

		int columnBound = getMaxColumn(row);

		if (column <= columnBound) {
			return true;
		}
		return false;
	}

	public String get(int row, int column) {
		if (isValidPosition(row, column)) {
			return cellArray.get(row).get(column).getContent();
		}
		return "";
	}

	public boolean set(int row, int column, String type) {
		if (!isValidPosition(row, column)) {
			return false;
		}
		cellArray.get(row).get(column).setContent(type);
		return true;
	}

	@Override
	public String toString() {
		String boardString = new String();
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numRows; j++) {
				boardString += get(i, j);
			}
			boardString += "\n";
		}
		return boardString;
	}

	public int getMaxColumn(int row) {
		if (row > this.numRows - 1) {
			return -1;
		}
		int boardMiddle = (this.numRows + 1) / 2 - 1,
			columnBound = boardMiddle + row;
		if (row > boardMiddle) {
			columnBound = boardMiddle + (this.numRows - 1) - row;
		}
		return columnBound;
	}

	/**
	 * returns a 2d arraylist, with each item of first layer being the row and column
	 * of a neighbour. eg [[1,2], [1,3]]
	 * @param row
	 * @param column
	 * @return
	 */
	public ArrayList<ArrayList<Integer> > getNeighbours(int row, int column) {
		/* Got to check for 6 directions, assume checking for x's neighbours, then
		 * got to check for x's top, right, bottom-right, bottom, left, and top-left */
		if (!isValidPosition(row, column)) {
			return null;
		}
		String content = get(row, column);
		ArrayList<ArrayList<Integer> > neighbours = new ArrayList<ArrayList<Integer> >();
		
		//Checks top
		if (get(row - 1, column).equals(content)) {
			neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column)));
		}
		
		//Checks right
		if (get(row, column + 1).equals(content)) {
			neighbours.add(new ArrayList<Integer>(Arrays.asList(row, column + 1)));
		}
		
		//Checks bottom-right
		if (get(row + 1, column + 1).equals(content)) {
			neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column + 1)));
		}
		
		//Checks bottom
		if (get(row + 1, column).equals(content)) {
			neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column)));
		}
		
		//Checks left
		if (get(row, column - 1).equals(content)) {
			neighbours.add(new ArrayList<Integer>(Arrays.asList(row, column - 1)));
		}
		
		//Checks top-left
		if (get(row - 1, column - 1).equals(content)) {
			neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column - 1)));
		}
		return neighbours;
	}
}
