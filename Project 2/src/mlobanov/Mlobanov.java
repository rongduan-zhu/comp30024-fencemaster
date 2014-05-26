package mlobanov;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import aiproj.fencemaster.*;

/**
 * 
 * @author Maxim Lobanov (mlobanov) & Rongduan Zhu (rz)
 *
 */

/* MARKING:
 * out of 22
 * 4 - quality of code and comments
 * 4 - correctness and readability of code
 * 		ALL SUBMITTED FILES MUST HAVE MAXIMUM LINE LENGTH OF 79 CHARACTERS
 * 7 - results of testing performance of game playing against other agents that uni provides
 * 7 - creativity of solution
 */

/* COMMENTS FILE:
 *  Describe approach taken in your solution in terms of:
 *  a) your search strategy
 *  b) your evaluation function
 *  c) any creative techniques you have applied such as how you optimised your search strategy or used machine learning
 */

/* STRATEGY:
 * 
 * Create a frame and then connect it to win while blocking opponent loop/high value positions
 * 
 * 
 * Evaluation Function? = w1f1(s) + w2f2(s) etc where w1 = weight and f1(s) = feature function e.g. number of white queens - black queens
 * 		Higher value to positions near stones of your own colour
 * 			Highest bonus for direct neighbour
 * 			Second highest for a stone that forms a virtual connection (can connect to it no matter what)
 * 			Third highest for stone that is 2 stones away from another one of your stones
 */

/* TO DO:
 * Need some way to determine if you can win with the next move
 */
public class Mlobanov implements Player, Piece {
	private int colour;
	private int opponentColour;
	private Board gameBoard;
	private int moveCount = 0;
	private Move opponentLastMove;
	
	/* Constructor */
	public Mlobanov() {
		
	}
	
	/* Function called by referee to initialize the player.
	 *  Return 0 for successful initialization and -1 for failed one.
	 */
	public int init(int n, int p) {
		// initialise player colour
		setColour(p);
		if (p == Piece.BLACK) { 
			setOpponentColour(Piece.WHITE);
		} else {
			setOpponentColour(Piece.BLACK);
		}
		// initialise the board
		gameBoard = new Board(n);
		
		
		return 0;
	}
	
	/**
	 * Testing init, pass in a board state
	 * @param oneBoard
	 * @param p
	 */
	public void init(Board oneBoard, int p) {
		setColour(p);
		if (p == Piece.BLACK) { 
			setOpponentColour(WHITE);
		} else {
			setOpponentColour(BLACK);
		}
		gameBoard = oneBoard; 
	}
	
	/* Function called by referee to request a move by the player.
	 *  Return object of class Move
	 */
	public Move makeMove() {
		/* distance between two points (x1,y1) and (x2,y2) is:
		 * (abs(x1-x2) + abs(y1-y2) + abs( (x1-y1) - (x2-y2))) / 2
		 */
		Move newMove;
		
		// determine if best move is to swap
		if (getMoveCount() == 1) {
			/* undo opponents move temporarily and see if it is best to swap with them */
			
			/* undo the move */
			gameBoard.getCell(opponentLastMove.Row, opponentLastMove.Col).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			
			newMove = negamaxDecision();
			
			if ((newMove.Row == opponentLastMove.Row) && (newMove.Col == opponentLastMove.Col)) {
				newMove = new Move(getColour(), true, opponentLastMove.Row, opponentLastMove.Col);
			} else {
				/* redo their move since we are not swapping */
				gameBoard.getCell(opponentLastMove.Row, opponentLastMove.Col)
					.setContent(pieceColourToCellColour(getOpponentColour()));
				
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
			}
		} else {
			/* otherwise search for a move normally */
			newMove = negamaxDecision();
		}
		
		/* old stuff 
		if (makeSwap()) {
			newMove = new Move(getColour(), false, opponentLastMove.Row, opponentLastMove.Col);
		} else {		
			// find best position on board to make move
			newMove = negamaxDecision();
		}*/
		
		
		/* update local move counter */
		setMoveCount(getMoveCount() + 1);
		
		/* get cell colour string to set cell content with */
		String self_colour = pieceColourToCellColour(getColour());
		
		gameBoard.getCell(newMove.Row, newMove.Col).setContent(self_colour);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
		
		return newMove;
	}
	
