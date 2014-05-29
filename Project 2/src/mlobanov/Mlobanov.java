package mlobanov;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import aiproj.fencemaster.*;

/**
 *
 * @author Maxim Lobanov (mlobanov) and Rongduan Zhu (rz)
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
	private Cell moveRef;
	private int lowestMovesForTerminalState;


	/* Constructor(You can delete this line) */
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
		System.out.println("I have been initialised. My colour is " + p + " and my opponent is " + getOpponentColour());
		gameBoard = new Board(n);
		/* Calculate lowest number of moves required for terminal state
		 * for given board size. Assumes board dimension is > 1.
		 * Includes moves for other player */
		if (n == 3) {
			/* Can make a quick tripod here */
			lowestMovesForTerminalState = 9;
		} else {
			/* Fastest win is with a ring */
			lowestMovesForTerminalState = 11;
		}

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
		System.out.println("I have been initialised. My colour is " + p + " and my opponent is " + getOpponentColour());
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
			/* undo opponents move temporarily and see if it is best to
			 * swap with them */

			/* undo the move */
			gameBoard.getCell(opponentLastMove.Row, opponentLastMove.Col).
								setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);

			newMove = minimaxDecision();

			if ((newMove.Row == opponentLastMove.Row) &&
				 (newMove.Col == opponentLastMove.Col)) {

				newMove = new Move(getColour(), true,
								opponentLastMove.Row, opponentLastMove.Col);
			} else {
				/* redo their move since we are not swapping */
				gameBoard.getCell(opponentLastMove.Row, opponentLastMove.Col)
					.setContent(pieceColourToCellColour(getOpponentColour()));

				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);
			}
		} else {
			/* otherwise search for a move normally */
			newMove = minimaxDecision();
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
		System.out.println("I am making a move at " + newMove.Row + ", " + newMove.Col);
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
	 * Find the best value move using Negamax search
	 * @return - The best value move to make
	 */
	public Move minimaxDecision() {
		Move nextMove;
		int value;
		int maxValue = Integer.MIN_VALUE;
		Cell oneCell, bestCell;
		/* Defining best cell to prevent possible uninitialised variable
		 * error. Value doesn't matter as value will always be changed on
		 * a real board. */
		bestCell = new Cell(0, 0);

		//String content = pieceColourToCellColour(getColour());

		/* java -cp . aiproj.fencemaster.Referee N mlobanov.Mlobanov mlobanov.Mlobanov */

		/* Begin negamax search with every empty position on the board
		 * as the root node. */
		for (int i = 0; i < gameBoard.getNumRows(); ++i) {
			for (int j = 0; j < gameBoard.getNumRows(); ++j) {

				/* Ensure the cell is valid and not taken. */
				oneCell = gameBoard.getCell(i, j);
				if (oneCell == null || oneCell.taken()) {
					continue;
				}

				/* using depth = 0 reveals true value of a cell. */

				/* Make a temporary move in order to detect a terminal state
				 * later, if it exists. */
				/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(content);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);*/

				//System.out.println("BEGINNING MINIMAX SEARCH. ROOT NODE: " + oneCell.getRow() + ", " + oneCell.getCol());
				short neighbourCounter = (short)gameBoard.getNeighbours(i, j, Board.ALL_NEIGHBOURS).size();

				value = minimaxValue(oneCell, getColour(), 1, Integer.MIN_VALUE, Integer.MAX_VALUE, (short) neighbourCounter, (short) 0);

				//System.out.println("Value of the cell " + oneCell.getRow() + ", " + oneCell.getCol() + " is " + value);

				/* Undo the temporary move. */
				/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(Cell.EMPTY);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);*/

				/* Save the cell with the highest value. */
				if (value > maxValue) {
					maxValue = value;
					bestCell = oneCell;
				}
			}
		}

		nextMove = new Move(getColour(), false, bestCell.getRow(), bestCell.getCol());
		return nextMove;
	}



	public int minimaxValue(Cell moveCell, int searchColour, int depth, int alpha, int beta, short neighbourCounter, short secondaryCounter) {

		int value, getWinnerResult;

		/* Make a temporary move in order to detect a terminal state later,
		 * if it exists */
		String content = pieceColourToCellColour(searchColour);
		gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(content);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);

		/* Check if board is in terminal state or at depth limit for
		 * searching. */
		getWinnerResult = getWinner();
		if ((getWinnerResult >= 0) || (depth == 0)) {

			/* Evaluate move. */
			moveRef = moveCell;
			value = minimaxEvaluateBoard(getWinnerResult, neighbourCounter, secondaryCounter);

			/* Undo the temporary move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return value;
		}

		/* Not at a terminal state. */
		Cell oneCell;
		int newAlpha = alpha;
		int newBeta = beta;
		int nextSearchColour;
		nextSearchColour = oppositeColour(searchColour);
		//short tempNeighbourCounter;

		if (nextSearchColour == getColour()) {
			for (int i = 0; i < gameBoard.getNumRows(); ++i) {
				
				for (int j = 0; j < gameBoard.getNumRows(); ++j) {

					/* Ensure cell is valid and not taken */
					oneCell = gameBoard.getCell(i, j);
					if (oneCell == null || oneCell.taken()) {
						continue;
					}
					//tempNeighbourCounter = neighbourCounter;

					//tempNeighbourCounter += gameBoard.getNeighbours(i, j, Board.ALL_NEIGHBOURS).size();
					//secondaryCounter += gameBoard.getSecondaryConnection(i, j, gameBoard.get(i, j)).size();
					
					/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(content);
					gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);*/

					/* Recurse and find the value of the node. */
					value = minimaxValue(oneCell, nextSearchColour, depth - 1, newAlpha, newBeta, neighbourCounter, secondaryCounter);

					/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(Cell.EMPTY);
					gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);*/

					/* Alpha beta pruning. */
					if (value > newAlpha) {
						newAlpha = value;
					}
					if (newBeta <= newAlpha) {
						break;
					}
				}
			}
			/* Undo the temporary move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return newAlpha;
		} else {
			for (int i = 0; i < gameBoard.getNumRows(); ++i) {
				for (int j = 0; j < gameBoard.getNumRows(); ++j) {

					/* Ensure cell is valid and not taken */
					oneCell = gameBoard.getCell(i, j);
					if (oneCell == null || oneCell.taken()) {
						continue;
					}

					/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(content);
					gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);*/

					/* Recurse and find the value of the node. */
					value = minimaxValue(oneCell, nextSearchColour, depth - 1, newAlpha, newBeta, neighbourCounter, secondaryCounter);

					/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(Cell.EMPTY);
					gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);*/

					/* Alpha beta pruning. */
					if (value < newBeta) {
						newBeta = value;
					}

					if (newBeta <= newAlpha) {
						break;
					}
				}
			}
			/* Undo the temporary move */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return newBeta;
		}
	}

	/**
	 * Calculate value of the move from the perspective of the player
	 * @param oneCell - The cell that would be taken
	 * @return Value of the move
	 */
	public int minimaxEvaluateBoard(int getWinnerResult, short neighbourCount, short secondaryCount) {
		if (getWinnerResult == getColour()) {
			return 1000;
		}
		if (getWinnerResult == getOpponentColour()) {
			return -1000;
		}

		int value = 0,
			onEdgeBonus = 2,
			counter = 0;
		int neighbourBonus = 3,
			secondaryNeighbourBonus = 1,
			distBonus = 5;
		int nCount = 0,
			sCount = 0;
		String colour =  pieceColourToCellColour(getColour());
		
		for (int i = 0; i < gameBoard.getNumRows(); ++i) {
			for (int j = 0; j < gameBoard.getNumRows(); ++j) {
				
			}
		}

		float min = 1000,
			  distTotal = 0;
		
		short whichEdge[] = {0, 0, 0, 0, 0, 0};

		for (int i = 0; i < gameBoard.getNumRows(); ++i) {
			for (int j = 0; j < gameBoard.getNumRows(); ++j) {
				min = 1000;
				if (gameBoard.isValidPosition(i, j) && gameBoard.get(i, j).equals(colour)) {
					// get closest neighbours
					ArrayList<ArrayList<Integer> > edgeList = gameBoard.getEdgeNodes();
					for (int k = 0; k < edgeList.size(); ++k) {
						float dist = (Math.abs(i - edgeList.get(k).get(0)) +
						            Math.abs(j - edgeList.get(k).get(1)) +
						            Math.abs( (i - j) - (edgeList.get(k).get(0) - edgeList.get(k).get(1)) )
						            ) / 2.0f;
						if (dist < min) {
							// if there is a node on the same edge, don't give it a high score
							if (gameBoard.isEdgeNode(i, j) && whichEdge[gameBoard.whichEdge(i, j)] > 0) {
								dist = 2;
							} else {
								min = dist;
							}
						}
					}
					++counter;
					distTotal += min;
					
					// if there is a node in list on a particular edge, don't give bonus to any other
					// node on the same edge
					if (gameBoard.isEdgeNode(i, j)) {
						whichEdge[gameBoard.whichEdge(i, j)] = 1;
					}
				
					nCount += gameBoard.getNeighbours(i, j, Board.ALL_NEIGHBOURS).size();
					sCount += gameBoard.getSecondaryConnection(moveRef.getRow(), moveRef.getCol(), colour).size();
				}
			}
		}

		//distInverse / counter to normalize the dist average with number of stones you have
		return nCount * neighbourBonus
				+ sCount * secondaryNeighbourBonus
				- distBonus * (int) distTotal;
	}

	/**
	 * Find the best value move using Negamax search
	 * @return - The best value move to make
	 */
	public Move negamaxDecision() {
		Move nextMove;
		int value;
		int maxValue = Integer.MIN_VALUE;
		Cell oneCell, bestCell;
		/* Defining best cell to prevent possible uninitialised variable
		 * error. Value doesn't matter as value will always be changed on
		 * a real board. */
		bestCell = new Cell(0, 0);

		//String content = pieceColourToCellColour(getColour());

		/* java -cp . aiproj.fencemaster.Referee N mlobanov.Mlobanov mlobanov.Mlobanov */

		/* Begin negamax search with every empty position on the board
		 * as the root node. */
		for (int i = 0; i < gameBoard.getNumRows(); ++i) {
			for (int j = 0; j < gameBoard.getNumRows(); ++j) {

				/* Ensure the cell is valid and not taken. */
				oneCell = gameBoard.getCell(i, j);
				if (oneCell == null || oneCell.taken()) {
					continue;
				}

				/* using depth = 0 reveals true value of a cell. */

				/* Make a temporary move in order to detect a terminal state
				 * later, if it exists. */
				/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(content);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);*/

				System.out.println("BEGINNING NEGAMAX SEARCH. ROOT NODE: " + oneCell.getRow() + ", " + oneCell.getCol());

				value = negamaxValue(oneCell, getColour(), 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

				System.out.println("value of the cell " + oneCell.getRow() + ", " + oneCell.getCol() + " is " + value);

				/* Undo the temporary move. */
				/*gameBoard.getCell(oneCell.getRow(), oneCell.getCol()).setContent(Cell.EMPTY);
				gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);*/

				/* Save the cell with the highest value. */
				if (value > maxValue) {
					maxValue = value;
					bestCell = oneCell;
				}
			}
		}

		nextMove = new Move(getColour(), false, bestCell.getRow(), bestCell.getCol());
		return nextMove;
	}



	public int negamaxValue(Cell moveCell, int searchColour, int depth, int alpha, int beta) {

		int value, getWinnerResult;

		/* Make a temporary move in order to detect a terminal state later,
		 * if it exists */
		String content = pieceColourToCellColour(searchColour);
		gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(content);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() + 1);

		/* Check if board is in terminal state or at depth limit for
		 * searching. */
		getWinnerResult = getWinner();
		if ((getWinnerResult >= 0) || (depth == 0)) {
			/* Evaluate move from the perspective of the player. */
			value = negamaxEvaluateMove(getWinnerResult);

			/* The value is negated if it was opponent's move as the
			 * heuristic is always calculated from player's perspective. */

			if (searchColour == getOpponentColour()) {
				value *= -1;
			}
			/* Undo the temporary move. */
			gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
			gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
			return value;
		}

		/* Not at a terminal state or depth limit */
		Cell oneCell;
		int maxValue = Integer.MIN_VALUE;
		int newAlpha = alpha;
		int nextSearchColour;
		nextSearchColour = oppositeColour(searchColour);

		/* Negamax search at the next depth over the remaining moves on
		 * the board */
		outerLoop:
		for (int i = 0; i < gameBoard.getNumRows(); i++) {
			for (int j = 0; j < gameBoard.getNumRows(); j++) {

				/* Ensure cell is valid and not taken. */
				oneCell = gameBoard.getCell(i, j);
				if (oneCell == null || oneCell.taken()) {
					continue;
				}

				/* Recurse and find the value of the node. */
				value = -1 * negamaxValue(oneCell, nextSearchColour, depth - 1, -beta, -newAlpha);

				if (value > maxValue) {
					maxValue = value;
				}
				/* Alpha beta pruning. */
				if (maxValue > newAlpha) {
					newAlpha = maxValue;
				}
				if (newAlpha >= beta) {
					break outerLoop;
				}
			}
		}
		/* Undo the temporary move */
		gameBoard.getCell(moveCell.getRow(), moveCell.getCol()).setContent(Cell.EMPTY);
		gameBoard.setOccupiedCells(gameBoard.getOccupiedCells() - 1);
		return maxValue;
	}

	/**
	 * Calculate value of the move from the perspective of the player
	 * @return Value of the move
	 */
	public int negamaxEvaluateMove(int getWinnerResult) {
		if (getWinnerResult == getColour()) {
			return 100;
		}
		if (getWinnerResult == getOpponentColour()) {
			return -100;
		}
		int value = 0;
		Cell oneCell;
		for (int i = 0; i < gameBoard.getNumRows(); ++i) {
			for (int j = 0; j < gameBoard.getNumRows(); ++j) {
				oneCell = gameBoard.getCell(i, j);
				if (oneCell == null) {
					continue;
				}

				if (oneCell.getRow() == 3) {
					if (oneCell.getContent().equals(pieceColourToCellColour(getColour()))) {
						value += 5;
					} else if (oneCell.getContent().equals(pieceColourToCellColour(getOpponentColour()))) {
						value -= 5;
					}
				}
			}
		}

		return value;
	}

	public int oppositeColour(int pieceColour) {
		if (pieceColour == Piece.WHITE) {
			return Piece.BLACK;
		} else {
			return Piece.WHITE;
		}
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
		if (!cellContent.equals(Cell.EMPTY) && !cellContent.equals(Cell.INVALID)) {
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
	// -1 = INVALID/Non-Terminal State, 0 = EMPTY/DRAW, 1 = WHITE, 2 = BLACK
	public int getWinner() {

		/* If not enough cells have been taken, can't be a terminal state */
		if (gameBoard.getOccupiedCells() < getLowestMovesForTerminalState()) {
			return -1;
		}
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
		// check if board is full with no winner = draw
		if (gameBoard.getOccupiedCells() == gameBoard.getTotalNumCells()) {
			return 0;
		}
		return -1;
	}

	/**
	* takes result as [blackResult, whiteResult]
	* returns 2 if black won, 1 if white, -1 if none
	*/
	private int whoWon(ArrayList<Boolean> result) {
		if (result.get(0)) {
			return 2;
		}
		if (result.get(1)) {
			return 1;
		}
		return -1;
	}

	/* Function called by referee to get the board configuration in String format
	 * from player
	 */
	public void printBoard(PrintStream output) {
		output.println(gameBoard);
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

	public int getOpponentColour() {
		return opponentColour;
	}

	public void setOpponentColour(int opponentColour) {
		this.opponentColour = opponentColour;
	}


	public int getLowestMovesForTerminalState() {
		return lowestMovesForTerminalState;
	}
}
