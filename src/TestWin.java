import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestWin {
	/* Temporary main method in here for testing */
	public static void main(String args[]) throws FileNotFoundException {
		Scanner stdIn = new Scanner(System.in);

		// correct input begins with an integer
		if (!stdIn.hasNextInt()) {
			System.out.println("Error: No dimension detected. Exiting program.");
			System.exit(1);
		}
		// dimension of the board, i.e. how many cells each edge has
		int dimension = stdIn.nextInt();
		// dimension must be > 5
		if (dimension <= 5) {
			System.out.println("Error: Board dimension too small. Exiting program.");
			System.exit(1);
		}
		Board board = createBoardFromInput(stdIn, dimension);

		System.out.println(board);
	}

	public static Board createBoardFromInput(Scanner stdIn, int dimension) {
		int numRows = dimension * 2 - 1;
		// create a new board object to store input into
		Board board = new Board(dimension);
		String item;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j <= board.getMaxColumn(i); j++) {
				if (stdIn.hasNext()) {
					item = stdIn.next();
					board.set(i, j, item);
				} else {
					// run out of values to read so wasn't even data inputed
					System.out.println("Error: Not enough cells in input. Exiting program.");
					System.exit(1);
				}
			}
		}
		return board;
	}
}
