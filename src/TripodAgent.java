import java.util.ArrayList;
import java.util.Arrays;


public class TripodAgent {
	private Board board;
	private ArrayList<ArrayList<Integer> > blackEdgeNodes = new ArrayList<ArrayList<Integer> >();
	private ArrayList<ArrayList<Integer> > whiteEdgeNodes = new ArrayList<ArrayList<Integer> >();

	public TripodAgent(Board board) {
		this.board = board;
	}
	
	/**
	 * 
	 * @param black
	 * @param white
	 * @return A tuple, first item is if black has a tripod, second for white
	 */
	public ArrayList<Boolean> searchForTripod() {
		ArrayList<Boolean> returnList = new ArrayList<Boolean>(Arrays.asList(false, false));
		findAllEdgeNodes();
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

	private boolean depth_first_search(ArrayList<ArrayList<Integer>> queue) {
		ArrayList<Boolean> edgeList = new ArrayList<Boolean>(
										Arrays.asList(false, false, false, false, false, false));
		ArrayList<ArrayList<Integer> > stack = new ArrayList<ArrayList<Integer> >();
		ArrayList<Integer> curNode;
		int edgeVisited = 0,
			edgeIndex;
		
		stack.add(pop(queue));
		while (stack.size() > 0) {
			curNode = pop(stack);
			board.getCell(curNode.get(0), curNode.get(1)).setVisited(true);
			//if current node is on edge
			if (board.isEdgeNode(curNode.get(0), curNode.get(1))) {
				//if current edge is unvisited
				edgeIndex = board.whichEdge(curNode.get(0), curNode.get(1));
				if (!edgeList.get(edgeIndex)) {
					++edgeVisited;
					edgeList.set(edgeIndex, true);
					board.getCell(curNode.get(0), curNode.get(1)).setVisited(true);
				}
			}
			if (edgeVisited == 3) {
				return true;
			}
			stack.addAll(board.getNeighbours(curNode.get(0), curNode.get(1)));
		}
		return false;
	}
	
	private ArrayList<Integer> pop(ArrayList<ArrayList<Integer> > stack) {
		ArrayList<Integer> temp = stack.get(stack.size() - 1);
		stack.remove(stack.size() - 1);
		return temp;
	}

	private void findAllEdgeNodes() {
		for (int i = 0; i < board.getNumRows(); ++i) {
			for (int j = 0; j < board.getMaxColumn(i); ++j) {
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
