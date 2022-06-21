package snakeAI.helper;

import java.awt.Color;

public enum FieldValues {
	WALL(Color.black, -2),
	HEAD(Color.green, 1),
	BODY(Color.blue, -1),
	GRASS(Color.BLACK, 0),
	APPLE(Color.red, 10),
	DEAD(Color.DARK_GRAY, -100),
	;
	
	private final Color color;
	private final int i;
	private FieldValues(final Color c, final int i) {
		this.color = c;
		this.i = i;
	}
	
	public Color getColor() {
		return this.color;
	}
	public int getI() {
		return this.i;
	}
}
