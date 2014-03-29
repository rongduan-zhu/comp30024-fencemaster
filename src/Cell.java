public class Cell {
	public static final String BLACK = "B";
	public static final String WHITE = "W";
	public static final String EMPTY = "-";

	private String content;
	private boolean visited = false;

	public Cell() {
		content = new String(EMPTY);
	}

	public Cell(String type) {
		content = new String(type);
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return content;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
