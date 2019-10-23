import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

class Client_GUI extends JFrame implements ItemListener,KeyListener {

	private JButton ovalBt;
	private JButton circleBt;
	private JButton rectangleBt;
	private JButton lineBt;
	private JButton eraserBt;
	private JButton penBt;
	private JButton textBt;
	private Gshape shape;
	private JComboBox colorBt;
	private Color c = Color.black;// should check if it's ok
	private JMenuBar JMenuBar;// define the menu bar
	private JMenu JMenuOpen;
	private JMenu JMenuNew;
	private JMenu JMenuSave;
	private JMenu JMenuSaveAs;
	private JMenu JMenuClose;// not sure if it's useful?
	private JFrame jfile;
	private String filePath;
	private BufferedImage myImage = null;
	private static int count = 0;// count
	private int action = 0;
	private User user = null;
	private DataOutputStream os = null;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private String text="";

	public Client_GUI(User user) {

		this.user = user;
		this.os = user.getOS();

		shape = new Gshape(this.os);
		// implement the button
		ovalBt = new JButton("Oval");
		circleBt = new JButton("Circle");
		rectangleBt = new JButton("Rectangle");
		lineBt = new JButton("Line");
		eraserBt = new JButton("eraser");
		penBt = new JButton("pen");
		colorBt = new JComboBox();
		textBt = new JButton("text");
		textBt.addKeyListener(this);

		// create the panel
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(4, 1));
		btnPanel.add(lineBt);
		btnPanel.add(rectangleBt);
		btnPanel.add(circleBt);
		btnPanel.add(ovalBt);
		btnPanel.add(eraserBt);
		btnPanel.add(penBt);
		btnPanel.add(colorBt);
		btnPanel.add(textBt);
		// color add
		colorBt.addItem("black");
		colorBt.addItem("blue");
		colorBt.addItem("red");
		colorBt.addItem("yellow");
		colorBt.addItem("green");
		colorBt.addItem("orange");
		colorBt.addItem("cyan");
		colorBt.addItem("darkGray");
		colorBt.addItem("gray");
		colorBt.addItem("lightGray");
		colorBt.addItem("magenta");
		colorBt.addItem("pink");
		colorBt.addItem("white");
		colorBt.addItem("bananaYellow");
		colorBt.addItem("ivoryRlack");
		colorBt.addItem("coralRed");
		colorBt.addItem("blackRed");
		colorBt.addItemListener(this);

		// create the container
		Container content = this.getContentPane();// get the pane
		content.setLayout(new BorderLayout());
		content.add(btnPanel, BorderLayout.WEST);// add the btn
		content.add(shape, BorderLayout.CENTER);// add the wb
		// content.add(JMenuBar,BorderLayout.NORTH);//add the menu
		// setJMenuBar(JMenuBar);// this is also ok for menu bar

