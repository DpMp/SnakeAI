package snakeAI;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import snakeAI.helper.DirectionValues;
import snakeAI.helper.Field;
import snakeAI.helper.FieldValues;
import snakeAI.helper.Node;

public class AIs {
	private Game game;

	private Node head;
	private Node apple;
	private ArrayList<Node> snake;

	private DirectionValues directionNext;
	private boolean up;
	private Field field;
	private ArrayList<Node> points = new ArrayList<>();
	private ArrayList<Node> path;
	private ArrayList<Node> openList;
	private ArrayList<Node> closedList;
	private Node last;
	private Node lastLast;

	private MultiLayerNetwork model;
	private double lastDistance, totalScore;
	private INDArray lastInputs;
	private double lastScore;

	public AIs(Game game) {
		this.game = game;
		this.field = game.getField();
		model = getModel();
	}

	public DirectionValues hamPath_Normal() {
		head = game.getSnake().getHead();

		if (directionNext == null) {
			if (head.y == 0) {
				directionNext = DirectionValues.DOWN;
				return DirectionValues.LEFT;
			}
			if (head.x == 29) {
				return DirectionValues.UP;
			}
			if (head.y == 29 && head.x != 29) {
				return DirectionValues.RIGHT;
			}
			if (head.x == 0) {
				return DirectionValues.DOWN;
			}
			if (head.y == 28) {
				directionNext = DirectionValues.UP;
				return DirectionValues.LEFT;
			}
			return game.getSnake().getDirection();
		} else {
			DirectionValues temp = directionNext;
			directionNext = null;
			return temp;
		}

	}

	public DirectionValues hamPath_Left() {

		head = game.getSnake().getHead();
		apple = game.getApple();
		snake = game.getSnake().getSnake();

		if (head.y == apple.y && head.x > apple.x && right() && snake.size() < 500 && head.y != 0) {
			up = true;
			return DirectionValues.LEFT;
		} else if (up) {
			if (head.x == 0 || head.y == 0) {
				up = false;
			} else {
				return up();
			}
		} else if (head.x < apple.x && head.y == 28) {
			directionNext = DirectionValues.RIGHT;
			return DirectionValues.DOWN;
		}

		if (directionNext == null) {
			up = false;
			if (head.y == 0) {
				directionNext = DirectionValues.DOWN;
				return DirectionValues.LEFT;
			}
			if (head.x == 29) {
				return DirectionValues.UP;
			}
			if (head.y == 29) {
				return DirectionValues.RIGHT;
			}
			if (head.x == 0) {
				return DirectionValues.DOWN;
			}
			if (head.y == 28) {
				directionNext = DirectionValues.UP;
				return DirectionValues.LEFT;
			}
			return game.getSnake().getDirection();
		} else {
			DirectionValues temp = directionNext;
			directionNext = null;
			return temp;
		}

	}

	private boolean right() {
		for (Node point : snake) {
			// if (!((point.y > apple.y && point.x != 29) || point.x == 29)) {
			if ((apple.x < point.x && point.x < head.x && point.y != 29) || apple.x == point.x) {
				return false;
			}
		}
		return true;
	}

	private DirectionValues up() {
		up = false;
		if ((head.x % 2) == 0) {
			if (head.y == 28 && head.x != 1) {
				directionNext = DirectionValues.UP;
				return DirectionValues.LEFT;
			} else {
				return DirectionValues.DOWN;
			}

		} else {
			if (head.y == 0) {
				directionNext = DirectionValues.DOWN;
				return DirectionValues.LEFT;
			} else {
				return DirectionValues.UP;
			}
		}
	}

