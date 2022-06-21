package snakeAI.helper;

import java.util.ArrayList;
import java.util.Arrays;

public class Snake {
	private Field field;
	private int x;
	private int y;
	private DirectionValues direction = DirectionValues.RIGHT;
	
	
	private Node head;
	private ArrayList<Node> snake = new ArrayList<>();
	
	public Snake(Field field) {
		this.field = field;
		
		if (field.getRows() < 5) {
			field.setRows(5);
		}
		if (field.getColumns() < 5) {
			field.setColumns(5);
		}
		this.x = field.getRows();
		this.y = field.getColumns();
		
		setHead(field.getNode(x/2, y/2));
		setSnake(new ArrayList<>(Arrays.asList(field.getNode(head.x - 1, head.y), field.getNode(head.x - 2, head.y))));
	}

	public Node getHead() {
		return head;
	}

	public void setHead(Node head) {
		this.head = head;
		field.getNode(head.x, head.y).setValue(FieldValues.HEAD);
	}

	public ArrayList<Node> getSnake() {
		return snake;
	}

	public void setSnake(ArrayList<Node> snake) {
		for (Node node : snake) {
			field.getNode(node.x, node.y).setValue(FieldValues.BODY);
		}
		this.snake = snake;
	}
	
	public void addBodyPart(Node bodyPart) {
		snake.add(bodyPart);
		bodyPart.setValue(FieldValues.BODY);
	}
	public void removeBodyPart(Node bodyPart) {
		snake.remove(bodyPart);
		field.getNode(bodyPart.x, bodyPart.y).setValue(FieldValues.GRASS);
	}

	public DirectionValues getDirection() {
		return direction;
	}

	public void setDirection(DirectionValues direction) {
		this.direction = direction;
	}
}
