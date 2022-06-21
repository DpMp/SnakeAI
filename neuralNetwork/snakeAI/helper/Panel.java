package snakeAI.helper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Panel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Field field;
	private Snake snake;

	public Panel(Field field, Snake snake) {
		this.snake = snake;
		this.field = field;
	}

	@Override
	public void paintComponent(Graphics graphics) {
//		super.paintComponents(graphics);

		int size = field.getNodesize();

//		Graphics2D ger = (Graphics2D) graphics;

		BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, field.getColumns() * size, field.getRows() * size);

		for (int x = 0; x < field.getRows(); x++) {
			for (int y = 0; y < field.getColumns(); y++) {
				switch (field.getNode(x, y).getValue()) {
				case APPLE: {
					g.setColor(Color.RED);
					g.fillRect(x * size, y * size, size, size);
					break;
				}
				case BODY: {
					g.setColor(Color.BLUE);
					g.fillRect(x * size, y * size, size, size);

					Node point = field.getNode(x, y);
					int index = snake.getSnake().indexOf(point);
					boolean[] directions = new boolean[4];

					if (index >= 0) {
						ArrayList<Node> points = new ArrayList<>();

						Node prev = null;
						Node next = null;
						if (index > 0) {
							prev = snake.getSnake().get(index - 1);
							points.add(prev);
						}
						if (index < (snake.getSnake().size() - 1)) {
							next = snake.getSnake().get(index + 1);
							points.add(next);
						}
						if (point.y < 29) {
							Node north = field.getNode(point.x, point.y + 1);
							directions[0] = !points.contains(north);
						}
						if (point.x < 29) {
							Node east = field.getNode(point.x + 1, point.y);
							directions[1] = !points.contains(east);
						}
						if (point.y > 0) {
							Node south = field.getNode(point.x, point.y - 1);
							directions[2] = !points.contains(south);
						}
						if (point.x > 0) {
							Node west = field.getNode(point.x - 1, point.y);
							directions[3] = !points.contains(west);
						}

					}

					g.setColor(Color.black);

					if (directions[3]) {
						g.drawLine(x * size, y * size, x * size, y * size + size);
					}
					if (directions[0]) {
						g.drawLine(x * size, y * size + size, x * size + size, y * size + size);
					}
					if (directions[1]) {
						g.drawLine(x * size + size, y * size, x * size + size, y * size + size);
					}
					if (directions[2]) {
						g.drawLine(x * size, y * size, x * size + size, y * size);
					}
					break;
				}
				case DEAD: {
					g.setColor(Color.GRAY);
					g.fillRect(x * size, y * size, size, size);
					break;
				}
				case HEAD: {
					g.setColor(Color.GREEN);
					g.fillRect(x * size, y * size, size, size);
					break;
				}
				case WALL: {
					g.setColor(Color.DARK_GRAY);
					g.fillRect(x * size, y * size, size, size);

					break;
				}
				case GRASS: {
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + field.getNode(x, y).getValue());
				}
			}
		}

		Graphics2D g2dComponent = (Graphics2D) graphics;
		g2dComponent.drawImage(bufferedImage, null, 0, 0);
	}

}