	public DirectionValues hamPath_Right() {
		head = game.getSnake().getHead();
		apple = game.getApple();
		snake = game.getSnake().getSnake();
		field = game.getField();

		boolean b = true;
		if (head.y == apple.y && head.x < apple.x) {
			for (int i = head.x + 1; i <= apple.x; i++) {
				if (field.getNode(i, head.y).getValue() != FieldValues.BODY) {
					if (game.getSnake().getDirection() == DirectionValues.UP
							&& field.getNode(head.y + 1, i).getValue() == FieldValues.BODY) {
						b = false;
						break;
					} else if (game.getSnake().getDirection() == DirectionValues.DOWN
							&& field.getNode(head.y - 1, i).getValue() == FieldValues.BODY) {
						b = false;
						break;
					}
				} else {
					b = false;
					break;
				}
			}
		} else {
			b = false;
		}

		if (head.x == 0 && head.y != 29 && snake.size() > 100) {
			directionNext = null;
			return DirectionValues.DOWN;
		} else if (head.y == 29 && head.x != 29) {
			directionNext = null;
			pointReset();
			return (DirectionValues.RIGHT);
		} else if (!points.isEmpty()) {
			if (head.y != 29) {
				directionNext = null;
				if (field.getNode(points.get(0).x, points.get(0).y).getValue() == FieldValues.HEAD) {
					points.remove(points.get(0));
				}
				if (!points.isEmpty()) {
					if (head.y == points.get(0).y) {
						if (head.x > points.get(0).x) {
							return (DirectionValues.LEFT);
						} else {
							return (DirectionValues.RIGHT);
						}
					} else if (head.x == points.get(0).x) {
						if (head.y > points.get(0).y) {
							if (head.y == 0) {
								setDirectionNext(null);
								return DirectionValues.LEFT;
							}
							return (DirectionValues.UP);
						} else {
							if (head.y == 29) {
								setDirectionNext(null);
								return DirectionValues.RIGHT;
							}
							return (DirectionValues.DOWN);
						}
					}
				} else if (head.x == 1) {
					up = true;
					return (DirectionValues.LEFT);
				}
//			} else {
//				directionNext = null;
//				pointReset();
//				return (DirectionValues.LEFT);
			}
		}

		else if (b && head.y != 29 && head.y != 0) {
			if (field.getNode(head.x + 1, head.y).getValue() != FieldValues.APPLE) {
				points.add(field.getNode(apple.x, apple.y));
				if (game.getSnake().getDirection() == DirectionValues.UP && head.x != 0) {
					points.add(field.getNode(apple.x, apple.y - 1));
					points.add(field.getNode(head.x, head.y - 1));
					if (head.x != 1) {
						directionNext = DirectionValues.UP;
					} else {
						directionNext = null;
					}
				} else if (game.getSnake().getDirection() == DirectionValues.DOWN && head.x != 29) {
					points.add(field.getNode(apple.x, apple.y + 1));
					if (head.x == 28) {
						directionNext = DirectionValues.RIGHT;
					} else {
						points.add(field.getNode(head.x, head.y + 1));
						if (head.x + 1 == 28) {
							directionNext = DirectionValues.LEFT;
						} else {
							directionNext = DirectionValues.DOWN;
						}
					}

				} else {
					directionNext = DirectionValues.RIGHT;
				}

				return DirectionValues.RIGHT;

			}
		}

		if (head.y == apple.y && head.x > apple.x && right() && snake.size() < 500) {
			up = true;
			directionNext = null;
			return DirectionValues.LEFT;
		} else if (up) {
			directionNext = null;
			return up();
		} else if (head.x < apple.x && head.y == 28) {
			directionNext = DirectionValues.RIGHT;
			return DirectionValues.DOWN;
		}

		else if (directionNext == null) {
			if (head.y == 0) {
				directionNext = DirectionValues.DOWN;
				return DirectionValues.LEFT;
			} else if (head.y == 29 && head.x != 29) {
				return DirectionValues.RIGHT;
			} else if (head.x == 0) {
				return DirectionValues.DOWN;
			} else if (head.y == 28 && head.x != 29) {
				directionNext = DirectionValues.UP;
				return DirectionValues.LEFT;
			} else if (head.x == 29) {
				return DirectionValues.UP;

			}
			return game.getSnake().getDirection();

		} else {
			DirectionValues temp = directionNext;
			directionNext = null;
//			System.out.println("Next: " + temp);
			return temp;
		}
	}

