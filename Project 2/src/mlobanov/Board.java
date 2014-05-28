package mlobanov;
import java.util.Arrays;
import java.util.ArrayList;

/**
 *
 * @author Maxim Lobanov (mlobanov) & Rongduan Zhu (rz)
 *
 */

public class Board {
	/** class constants that indicate which type of neighbours to get */
	public static final int ALL_NEIGHBOURS = 1;
	public static final int VISITED_NEIGHBOURS = 2;
	public static final int UNVISITED_NEIGHBOURS = 3;

	/** internal array to represent the board */
	private ArrayList<ArrayList<Cell> > cellArray;
	/** Integer representing total number of rows */
	private Integer numRows;
	/** Total number of cells given dimension */
	private Integer totalNumCells;
	/** Number of occupied cells */
	private Integer occupiedCells = 0;
	/** Dimension of the board */
	private Integer dimension;
	/** Stores all edge cells*/
	private ArrayList<ArrayList<Integer> > edgeNodes = new ArrayList<ArrayList<Integer> >();
	/**
	 * Board constructor, makes a board with all Cells initialized to empty
	 * @param numRows, specifying total number of rows of board
	 */
	public Board(int dimension) {
		/* Specifying capacity at start will save the time of dynamically reallocating
		 * more memory */
		this.numRows = dimension * 2 - 1;
		/* Assume edge length is x, then total number of valid positions is
		 * 3*n^2 - 3*n + 1
		 * */
		this.totalNumCells = 3 * dimension * dimension - 3 * dimension + 1;
		this.dimension = dimension;

		// a 2D ArrayList
		cellArray = new ArrayList<ArrayList<Cell>>(this.numRows);
		for (int i = 0; i < this.numRows; i++) {
			cellArray.add(new ArrayList<Cell>());
			for (int j = 0; j < this.numRows; j++) {
				cellArray.get(i).add(new Cell(i, j));
				if (isEdgeNode(i, j)) {
					edgeNodes.add(new ArrayList<Integer>(Arrays.asList(i, j)));
				}
			}
		}
	}

