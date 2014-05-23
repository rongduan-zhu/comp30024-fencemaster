package mlobanov;

import Board;
import Cell;

import java.util.Scanner;

public class Test {
	/**
	 * @param stdIn, input stream
	 * @param dimension, dimension of the board, that is how long an individual edge is
	 * @return the board constructed from standard input as Board
	 */
	public static Board createBoardFromInput(Scanner stdIn, int dimension) {
		int numRows = dimension * 2 - 1;
		// create a new board object to store input into
		Board board = new Board(dimension);
		String item;
		for (int i = 0; i < numRows; ++i) {
			for (int j = 0; j < numRows; ++j) {
				if (!board.isValidPosition(i, j)) {
					continue;
				}
				if (stdIn.hasNext()) {
					item = stdIn.next();
					if (!validInput(item)) {
						System.out.println("Error: Invalid input");
						System.exit(1);
					}

					board.set(i, j, item);
					if (!item.equals(Cell.EMPTY)) {
						board.incrementOccupiedCells();
					}
				} else {
					// run out of values to read so not enough data inputed
					System.out.println("Error: Not enough cells in input. Exiting program.");
					System.exit(1);
				}
			}
		}
		return board;
	}

}
