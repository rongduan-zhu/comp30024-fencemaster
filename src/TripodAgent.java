import java.util.ArrayList;
import java.util.Arrays;


public class TripodAgent {
	/** Reference to the current board */
	private Board board;

	/** Queues for holding all nodes that is on an edge but not corners*/
	private ArrayList<ArrayList<Integer>> blackEdgeNodes = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> whiteEdgeNodes = new ArrayList<ArrayList<Integer>>();

	/**
	 * @param Initializes board to the game board
	 */
	public TripodAgent(Board board) {
		this.board = board;
	}

	/**
	 * @return A "tuple", first item is if black has a tripod, second for white
	 */
	public ArrayList<Boolean> searchForTripod() {
		ArrayList<Boolean> returnList = new ArrayList<Boolean>(Arrays.asList(false, false));
		//reset visited state of all nodes
		board.resetVisited();
		//find all nodes that are on an edge but not corner
		findAllEdgeNodes();
		//Searches for black tripod, stops as soon as a tripod has been found
		for (int i = 0; i < blackEdgeNodes.size(); ++i) {
			ArrayList<Integer> position = blackEdgeNodes.get(i);
			if(board.getCell(position.get(0), position.get(1)).isVisited()) {
				continue;
			}
			if (depth_first_search(blackEdgeNodes)) {
				returnList.set(0, true);
				break;
			}
		}
		//Searches for white tripod, stops as soon as a tripod has been found
		for (int i = 0; i < whiteEdgeNodes.size(); ++i) {
			ArrayList<Integer> position = whiteEdgeNodes.get(i);
			if(board.getCell(position.get(0), position.get(1)).isVisited()) {
				continue;
			}
			if (depth_first_search(whiteEdgeNodes)) {
				returnList.set(1, true);
				break;
			}
		}
		return returnList;
	}
	/**
	 * Non-recursive implementation of DFS to search for tripod
	 * @param queue, holding all edge nodes of the same colour that are not on a corner
	 * @return true if a tripod has been found
	 */
	private boolean depth_first_search(ArrayList<ArrayList<Integer>> queue) {
		//Sets 6 edges to unvisited
		ArrayList<Boolean> edgeList = new ArrayList<Boolean>(
										Arrays.asList(false, false, false, false, false, false));
		ArrayList<ArrayList<Integer> > stack = new ArrayList<ArrayList<Integer> >();
		ArrayList<Integer> curNode;
		int edgeVisited = 0,
			edgeIndex;

		stack.add(pop(queue));
		//Continues exploring graph until stack is empty
		while (stack.size() > 0) {
			curNode = pop(stack);
			board.getCell(curNode.get(0), curNode.get(1)).setVisited(true);
			//if current node is on edge
			if (board.isEdgeNode(curNode.get(0), curNode.get(1))) {
				edgeIndex = board.whichEdge(curNode.get(0), curNode.get(1));
				/* if the edge the current node is on is unvisited, then
				 * add 1 to the number of edges visited
				 */
				if (!edgeList.get(edgeIndex)) {
					++edgeVisited;
					edgeList.set(edgeIndex, true);
					/* Also if the edge node is in the all edge node list, set it to visited,
					 *  so it won't search the same node twice
					 */
					board.getCell(curNode.get(0), curNode.get(1)).setVisited(true);
				}
			}
			//if three unique edges contains connected nodes of same colour, then found
			if (edgeVisited == 3) {
				return true;
			}
			stack.addAll(board.getNeighbours(curNode.get(0), curNode.get(1), Board.UNVISITED_NEIGHBOURS));
		}
		//if number of unique edges visited is less than 3, then no tripod
		return false;
	}

	/**
	 * Since ArrayList don't have a pop method, implemented my own
	 * @param stack
	 * @return the last item (last in) in the stack and deletes the item from the stack
	 */
	private ArrayList<Integer> pop(ArrayList<ArrayList<Integer> > stack) {
		ArrayList<Integer> temp = stack.get(stack.size() - 1);
		stack.remove(stack.size() - 1);
		return temp;
	}

	/**
	 * Finds all nodes that are on an edge but not a corner into its corresponding
	 * queue (container)
	 */
	private void findAllEdgeNodes() {
		for (int i = 0; i < board.getNumRows(); ++i) {
			for (int j = 0; j <= board.getMaxColumn(i); ++j) {
				if (board.isEdgeNode(i, j)) {
					if (board.get(i, j).equals(Cell.BLACK)) {
						blackEdgeNodes.add(new ArrayList<Integer>(Arrays.asList(i, j)));
					} else if (board.get(i, j).equals(Cell.WHITE)) {
						whiteEdgeNodes.add(new ArrayList<Integer>(Arrays.asList(i, j)));
					}
				}
			}
		}
	}
}