	/**
	 * Finds whether specified position is valid on the board
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @return true if (row, column) is a valid position, false otherwise
	 */
	public boolean isValidPosition(int row, int column) {
		if (row < 0 || row > this.numRows - 1 ||
				column < 0 || column > this.numRows - 1) {
			return false;
		}

		int columnBound = getColumnBound(row);

		if (row <= getMiddleRowIndex()) {
			return column <= columnBound;
		} else {
			return column >= columnBound;
		}
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
		String content;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numRows; j++) {
				content = get(i, j);
				if (content.isEmpty()) {
					boardString += " ";
				} else {
					boardString += get(i, j);
				}
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
	public int getColumnBound(int row) {
		if (row > this.numRows - 1) {
			return -1;
		}
		int boardMiddle = getMiddleRowIndex(),
			columnBound = boardMiddle + row;
		if (row > boardMiddle) {
			columnBound = row - boardMiddle;
		}
		return columnBound;
	}

	/** Return the index of the row in the middle
	 * 	@return the index of the middle row
	 */
	public int getMiddleRowIndex() {
		return (this.numRows + 1) / 2 - 1;
	}

	/**
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @param type, all neighbours | all unvisited | all visited
	 * @return returns neighbours of type as a 2d arraylist, with each item of first layer being the row and column
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

		if (isValidPosition(row + 1, column + 1)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(row + 1, column + 1).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(row + 1, column + 1).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column + 1)));
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

		//Checks top-left
		if (isValidPosition(row - 1, column - 1)) {
			if (type == ALL_NEIGHBOURS) {
				typeConstraint = true;
			} else {
				typeConstraint = getCell(row - 1, column - 1).isVisited();
				typeConstraint = type == VISITED_NEIGHBOURS ? typeConstraint : !typeConstraint;
			}
			if (typeConstraint && get(row - 1, column - 1).equals(content)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column - 1)));
			}
		}
		return neighbours;
	}

	/**
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @param colour the colour which you wish to find neighbours that aren't that colour
	 * @return returns all neighbours as a 2d arraylist, with each item of first layer being the row and column
	 * 		   of a neighbour. eg [[1,2], [1,3]]
	 */
	public ArrayList<ArrayList<Integer> > getNonSameColourNeighbours(int row, int column, String colour) {
		/* Got to check for 6 directions, assume checking for x's neighbours, then
		 * got to check for x's top, right, bottom-right/bottom-left, bottom, left,
		 * and top-left/top-right */
		if (!isValidPosition(row, column)) {
			return null;
		}
		ArrayList<ArrayList<Integer>> neighbours = new ArrayList<ArrayList<Integer>>();

		//Checks top
		if (isValidPosition(row - 1, column)) {
			if (!get(row - 1, column).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column)));
			}
		}

		//Checks right
		if (isValidPosition(row, column + 1)) {
			if (!get(row, column + 1).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row, column + 1)));
			}
		}

		//Checks bottom-right
		if (isValidPosition(row + 1, column + 1)) {
			if (!get(row + 1, column + 1).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column + 1)));
			}
		}

		//Checks bottom
		if (isValidPosition(row + 1, column)) {
			if (!get(row + 1, column).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column)));
			}
		}

		//Checks left
		if (isValidPosition(row, column - 1)) {
			if (!get(row, column - 1).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row, column - 1)));
			}
		}

		//Checks top-left
		if (isValidPosition(row - 1, column - 1)) {
			if (!get(row - 1, column - 1).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column - 1)));
			}
		}
		return neighbours;
	}

	/**
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @param colour the secondary connection should be
	 * @return returns all secondary connections that have the same colour as "colour"
	 * 		    eg [[1,2], [1,3]]
	 */
	public ArrayList<ArrayList<Integer> > getSecondaryConnection(int row, int column, String colour) {
		/* Got to check for 6 directions, assume checking for x's neighbours, then
		 * got to check for x's top, right, bottom-right/bottom-left, bottom, left,
		 * and top-left/top-right */
		if (!isValidPosition(row, column)) {
			return null;
		}
		ArrayList<ArrayList<Integer>> neighbours = new ArrayList<ArrayList<Integer>>();

		//Checks top
		if (isValidPosition(row - 2, column - 1)) {
			if (get(row - 2, column - 1).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 2, column - 1)));
			}
		}

		//Checks right
		if (isValidPosition(row - 1, column + 1)) {
			if (get(row - 1, column + 1).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column + 1)));
			}
		}

		//Checks bottom-right
		if (isValidPosition(row + 1, column + 2)) {
			if (get(row + 1, column + 2).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column + 2)));
			}
		}

		//Checks bottom
		if (isValidPosition(row + 2, column)) {
			if (get(row + 2, column).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 2, column)));
			}
		}

		//Checks left
		if (isValidPosition(row + 1, column - 1)) {
			if (get(row + 1, column - 1).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row + 1, column - 1)));
			}
		}

		//Checks top-left
		if (isValidPosition(row - 1, column - 2)) {
			if (get(row - 1, column - 2).equals(colour)) {
				neighbours.add(new ArrayList<Integer>(Arrays.asList(row - 1, column - 2)));
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
		int boardMiddle = getMiddleRowIndex();
		//Nothing on the middle row is an edge node
		if (row < boardMiddle || row > boardMiddle) {
			int columnBound = getColumnBound(row);
			//if its not top row or bottom row, then if its on either side, its an edge
			if (row != 0 && row != this.numRows - 1) {
				return column == 0 || column == columnBound || column == this.numRows - 1;
			} else {
				//if its top row or bottom row, then get all row except for the edges
				return !(column == 0 || column == columnBound || column == this.numRows - 1);
			}
		}
		return false;
	}

	/**
	 * @param row, row index, 0 based
	 * @param column, column index, 0 based
	 * @return true if (row, column) is on an edge or a corner
	 */
	public boolean isEdgeOrCornerNode(int row, int column) {
		if (!isValidPosition(row, column)) {
			return false;
		}
		int boardMiddle = getMiddleRowIndex();
		// check non middle row cells
		if (row < boardMiddle || row > boardMiddle) {
			int columnBound = getColumnBound(row);
			//if its not top row or bottom row, then if its on either side, its an edge
			if (row != 0 && row != this.numRows - 1) {
				return column == 0 || column == columnBound || column == this.numRows - 1;
			} else {
				return true;
			}
		}
		return column == 0 || column == (this.numRows - 1);
	}

	/**
	 * Find which edge the cell is on. This method assumes that the node is an edge node
	 * top = 0, right = 1, bottom-right = 2, bottom = 3, left = 4, top-left = 5
	 * @return which edge a node belongs to.
	 */
	public int whichEdge(int row, int column) {
		int boardMiddle = getMiddleRowIndex();
		if (row == 0) {
			return 0;
		} else if (row < boardMiddle) {
			return column == 0 ? 5 : 1;
		} else if (row < numRows - 1) {
			return column == this.numRows - 1 ? 4 : 2;
		} else {
			return 3;
		}
	}

	/**
	 * Resets all visited states of cell to unvisited
	 */
	public void resetVisited() {
		for (int i = 0; i < numRows; ++i) {
			for (int j = 0; j < numRows; ++j) {
				if (isValidPosition(i, j)) {
					cellArray.get(i).get(j).setVisited(false);
				}
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
	 * @param column, column index, 0 based
	 * @return the Cell at (row, column) as a Cell object
	 */
	public Cell getCell(int row, int column) {
		if (!isValidPosition(row, column)) {
			return null;
		}

		return cellArray.get(row).get(column);
	}

	/**
	 * @return the total number of occupied cells as int
	 */
	public Integer getOccupiedCells() {
		return occupiedCells;
	}

	/**
	 * @param occupiedCells, set number of occupied cells to this
	 */
	public void setOccupiedCells(Integer occupiedCells) {
		this.occupiedCells = occupiedCells;
	}

	/**
	 * @return the total number of cells as int
	 */
	public Integer getTotalNumCells() {
		return totalNumCells;
	}

	/**
	 * Increments the number of occupied cells by 1
	 */
	public void incrementOccupiedCells() {
		++occupiedCells;
	}

	/**
	 * Gets a list of all nodes on an edge
	 */
	public ArrayList<ArrayList<Integer>> getEdgeNodes() {
		return edgeNodes;
	}

}
