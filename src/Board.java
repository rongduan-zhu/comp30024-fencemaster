import java.util.ArrayList;
import java.util.Arrays;

public class Board {
	/** class constants that indicate which type of neighbours to get */
	public static final int ALL_NEIGHBOURS = 1;
	public static final int VISITED_NEIGHBOURS = 2;
	public static final int UNVISITED_NEIGHBOURS = 3;

	/** internal array to represent the board */
	private ArrayList<ArrayList<Cell> > cellArray;
	/** Integer representing total number of rows */
	private Integer numRows;

	/**
	 * Board constructor, makes a board with all Cells initialized to empty
	 * @param numRows, specifying total number of rows of board
	 */
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

	/**
	 *
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @return true if (row, column) is a valid position, false otherwise
	 */
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

	/**
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @return the content of the Cell at (row, column)
	 */
	public String get(int row, int column) {
		if (isValidPosition(row, column)) {
			return cellArray.get(row).get(column).getContent();
		}
		return "";
	}

	/**
	 *
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @param type, B or W or -, use Cell to access
	 * @return true if the insert was success, false otherwise
	 */
	public boolean set(int row, int column, String type) {
		if (!isValidPosition(row, column)) {
			return false;
		}
		cellArray.get(row).get(column).setContent(type);
		return true;
	}

	/**
	 * Converts the board to a string, same format as inputs given by lecturer/tutors
	 */
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

	/**
	 * Return the right most column index (0 based) of the input row
	 * @param row
	 * @return right most column index as int
	 */
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
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @param type, all neighbours | all unvisited | all visited
	 * @return returns all unvisited neighbours as a 2d arraylist, with each item of first layer being the row and column
	 * 		   of a neighbour. eg [[1,2], [1,3]]
	 */
	public ArrayList<ArrayList<Integer> > getNeighbours(int row, int column, int type) {
		/* Got to check for 6 directions, assume checking for x's neighbours, then
		 * got to check for x's top, right, bottom-right/bottom-left, bottom, left,
		 * and top-left/top-right */
		if (!isValidPosition(row, column)) {
			return null;
		}
		String content = get(row, column);
		ArrayList<ArrayList<Integer>> neighbours = new ArrayList<ArrayList<Integer>>();
		int neighbourRow,
			neighbourColumn,
			boardMiddle = (this.numRows + 1) / 2 - 1;
		boolean typeConstraint;

		//Checks top
		if (isValidPosition(row - 1, column)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(row - 1, column).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(row - 1, column).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column)));
			}
		}

		//Checks right
		if (isValidPosition(row, column + 1)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(row, column + 1).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(row, column + 1).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row, column + 1)));
			}
		}

		//Checks bottom-right/bottom-left
		if (row < boardMiddle) {
			neighbourRow = row + 1;
			neighbourColumn = column + 1;
		} else {
			neighbourRow = row + 1;
			neighbourColumn = column - 1;
		}
		if (isValidPosition(neighbourRow, neighbourColumn)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(neighbourRow, neighbourColumn).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(neighbourRow, neighbourColumn).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(neighbourRow, neighbourColumn)));
			}
		}

		//Checks bottom
		if (isValidPosition(row + 1, column)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(row + 1, column).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(row + 1, column).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column)));
			}
		}

		//Checks left
		if (isValidPosition(row, column - 1)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(row, column - 1).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(row, column - 1).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row, column - 1)));
			}
		}

		//Checks top-left/top-right
		if (row <= boardMiddle) {
			neighbourRow = row - 1;
			neighbourColumn = column - 1;
		} else {
			neighbourRow = row - 1;
			neighbourColumn = column + 1;
		}
		if (isValidPosition(neighbourRow, neighbourColumn)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(neighbourRow, neighbourColumn).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(neighbourRow, neighbourColumn).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(neighbourRow, neighbourColumn)));
			}
		}
		return neighbours;
	}

	/**
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @return true if (row, column) is on an edge but not corner
	 */
	public boolean isEdgeNode(int row, int column) {
		if (!isValidPosition(row, column)) {
			return false;
		}
		int boardMiddle = (this.numRows + 1) / 2 - 1;
		//Nothing on the middle row is an edge node
		if (row < boardMiddle || row > boardMiddle) {
			//if its not top row or bottom row, then if its on either side, its an edge
			if (row != 0 && row != this.numRows - 1) {
				if (column == 0 || column == getMaxColumn(row)) {
					return true;
				} else {
					return false;
				}
			} else {
				//if its top row or bottom row, then get all row except for the edges
				if (column > 0 && column < boardMiddle) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * on an edge
	 * top = 0, right = 1, bottom-right = 2, bottom = 3, left = 4, top-left = 5
	 * @return which edge a node belongs to. This method assumes that the node is a node
	 */
	public int whichEdge(int row, int column) {
		int boardMiddle = (this.numRows + 1) / 2 - 1;
		if (row == 0) {
			return 0;
		} else if (row < boardMiddle) {
			return column == 0 ? 5 : 1;
		} else if (row < numRows - 1) {
			return column == 0 ? 4 : 2;
		} else {
			return 3;
		}
	}

	/**
	 * Resets all visited states of cell to unvisited
	 */
	public void resetVisited() {
		for (int i = 0; i < numRows; ++i) {
			for (int j = 0; j <= getMaxColumn(i); ++j) {
				cellArray.get(i).get(j).setVisited(false);
			}
		}
	}

	/**
	 * @return the total number of rows
	 */
	public Integer getNumRows() {
		return numRows;
	}

	/**
	 * @param row, row index, 0 based
	 * @param column, colulmn index, 0 based
	 * @return the Cell at (row, column) as a Cell object
	 */
	public Cell getCell(int row, int column) {
		if (!isValidPosition(row, column)) {
			return null;
		}

		return cellArray.get(row).get(column);
	}

}
