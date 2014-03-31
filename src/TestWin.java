import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestWin {
	/* Temporary main method in here for testing */
	public static void main(String args[]) throws FileNotFoundException {
		// set the standard input stream to be a text file (for testing input)
		System.setIn(new FileInputStream("C:/Users/Maxim/Documents/Uni/COMP30024/Project1/AIProj/src/input-loop13.txt"));

		Scanner stdIn = new Scanner(System.in);

		// correct input begins with an integer
		if (!stdIn.hasNextInt()) {
			System.out.println("Error: No dimension detected. Exiting program.");
			System.exit(1);
		}
		// dimension of the board, i.e. how many cells each edge has
		int dimension = stdIn.nextInt();
		// dimension must be > 5
		if (dimension < 5) {
			System.out.println("Error: Board dimension too small. Exiting program.");
			System.exit(1);
		}
		Board board = createBoardFromInput(stdIn, dimension);
		System.out.println(board);
		//Tests get neighbours
		/*for (ArrayList<Integer> x : board.getNeighbours(0, 1)) {
			System.out.println(x.get(0) + " " + x.get(1));
		}*/
		LoopSearch2 findLoop2 = new LoopSearch2(board);
		System.out.println("TESTING FOR BLACK LOOP");
		System.out.println("");
		findLoop2.searchForLoop(Cell.BLACK);
		System.out.println("TESTING FOR WHITE LOOP");
		System.out.println("");
		findLoop2.searchForLoop(Cell.WHITE);
		/*long startTime, endTime;
		LoopSearch findLoop = new LoopSearch(board);

		startTime = System.nanoTime();
		System.out.println("TESTING FOR BLACK LOOP");
		System.out.println("");
		System.out.println(findLoop.searchForLoop(Cell.BLACK));
		System.out.println("");
		System.out.println("");
		System.out.println("TESTING FOR WHITE LOOP");
		System.out.println("");
		System.out.println(findLoop.searchForLoop(Cell.WHITE));

		endTime = System.nanoTime();

		System.out.println("Finding a loop took: " + (endTime - startTime) / Math.pow(10, 9) + " seconds.");
		 SEARCH
		TripodAgent findTripod = new TripodAgent(board);

		startTime = System.nanoTime();

		ArrayList<Boolean> tripods = findTripod.searchForTripod();

		endTime = System.nanoTime();
		System.out.println("Finding a tripod took: " + (endTime - startTime) / Math.pow(10, 9) + " seconds.");
		System.out.println(tripods.get(0) + " " + tripods.get(1));
		*/
		return;
	}

	public static Board createBoardFromInput(Scanner stdIn, int dimension) {
		int numRows = dimension * 2 - 1;
		// create a new board object to store input into
		Board board = new Board(dimension);
		String item;
		for (int i = 0; i < numRows; ++i) {
			for (int j = 0; j <= board.getMaxColumn(i); ++j) {
				if (stdIn.hasNext()) {
					item = stdIn.next();
					board.set(i, j, item);
					/*if (board.isEdgeNode(i, j)) {
						System.out.println(i + " " + j);
					}*/
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
