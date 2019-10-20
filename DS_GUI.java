import java.awt.AWTException;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

class DS_GUI extends JFrame implements ItemListener {

	private JButton ovalBt;
	private JButton circleBt;
	private JButton rectangleBt;
	private JButton lineBt;
	private JButton eraserBt;
	private JButton penBt;
	private JButton textBt;
	private Gshape shape;
	private JComboBox colorBt;
	static Color c = Color.black;// should check if it's ok
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

	public DS_GUI() {

		shape = new Gshape();
		// implement the button
		ovalBt = new JButton("Oval");
		circleBt = new JButton("Circle");
		rectangleBt = new JButton("Rectangle");
		lineBt = new JButton("Line");
		eraserBt = new JButton("eraser");
		penBt = new JButton("pen");
		colorBt = new JComboBox();
		textBt=new JButton("text");
		
		// implement the menu
		JMenuBar = new JMenuBar();
		JMenuOpen = new JMenu("Open");
		JMenuNew = new JMenu("New");
		JMenuSave = new JMenu("Save");
		JMenuSaveAs = new JMenu("SaveAs");
		JMenuClose = new JMenu("Close");

		// add the menu in menu bar
		JMenuBar.add(JMenuOpen);
		JMenuBar.add(JMenuNew);
		JMenuBar.add(JMenuSave);
		JMenuBar.add(JMenuSaveAs);
		JMenuBar.add(JMenuClose);

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
		setJMenuBar(JMenuBar);// this is also ok for menu bar

		// this part is to add action for the button
		lineBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("line");
			}
		});
		rectangleBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("rectangle");
			}
		});
		circleBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("circle");
			}
		});
		ovalBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("oval");
			}
		});
		eraserBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("eraser");
			}
		});
		penBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("pen");
			}
		});
		textBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape.setObject("text");
			}
		});
		//this part is for the textInput
		

		// this part is to add action for the menu

		JMenuOpen.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				/*
				 * int width = shape.getWidth(); int height = shape.getHeight(); BufferedImage
				 * bufImage = (BufferedImage) shape.createImage(width, height); Graphics2D gc =
				 * bufImage.createGraphics(); gc.setColor(Color.white); gc.fillRect(0, 0, width,
				 * height);
				 */
				openDialog();
				Graphics g = shape.getGraphics();

				Graphics2D g2 = (Graphics2D) g;
				BufferedImage bufImage;
				try {
					bufImage = ImageIO.read(new File(filePath));
					shape.setBufImage(bufImage);
					g2.drawImage(bufImage, null, 0, 0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		JMenuNew.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				/*
				 * JFrame newgui=new DS_GUI(); newgui.setBounds(100, 100, 600, 437);
				 * newgui.setTitle("Shared Whiteboard"); newgui.setVisible(true);
				 * newgui.setBackground(Color.white);
				 */
				// shape.getGraphics();
				// newgui.setVisible(true);

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

			}
		});
		JMenuSave.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				System.out.println("insave");
				// String path=System.getProperty("user.dir"); //get current directory

				try {
					/*
					 * int height=shape.getBufImage().getHeight(); int
					 * width=shape.getBufImage().getWidth(); int x=shape.getX(); int y=shape.getY();
					 * myImage = new Robot().createScreenCapture(new Rectangle(x,y,width,height));
					 */
					myImage = shape.getBufImage();
					ImageIO.write(myImage, "jpg", new File("./image" + count + ".jpg"));
					count++;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JMenuSaveAs.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				saveDialog();
				myImage = shape.getBufImage();
				try {
					ImageIO.write(myImage, "jpg", new File(filePath + ".jpg"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JMenuClose.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});

		this.pack();// adjust the size of btn auto
	}

	private void openDialog() {
		FileDialog fd = new FileDialog(jfile, "Open", FileDialog.LOAD);
		fd.setVisible(true);
		/*
		 * while(fd.getFile()==null) { continue; }
		 */
		filePath = fd.getDirectory() + fd.getFile();
	}

	private void saveDialog() {
		FileDialog fd = new FileDialog(jfile, "SaveAs", FileDialog.SAVE);
		fd.setVisible(true);
		/*
		 * while(fd.getFile()==null) { continue; }
		 */
		filePath = fd.getDirectory() + fd.getFile();
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
		
		int[][] rgbValue1=getMatrixofImage();
		//row
        int width = rgbValue1.length;
        //column
        int height = rgbValue1[0].length;
        
        //composite a new array
        int[][] compositeRgbValue=new int[width][height];
        for(int x=0; x< width; x++){
            for(int y=0; y< height; y++){
            	//RGB is binary need to implement bitwise
            	compositeRgbValue[x][y]=rgbValue1[x][y]&rgbValue2[x][y];
            }
        }
 
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int x=0; x< width; x++){
            for(int y=0; y< height; y++){
                bufferedImage.setRGB(x,y,compositeRgbValue[x][y]);  
            }
        }
        Graphics g = shape.getGraphics();

		Graphics2D g2 = (Graphics2D) g;
        shape.setBufImage(bufferedImage);  
        g2.drawImage(bufferedImage, null, 0, 0);
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
			if (s.equals("black"))
				c = Color.black;
			if (s.equals("blue"))
				c = Color.blue;
			if (s.equals("red"))
				c = Color.red;
			if (s.equals("yellow"))
				c = Color.yellow;
			if (s.equals("green"))
				c = Color.green;
			if (s.equals("organge"))
				c = Color.orange;
			if (s.equals("cyan"))
				c = Color.cyan;
			if (s.equals("darkGray"))
				c = Color.darkGray;
			if (s.equals("gray"))
				c = Color.gray;
			if (s.equals("lightGray"))
				c = Color.lightGray;
			if (s.equals("magenta"))
				c = Color.magenta;
			if (s.equals("pink"))
				c = Color.pink;
			if (s.equals("white"))
				c = Color.white;
			if (s.equals("bananaYellow"))
				c = new Color(227, 207, 87);
			if (s.equals("ivoryBlack"))
				c = new Color(88, 87, 86);
			if (s.equals("coralRed"))
				c = new Color(255, 127, 80);
			if (s.equals("blackRed"))
				c = new Color(116, 0, 0);
		}
	}
}