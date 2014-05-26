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
		int boardTotalCells=3*n*(n-1)+1;
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
			setOpponentColour(Piece.WHITE);
		} else {
			setOpponentColour(Piece.BLACK);
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
		if (makeSwap()) {
			newMove = new Move(getColour(), false, opponentLastMove.Row, opponentLastMove.Col);
		} else {		
			// find best position on board to make move
			newMove = minimaxDecision();
		}
		 
		setMoveCount(getMoveCount() + 1);
		String self_colour;
		if (getColour() == Piece.BLACK) {
			self_colour = Cell.BLACK;
		} else {
			self_colour = Cell.WHITE;
		}
		gameBoard.getCell(newMove.Row, newMove.Col).setContent(self_colour);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
		System.out.println(gameBoard);
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
	public Move minimaxDecision() {
		Move nextMove;
		int value;
		int maxValue = Integer.MIN_VALUE;
		Cell oneCell, bestCell;
		/* defining best cell to prevent possible uninitialised variable error
		 * value doesn't matter as value will always be changed on a real board */		
		bestCell = new Cell(0, 0);
		
		String content;
		if (getColour() == Piece.BLACK) {
			content = Cell.BLACK;
		} else {
			content = Cell.WHITE;
		}	

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
				gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(content);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
				
				value = negamaxValue(oneCell, getColour(), 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
				System.out.println("value of the cell " + oneCell.getRow() + ", " + oneCell.getCol() + " is " + value);
				
				/* undo the move */
				gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(Cell.EMPTY);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
				
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
	
	/**
	 * 
	 * @param moveCell - The cell that has been 'taken' at this level of the tree
	 * @param colour
	 * @param depth
	 * @return
	 */
	public int minimaxValue(Cell moveCell, int colour, int depth) {
		int maxValue = -1;
		int minValue = 10000;
		int value;
		
		Cell oneCell;
		
		/* SHOULD MAKE PIECE COLOUR TO CELL COLOUR METHOD OR USE SAME VALUES FOR BOTH?? */
		String content;
		if (colour == Piece.BLACK) {
			content = Cell.BLACK;
		} else {
			content = Cell.WHITE;
		}
		/* make the move */
		gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(content);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
		
		/* search until a depth limit */
		if ((depth == 0) || (getWinner() >= 0)) {
			/* evaluate move based on which player's move it is */
			value = evaluateMove(moveCell, colour);
			/* undo the move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return value;
		} else if (colour == getColour()) {
			/* currently player's turn */
			for (int i = 0; i < gameBoard.getNumRows(); i++) {
				for (int j = 0; j < gameBoard.getNumRows(); j++) {
					
					// ensure cell is valid and not taken
					oneCell = gameBoard.getCell(i, j);
					if (oneCell == null || oneCell.taken()) {
						continue;
					}
					
					value = minimaxValue(oneCell, getOpponentColour(), depth - 1);
					if (value > maxValue) {
						maxValue = value;
					}
				}
			}			
			/* undo the move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return maxValue;
			/* return max of minimaxValue(each successor) */
		} else {
			/* currently enemy turn */
			for (int i = 0; i < gameBoard.getNumRows(); i++) {
				for (int j = 0; j < gameBoard.getNumRows(); j++) {
					if (!gameBoard.isValidPosition(i, j)) {
						continue;
					}
					
					oneCell = gameBoard.getCell(i, j);
					// check if the cell has been taken
					if (oneCell.taken()) {
						continue;
					}
					value = minimaxValue(oneCell, getColour(), depth - 1);
					if (value < minValue) {
						minValue = value;
					}
				}
			}
			/* undo the move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return minValue;
		}
		/*if cutoff/terminal_state(game) {
			return evaluation_fn(state)
		} else if (my move) {
			return highest minimax-value of successors(state)
		} else {
			return lowest minimax-value of successors(state)
		}*/
	}
	
	public int negamaxValue(Cell moveCell, int colour, int depth, int alpha, int beta) {
		int value, getWinnerResult;
		String content;
		
		/* SHOULD MAKE PIECE COLOUR TO CELL COLOUR METHOD OR USE SAME VALUES FOR BOTH?? */
		if (colour == Piece.BLACK) {
			content = Cell.BLACK;
		} else {
			content = Cell.WHITE;
		}
		
		/* search until a depth limit or terminal node - a winning state */
		getWinnerResult = getWinner();
		if ((depth == 0) || (getWinnerResult >= 0)) {
			/* evaluate move based on which player's move it is */
			if (getWinnerResult >= 0) {
				System.out.println("There is a winner at depth: " + depth + "and it is " + getWinnerResult);
				System.out.println(gameBoard);
				System.out.println("=====================================");
			}
			value = evaluateMove(moveCell, colour, getWinnerResult);
			
			/* the value is negated if opponents move */
			if (colour == getOpponentColour()) {
				value *= -1;
			}
			
			/* undo the move */
			/*gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);*/
			
			return value;
		} else {
			int searchColour;
			int maxValue = Integer.MIN_VALUE;
			int newAlpha = alpha;
			Cell oneCell;
			
			/* update searchColour depending on which player's move it is */
			if (colour == getColour()) {
				searchColour = getOpponentColour();
			} else {
				searchColour = colour;
			}
			/* check over each remaining move */
			outerLoop:
			for (int i = 0; i < gameBoard.getNumRows(); i++) {
				for (int j = 0; j < gameBoard.getNumRows(); j++) {
					
					// ensure cell is valid and not taken
					oneCell = gameBoard.getCell(i, j);
					if (oneCell == null || oneCell.taken()) {
						continue;
					}
					
					/* make the move */
					gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(content);
					gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
					
					value = -1 * negamaxValue(oneCell, searchColour, depth - 1, -beta, -alpha);
					if (value > maxValue) {
						maxValue = value;
					}
					
					/* undo the move */
					gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(Cell.EMPTY);
					gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
					
					/* alpha beta pruning */					
					if (value > alpha) {
						newAlpha = value;
					}
					if (newAlpha > beta) {
						break outerLoop;
					}					
				}
			}			
			
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
		if (getWinnerResult == getColour()) {
			System.out.println("FOUND A WINNER");
			System.out.println("FOUND A WINNER");
			System.out.println("FOUND A WINNER");
			System.out.println("FOUND A WINNER");
			return 100;
		}
		return (int)(oneCell.getRow() + oneCell.getCol());
	}
	
	
	/* Function called by referee to inform the player about the opponent's move
	 *  Return -1 if the move is illegal otherwise return 0
	 */
	public int opponentMove(Move m) {
		setMoveCount(getMoveCount() + 1);
		// Check if the move is illegal
		
		// Can only swap on second move in the game
		if (getMoveCount() != 2 && m.IsSwap) {
			return -1;
		}
		
		// Can't place a piece on top of another piece or invalid position
		String cellContent = gameBoard.getCell(m.Row, m.Col).getContent(); 
		if (!cellContent.equals(Cell.EMPTY)) {
			return -1;
		}
		
		String colour;
		if (getOpponentColour() == Piece.BLACK) {
			colour = Cell.BLACK;
		} else {
			colour = Cell.WHITE;
		}
		gameBoard.getCell(m.Row, m.Col).setContent(colour);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
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
		// Kevin please do this
		// gameBoard.searchForTripod()
		// gameBoard.searchForLoop()
		
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
}
