import java.util.ArrayList;
import java.util.Comparator;


public class CellComparator implements Comparator<ArrayList<Integer>> {
	@Override
	public int compare(ArrayList<Integer> one, ArrayList<Integer> two) {
		if (one.get(2) < two.get(2)) {
			return -1;
		} else if (one.get(2) > two.get(2)) {
			return 1;
		} else {
			return 0;
		}
		
	}
}
