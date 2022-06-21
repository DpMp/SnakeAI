package snakeAI.helper;

import snakeAI.Game;

public class Field {
	private int rows;
	private int columns;
	private int nodesize;
	private Node[][] field;
	private Game game;

	public Field(int size, int nodesize, Game game) {
		this(size, size, nodesize, game);
	}

	public Field(int rows, int columns, int nodesize, Game game) {
		this.game = game;
		this.rows = rows;
		this.columns = columns;
		this.setNodesize(nodesize);

		this.field = new Node[columns][rows];
		
		for (int x = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++) {
				field[x][y] = new Node(x, y, FieldValues.GRASS);
			}
		}
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public Node[][] getField() {
		return field;
	}

	public void setField(Node[][] field) {
		this.field = field;
	}

	public int getNodesize() {
		return nodesize;
	}

	public void setNodesize(int nodesize) {
		this.nodesize = nodesize;
	}

	public Node getNode(int x, int y) {
		if (x < rows && y < columns && x >= 0 && y >= 0) {
			return field[y][x];
		}
		return null;	
	}

	public Node generateApple() {
		if (game.getApples() >= 897) {
			game.setPlaying(false);
			return null;
		}
		Node apple;
		int row;
		int column;
		while (true) {
			row = (int) (Math.random() * rows);
			column = (int) (Math.random() * columns);
			apple = field[row][column];

			if (!(apple.getValue() == FieldValues.BODY || apple.getValue() == FieldValues.DEAD
					|| apple.getValue() == FieldValues.HEAD || apple.getValue() == FieldValues.WALL)) {
				apple.setValue(FieldValues.APPLE);
				game.setApple(apple);
				return apple;
			}
		}
	}
}
