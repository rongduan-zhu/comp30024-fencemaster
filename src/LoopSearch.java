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

		int middleRowIndex = (board.getNumRows() + 1) / 2 - 1;

		for (ArrayList<Integer> oneCell : colourCells) {
			if (board.getCell(oneCell.get(0), oneCell.get(1)).isVisited()) {
				continue;
			}
			// create a queue of cells to visit and add this cell as the intial cell to visit
			ArrayList<ArrayList<Integer>> cellsToVisitQueue = new ArrayList<ArrayList<Integer>>();
			// !!!!!!!!!! starting cell should check in 4 directions, fix later
			oneCell.add(directionToInt("right"));
			cellsToVisitQueue.add(oneCell);
			// cell that the loop search began from
			ArrayList<Integer> startCell = oneCell;
			// current cell that loop search is in
			ArrayList<Integer> currentCell;
			// 2D array list of neighbour cells to current cell
			ArrayList<ArrayList<Integer>> currentCellNeighbours;
			System.out.println("i am starting at cell: " + startCell);
			String direction, nextDirection;
			while (cellsToVisitQueue.size() > 0) {
				System.out.println("queue is: " + cellsToVisitQueue);
				currentCell = cellsToVisitQueue.remove(0);

				currentCellNeighbours = board.getNeighbours(currentCell.get(0), currentCell.get(1), Board.ALL_NEIGHBOURS);
				System.out.println("i am at cell: " + currentCell + " and the neighbours are: " + currentCellNeighbours);
				// every cell in a loop must have at least two neighbours
				if (currentCellNeighbours.size() < 2) {
					// not enough neighbours, go to next cell in queue
					System.out.println("not enough neighbours, skipping this cell");
					// set to visited as this cell cannot possibly start a loop
					board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
					continue;
				} else if (currentCellNeighbours.size() == 2) {
					// when you have two neighbours, they cannot be neighbours of each other
					// (i.e. they must 'spread apart' from start)
					// otherwise it is not a loop
					ArrayList<Integer> neighbourOne = currentCellNeighbours.get(0);
					ArrayList<Integer> neighbourTwo = currentCellNeighbours.get(1);
					ArrayList<ArrayList<Integer>> neighbourOneNeighbours = board.getNeighbours(neighbourOne.get(0), neighbourOne.get(1), Board.ALL_NEIGHBOURS);
					if (neighbourOneNeighbours.contains(neighbourTwo)) {
						System.out.println("two neighbours next to each other, skipping this cell");
						// set to visited as this cell cannot possibly start a loop
						board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
						continue;
					}
				} else if (currentCellNeighbours.size() == 6) {
					// the only case of having six neighbours is a cell completely surrounded which is not a loop
					System.out.println("found 6 neighbours, skipping this cell");
					// set to visited as this cell cannot possibly start a loop
					board.getCell(currentCell.get(0), currentCell.get(1)).setVisited(true);
					continue;
				}

				direction = intToDirection(currentCell.get(2));

				// default next direction to avoid an error
				nextDirection = direction;
				for (ArrayList<Integer> oneNeighbour : currentCellNeighbours) {
					if (!board.getCell(oneNeighbour.get(0), oneNeighbour.get(1)).isVisited()) {

						// exclude all neighbours that aren't in the 'forward' direction of the current cell
						if (direction.equals("right")) {
							if (oneNeighbour.get(1) < currentCell.get(1)) {
								continue;
							}
							// above middle row where board is increasing
							if (oneNeighbour.get(0) < middleRowIndex) {
								if ((oneNeighbour.get(0) > currentCell.get(0)) && (oneNeighbour.get(1) == currentCell.get(1))) {
									continue;
								}
							} else if (oneNeighbour.get(0) == middleRowIndex) {
								if ((oneNeighbour.get(0) == currentCell.get(0)) && (oneNeighbour.get(1) < currentCell.get(1))) {
									continue;
								}
							} else {
								if ((oneNeighbour.get(1) == currentCell.get(1)) && (oneNeighbour.get(0) < currentCell.get(0))) {
									continue;
								}
							}
						}
						if (direction.equals("down-right")) {
							if (oneNeighbour.get(0) < currentCell.get(0)) {
								continue;
							}
							// above middle row where board is increasing
							if (oneNeighbour.get(0) < middleRowIndex) {
								if (oneNeighbour.get(1) < currentCell.get(1)) {
									continue;
								}
							} else {
								if ((oneNeighbour.get(1) < currentCell.get(1)) && (oneNeighbour.get(0) == currentCell.get(0))) {
									continue;
								}
							}
						}
						if (direction.equals("down-left")) {
							if (oneNeighbour.get(0) < currentCell.get(0)) {
								continue;
							}
							// above middle row where board is increasing
							if (oneNeighbour.get(0) < middleRowIndex) {
								if (oneNeighbour.get(1) > currentCell.get(1)) {
									continue;
								}
							} else {
								if ((oneNeighbour.get(1) > currentCell.get(1)) && (oneNeighbour.get(0) == currentCell.get(0))) {
									continue;
								}
							}
						}
						if (direction.equals("up-right")) {
							// can't go to cell below this one
							if (oneNeighbour.get(0) > currentCell.get(0)) {
								continue;
							}
							// can't go left
							if ((oneNeighbour.get(1) < currentCell.get(1)) && (oneNeighbour.get(0) == currentCell.get(0))) {
								continue;
							}
						}
						if (direction.equals("up-left")) {
							// can't go to cell below this one
							if (oneNeighbour.get(0) > currentCell.get(0)) {
								continue;
							}
							// above middle row where board is increasing
							// can't go right
							if ((oneNeighbour.get(1) > currentCell.get(1)) && (oneNeighbour.get(0) == currentCell.get(0))) {
								continue;
							}
						}
						if (direction.equals("left")) {
							if (oneNeighbour.get(1) > currentCell.get(1)) {
								continue;
							}
							// above middle row where board is increasing
							if (oneNeighbour.get(0) < middleRowIndex) {
								if ((oneNeighbour.get(0) < currentCell.get(0)) && (oneNeighbour.get(1) == currentCell.get(1))) {
									continue;
								}
							} else if (oneNeighbour.get(0) == middleRowIndex) {
								if ((oneNeighbour.get(0) == currentCell.get(0)) && (oneNeighbour.get(1) > currentCell.get(1))) {
									continue;
								}
							} else {
								if ((oneNeighbour.get(1) == currentCell.get(1)) && (oneNeighbour.get(0) > currentCell.get(0))) {
									continue;
								}
							}
						}
						// get the direction that the neighbour is from the current cell
						// neighbour is right of current cell
						if (oneNeighbour.get(0) == currentCell.get(0)) {
							if (oneNeighbour.get(1) > currentCell.get(1)) {
								// neighbour is directly right of current cell
								nextDirection = "right";
							} else {
								// neighbour is directly left of current cell
								nextDirection = "left";
							}
						}
						// check for cells above middle row
						if (oneNeighbour.get(0) < middleRowIndex) {
							if (oneNeighbour.get(0) > currentCell.get(0)) {
								if (oneNeighbour.get(1) > currentCell.get(1)) {
									// neighbour is down and right of current cell
									nextDirection = "down-right";
								} else {
									// neighbour is down and left of current cell
									nextDirection = "down-left";
								}
							} else if (oneNeighbour.get(0) < currentCell.get(0)) {
								if (oneNeighbour.get(1) == currentCell.get(1)) {
									// neighbour is up and right of current cell
									nextDirection = "up-right";
								} else {
									// neighbour is up and left of current cell
									nextDirection = "up-left";
								}
							}
						} else {
							// check for cells in middle row and below
							if (oneNeighbour.get(0) > currentCell.get(0)) {
								if (oneNeighbour.get(1) == currentCell.get(1)) {
									// neighbour is down and right of current cell
									nextDirection = "down-right";
								} else {
									// neighbour is down and left of current cell
									nextDirection = "down-left";
								}
							} else if (oneNeighbour.get(0) < currentCell.get(0)) {
								if (oneNeighbour.get(1) > currentCell.get(1)) {
									// neighbour is up and right of current cell
									nextDirection = "up-right";
								} else {
									// neighbour is up and left of current cell
									nextDirection = "up-left";
								}
							}
						}

						if ((oneNeighbour.get(0) == startCell.get(0)) &&
								(oneNeighbour.get(1) == startCell.get(1)) &&
								!currentCell.equals(startCell)) {
							System.out.println("FOUND IT!!! at cell:" + currentCell + " and start cell is:" + startCell);
							return true;
						}
						// add the neighbour to the queue with the 'forward' direction, which is the direction
						// the current cell had to take to move into it
						oneNeighbour.add(directionToInt(nextDirection));
						cellsToVisitQueue.add(0, oneNeighbour);
					}
				}
			}
		}
		System.out.println("failed, returning false");
		return false;
	}
	/* direction list:
	 * 0 - right
	 * 1 - down-right
	 * 2 - down-left
	 * 3 - left
	 * 4 - up-left
	 * 5 - up-right
	 */
	private int directionToInt(String direction) {
		if (direction.equals("right")) {
			return 0;
		} else if (direction.equals("down-right")) {
			return 1;
		} else	if (direction.equals("down-left")) {
			return 2;
		} else if (direction.equals("left")) {
			return 3;
		} else if (direction.equals("up-left")) {
			return 4;
		} else {
			return 5;
		}
	}

	private String intToDirection(int directionNum) {
		if (directionNum == 0) {
			return "right";
		} else if (directionNum == 1) {
			return "down-right";
		} else if (directionNum == 2) {
			return "down-left";
		} else if (directionNum == 3) {
			return "left";
		} else if (directionNum == 4) {
			return "up-left";
		} else {
			return "up-right";
		}
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