	public void pointReset() {
		points.clear();
		directionNext = null;
	}

	public DirectionValues AStar_Normal() {
		this.field = game.getField();
		this.snake = game.getSnake().getSnake();
		this.head = game.getSnake().getHead();
		this.apple = game.getApple();

		ArrayList<Node> appleToEnd = solveBasic(apple, snake.get(0));
		reset();

		ArrayList<Node> toEnd = solveBasic(head, snake.get(0));
		reset();

		if (appleToEnd.isEmpty()) {
			return directionTo(head, toEnd.get(0));
		} else {
			ArrayList<Node> toApple = solveBasic(head, apple);
			reset();

			if (toApple.isEmpty()) {
				return directionTo(head, toEnd.get(0));
			} else {
				return directionTo(head, toApple.get(0));
			}
		}

//		if (!appleToEnd.isEmpty() && !toEnd.isEmpty()) {
//			return directionTo(head, appleToEnd.get(appleToEnd.size() - 2));
//		} else {
//			
//			for (Node node : snake) {
//				node.setValid(false);
//			}
//			game.getSnake().getHead().setValid(false);
//			
//			appleToEnd = solve(head, snake.get(0));
//			reset();
//			if (!appleToEnd.isEmpty()) {
//				return directionTo(head, appleToEnd.get(appleToEnd.size() - 2));
//			} else {
//				return game.getSnake().getDirection();
//			}
//			
//		}
	}
	
	private int getIntNeighboursNow(Node apple2) {
		int i = 0;
		field = game.getField();
		if (apple2.x < 29 && field.getNode(apple2.x + 1, apple2.y).getValue() == FieldValues.BODY) {
			i++;
		}
		if (apple2.x > 0 && field.getNode(apple2.x - 1, apple2.y).getValue() == FieldValues.BODY) {
			i++;
		}
		if (apple2.y < 29 && field.getNode(apple2.x, apple2.y + 1).getValue() == FieldValues.BODY) {
			i++;
		}
		if (apple2.y > 0 && field.getNode(apple2.x, apple2.y - 1).getValue() == FieldValues.BODY) {
			i++;
		}
		if (apple2.x == 29 && apple2.y != 0) {
			i--;
		}
		if (apple2.x == 0 && apple2.y != 29) {
			i--;
		}
		if (apple2.y == 29 && apple2.x != 29) {
			i--;
		}
		if (apple2.y == 0 && apple.x != 0) {
			i--;
		}

		return i;

	}

	private int getIntNeighbours(Node apple2) {
		int i = 0;
		field = game.getField();
		if (apple2.x < 29 && field.getNode(apple2.x + 1, apple2.y).getValue() == FieldValues.BODY) {
			i++;
		}
		if (apple2.x > 0 && field.getNode(apple2.x - 1, apple2.y).getValue() == FieldValues.BODY) {
			i++;
		}
		if (apple2.y < 29 && field.getNode(apple2.x, apple2.y + 1).getValue() == FieldValues.BODY) {
			i++;
		}
		if (apple2.y > 0 && field.getNode(apple2.x, apple2.y - 1).getValue() == FieldValues.BODY) {
			i++;
		}

		return i;

	}

	public DirectionValues AStar_Smooth() {
		this.field = game.getField();
		this.snake = game.getSnake().getSnake();
		this.head = game.getSnake().getHead();
		this.apple = game.getApple();

		ArrayList<Node> appleToEnd = solve(apple, snake.get(0));
		reset();

		ArrayList<Node> toEnd = solve(head, snake.get(0));
		reset();

		if (appleToEnd.isEmpty()) {
			return directionTo(head, toEnd.get(0));
		} else {
			ArrayList<Node> toApple = solve(head, apple);
			reset();

			if (toApple.isEmpty() || getIntNeighbours(apple) > 2) {
				return directionTo(head, toEnd.get(0));
			} else {
				return directionTo(head, toApple.get(0));
			}
		}

	}
	