	/**
	 * Decide whether to make a swap
	 * @return True if swapping is advantageous, false otherwise.
	 */
	
	public boolean makeSwap() {
		// check if a swap is possible
		if (getMoveCount() == 1) {
			// swap if opponent's move is on a position that has good value
			int threshold = 5;
			if (gameBoard.getCell(opponentLastMove.Row, 
					opponentLastMove.Col).getValue() > threshold) {

				return true;
			}
		}
		return false;
	}
	
	/** 
	 * 
	 * @param currentBoard
	 * @return
	 */
	public Move negamaxDecision() {
		Move nextMove;
		int value;
		int maxValue = Integer.MIN_VALUE;
		Cell oneCell, bestCell;
		/* defining best cell to prevent possible uninitialised variable error
		 * value doesn't matter as value will always be changed on a real board */		
		bestCell = new Cell(0, 0);
		
		String content = pieceColourToCellColour(getColour());

		/* java -cp . aiproj.fencemaster.Referee N mlobanov.Mlobanov mlobanov.Mlobanov */
		
		for (int i = 0; i < gameBoard.getNumRows(); ++i) {
			for (int j = 0; j < gameBoard.getNumRows(); ++j) {
				
				// ensure cell is valid and not taken
				oneCell = gameBoard.getCell(i, j);
				if (oneCell == null || oneCell.taken()) {
					continue;
				}
				/* pretend you have taken the cell and see what the value is */
				/* using depth = 0 reveals true vale of a cell */
							
				/* make the move */
				/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(content);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);*/
				
				value = negamaxValue(oneCell, getColour(), 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
				System.out.println("value of the cell " + oneCell.getRow() + ", " + oneCell.getCol() + " is " + value);
				
				/* undo the move */
				/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(Cell.EMPTY);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);*/
				
				/* record cell with the highest value */
				if (value > maxValue) {
					maxValue = value;
					bestCell = oneCell;
				}
			}
		}

