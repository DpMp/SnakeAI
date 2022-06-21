package snakeAI.helper;

public enum DirectionValues {
	
	RIGHT,
	UP,
	DOWN,
	LEFT;
	
	public static DirectionValues getDirectionForAction(int action) {
		return DirectionValues.values()[action];
	}
}
