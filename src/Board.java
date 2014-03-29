import java.util.ArrayList;


public class Board {
	private ArrayList<ArrayList<Cell> > cellArray;
	private Integer dimension;


	public Board(int dimension) {
		/* Specifying capacity at start will save the time of dynamically reallocating
		 * more memory */
		this.dimension = dimension * 2 - 1;
		cellArray = new ArrayList<ArrayList<Cell>>(this.dimension);
		for (int i = 0; i < this.dimension; ++i) {
			cellArray.add(new ArrayList<Cell>(this.dimension));
			for (int j = 0; j < this.dimension; ++j) {
				if (isValidPosition(i, j)) {
					cellArray.get(i).add(new Cell());
				}
			}
		}
	}

	public boolean isValidPosition(int row, int column) {
		if (row < 0 || row > this.dimension - 1 ||
				column < 0 || column > this.dimension - 1) {
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
		for (int i = 0; i < dimension; ++i) {
			for (int j = 0; j < dimension; ++j) {
				boardString += get(i, j);
			}
			boardString += "\n";
		}
		return boardString;
	}

	public int getMaxColumn(int row) {
		if (row > this.dimension - 1) {
			return -1;
		}
		int boardMiddle = (this.dimension + 1) / 2 - 1,
			columnBound = boardMiddle + row;
		if (row > boardMiddle) {
			columnBound = boardMiddle + (this.dimension - 1) - row;
		}
		return columnBound;
	}

	public String getNeighbour(int row, int column) {
		return "";
	}
}
