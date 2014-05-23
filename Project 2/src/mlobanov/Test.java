package mlobanov;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import aiproj.fencemaster.Piece;

public class Test {
	
	public static void main(String[] args) {
		try {
			System.setIn(new FileInputStream("input.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scanner stdIn = new Scanner(System.in);
		
		// correct input begins with an integer
		if (!stdIn.hasNextInt()) {
			System.out.println("Error: No dimension detected. Exiting program.");
			System.exit(1);
		}
		// dimension of the board, i.e. how many cells each edge has
		int dimension = stdIn.nextInt();
		// dimension must be > 0
		if (dimension < 1) {
			System.out.println("Error: Board dimension too small. Exiting program.");
			System.exit(1);
		}
		
		Board board = createBoardFromInput(stdIn, dimension);
		System.out.println(board);
		
		Mlobanov player = new Mlobanov();
		player.init(board, Piece.WHITE);
		player.makeMove();
		
		stdIn.close();
	}
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
	
	public static boolean validInput(String item) {
		return item.equals(Cell.BLACK) || item.equals(Cell.WHITE) || item.equals(Cell.EMPTY);
	}

}