		// this part is to add action for the button
		lineBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("line");
				action = 1;
			}
		});
		rectangleBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("rectangle");
				action = 1;
			}
		});
		circleBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("circle");
				action = 1;
			}
		});
		ovalBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("oval");
				action = 1;
			}
		});
		eraserBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("eraser");
				action = 1;
			}
		});
		penBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("pen");
				action = 1;
			}
		});
		textBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("text");
				action = 1;
			}
		});
		// this part is for the textInput

		this.pack();// adjust the size of btn auto
	}

	public int[][] getMatrixofImage() {
		BufferedImage bimg;

		bimg = shape.getBufImage();
		int[][] data = new int[bimg.getWidth()][bimg.getHeight()];
		for (int i = 0; i < bimg.getWidth(); i++) {
			for (int j = 0; j < bimg.getHeight(); j++) {
				data[i][j] = bimg.getRGB(i, j);
			}
		}

		return data;
	}

	public void updateImage(int[][] rgbValue2) {
		shape.setObject("");

//		int[][] rgbValue1 = getMatrixofImage();
		// row
		int width = rgbValue2.length;
		// column
		int height = rgbValue2[0].length;

		// composite a new array
//		int[][] compositeRgbValue = new int[width][height];
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				// RGB is binary need to implement bitwise
//				// red = (pixel >> 16) & 0xFF;
//				if ((((rgbValue1[x][y] >> 16) & 0xFF) == 0xFE)
//						|| (((rgbValue2[x][y] >> 16) & 0xFF) == 0xFE)) {
//					compositeRgbValue[x][y] = 0xffffffff;
//				} else {
//					compositeRgbValue[x][y] = rgbValue1[x][y] & rgbValue2[x][y];
//				}
//			}
//		}

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				bufferedImage.setRGB(x, y, rgbValue2[x][y]);
			}
		}
		Graphics g = shape.getGraphics();

		Graphics2D g2 = (Graphics2D) g;
		shape.setBufImage(bufferedImage);
		g2.drawImage(bufferedImage, null, 0, 0);
	}

	public void setAction(int action) {
		this.action = action;

	}

	public int getAction() {
		return action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == colorBt) {// ÑÕÉ«
			String s = colorBt.getSelectedItem().toString();
			if (s.equals("black")) {
				c = Color.black;
				shape.setColor(c);
			}
			if (s.equals("blue")) {
				c = Color.blue;
				shape.setColor(c);
			}
			if (s.equals("red")) {
				c = Color.red;
				shape.setColor(c);
			}
			if (s.equals("yellow")) {
				c = Color.yellow;
				shape.setColor(c);
			}
			if (s.equals("green")) {
				c = Color.green;
				shape.setColor(c);
			}
			if (s.equals("organge")) {
				c = Color.orange;
				shape.setColor(c);
			}
			if (s.equals("cyan")) {
				c = Color.cyan;
				shape.setColor(c);
			}
			if (s.equals("darkGray")) {
				c = Color.darkGray;
				shape.setColor(c);
			}
			if (s.equals("gray")) {
				c = Color.gray;
				shape.setColor(c);
			}
			if (s.equals("lightGray")) {
				c = Color.lightGray;
				shape.setColor(c);
			}
			if (s.equals("magenta")) {
				c = Color.magenta;
				shape.setColor(c);
			}
			if (s.equals("pink")) {
				c = Color.pink;
				shape.setColor(c);
			}
			if (s.equals("white")) {
				c = Color.white;
				shape.setColor(c);
			}
			if (s.equals("bananaYellow")) {
				c = new Color(227, 207, 87);
				shape.setColor(c);
			}
			if (s.equals("ivoryBlack")) {
				c = new Color(88, 87, 86);
				shape.setColor(c);
			}
			if (s.equals("coralRed")) {
				c = new Color(255, 127, 80);
				shape.setColor(c);
			}
			if (s.equals("blackRed")) {
				c = new Color(116, 0, 0);
				shape.setColor(c);
			}
		}
	}

	public void send() {
		try {
			JSONObject test = new JSONObject();
			test.put("command_name", "send");
			os.writeUTF(test.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void paint(JSONObject command) {
		if (command.get("operation").toString().equals("mousePressed")) {
			startX = Integer.parseInt(command.get("startX").toString());
			startY = Integer.parseInt(command.get("startY").toString());
			text="";
		} else if (command.get("operation").toString().equals("mouseReleased")) {
			endX = Integer.parseInt(command.get("endX").toString());
			endY = Integer.parseInt(command.get("endY").toString());
			Graphics2D g2 = shape.getBufImage().createGraphics();
			Color c1 = toColor(command.get("color").toString());
			
			System.out.println(command.toString());
			g2.setColor(c1);
			String type = command.get("shape").toString();
			drawUpdate(type, g2);
			shape.repaint();
		} else if (command.get("operation").toString().equals("mouseDragged")) {
			startX = Integer.parseInt(command.get("startX").toString());
			startY = Integer.parseInt(command.get("startY").toString());
			endX = Integer.parseInt(command.get("endX").toString());
			endY = Integer.parseInt(command.get("endY").toString());
			Graphics2D g2 = shape.getBufImage().createGraphics();
			Color c1 = toColor(command.get("color").toString());
			g2.setColor(c1);
			String type = command.get("shape").toString();
			drawUpdate(type, g2);
			shape.repaint();
		}
		
		else if(command.get("operation").toString().equals("type")) {  
			String type = command.get("shape").toString();  //shape, text
			Graphics2D g2 = shape.getBufImage().createGraphics();
			text+=command.get("text").toString(); //text,"keyboardinput"
			Color c1 = toColor(command.get("color").toString());
			g2.setColor(c1);
			drawText(type, g2,text);
			shape.repaint();
		
		}

	}
	
	private void drawText(String type, Graphics2D g2,String text) {
		System.out.println(text);
		System.out.println(startX);
		System.out.println(startY);
		
		g2.drawString(text,startX,startY);
		
	}

	public void drawUpdate(String object, Graphics2D g2) {
		switch (object) {
		case "line":
			g2.drawLine(startX, startY, endX, endY);

			break;
		case "rectangle":
			g2.drawRect(startX, startY, endX - startX, endY - startY);
			break;
		case "circle":
			g2.drawArc(startX, startY, endX - startX, endX - startX, 0, 360);
			break;
		case "oval":
			g2.drawOval(startX, startY, endX - startX, endY - startY);
			break;
		case "eraser":
			// g2.setColor(Color.white);
			g2.setColor(new Color(254, 255, 255));// similar to white
			g2.setStroke(new BasicStroke(20));
			g2.drawLine(startX, startY, endX, endY);
			g2.setColor(this.c);
			break;
		case "pen":
			// how to implement the pen func
			g2.drawLine(startX, startY, endX, endY);
			break;
		default:
			break;
		}
	}

	public static Color toColor(String str) {

		int i = Integer.parseInt(str.substring(1), 16);

		return new Color(i);

	}

	public void newPicture() {
		int width = shape.getWidth();
		int height = shape.getHeight();
		BufferedImage bufImage = (BufferedImage) shape.createImage(width, height);
		Graphics2D gc = bufImage.createGraphics();
		gc.setColor(Color.white);
		gc.fillRect(0, 0, width, height);

		Graphics g = shape.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		shape.setBufImage(bufImage);
		g2.drawImage(bufImage, null, 0, 0);
		shape.setObject("");
		shape.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		  String str=String.valueOf(e.getKeyChar());
		  shape.setText(str);
		//  System.out.println(str);
		  Graphics2D ga =shape.getBufImage().createGraphics();
		  JSONObject command = new JSONObject();
		  command.put("command_name", "draw");
		  command.put("operation", "type");
		  command.put("shape", "text");
		  command.put("text", str);
		  command.put("color",shape.getColor());
		  command.put("which", "0");
		  try {
			os.writeUTF(command.toString());
			os.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  
		  
		  shape.drawText(ga);
		  //this.repaint();
		  shape.repaint();
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}