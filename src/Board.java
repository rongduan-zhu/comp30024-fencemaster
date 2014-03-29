import java.util.ArrayList;


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

	public String getNeighbour(int row, int column) {
		return "";
	}
}