	public DirectionValues AStar_Now() {
		this.field = game.getField();
		this.snake = game.getSnake().getSnake();
		this.head = game.getSnake().getHead();
		this.apple = game.getApple();

		ArrayList<Node> appleToEnd = solveNow(apple, snake.get(0));
		reset();

		ArrayList<Node> toEnd = solveNow(head, snake.get(0));
		reset();

		if (appleToEnd.isEmpty()) {
			return directionTo(head, toEnd.get(0));
		} else {
			ArrayList<Node> toApple = solveNow(head, apple);
			reset();
			
			if (game.getMovesFromApple() > 250 && game.getMovesFromApple() < 300) {
				ArrayList<Node> test = solveNow(head, field.getNode(29 - apple.x, 29 - apple.y));
				reset();
				if (!test.isEmpty()) {
					return directionTo(head, test.get(0));
				}
			}

			if (toApple.isEmpty() || getIntNeighbours(apple) > 2) {
				try {
					return directionTo(head, toEnd.get(0));
				} catch (Exception e) {
					Game.getAi().reset();
					return null;
				}
				
			} else {
				return directionTo(head, toApple.get(0));
			}
		}
	}
	
	private ArrayList<Node> solveNow(Node start, Node end) {
		for (Node node : snake) {
			node.setValid(false);
		}
		game.getSnake().getHead().setValid(false);
		snake.get(0).setValid(true);
		snake.get(snake.size() - 1).setValid(false);

		if (start == null && end == null) {
			return null;
		}

		if (start.equals(end)) {
			this.path = new ArrayList<>();
			return path;
		}

		this.path = new ArrayList<>();

		this.openList = new ArrayList<>();
		this.closedList = new ArrayList<>();

		this.openList.add(start);

//		Tile e = (Tile) end;

//		for (Point point : snake) {
//			if (Math.abs(phead.x - point.x) + Math.abs(phead.y - point.y) < snake.size() - snake.indexOf(point) + 5) {
//				grid.find(point.x, point.y).setValid(false);
//			}
//		}
//		grid.find(snake.get(0).x, snake.get(0).y).setValid(false);
//		grid.find(snake.get(1).x, snake.get(1).y).setValid(false);
//		grid.find(snake.get(2).x, snake.get(2).y).setValid(false);

		while (!openList.isEmpty()) {
			Node current;
			current = getLowestF();

			if (end.x == current.x && end.y == current.y) {
				retracePath(current, start, end);
				break;
			}

			openList.remove(current);
			closedList.add(current);

			ArrayList<Node> temp = calculateNeighbours(current);

			for (Node n : temp) {

				if (closedList.contains(n) || !n.isValid()) {
					continue;
				}
				
				double tempScore = 0;
				if (game.getMovesFromApple() > 250) {
					tempScore = current.getCost() + (current.y - n.y);
				} else {
					tempScore = current.getCost() + (current.x - n.x);
					if (snake.size() > 25 && game.getMoves() > 1000) {
						tempScore -= 0.75 * getIntNeighboursNow(current);
					}
				}
				
				

//				Node temp2 = current;
//				if (temp2.x == n.x || temp2.y == n.y) {
//					tempScore += 10;
//				}

				if (openList.contains(n)) {
					if (tempScore < n.getCost()) {
						n.setCost(tempScore);
						n.setParent(current);
					}
				} else {
					n.setCost(tempScore);
					openList.add(n);
					n.setParent(current);
				}

				if (game.getMovesFromApple() > 250) {
					n.setHeuristic((n.y - end.y) - 0.75 * getIntNeighboursNow(n));
				} else {
					if (snake.size() > 25 && game.getMovesFromApple() > 1000) {
						n.setHeuristic((n.x - end.x) - 0.75 * getIntNeighboursNow(n));
					} else {
						n.setHeuristic(n.x - end.x);
					}
					
				}
				
				n.setFunction(n.getCost() + n.getHeuristic());

			}

		}
		return path;
	}

