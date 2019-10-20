import java.awt.Color;

import javax.swing.JFrame;

/**
 *testGUI.java 
 *wybcloud
 *2019Äê9ÔÂ24ÈÕ
 */

/**
 * @author wybcloud
 *
 */
public class testGUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DS_GUI gui=new DS_GUI();
		gui.setBounds(100, 100, 600, 437);
		gui.setTitle("Shared Whiteboard");
		gui.setVisible(true);
		gui.setBackground(Color.white);//set the color for the eraser func,but need better solu
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//---this method is to get the matrix of the current image
		//gui.getMatrixofImage(); 
		//---this method is to update the client's image,parameter is a two-dimensional array
		//gui.updateImage(int[][] data); 
	}

}
