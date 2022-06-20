package snakeAI.helper;

public class Node {
	public final int x, y;
	private FieldValues value;
	private boolean isValid = true;
	private double cost, heuristic, function;
	private Node parent;
	
	public Node(int x, int y, FieldValues value) {
		this.x = y;
		this.y = x;
		this. value = value;
	}

	public FieldValues getValue() {
		return value;
	}

	public void setValue(FieldValues value) {
		this.value = value;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(double heuristic) {
		this.heuristic = heuristic;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
//		parent.setChildren(this);
	}

	public double getFunction() {
		return function;
	}

	public void setFunction(double function) {
		this.function = function;
	}

//	public Node getChildren() {
//		return children;
//	}
//
//	public void setChildren(Node children) {
//		this.children = children;
//	}
	
	public String toString() {
		return "x = " + x + " ; y = " + y;
	}
}