	private ArrayList<Node> solve(Node start, Node end) {
		for (Node node : snake) {
			node.setValid(false);
		}
		game.getSnake().getHead().setValid(false);
		snake.get(0).setValid(true);
		snake.get(snake.size() - 1).setValid(false);

		if (start == null && end == null) {
			return null;
		}

		if (start.equals(end)) {
			this.path = new ArrayList<>();
			return path;
		}

		this.path = new ArrayList<>();

		this.openList = new ArrayList<>();
		this.closedList = new ArrayList<>();

		this.openList.add(start);

//		Tile e = (Tile) end;

//		for (Point point : snake) {
//			if (Math.abs(phead.x - point.x) + Math.abs(phead.y - point.y) < snake.size() - snake.indexOf(point) + 5) {
//				grid.find(point.x, point.y).setValid(false);
//			}
//		}
//		grid.find(snake.get(0).x, snake.get(0).y).setValid(false);
//		grid.find(snake.get(1).x, snake.get(1).y).setValid(false);
//		grid.find(snake.get(2).x, snake.get(2).y).setValid(false);

		while (!openList.isEmpty()) {
			Node current;
			current = getLowestF();

			if (end.x == current.x && end.y == current.y) {
				retracePath(current, start, end);
				break;
			}

			openList.remove(current);
			closedList.add(current);

			ArrayList<Node> temp = calculateNeighbours(current);

			for (Node n : temp) {

				if (closedList.contains(n) || !n.isValid()) {
					continue;
				}
				
				double tempScore = 0;
				if (game.getMovesFromApple() > 25) {
					tempScore = current.getCost() + (current.y - n.y) + getIntNeighboursNow(current);
				} else if(game.getMovesFromApple() > 75 && game.getMoves() < 150) {
					tempScore = current.getCost() - (current.y + (29 - n.y) - getIntNeighboursNow(current));
				} else {
					tempScore = current.getCost() + (current.x - n.x) + getIntNeighboursNow(current);
				}
				
				

//				Node temp2 = current;
//				if (temp2.x == n.x || temp2.y == n.y) {
//					tempScore += 10;
//				}

				if (openList.contains(n)) {
					if (tempScore < n.getCost()) {
						n.setCost(tempScore);
						n.setParent(current);
					}
				} else {
					n.setCost(tempScore);
					openList.add(n);
					n.setParent(current);
				}

				if (game.getMovesFromApple() > 25) {
					n.setHeuristic((n.y - end.y) + getIntNeighboursNow(n));
				} else if(game.getMovesFromApple() > 75 && game.getMoves() < 150) {
					n.setHeuristic((n.y + (29 - end.y)) - getIntNeighboursNow(n));
				} else {
					n.setHeuristic(n.x - end.x);
				}
				
				n.setFunction(n.getCost() + n.getHeuristic());

			}

		}
		return path;
	}

