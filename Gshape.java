import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;

import javax.swing.*;

import org.json.simple.JSONObject;

/**
 *Gshape.java
 *wybcloud
 *2019Äê9ÔÂ24ÈÕ
 */

/**
 * @author wybcloud
 *
 */
public class Gshape extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private int LENGTH = 1000;// the length of the wb
    private int WEIGTH = 600;
    private int startX;
    private int startY;
    private int tempX;
    private int tempY;
    private int endX;
    private int endY;
    private BufferedImage bufImage = null;
    private String object = "";
    private Color c = Color.black;
    //	private String menu="";
    private String text = "";
    private DataOutputStream os = null;
    int[][] quene = new int[2][2];// for pen

    public Gshape(DataOutputStream os) {
        this.os = os;

        setPreferredSize(new Dimension(LENGTH, WEIGTH));
        setBackground(Color.white);
        this.addMouseListener(this);// add into the whiteboard
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

    public void drawObj(Graphics2D g2) {
        // it may exist problem about the static color
        g2.setColor(this.c);
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

    public void setText(String str) {
        this.text+=str;
    }

    public void setStart() {
        this.startX=0;
        this.startY=0;
    }

    public void setObject(String str) {
        this.object = str;
        // System.out.println(object);
    }

    public void setColor(Color c) {
        this.c = c;
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
    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        JSONObject command = new JSONObject();
        // TODO Auto-generated method stub
        // ||!this.object.equals("eraser")
        if(this.object.equals("text")&&!text.isEmpty()) {
            startX = e.getX();
            startY = e.getY();
            this.text="";
        }
        if ((!this.object.equals("pen")) && (!this.object.equals("eraser"))) {
            startX = e.getX();
            startY = e.getY();
            command.put("command_name", "draw");
            command.put("operation", "mousePressed");
            command.put("startX", startX);
            command.put("startY", startY);
            command.put("which", "0");
            try {
                os.writeUTF(command.toString());
                os.flush();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                //System.out.println("Connection Lost! Please close the application.");
                JOptionPane.showMessageDialog(null, "Connection Lost! Closing thhe application");
                System.exit(0);
            }

            // endX = startX;
            // endY = startY;
        } else if (this.object.equals("pen")) {
            // System.out.println("in");
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
        } else if (this.object.equals("eraser")) {
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

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        JSONObject command = new JSONObject();
        // TODO Auto-generated method stub
        endX = e.getX();
        endY = e.getY();
        command.put("command_name", "draw");
        command.put("shape", object);
        command.put("operation", "mouseReleased");
        command.put("endX", endX);
        command.put("endY", endY);
        command.put("color", ColorToString(this.c));
        command.put("which", "0");
        try {
            os.writeUTF(command.toString());
            os.flush();
        } catch (Exception ie) {
            // TODO Auto-generated catch block
            //System.out.println("Connection Lost! Please close the application.");
            JOptionPane.showMessageDialog(null, "Connection Lost! Closing thhe application");
            System.exit(0);
        }
        // check with xu
        Graphics2D ga = bufImage.createGraphics();
        drawObj(ga);
        this.repaint();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        JSONObject command = new JSONObject();
        // TODO Auto-generated method stub
        // ||!this.object.equals("eraser")
        if ((!this.object.equals("pen")) && (!this.object.equals("eraser"))) {
            endX = e.getX();
            endY = e.getY();
        } else if (this.object.equals("pen")) {
            // System.out.println("in");
            endX = e.getX();
            endY = e.getY();
            startX = quene[0][0];
            startY = quene[0][1];
            quene[0][0] = quene[1][0];
            quene[0][1] = quene[1][1];
            quene[1][0] = endX;
            quene[1][1] = endY;

            command.put("command_name", "draw");
            command.put("shape", object);
            command.put("operation", "mouseDragged");
            command.put("startX", startX);
            command.put("startY", startY);
            command.put("endX", endX);
            command.put("endY", endY);
            command.put("color", ColorToString(this.c));
            command.put("which", "0");
            try {
                os.writeUTF(command.toString());
                os.flush();
            } catch (Exception ie) {
                // TODO Auto-generated catch block
                //System.out.println("Connection Lost! Please close the application.");
                JOptionPane.showMessageDialog(null, "Connection Lost! Closing thhe application");
                System.exit(0);
            }
            // this.repaint();
            // used to update the pen's trace
            Graphics2D gb = bufImage.createGraphics();
            drawObj(gb);
        } else if (this.object.equals("eraser")) {
            endX = e.getX();
            endY = e.getY();
            startX = tempX;
            startY = tempY;
            tempX = endX;
            tempY = endY;
            command.put("command_name", "draw");
            command.put("shape", object);
            command.put("operation", "mouseDragged");
            command.put("startX", startX);
            command.put("startY", startY);
            command.put("endX", endX);
            command.put("endY", endY);
            command.put("color", ColorToString(this.c));
            command.put("which", "0");
            try {
                os.writeUTF(command.toString());
                os.flush();
            } catch (Exception ie) {
                // TODO Auto-generated catch block
                //System.out.println("Connection Lost! Please close the application.");
                JOptionPane.showMessageDialog(null, "Connection Lost! Closing thhe application");
                System.exit(0);
            }

            // used to update the eraser's trace
            Graphics2D gb = bufImage.createGraphics();
            drawObj(gb);
        }

        this.repaint();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    // ?the func with update
    public void update(Graphics2D g2) {
        drawObj(g2);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        text = String.valueOf(e.getKeyChar());

    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public static String ColorToString(Color color) {
        String R = Integer.toHexString(color.getRed());
        R = R.length() < 2 ? ('0' + R) : R;
        String B = Integer.toHexString(color.getBlue());
        B = B.length() < 2 ? ('0' + B) : B;
        String G = Integer.toHexString(color.getGreen());
        G = G.length() < 2 ? ('0' + G) : G;
        return '#' + R + G + B;

    }

    public void drawText(Graphics2D ga) {
        ga.setColor(this.c);
        if(this.c==Color.black) {
            System.out.println("11111111111111");
        }
        ga.drawString(text, startX, startY);

    }

    public String getColor() {
        return ColorToString(this.c);
    }

}
