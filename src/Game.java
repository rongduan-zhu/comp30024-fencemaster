import java.util.Scanner;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;


public class Game {
	/* Temporary main method in here for testing */
	public static void main(String args[]) {
		Board board = new Board(5);
		readStdInput();		
		System.out.println(board);
	}
	
	public static void readStdInput(Board board) {
		Scanner stdin = new Scanner(System.in);
		int size = stdin.nextInt();
	}
}