	private ArrayList<Node> solveBasic(Node start, Node end) {
		for (Node node : snake) {
			node.setValid(false);
		}
		game.getSnake().getHead().setValid(false);
		snake.get(0).setValid(true);

		if (start == null && end == null) {
			return null;
		}

		if (start.equals(end)) {
			this.path = new ArrayList<>();
			return path;
		}

		this.path = new ArrayList<>();

		this.openList = new ArrayList<>();
		this.closedList = new ArrayList<>();

		this.openList.add(start);

//		Tile e = (Tile) end;

//		for (Point point : snake) {
//			if (Math.abs(phead.x - point.x) + Math.abs(phead.y - point.y) < snake.size() - snake.indexOf(point) + 5) {
//				grid.find(point.x, point.y).setValid(false);
//			}
//		}
//		grid.find(snake.get(0).x, snake.get(0).y).setValid(false);
//		grid.find(snake.get(1).x, snake.get(1).y).setValid(false);
//		grid.find(snake.get(2).x, snake.get(2).y).setValid(false);

		while (!openList.isEmpty()) {
			Node current;
			current = getLowestF();

			if (end.x == current.x && end.y == current.y) {
				retracePath(current, start, end);
				break;
			}

			openList.remove(current);
			closedList.add(current);

			ArrayList<Node> temp = calculateNeighbours(current);

			for (Node n : temp) {

				if (closedList.contains(n) || !n.isValid()) {
					continue;
				}

				double tempScore = current.getCost() + distanceTo(current, n);

//				Node temp2 = current;
//				if (temp2.x == n.x || temp2.y == n.y) {
//					tempScore += 10;
//				}

				if (openList.contains(n)) {
					if (tempScore < n.getCost()) {
						n.setCost(tempScore);
						n.setParent(current);
					}
				} else {
					n.setCost(tempScore);
					openList.add(n);
					n.setParent(current);
				}

				n.setHeuristic(distanceTo(n, end));
				n.setFunction(n.getCost() + n.getHeuristic());

			}

		}
		return path;
	}

	public void reset() {
		this.path = null;
		this.openList = null;
		this.closedList = null;
		this.path = null;
		for (int i = 0; i < field.getRows(); i++) {
			for (int j = 0; j < field.getColumns(); j++) {
				field.getNode(i, j).setValid(true);
				field.getNode(i, j).setCost(0);
				field.getNode(i, j).setFunction(0);
				field.getNode(i, j).setHeuristic(0);
				field.getNode(i, j).setParent(null);
			}
		}
	}

	private void retracePath(Node current, Node start, Node end) {
		Node temp = current;
		this.path.add(current);

		while (temp.getParent() != null) {
			this.path.add(temp.getParent());
			temp = temp.getParent();
		}
		if (path.contains(start)) {
			path.remove(start);
		}

		if (!path.contains(end)) {
			path.add(0, end);
		}

		Collections.reverse(path);
	}

	private Node getLowestF() {
		Node lowest = openList.get(0);
		for (Node n : openList) {
			if (n.getFunction() < lowest.getFunction()) {
				lowest = n;
			}
		}
		return lowest;
	}

	private ArrayList<Node> calculateNeighbours(Node node) {

		ArrayList<Node> nodes = new ArrayList<>();

		int minX = 0;
		int minY = 0;
		int maxX = field.getRows() - 1;
		int maxY = field.getColumns() - 1;

		if (node.x > minX) {
			nodes.add(field.getNode(node.x - 1, node.y)); // west
		}

		if (node.x < maxX) {
			nodes.add(field.getNode(node.x + 1, node.y)); // east
		}

		if (node.y > minY) {
			nodes.add(field.getNode(node.x, node.y - 1)); // north
		}

		if (node.y < maxY) {
			nodes.add(field.getNode(node.x, node.y + 1)); // south
		}

		return nodes;

	}

	private double distanceTo(Node from, Node to) {
		return new Point(from.x, from.y).distance(new Point(to.x, to.y));
	}

	private DirectionValues directionTo(Node from, Node to) {
		if (from.x < to.x) {
			return DirectionValues.RIGHT;
//			data.setDirectionValuesNeu(DirectionValues.RECHTS);
		} else if (from.x > to.x) {
			return DirectionValues.LEFT;
//			data.setDirectionValuesNeu(DirectionValues.LINKS);
		} else if (from.y > to.y) {
			return DirectionValues.UP;
//			data.setDirectionValuesNeu(DirectionValues.OBEN);
		} else {
//			data.setDirectionValuesNeu(DirectionValues.UNTEN);
			return DirectionValues.DOWN;
		}
	}

