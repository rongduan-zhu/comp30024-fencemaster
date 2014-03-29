import java.util.Scanner;

public class Game {
	/* Temporary main method in here for testing */
	public static void main(String args[]) {
		Board board = new Board(5);
		readStdInput(board);
		System.out.println(board);
	}

	public static void readStdInput(Board board) {
		Scanner stdin = new Scanner(System.in);
		int size = stdin.nextInt() * 2 - 1;
		String item = new String();
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				if (board.isValidPosition(i, j)) {
					item = stdin.next();
					board.set(i, j, item);
				}
			}
		}
	}
}
