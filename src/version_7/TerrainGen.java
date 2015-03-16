package version_7;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.*;

public class TerrainGen extends JPanel {

	private static final long serialVersionUID = -3658165219937024605L;
	/**
	 * @param args
	 */
	final static int ITERATIONS = 5;
	final static double VARIANCE = 0.2;
	static Random random = new Random();
	static BufferedImage testImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
	static Graphics2D test = testImage.createGraphics();

	public static void main(String[] args) {
		double[][] terrain = {{2,3},{2,3}};
		for (int i=2; i<ITERATIONS; i++) {
			int sqno = (int) (Math.pow(2, i)+1);
			//Each iteration
			//New square contains twice as many squares as previous step
			double[][] newsquare = new double[sqno][sqno];
			System.out.println(sqno+" size");
			int i2 = i*2;
			//4^i = number of squares
			for (int x=0; x<sqno-1; x+=2) {
				for (int y=0; y<sqno-1; y+=2) {
					newsquare[x][y] = terrain[x/2][y/2];
					System.out.println(x+", "+y+" is "+newsquare[x][y]);
				}
				for (int y=1; y<=sqno-3; y+=2) {
					newsquare[x][y] = (newsquare[x][y-1]+newsquare[x][y+1])/2;
					System.out.println(x+", "+y+" is "+newsquare[x][y]);
				}
			}
			
			for (int x=1; x<=i2-2; x+=2) {
				for (int y=0; y<i2; y+=2) {
					newsquare[x][y] = (newsquare[x-1][y]+newsquare[x+1][y])/2;
					System.out.println(x+", "+y+" is "+newsquare[x][y]);
				}
				for (int y=1; y<=i2-2; y+=2) {
					double avg = 
							(newsquare[x-1][y]+newsquare[x+1][y]
							+newsquare[x][y-1]+newsquare[x][y+1])/4;
					double ran = ((random.nextDouble()*avg*2)-avg)*VARIANCE;
					newsquare[x][y] = avg+ran;
					System.out.println(x+", "+y+" is "+newsquare[x][y]);
				}
			}
			System.out.println(i);
			terrain=newsquare;
		}
		
		for (int y=0; y<terrain.length; y++) {
			for (int x=0; x<terrain.length; x++) {
				terrain[x][y] = Math.round(terrain[x][y]*10);
				System.out.print(terrain[x][y]+" ");
			}
			System.out.println();
		}
		JFrame frame = new JFrame();
		TerrainGen pan = new TerrainGen();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(pan);
		frame.setVisible(true);
		frame.setSize(1024,768);                                            
		frame.setTitle("Game Thing");                   
		pan.setVisible(true);
		
		test.setBackground(Color.WHITE);
		test.setColor(Color.WHITE);
		frame.getContentPane().add(pan);
		frame.setSize(346, 368);
		pan.setVisible(true);
		//panel.add(new JLabel("HERE"));
		pan.paintComponent(test);
		//test.fillRect(100, 100, 100, 100);
		for (int j=0; j<terrain[0].length; j++) {
			for (int i=0; i<terrain.length; i++) {
				test.setColor(new Color(255,255,255,(int)terrain[i][j]*5));
				test.fillRect(i*30, j*30, 30, 30);
			}
		}
		pan.repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(testImage, 0, 0, null);
	}

}
