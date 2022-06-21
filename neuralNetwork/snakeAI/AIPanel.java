package snakeAI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import snakeAI.helper.AIValues;
import snakeAI.helper.DirectionValues;
import snakeAI.helper.FieldValues;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class AIPanel extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Game game;

	private final JPanel contentPanel = new JPanel();
	
	private int maxApples = 0;

	/**
	 * Create the dialog.
	 */

	public AIPanel(Game game) {
		this.game = game;

		initialise();
		setVisible(true);

	}

	public void initialise() {
		setBounds(20, 250, 250, 630);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JButton btn_hamNormal = new JButton("hamPath_Normal");
		btn_hamNormal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				game.setAIType(AIValues.hamPath_NORMAL);
			}
		});

		btn_hamNormal.setBounds(10, 11, 214, 23);
		contentPanel.add(btn_hamNormal);

		JButton btn_hamLeft = new JButton("hamPath_Left");
		btn_hamLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				game.setAIType(AIValues.hamPath_LEFT);
			}
		});
		btn_hamLeft.setBounds(10, 45, 214, 23);
		contentPanel.add(btn_hamLeft);

		JButton btn_hamRight = new JButton("hamPath_Right");
		btn_hamRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				game.setAIType(AIValues.hamPath_RIGHT);
			}
		});
		btn_hamRight.setBounds(10, 79, 214, 23);
		contentPanel.add(btn_hamRight);

		JButton btn_AStar_Normal = new JButton("AStar_Normal");
		btn_AStar_Normal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				game.setAIType(AIValues.AStar_NORMAL);
			}
		});
		btn_AStar_Normal.setBounds(10, 139, 214, 23);
		contentPanel.add(btn_AStar_Normal);

		JButton btn_AStar_Smooth = new JButton("AStar_Smooth");
		btn_AStar_Smooth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				game.setAIType(AIValues.AStar_SMOOTH);
			}
		});
		btn_AStar_Smooth.setBounds(10, 173, 214, 23);
		contentPanel.add(btn_AStar_Smooth);

		JButton btn_AStarNow = new JButton("AStar_Now");
		btn_AStarNow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				game.setAIType(AIValues.AStar_NOW);
			}
		});
		btn_AStarNow.setBounds(10, 207, 214, 23);
		contentPanel.add(btn_AStarNow);

		JButton btn_AI1 = new JButton("AI 1");
		btn_AI1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				game.setAIType(AIValues.AI_1);
			}
		});
		btn_AI1.setBounds(10, 264, 97, 23);
		contentPanel.add(btn_AI1);

		JButton btn_AI2 = new JButton("AI 2");
		btn_AI2.setBounds(10, 295, 97, 23);
		contentPanel.add(btn_AI2);

		JCheckBox btn_wait = new JCheckBox("Wait");
		btn_wait.setSelected(true);
		btn_wait.setBounds(10, 524, 97, 23);
		contentPanel.add(btn_wait);
		btn_wait.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				game.setWAIT(btn_wait.isSelected());
			}
		});

		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setBounds(178, 528, 46, 14);
		contentPanel.add(lblNewLabel);

		JSlider slider_wait = new JSlider();
		slider_wait.setValue(10);
		slider_wait.setMinimum(1);
		slider_wait.setMaximum(50);
		slider_wait.setBounds(10, 554, 214, 26);
		contentPanel.add(slider_wait);
		slider_wait.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				game.setDELAY(slider_wait.getValue());
				lblNewLabel.setText(String.valueOf(slider_wait.getValue()));
			}
		});

		JCheckBox btn_statistic = new JCheckBox("Statistic");
		btn_statistic.setBounds(10, 498, 97, 23);
		contentPanel.add(btn_statistic);

		JButton btn_printStat = new JButton("printStat");
		btn_printStat.setBounds(135, 498, 89, 23);
		contentPanel.add(btn_printStat);
		
		JButton btn_AI1_save = new JButton("Save");
		btn_AI1_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					game.getAis();
					game.getAis().receiveModel().save(new File(AIs.filename));
					System.out.println("Saved Model");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btn_AI1_save.setBounds(127, 264, 97, 23);
		contentPanel.add(btn_AI1_save);
		btn_printStat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				float moves = 0;
				float apples = 0;

				for (int i = 0; i < game.getStat_apples().size(); i++) {
					apples += game.getStat_apples().get(i);
				}
				for (int i = 0; i < game.getStat_moves().size(); i++) {
					moves += game.getStat_moves().get(i);
				}
				System.out.println("Durchschnittliche Züge: " + moves / game.getStat_moves().size());
				System.out.println("Durchschnittliche Äpfel: " + apples / game.getStat_apples().size());
				System.out.println("Es wurden " + game.getStat_moves().size() + " Spiele gespielt");
			}
		});

		btn_statistic.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				btn_wait.setSelected(!btn_statistic.isSelected());
				game.setStatistic(btn_statistic.isSelected());				
			}
		});

	}

	public void reset() {
		System.out.println("Apples: " + game.getApples());
		System.out.println("Moves: " + game.getMoves());

//		if (game.getApples() > maxApples) {
//			maxApples = game.getApples();
//			System.out.println("Neuer Rekord von " + maxApples + " in Spiel " + game.getStat_moves().size());
//		}
		
		game.setApples(0);
		game.setMoves(0);

		int x = game.getField().getRows();
		int y = game.getField().getColumns();
		for (int x2 = 0; x2 < x; x2++) {
			for (int y2 = 0; y2 < y; y2++) {
				game.getField().getNode(x2, y2).setValue(FieldValues.GRASS);
			}
		}

		game.setMovesFromApple(0);
		game.getAis().pointReset();
		game.getSnake().setDirection(DirectionValues.RIGHT);
		game.getSnake().setHead(game.getField().getNode(x / 2, y / 2));
		game.getSnake().setSnake(new ArrayList<>(
				Arrays.asList(game.getField().getNode(x / 2 - 1, y / 2), game.getField().getNode(x / 2 - 2, y / 2))));
		game.getField().generateApple();
		game.setPlaying(true);
	}
}
