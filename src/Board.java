import java.util.ArrayList;


public class Board {
	private ArrayList<ArrayList<Cell> > cellArray;

	public Board(int dimension) {
		/* Specifying capacity at start will save the time of dynamically reallocating
		 * more memory */
		dimension = dimension * 2 - 1;
		cellArray = new ArrayList<ArrayList<Cell>>(dimension * 2 - 1);
		for (int i = 0; i < dimension; ++i) {
			cellArray.add(new ArrayList<Cell>());
//			todo: allocate memory for each cell
		}
	}
}
