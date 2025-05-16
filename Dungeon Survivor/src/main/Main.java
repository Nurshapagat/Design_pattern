package src.main;

import javax.swing.JFrame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setTitle("Dungeon Survivor");

		GamePanel gamePanel = new GamePanel();
		frame.add(gamePanel);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		if (gd.isFullScreenSupported()) {
			frame.setUndecorated(true);
			frame.pack();
			frame.setLocationRelativeTo(null);
			gd.setFullScreenWindow(frame);
			frame.setVisible(true);
		} else {
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}

		gamePanel.startGameThread();
	}
}


