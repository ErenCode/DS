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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

class Client_GUI extends JFrame implements ItemListener,KeyListener,WindowListener {

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

    static Color[] colors = {Color.black, Color.blue, Color.RED,Color.yellow,Color.green,Color.orange,Color.cyan, Color.darkGray,Color.gray,Color.lightGray,Color.magenta,Color.pink,Color.white,new Color(227, 207, 87),new Color(88, 87, 86),new Color(255, 127, 80),new Color(116, 0, 0)};
    static String[] strings = {"Black", "Blue", "Red","Yellow","Green","Orange","Cyan","Dark Gray","Gray","Light Gray","Magenta", "Pink","White","Banana Yellow","Ivory Black","Coral Red","Black Red"};

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
        colorBt = new JComboBox(strings);
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

        ComboBoxRenderer renderer = new ComboBoxRenderer(colorBt);

        renderer.setColors(colors);
        renderer.setStrings(strings);

        colorBt.setRenderer(renderer);

        // color add
//        colorBt.addItem("black");
//        colorBt.addItem("blue");
//        colorBt.addItem("red");
//        colorBt.addItem("yellow");
//        colorBt.addItem("green");
//        colorBt.addItem("orange");
//        colorBt.addItem("cyan");
//        colorBt.addItem("darkGray");
//        colorBt.addItem("gray");
//        colorBt.addItem("lightGray");
//        colorBt.addItem("magenta");
//        colorBt.addItem("pink");
//        colorBt.addItem("white");
//        colorBt.addItem("bananaYellow");
//        colorBt.addItem("ivoryRlack");
//        colorBt.addItem("coralRed");
//        colorBt.addItem("blackRed");
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
            if (s.equals("Black")) {
                c = Color.black;
                shape.setColor(c);
            }
            if (s.equals("Blue")) {
                c = Color.blue;
                shape.setColor(c);
            }
            if (s.equals("Red")) {
                c = Color.red;
                shape.setColor(c);
            }
            if (s.equals("Yellow")) {
                c = Color.yellow;
                shape.setColor(c);
            }
            if (s.equals("Green")) {
                c = Color.green;
                shape.setColor(c);
            }
            if (s.equals("Orange")) {
                c = Color.orange;
                shape.setColor(c);
            }
            if (s.equals("Cyan")) {
                c = Color.cyan;
                shape.setColor(c);
            }
            if (s.equals("Dark Gray")) {
                c = Color.darkGray;
                shape.setColor(c);
            }
            if (s.equals("Gray")) {
                c = Color.gray;
                shape.setColor(c);
            }
            if (s.equals("Light Gray")) {
                c = Color.lightGray;
                shape.setColor(c);
            }
            if (s.equals("Magenta")) {
                c = Color.magenta;
                shape.setColor(c);
            }
            if (s.equals("Pink")) {
                c = Color.pink;
                shape.setColor(c);
            }
            if (s.equals("White")) {
                c = Color.white;
                shape.setColor(c);
            }
            if (s.equals("Banana Yellow")) {
                c = new Color(227, 207, 87);
                shape.setColor(c);
            }
            if (s.equals("Ivory Black")) {
                c = new Color(88, 87, 86);
                shape.setColor(c);
            }
            if (s.equals("Coral Red")) {
                c = new Color(255, 127, 80);
                shape.setColor(c);
            }
            if (s.equals("Black Red")) {
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Connection Lost! Please close the application.");
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
            drawDragged(type, g2);
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



    private void drawDragged(String type, Graphics2D g2) {
        switch (type) {
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

    private void drawText(String type, Graphics2D g2,String text) {
        g2.drawString(text,startX,startY);

    }

    public void drawUpdate(String object, Graphics2D g2) {
        switch (object) {
            case "line":
                g2.drawLine(startX, startY, endX, endY);

                break;
            case "rectangle":
                if(startX<endX&&startY<endY) {
                    g2.drawRect(startX, startY, endX - startX,endY - startY);
                }else if(startX<endX&&startY>endY) {
                    g2.drawRect(startX, endY, endX - startX,startY-endY);
                }else if(startX>endX&&startY<endY) {
                    g2.drawRect(endX, startY,startX-endX,endY - startY);
                    //System.out.println("1111");
                }else if(startX>endX&&startY>endY) {
                    g2.drawRect(endX, endY,startX-endX,startY-endY);
                }
                break;
            case "circle":
                if(startX<endX&&startY<endY) {
                    g2.drawArc(startX, startY, endX - startX, endX - startX, 0, 360);
                }else if(startX<endX&&startY>endY) {
                    g2.drawArc(startX, endY, endX - startX, endX - startX, 0, 360);
                }else if(startX>endX&&startY<endY) {
                    g2.drawArc(endX, startY, startX-endX, startX-endX, 0, 360);
                }else if(startX>endX&&startY>endY){
                    g2.drawArc(endX, endY, startY-endY, startY-endY, 0, 360);//weired
                    //System.out.println("11111");
                }
                break;
            case "oval":
                if(startX<endX&&startY<endY) {
                    g2.drawOval(startX, startY, endX - startX, endY - startY);
                }else if(startX<endX&&startY>endY) {
                    g2.drawOval(startX, endY, endX - startX,startY-endY);
                }else if(startX>endX&&startY<endY) {
                    g2.drawOval(endX, startY, startX-endX,endY - startY);
                }else if(startX>endX&&startY>endY){
                    g2.drawOval(endX, endY, startX-endX, startY-endY);
                    //System.out.println("11111");
                }
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
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            //System.out.println("Connection Lost! Please close the application.");
            JOptionPane.showMessageDialog(null, "Connection Lost. Closing the application.");
            System.exit(0);
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

    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosing(WindowEvent e) {
        JSONObject newCommand = new JSONObject();
        newCommand = new JSONObject();
        newCommand.put("command_name", "quit");
        newCommand.put("client_ID", user.user_id);
        try {
            if (!user.is_kicked) {
                os.writeUTF(newCommand.toJSONString());
                os.flush();
                os.close();
                user.is.close();

                // client.close();
            }
        } catch (Exception e2) {
            System.out.println("Connection Lost! Please close the application.");
            JOptionPane.showMessageDialog(null, "Connection closed. Closing the application. ");
            System.exit(0);
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

}