		nextMove = new Move(getColour(), false, bestCell.getRow(), bestCell.getCol());
		return nextMove;
	}
	
	
	
	public int negamaxValue(Cell moveCell, int colour, int depth, int alpha, int beta) {
		int value, getWinnerResult, searchColour;
		String content;
		
		/* update searchColour depending on which player's move it is */
		if (colour == Piece.BLACK) {
			content = Cell.BLACK;
			searchColour = Piece.WHITE;
		} else {
			content = Cell.WHITE;
			searchColour = Piece.BLACK;
		}
		
		/* make the move */
		gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(content);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
		
		/* search until a depth limit or terminal node (win/loss/draw) */
		getWinnerResult = getWinner();
		if ((depth == 0) || (getWinnerResult >= 0)) {
			/* evaluate move based on which player's move it is */
			value = evaluateMove(moveCell, colour, getWinnerResult);
			
			/* the value is negated if opponents move */
			if (colour == getOpponentColour()) {
				value *= -1;
			}
			/* undo the move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return value;
		} else {
			Cell oneCell;
			int maxValue = Integer.MIN_VALUE;
			int newAlpha = alpha;
			
			/* check over each remaining move */
			for (int i = 0; i < gameBoard.getNumRows(); i++) {
				for (int j = 0; j < gameBoard.getNumRows(); j++) {
					
					// ensure cell is valid and not taken
					oneCell = gameBoard.getCell(i, j);
					if (oneCell == null || oneCell.taken()) {
						continue;
					}
					
					
					
					value = -1 * negamaxValue(oneCell, searchColour, depth - 1, -beta, -newAlpha);
					if (value > maxValue) {
						maxValue = value;
					}
					
					
					
					/* alpha beta pruning */					
					if (maxValue > newAlpha) {
						newAlpha = maxValue;
					}
					if (newAlpha >= beta) {
						return newAlpha;
					}					
				}
			}			
			/* undo the move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return maxValue;
		}
	}
	
	/**
	 * Calculate value of the move to the specified player
	 * @param oneCell - The cell that would be taken
	 * @param colour - The colour of player that would take the cell
	 * @return Value of the move 
	 */
	public int evaluateMove(Cell oneCell, int colour, int getWinnerResult) {
		/* give highest value to a winning move */
		if (getWinnerResult == getColour()) {
			return 100;
		} else if (getWinnerResult == getOpponentColour()) {
			/* very bad if opponent has a win */
		}
		return (int)(oneCell.getRow() + oneCell.getCol());
	}
	
	
	/* Function called by referee to inform the player about the opponent's move
	 *  Return -1 if the move is illegal otherwise return 0
	 */
	public int opponentMove(Move m) {
		boolean swapped = false;
		
		setMoveCount(getMoveCount() + 1);
		
		// Can only swap on second move in the game
		if (getMoveCount() != 2 && m.IsSwap) {
			return -1;
		}
		
		// Can't place a piece on top of another piece or invalid position
		String cellContent = gameBoard.getCell(m.Row, m.Col).getContent(); 
		if (!cellContent.equals(Cell.EMPTY)) {
			// check if the content is our own cell colour (in the case of a swap)
			if (cellContent.equals(pieceColourToCellColour(getColour()))) {
				if (getMoveCount() == 2 && m.IsSwap){
					swapped = true;
				}
			}
			if (!swapped) {
				return -1;
			}
		}
		
		String colour = pieceColourToCellColour(getOpponentColour());
		gameBoard.getCell(m.Row, m.Col).setContent(colour);
		if (!swapped) {
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
		}
		/*System.out.println("Updating row and column: " + m.Row + ", " + m.Col + " for opponent:");*/
		// update instance variable of opponent's last move
		setOpponentLastMove(m);
		return 0;
	}
	
	/* This function when called by referee should return the winner
	 *  
	 */
	// THIS FUNCTION TAKES TOO LONG TO BE ABLE TO BE USED IN MINIMAX
	public int getWinner() {
		
		// -1 = INVALID/Non-Terminal State, 0 = EMPTY/DRAW, 1 = WHITE, 2 = BLACK
		LoopSearch findLoop = new LoopSearch(gameBoard);
		TripodAgent findTripod = new TripodAgent(gameBoard);
		// test if there is a tripod win
		ArrayList<Boolean> tripods = findTripod.searchForTripod();
		int result = whoWon(tripods);
		if (result != -1) {
			return result;
		}
		// test if there is a loop win
		ArrayList<Boolean> loops = new ArrayList<Boolean> (
				Arrays.asList(
						findLoop.searchForLoop(Cell.BLACK),
						findLoop.searchForLoop(Cell.WHITE)
				)
		);
		result = whoWon(loops);
		if (result != -1) {
			return result;
		}
		// check if board is not full
		if (gameBoard.getOccupiedCells() < gameBoard.getTotalNumCells()) {
			return -1;
		}
		return 0;
	}
	
	/* Function called by referee to get the board configuration in String format
	 * from player 
	 */
	public void printBoard(PrintStream output) {
		output.println(gameBoard);
	}
	
	/* Getters and Setters */

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

	public Move getOpponentLastMove() {
		return opponentLastMove;
	}

	public void setOpponentLastMove(Move opponentLastMove) {
		this.opponentLastMove = opponentLastMove;
	}

	/**
	* takes result as [blackResult, whiteResult]
	* returns 2 if black won, 1 if white, -1 if none
	*/
	private int whoWon(ArrayList<Boolean> result) {
		if (result.get(0)) {
			return 2;
		}
		if (result.get(0)) {
			return 1;
		}
		return -1;
	}

	public int getOpponentColour() {
		return opponentColour;
	}

	public void setOpponentColour(int opponentColour) {
		this.opponentColour = opponentColour;
	}
	
	public static int cellColourToPieceColour(String cellColour) {
		if (cellColour.equals(Cell.WHITE)) {
			return 1;
		}
		if (cellColour.equals(Cell.BLACK)) {
			return 2;
		}
		return -1;
	}
	
	public static String pieceColourToCellColour(int pieceColour) {
		if (pieceColour == Piece.WHITE) {
			return Cell.WHITE;
		}
		if (pieceColour == Piece.BLACK) {
			return Cell.BLACK;
		}
		return Cell.INVALID;
	}
}