	public boolean willDieAtPosition(int[] xy) {
		if (xy.length != 2) {
			return false;
		}
		if (xy[0] < 0 || xy[0] > 29 || xy[1] < 0 || xy[1] > 29 || snake.contains(field.getNode(xy[0], xy[1]))) {
			return true;
		}

		return false;
	}

	private INDArray inputs = Nd4j.create(1, 20);

	public DirectionValues AI_1() {
		this.field = game.getField();
		this.snake = game.getSnake().getSnake();
		this.head = game.getSnake().getHead();
		this.apple = game.getApple();

		inputs = getInputs();

		int action = getAction(inputs);

		DirectionValues direct = DirectionValues.getDirectionForAction(action);
		train(inputs, action);

		return direct;

	}

	private void train(INDArray inputs, int action) {
		INDArray output = model.output(inputs, true);
		double score = getScore(action);
		INDArray updatedOutput = output.putScalar(action, score);
		totalScore += score;

		model.fit(inputs, updatedOutput);
	}

	private int getAction(INDArray inputs) {
		INDArray output = model.output(inputs, false);
		float[] outputValues = output.data().asFloat();

		int maxAt = 0;

		for (int i = 0; i < outputValues.length; i++) {
			maxAt = outputValues[i] > outputValues[maxAt] ? i : maxAt;
		}

		return maxAt;
	}

	private int input = 8;

	private INDArray getInputs() {
		INDArray inputs = Nd4j.create(1, input);

		inputs.getRow(0).getColumn(0).assign(apple.x);
		inputs.getRow(0).getColumn(1).assign(apple.y);

//		for (int i = 0; i < field.getColumns(); i++) {
//			for (int j = 0; j < field.getRows(); j++) {
//				inputs.getRow(0).getColumn(i * 30 + j + 2).assign(field.getNode(i, j).getValue().getI());
//			}
//		}

		inputs.getRow(0).getColumn(2).assign(willDieAtPosition(new int[] { head.x + 1, head.y + 1 }));
		inputs.getRow(0).getColumn(3).assign(willDieAtPosition(new int[] { head.x + 1, head.y - 1 }));
		inputs.getRow(0).getColumn(4).assign(willDieAtPosition(new int[] { head.x - 1, head.y + 1 }));
		inputs.getRow(0).getColumn(5).assign(willDieAtPosition(new int[] { head.x - 1, head.y - 1 }));
//		inputs.getRow(0).getColumn(6).assign(willDieAtPosition(new int[] { head.x, head.y + 2 }));
//		inputs.getRow(0).getColumn(7).assign(willDieAtPosition(new int[] { head.x, head.y - 2 }));
//		inputs.getRow(0).getColumn(8).assign(willDieAtPosition(new int[] { head.x + 2, head.y }));
//		inputs.getRow(0).getColumn(9).assign(willDieAtPosition(new int[] { head.x - 2, head.y }));
//		inputs.getRow(0).getColumn(10).assign(willDieAtPosition(new int[] { head.x, head.y + 3 }));
//		inputs.getRow(0).getColumn(11).assign(willDieAtPosition(new int[] { head.x, head.y - 3 }));
//		inputs.getRow(0).getColumn(12).assign(willDieAtPosition(new int[] { head.x + 3, head.y }));
//		inputs.getRow(0).getColumn(13).assign(willDieAtPosition(new int[] { head.x - 3, head.y }));
		inputs.getRow(0).getColumn(6).assign(head.x);
		inputs.getRow(0).getColumn(7).assign(head.y);

		lastInputs = inputs;
		return inputs;
	}

