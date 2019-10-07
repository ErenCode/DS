import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *Gshape.java 
 *wybcloud
 *2019Äê9ÔÂ24ÈÕ
 */

/**
 * @author wybcloud
 *
 */
public class Gshape extends JPanel implements MouseListener, MouseMotionListener{

	private int LENGTH = 1000;//the length of the wb
	private int WEIGTH = 600;
	private int startX;
	private int startY;
	private int tempX;
	private int tempY;
	private int endX;
	private int endY;
	private BufferedImage bufImage = null;
	private String object="";
//	private String menu="";
	int[][] quene = new int[2][2];// for pen
	
	public Gshape() {
		
		setPreferredSize(new Dimension(LENGTH,WEIGTH));
		setBackground(Color.white);
		System.out.println("in Gshape");
		this.addMouseListener(this);//add into the whiteboard
		this.addMouseMotionListener(this);
	}
	
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; 
		
		if (bufImage == null) {
			int width = this.getWidth();
			int height = this.getHeight();
			bufImage = (BufferedImage) this.createImage(width, height);
			Graphics2D gc = bufImage.createGraphics();
			gc.setColor(Color.white);
			gc.fillRect(0, 0, width, height); 
		}
		g2.drawImage(bufImage, null, 0, 0); 
		drawObj(g2);
				
	}


	private void drawObj(Graphics2D g2) {
		//it may exist problem about the static color
		g2.setColor(DS_GUI.c);
		switch(object) {
		case"line":
			g2.drawLine(startX, startY, endX, endY);
			break;
		case"rectangle":
			g2.drawRect(startX, startY, endX-startX, endY-startY);
			break;
		case"circle":
			g2.drawArc(startX, startY, endX - startX, endX-startX, 0,360);
			break;
		case"oval":
			g2.drawOval(startX, startY, endX-startX, endY-startY);
			break;
		case"eraser":
			g2.setColor(Color.white);
			g2.setStroke(new BasicStroke(20));
			g2.drawLine(startX, startY, endX, endY);
			g2.setColor(DS_GUI.c);
			break;	
		case"pen":
			//how to implement the pen func
			g2.drawLine(startX, startY, endX, endY);
			break;
		default:
			break;
		}
	}
	
	public void setObject(String str) {
		this.object=str;
		//System.out.println(object);
	}
	
	/**
	 * @return the bufImage
	 */
	public BufferedImage getBufImage() {
		return bufImage;
	}
	/**
	 * @param bufImage the bufImage to set
	 */
	public void setBufImage(BufferedImage bufImage) {
		this.bufImage = bufImage;
	}
	// -----------this part to get the mouse postion---------------------
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		//||!this.object.equals("eraser")
		if((!this.object.equals("pen"))&&(!this.object.equals("eraser"))) {
		startX=e.getX();
		startY=e.getY();
		//endX = startX;
		//endY = startY;
		}else if(this.object.equals("pen")) {
			//System.out.println("in");
			// initial the array
            int x = e.getX();
            int y = e.getY();
            quene[0][0] = x;
            quene[1][0] = x;
            quene[0][1] = y;
            quene[1][1] = y;
            startX = x;
            startY = y;
            endX = x;
            endY = y;
		}else if(this.object.equals("eraser")) {
			int x = e.getX();
            int y = e.getY();
			startX = x;
            startY = y;
            tempX = x;
            tempY = y;
            endX = x;
            endY = y;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		endX=e.getX();
		endY=e.getY();
		//check with xu
		Graphics2D ga = bufImage.createGraphics();
		drawObj(ga);
		this.repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		//||!this.object.equals("eraser")
		if((!this.object.equals("pen"))&&(!this.object.equals("eraser"))) {
			endX = e.getX(); 
			endY = e.getY();
		}else if(this.object.equals("pen")) {
			//System.out.println("in");
			endX = e.getX();
			endY = e.getY();
			startX = quene[0][0];
			startY = quene[0][1];
			quene[0][0] = quene[1][0];
			quene[0][1] = quene[1][1];
			quene[1][0] = endX;
			quene[1][1] = endY;
			//this.repaint();
			// used to update the pen's trace
			Graphics2D gb = bufImage.createGraphics();
			drawObj(gb);
		}else if(this.object.equals("eraser")) {
			endX = e.getX();
			endY = e.getY();
			startX = tempX;
			startY = tempY;
			tempX=endX;
			tempY=endY;
			
			// used to update the eraser's trace
			Graphics2D gb = bufImage.createGraphics();
			drawObj(gb);
		}
		
		this.repaint(); 
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	//?the func with update
    public void update(Graphics2D g2) {
    	drawObj(g2);
    }
	
	
}
