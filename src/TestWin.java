import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TestWin {

	public static void main(String args[]) {
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
		LoopSearch findLoop = new LoopSearch(board);
		TripodAgent findTripod = new TripodAgent(board);
		ArrayList<Boolean> tripods = findTripod.searchForTripod();
		ArrayList<Boolean> loops = new ArrayList<Boolean> (
				Arrays.asList(
						findLoop.searchForLoop(Cell.BLACK),
						findLoop.searchForLoop(Cell.WHITE)
				)
		);
		outputResult(board, tripods, loops);
	}

	/**
	 * Print the appropriate output based on the specification using the output provided by the
	 * searching algorithms
	 * @param board, current board
	 * @param tripods, results from tripod search, (black, white)
	 * @param loops, results from loop search
	 */
	public static void outputResult(Board board, ArrayList<Boolean> tripods, ArrayList<Boolean> loops) {
		if (tripods.get(0) && loops.get(0)) {
			System.out.println("Black\nBoth");
		} else if (tripods.get(1) && loops.get(1)) {
			System.out.println("White\nBoth");
		} else if (tripods.get(0) || loops.get(0)) {
			System.out.println("Black\n" + (tripods.get(0) ? "Tripod" : "Loop"));
		} else if (tripods.get(1) || loops.get(1)) {
			System.out.println("White\n" + (tripods.get(1) ? "Tripod" : "Loop"));
		} else {
			System.out.println(
					(board.getOccupiedCells() < board.getTotalNumCells()
							? "None" : "Draw")
					+ "\nNil");
		}
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
