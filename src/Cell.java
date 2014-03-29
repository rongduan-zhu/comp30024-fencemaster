public class Cell {
	public final String BLACK = "B";
	public final String WHITE = "W";
	public final String EMPTY = "-";

	private String content;

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
}
