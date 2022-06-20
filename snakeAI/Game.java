package snakeAI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import snakeAI.helper.AIValues;
import snakeAI.helper.DirectionValues;
import snakeAI.helper.Field;
import snakeAI.helper.FieldValues;
import snakeAI.helper.Node;
import snakeAI.helper.Panel;
import snakeAI.helper.Snake;

public class Game extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Field field;
	private Snake snake;
	private DirectionValues directionNew = DirectionValues.RIGHT;
	private Node headNew;
	private boolean playing = false;
	private static Game instance;
	private static AIPanel ai;
	private AIs ais;
	private AIValues type;
	private Node apple;
	
	private int apples;
	private int moves;
	private int movesFromApple;

	private static final int FIELDSIZE = 30;
	private static final int NODESIZE = 20;
	private boolean WAIT = true;
	private int DELAY = 10;
	
	private boolean statistic = false;
	private ArrayList<Integer> stat_moves = new ArrayList<>();
	private ArrayList<Integer> stat_apples = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Game();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Game() {

		instance = this;

		field = new Field(FIELDSIZE, NODESIZE, instance);
		snake = new Snake(field);
		
		initialise();
		
		ai = new AIPanel(instance);
		ais = new AIs(this);

		apple = field.generateApple();

		instance.setVisible(true);
		
		new Thread(() -> {
			while (true) {
				if(playing) {
					doAction();

					contentPane.invalidate();
					contentPane.repaint();

					if (WAIT) {
						this.sleep(DELAY);
					}
				} else {
					this.sleep(1);
					if (statistic && moves > 0) {
						stat_apples.add(apples);
						stat_moves.add(moves);
						ai.reset();
					}
					
				}
			}
		}).start();
	}

	private Node getNextHead() {
		if(directionNew == null) {
			return null;
		}
		switch (directionNew) {
		case UP: {
			return field.getNode(snake.getHead().x, snake.getHead().y - 1);
		}
		case DOWN: {
			return field.getNode(snake.getHead().x, snake.getHead().y + 1);
		}
		case LEFT: {
			return field.getNode(snake.getHead().x - 1, snake.getHead().y);
		}
		case RIGHT: {
			return field.getNode(snake.getHead().x + 1, snake.getHead().y);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + directionNew);
		}
	}

	private void doAction() {
		
//		try {
//			System.out.println("Richtung: " + directionNew);
//			System.out.println("    next: " + ais.getDirectionNext());
//			System.out.println("       x: " + snake.getHead().x);
//			System.out.println(" apple x: " + snake.getHead().x);
//			System.out.println("       y: " + headNew.y);
//			System.out.println(" apple y: " + apple.y);
//			System.out.println("      up: " + ais.isUp());
//			System.out.println(ais.getPoints().isEmpty());
//			System.out.println("--------------------------");
//		} catch (Exception e) {}
		
		if(type != null) {
			switch (type) {
			case hamPath_NORMAL: {
				directionNew = ais.hamPath_Normal();
				break;
			}
			case hamPath_LEFT: {
				directionNew = ais.hamPath_Left();
				break;
			}
			case hamPath_RIGHT: {
				directionNew = ais.hamPath_Right();
				if (this.getNextHead() == null) {
					directionNew = ais.hamPath_Right();
				}
				break;
			}
			case AStar_NORMAL: {
				directionNew = ais.AStar_Normal();
				break;
			}
			case AStar_SMOOTH: {
				directionNew = ais.AStar_Smooth();
				break;
			}
			case AStar_NOW: {
				directionNew = ais.AStar_Now();
				break;
			}
			case AI_1: {
				directionNew = ais.AI_1();
				break;
			}
			case AI_2: {
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + ais);
			}
		}
		
		if(directionNew == null) {
			ai.reset();
		}
		
		
		instance.getSnake().setDirection(directionNew);

		headNew = this.getNextHead();

		if (headNew == null || movesFromApple > 10000) {
			
			snake.getHead().setValue(FieldValues.DEAD);
			instance.playing = false;

			// Test auf Kollision mit Blatt

		} else if (headNew.getValue() == FieldValues.APPLE) {
			snake.addBodyPart(snake.getHead());
			snake.setHead(headNew);
			
//			data.setApples(data.getApples() + 1);
//			game.getLbl_highScore().setText("Apples: " + data.getApples());
//			data.setLowest(60);
//			data.setLowestP(new Point(-1, -1));

			// score++;
			// settext

			apples++;
			movesFromApple = 0;
			apple = field.generateApple();

			// Test auf Kollision mit Koerper

		} else if (headNew.getValue() == FieldValues.BODY
				&& (snake.getSnake().get(0).x != headNew.x
						&& snake.getSnake().get(0).y != headNew.y)) {
			headNew.setValue(FieldValues.DEAD);
			instance.playing = false;
			
			// Keine Kollision

		} else {
			snake.addBodyPart(snake.getHead());
			snake.setHead(headNew);
			snake.removeBodyPart(snake.getSnake().get(0));
		}
		
		moves++;
		movesFromApple++;

	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialise() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 250, field.getRows() * field.getNodesize() + 17,
				(1 + field.getColumns()) * field.getNodesize() + 17);
		setResizable(false);
		setBackground(Color.BLACK);
		contentPane = new Panel(field, snake);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		getContentPane().add(contentPane);
		
		
	}

	public DirectionValues getDirectionNew() {
		return directionNew;
	}

	public void setDirectionNew(DirectionValues directionNew) {
		this.directionNew = directionNew;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Snake getSnake() {
		return snake;
	}

	public void setSnake(Snake snake) {
		this.snake = snake;
	}

	public Node getHeadNew() {
		return headNew;
	}

	public void setHeadNew(Node headNew) {
		this.headNew = headNew;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public static Game getInstance() {
		return instance;
	}

	public static void setInstance(Game instance) {
		Game.instance = instance;
	}

	public static int getFieldsize() {
		return FIELDSIZE;
	}

	public static int getNodesize() {
		return NODESIZE;
	}

	public static AIPanel getAi() {
		return ai;
	}

	public static void setAi(AIPanel ai) {
		Game.ai = ai;
	}

	public AIs getAis() {
		return ais;
	}

	public void setAis(AIs ais) {
		this.ais = ais;
	}

	public AIValues getAIType() {
		return type;
	}

	public void setAIType(AIValues type) {
		this.type = type;
	}

	public int getApples() {
		return apples;
	}

	public void setApples(int apples) {
		this.apples = apples;
	}

	public int getMoves() {
		return moves;
	}

	public void setMoves(int moves) {
		this.moves = moves;
	}

	public Node getApple() {
		return apple;
	}

	public void setApple(Node apple) {
		this.apple = apple;
	}

	public boolean isWAIT() {
		return WAIT;
	}

	public void setWAIT(boolean wAIT) {
		WAIT = wAIT;
	}

	public int getDELAY() {
		return DELAY;
	}

	public void setDELAY(int dELAY) {
		DELAY = dELAY;
	}

	public boolean isStatistic() {
		return statistic;
	}

	public void setStatistic(boolean statistic) {
		this.statistic = statistic;
	}

	public ArrayList<Integer> getStat_moves() {
		return stat_moves;
	}

	public void setStat_moves(ArrayList<Integer> stat_moves) {
		this.stat_moves = stat_moves;
	}

	public ArrayList<Integer> getStat_apples() {
		return stat_apples;
	}

	public void setStat_apples(ArrayList<Integer> stat_apples) {
		this.stat_apples = stat_apples;
	}

	public int getMovesFromApple() {
		return movesFromApple;
	}

	public void setMovesFromApple(int movesFromApple) {
		this.movesFromApple = movesFromApple;
	}
}