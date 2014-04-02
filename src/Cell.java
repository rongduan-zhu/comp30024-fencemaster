public class Cell {
	/** class constant strings determining what the cell contains */
	public static final String BLACK = "B";
	public static final String WHITE = "W";
	public static final String EMPTY = "-";

	/** stores what is in this cell */
	private String content;
	/** stores if this node has been visited */
	private boolean visited = false;

	/**
	 * Initialize the Cell, with its content set to Empty
	 */
	public Cell() {
		content = new String(EMPTY);
	}

	/**
	 * Initialize the Cell to "type"
	 * @param type, content to be initialized for this cell
	 */
	public Cell(String type) {
		content = new String(type);
	}

	/**
	 * @return the content of current cell
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content, set the current content to the new content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @param content, return cell as a String
	 */
	@Override
	public String toString() {
		return content;
	}

	/**
	 * @return true if it has been visited, false otherwise as boolean
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 * @param visited, set the visited state of the cell
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
