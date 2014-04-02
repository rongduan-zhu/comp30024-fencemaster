import java.util.ArrayList;
import java.util.Arrays;


public class LoopSearch2 {
	private Board board;
	private int middleRowIndex;

	public LoopSearch2(Board board) {
		this.board = board;
		this.middleRowIndex = (board.getNumRows() + 1) / 2 - 1;
	}

	/**
	 * Search for loops on the board
	 * Loop is a connected set of cells of the same colour with an empty cell/cell of different colour within the loop
	 * @param colour Colour of loop to search for
	 * @return true if a loop is found, false otherwise
	 */
	public boolean searchForLoop(String colour) {
		// check each cell
		for (int i = 0; i < board.getNumRows(); i++) {
			for (int j = 0; j <= board.getMaxColumn(i); j++) {
				/*System.out.println("cell: " + i + ", " + j + ". down left: " + sameColourDownLeft(i,j) + " . right: " + sameColourRight(i,j) + " . down right: " + sameColourDownRight(i,j));*/
				/* find a corner cell - cell where down-left cell is same colour and
				 * right cell is same colour but down-right cell is not same colour
				 * to begin possible location to start searching for a loop
				 * e.g. a situation like:
				 *    B B
				 *   B -
				 */
				if (board.getCell(i, j).getContent().equals(colour) &&
						sameColourDownLeft(i, j) &&
						sameColourRight(i,j) &&
						!sameColourDownRight(i, j)) {
					/* once this is found, find the other top corner
					 * e.g.:
					 *      B B
					 *       - B
					 */

					// cell to begin the search from
					ArrayList<Integer> startCell = new ArrayList<Integer>(Arrays.asList(i,j));
					// current cell (for iteration)
					ArrayList<Integer> currentCell = new ArrayList<Integer>(Arrays.asList(i,j));
					// a list of the current cells
					ArrayList<ArrayList<Integer>> cellList = new ArrayList<ArrayList<Integer>>();
					// flag to see whether loop was broken out of
					int brokeOutOfLoop = 0;
					/* begin searching cells to the right of the starting cell
					 * if the down-right cell is the same colour, this is a potential end for the top row of the loop
					 * as the loop top row must eventually go down to connect to the other side */
					while (!sameColourDownRight(currentCell.get(0), currentCell.get(1))) {
						/* if the cell to the right is not the same colour (and loop hasn't broken yet)
						 * then this isn't the start of a loop */
						if (!sameColourRight(currentCell.get(0), currentCell.get(1))) {
							brokeOutOfLoop = 1;
							break;
						}
						// add the current cell to the cell list
						cellList.add(new ArrayList<Integer>(Arrays.asList(currentCell.get(0), currentCell.get(1))));
						// go to the next cell on the right
						currentCell.set(1, currentCell.get(1) + 1);
					}
					// if the down-right cell was the same colour, found a viable top row for a loop
					if (brokeOutOfLoop != 1) {
						// add final top row cell
						cellList.add(currentCell);
						// perform a depth first search from the starting cell to the end cell
						if (dfsForFullLoop(startCell, cellList) == true) {
							System.out.println("FOUND A LOOP");
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Depth first search from a given startCell to the final/goal cell (cell down-right of final topRow cell)
	 * Will only search for cells on the same row or below as the startCell
	 * @param startCell the cell where the search will start from
	 * @param topRow a list of cells that are in the top row of this potential loop
	 * @return true if a loop is found, false otherwise
	 */
	private boolean dfsForFullLoop(ArrayList<Integer> startCell, ArrayList<ArrayList<Integer>> topRow) {
		// create a queue and initially add the start cell
		ArrayList<ArrayList<Integer>> cellsToSearchQueue = new ArrayList<ArrayList<Integer>>();
		cellsToSearchQueue.add(startCell);

		ArrayList<Integer> currentCell, downRightCell, goalCell, lastTopRowCell;
		ArrayList<ArrayList<Integer>> neighbours;

		// save the goal cell to a variable
		lastTopRowCell = topRow.get(topRow.size()-1);
		goalCell = getDownRightCell(lastTopRowCell.get(0), lastTopRowCell.get(1));

		// mark all cells in top row and those down-right of top row as visited (to prevent DFS going to them)
		for (ArrayList<Integer> oneCell : topRow) {
			board.getCell(oneCell.get(0), oneCell.get(1)).setVisited(true);
			// get the down-right cell
			downRightCell = getDownRightCell(oneCell.get(0), oneCell.get(1));
			if (downRightCell.get(0) != -1 ) {
				board.getCell(downRightCell.get(0), downRightCell.get(1)).setVisited(true);
			}
		}

		// begin depth first searching
		while (cellsToSearchQueue.size() > 0) {
			currentCell = cellsToSearchQueue.remove(0);
			// set cells to visited (to prevent going back to same cell)
			board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
			// get all the neighbours of the current cell
			neighbours = board.getNeighbours(currentCell.get(0), currentCell.get(1), Board.ALL_NEIGHBOURS);

			for (ArrayList<Integer> oneNeighbour : neighbours) {
				// check if the neighbour is the goal cell
				if ((oneNeighbour.get(0) != goalCell.get(0)) || (oneNeighbour.get(1) != goalCell.get(1))) {
					/* if neighbour isn't goal, then check for nodes to continue the search with
					 * neighbour can't be in the row above start cell, i.e. search can't go up (as top row is already found) */
					if (oneNeighbour.get(0) >= startCell.get(0)) {
						// neighbour can't be visited (prevent visiting same cells)
						if (!(board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).isVisited())) {
							// neighbour has passed all requirements, add it to the queue of nodes to search
							cellsToSearchQueue.add(oneNeighbour);
						}
					}
				} else {
					// found the goal cell!
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Whether the cell down-left of inputed cell is same colour
	 * @param i row index of cell
	 * @param j column index of cell
	 * @return true if down-left cell is same colour
	 */
	private boolean sameColourDownLeft(int i, int j) {
		if (board.isValidPosition(i, j)) {
			String colour = board.getCell(i, j).getContent();
			if (i < middleRowIndex) {
				if (board.isValidPosition(i+1, j)) {
					return board.getCell(i+1, j).getContent().equals(colour);
				} else {
					return false;
				}
			} else {
				if (board.isValidPosition(i+1, j-1)) {
					return board.getCell(i+1, j-1).getContent().equals(colour);
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * Whether the cell right of inputed cell is same colour
	 * @param i row index of cell
	 * @param j column index of cell
	 * @return true if right cell is same colour
	 */
	private boolean sameColourRight(int i, int j) {
		if (board.isValidPosition(i, j)) {
			String colour = board.getCell(i, j).getContent();
			if (board.isValidPosition(i, j+1)) {
				return board.getCell(i, j+1).getContent().equals(colour);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Whether the cell down-right of inputed cell is same colour
	 * @param i row index of cell
	 * @param j column index of cell
	 * @return true if down-right cell is same colour
	 */
	private boolean sameColourDownRight(int i, int j) {
		if (board.isValidPosition(i, j)) {
			String colour = board.getCell(i, j).getContent();
			if (i < middleRowIndex) {
				if (board.isValidPosition(i+i, j+1)) {
					return board.getCell(i+1, j+1).getContent().equals(colour);
				} else {
					return false;
				}
			} else {
				if (board.isValidPosition(i+1, j)) {
					return board.getCell(i+1, j).getContent().equals(colour);
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * Find the cell that is down and right of inputed cell
	 * @param i row index of cell
	 * @param j column index of cell
	 * @return ArrayList<Integer> (size 2) of the cell. E.g. [1,2]
	 */
	private ArrayList<Integer> getDownRightCell(int i, int j) {
		if (board.isValidPosition(i, j)) {
			if (i < middleRowIndex) {
				return new ArrayList<Integer>(Arrays.asList(i+1, j+1));
			} else {
				return new ArrayList<Integer>(Arrays.asList(i+1, j));
			}
		} else {
			return new ArrayList<Integer>(Arrays.asList(-1, -1));
		}
	}
}
