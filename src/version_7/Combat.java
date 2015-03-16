package version_7;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Combat {

	private static String attackDir;
	private static int swingDir;
	
	//I think this was used for hammer, sword etc
	//private static String attackType;
	private static boolean firstSwing = false;
	private static boolean takeSwing = false;
	private static JFrame frame;

	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		frame = new JFrame("Event Test");

		Toolkit tk = Toolkit.getDefaultToolkit();
		int xSize = ((int) tk.getScreenSize().getWidth() / 2) + 100;
		int ySize = ((int) tk.getScreenSize().getHeight() / 2) + 50;

		frame.setSize(xSize, ySize);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (!firstSwing) {
					switch (e.getKeyChar()) {
					case 'w':
						System.out.println("Moved forward");
						break;
					case 'd':
						System.out.println("Moved right");
						break;
					case 's':
						System.out.println("Moved downward");
						break;
					case 'a':
						System.out.println("Moved left");
						break;
					case 'W':
						attackDir = "front";
						swingDir -= 1;
						firstSwing = true;
						break;
					case 'E':
						attackDir = "front right";
						swingDir -= 2;
						firstSwing = true;
						break;
					case 'D':
						attackDir = "right";
						swingDir -= 3;
						firstSwing = true;
						break;
					case 'C':
						attackDir = "back right";
						swingDir -= 4;
						firstSwing = true;
						break;
					case 'X':
						attackDir = "back";
						swingDir -= 5;
						firstSwing = true;
						break;
					case 'Z':
						attackDir = "back left";
						swingDir -= 6;
						firstSwing = true;
						break;
					case 'A':
						attackDir = "left";
						swingDir -= 7;
						firstSwing = true;
						break;
					case 'Q':
						attackDir = "front left";
						swingDir -= 8;
						firstSwing = true;
						break;
					}
				} else {
					switch (e.getKeyChar()) {
					case 'W':
						swingDir += 1;
						firstSwing = false;
						break;
					case 'E':
						swingDir += 2;
						firstSwing = false;
						break;
					case 'D':
						swingDir += 3;
						firstSwing = false;
						break;
					case 'C':
						swingDir += 4;
						firstSwing = false;
						break;
					case 'X':
						swingDir += 5;
						firstSwing = false;
						break;
					case 'Z':
						swingDir += 6;
						firstSwing = false;
						break;
					case 'A':
						swingDir += 7;
						firstSwing = false;
						break;
					case 'Q':
						swingDir += 8;
						firstSwing = false;
						break;
					}
					if (e.getKeyChar()=='S') {
						swingDir=-1;
						firstSwing = false;
					} else {
						swingDir = (swingDir+8) % 8;
					}
					takeSwing = true;
					System.out.println(swingDir);
				}
				if (takeSwing) {
					switch(swingDir) {
					case -1:
						System.out.println("You perform a thrust to the "+attackDir);
						break;
					case 0:
						System.out.println("You slice downwards to the "+attackDir);
						break;
					case 1:
						System.out.println("You perform a left swipe to the "+attackDir);
						break;
					case 2:
						System.out.println("You perform a left slice to the "+attackDir);
						break;
					case 3:
						System.out.println("You slice from the top left to the bottom right in a "+attackDir+" direction");
						break;
					case 4:
						System.out.println("You slice from bottom to top to the "+attackDir);
						break;
					case 5:
						System.out.println("You slice from the top right to the bottom left in a "+attackDir+" direction");
						break;
					case 6:
						System.out.println("You perform a right slice to the "+attackDir);
						break;
					case 7:
						System.out.println("You perform a right swipe to the "+attackDir);
						break;
					}
					takeSwing = false;
					swingDir=0;
					attackDir="";
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 16) {
					if (firstSwing) {
						System.out.println("You ready up an attack but release it");
						firstSwing = false;
					}
					swingDir=0;
				}
			}

			public void keyPressed(KeyEvent e) {
				// System.out.println("Pressed: "+e.getKeyChar());
			}
		});

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}