	private int[] getFuturePositionForDirection(DirectionValues direction) {
		int[] futurePosition = new int[2];
		futurePosition[0] = head.x;
		futurePosition[1] = head.y;

		if (direction == DirectionValues.UP) {
			futurePosition[1] -= 1;
		} else if (direction == DirectionValues.DOWN) {
			futurePosition[1] += 1;
		} else if (direction == DirectionValues.RIGHT) {
			futurePosition[0] += 1;
		} else if (direction == DirectionValues.LEFT) {
			futurePosition[0] -= 1;
		}

		return futurePosition;
	}

	private double getScore(int action) {
		double score = 0;
		int[] fP = getFuturePositionForDirection(DirectionValues.getDirectionForAction(action));
		Node futurePosition = field.getNode(fP[0], fP[1]);

		if (futurePosition != null) {
			double distance = distanceTo(futurePosition, apple);
			if (distance < lastDistance) {
				score += 100;
			} else {
				score -= 150;
			}
			lastDistance = distance;
			score -= distanceTo(futurePosition, apple);
		}
		
		
		
		if (head == lastLast) {
			score -= 100;
		}

		lastLast = last;
		last = head;
		
		if (futurePosition == null || snake.contains(futurePosition)) {
			score = Integer.MIN_VALUE;
		} else if (futurePosition.getValue() == FieldValues.APPLE) {
			score += 10000;
		}
		lastScore = score;
		return score;
	}

	public static final String filename = "network.zip";

	private MultiLayerNetwork getModel() {
		try {
			MultiLayerNetwork model = MultiLayerNetwork.load(new File(filename), true);
			model.init();
			model.setListeners(new ScoreIterationListener(1));
			System.out.println("Loaded saved model");
			return model;
		} catch (Exception e) {
			System.out.println("No saved model found");
		}

		final int numInputs = input;
		int outputNum = 4;
		int hiddenLayers = 200;

		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(1234).biasInit(0)
				.optimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT).weightInit(WeightInit.XAVIER)
				.updater(new Adam(0.001)).l2(0.001).list()
				.layer(0,
						new DenseLayer.Builder().nIn(numInputs).nOut(hiddenLayers).weightInit(WeightInit.XAVIER)
								.activation(Activation.RELU).build())
				.layer(1,
						new DenseLayer.Builder().nIn(hiddenLayers).nOut(hiddenLayers).weightInit(WeightInit.XAVIER)
								.activation(Activation.RELU).build())
				.layer(2,
						new DenseLayer.Builder().nIn(hiddenLayers).nOut(hiddenLayers).weightInit(WeightInit.XAVIER)
								.activation(Activation.RELU).build())
				.layer(3, new DenseLayer.Builder().nIn(hiddenLayers).nOut(hiddenLayers).weightInit(WeightInit.XAVIER)
						.activation(Activation.RELU).build())
//				.layer(4,
//						new DenseLayer.Builder().nIn(hiddenLayers).nOut(hiddenLayers).weightInit(WeightInit.XAVIER)
//								.activation(Activation.RELU).build())
//				.layer(5,
//						new DenseLayer.Builder().nIn(hiddenLayers).nOut(hiddenLayers).weightInit(WeightInit.XAVIER)
//								.activation(Activation.RELU).build())
				.layer(4,
						new OutputLayer.Builder(LossFunctions.LossFunction.XENT).nIn(hiddenLayers).nOut(outputNum)
								.weightInit(WeightInit.RELU).activation(Activation.SIGMOID).build())
				.backpropType(BackpropType.Standard).build();

		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		model.setListeners(new ScoreIterationListener(1));
		System.out.println("Created new model");
		return model;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public DirectionValues getDirectionNext() {
		return directionNext;
	}

	public void setDirectionNext(DirectionValues directionNext) {
		this.directionNext = directionNext;
	}

	public ArrayList<Node> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Node> points) {
		this.points = points;
	}

	public void setModel(MultiLayerNetwork model) {
		this.model = model;
	}

	public MultiLayerNetwork receiveModel() {
		return model;
	}
}