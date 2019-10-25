/*
 * author: Linchu Liu
 * ID: 978006
 */
import javax.swing.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class User implements ActionListener {

    private TextField tf = null;
    private String address;
    private int port = 0;
    private String username;
    private TextArea ta = null;
    private JButton list = null;
    private DataInputStream is = null;
    private DataOutputStream os = null;
    private Socket client = null;
    private JSONObject newCommand = null;
    private JSONObject received = null;
    private String user_id;
    private boolean is_kicked = false;
    private TextArea ua;
    //	private Client_GUI gui=null;
    private String newMatrix = "";
    private Client_GUI gui = null;
    private int[][] matrix1 = null;
    private int[][] matrix2 = null;
    private JButton send;
    private TextArea ta2;

    public static void main(String[] args) {
        User client_obj = new User();

        try {
            client_obj.address = args[0];
            client_obj.setPort(args[1]);
            client_obj.username = args[2];

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("You didn't enter all of address, port number and username.");
            System.out.println("Please reopen this application and follow the pattern: java -jar Manager.jar <ip_address> <port_number> username");
            System.exit(0);
        }

        try {
            client_obj.client = new Socket(client_obj.address, client_obj.port);
            client_obj.is = new DataInputStream(client_obj.client.getInputStream());
            client_obj.os = new DataOutputStream(client_obj.client.getOutputStream());
            // if the port is valid
            if (client_obj.port == 0) {
                client_obj.ta.append("Wrong port number.\n");
            }
            client_obj.newCommand = new JSONObject();
            client_obj.newCommand.put("command_name", "join_request");
            client_obj.os.writeUTF(client_obj.newCommand.toJSONString());

        } catch(ConnectException e) {
            System.out.println("The address is not reachable, please input right address");
            System.exit(0);
        } catch (Exception e1) {
            System.out.println("Connection failed. You didn't enter the correct server address or port number. \n Please try again. \n");
            System.exit(0);
        }
        client_obj.gui = new Client_GUI(client_obj);
        client_obj.gui.setBounds(100, 100, 600, 437);
        client_obj.gui.setTitle("Shared Whiteboard (Client Version) --- "+ client_obj.username);
        client_obj.gui.setVisible(true);
        client_obj.gui.setBackground(Color.white);// set the color for the eraser func,but need
        // better solu
        client_obj.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Thread CommunicationThread = new Thread(() -> client_obj.start());
        CommunicationThread.start();
        Thread sendDrawingThread = new Thread(() -> client_obj.startDraw());
        sendDrawingThread.start();
    }

    private void startDraw() {

        ta.append("Please wait for manager to approve your request.\n");

        newCommand = new JSONObject();
        newCommand.put("command_name", "create");

        try {
            os.writeUTF(newCommand.toJSONString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "Connection Lost! Closing the application.");
            System.exit(0);
        }
        int count1 = 0;

    }

    public String convertToString(int[][] array, int row, int col) {
        String str = "";
        String newStr = null;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newStr = String.valueOf(array[i][j]);
                str = str + newStr + ",";
            }
        }
        return str;
    }

    public int[][] convertToArray(String str, int row, int col) {
        int[][] intArray = new int[row][col];
        int count = 0;
        String[] strArray = str.split(",");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                intArray[i][j] = Integer.parseInt(strArray[count]);
                ++count;
            }
        }
        return intArray;
    }

    // start client
    private void start() {

        try {

            while (true) {

                if (is.available() > 0) {
                    newCommand = new JSONObject();
                    JSONParser parser = new JSONParser();
                    received = (JSONObject) parser.parse(is.readUTF());
                    if (received.containsKey("command_name")) {
                        String command_name = received.get("command_name").toString();

                        if (command_name.equals("join_response")) {
                            String response = received.get("response").toString();
                            user_id = received.get("client_ID").toString();
                            if (response.equals("success")) {
                                if (received.get("identifier").equals("start")) {
                                    newMatrix = "";
                                    ta.append("Server is sending a new picture to you.\n");
                                    ta.append("Please do not do any operations.\n");
                                    newMatrix += received.get("content").toString();
                                } else {
                                    newMatrix += received.get("content").toString();
                                }
                                if (received.get("finish").equals("True")) {
                                    int cow = Integer.parseInt(received.get("row").toString());
                                    int col = Integer.parseInt(received.get("col").toString());
                                    int[][] matrix = convertToArray(newMatrix, cow, col);
                                    // String fromWhich = received.get("which").toString();
                                    // ta.append(cow+",,,"+col+"|||"+fromWhich+"\n");
                                    gui.updateImage(matrix);
                                    ta.append("Transmission complete.\n");
                                    ta.append("Join white board successfully.\n");
                                    ta.append("Your username is " + username +".\n");
                                    ta.append("Your auto generated user id is " + Integer.parseInt(user_id) + ".\n");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Cannot join the whiteboard. Connection is rejected by the manager. \n Closing the application.");
                                System.exit(0);
                                break;
                            }
                        }

                        else if (command_name.equals("notified")) {
                            if (received.get("reason").equals("kick")) {
                                is_kicked = true;
                                ta.append(
                                        "You are kicked by manager. Please close the application.\n");
                                ua.setText("");
                                JOptionPane.showMessageDialog(null, "You are kicked by manager. Closing the application.");
                                System.exit(0);
                            }
                            if (received.get("reason").equals("close")) {
                                ta.append("Manager is closed. Please close the application.\n");
                                ua.setText("");
                                JOptionPane.showMessageDialog(null, "Manager is closed. Closing the application.");
                                System.exit(0);
                            }

                        }

                        else if (command_name.equals("list")) {

                            ua.setText("");
                            ua.append("User list: total 1 manager and "
                                    + received.get("user_count").toString() + " users\n");
                            ua.append("-------------------------------------------\n");
                            ua.append("#ManagerName: User 0\n");
                            StringTokenizer user_list = new StringTokenizer(
                                    received.get("user").toString());
                            for (int i = 0; i < Integer
                                    .parseInt(received.get("user_count").toString()); i++) {
                                ua.append("#Username: User " + user_list.nextToken() + "\n");
                            }
                            ua.append("-------------------------------------------\n");
                        } else if (command_name.equals("drawUpdate")) {
                            if (received.get("identifier").equals("start")) {
                                newMatrix = "";
                                newMatrix += received.get("content").toString();
                            } else {
                                newMatrix += received.get("content").toString();
                            }
                            if (received.get("finish").equals("True")) {
                                int cow = Integer.parseInt(received.get("row").toString());
                                int col = Integer.parseInt(received.get("col").toString());
                                int[][] matrix = convertToArray(newMatrix, cow, col);
                                String fromWhich = received.get("which").toString();
                                // ta.append(cow+",,,"+col+"|||"+fromWhich+"\n");
                                gui.updateImage(matrix);
                            }
                        }

                        else if (command_name.equals("draw")) {

                            gui.paint(received);

                        } else if (command_name.equals("newPicture")) {
                            gui.newPicture();
                        }

                        else if (command_name.equals("openFile")) {
                            if (received.get("identifier").equals("start")) {
                                newMatrix = "";
                                ta.append("Server is sending a new picture to you.\n");
                                ta.append("Please do not do any operations.\n");
                                newMatrix += received.get("content").toString();
                            } else {
                                newMatrix += received.get("content").toString();
                            }
                            if (received.get("finish").equals("True")) {
                                int cow = Integer.parseInt(received.get("row").toString());
                                int col = Integer.parseInt(received.get("col").toString());
                                int[][] matrix = convertToArray(newMatrix, cow, col);
                                // String fromWhich = received.get("which").toString();
                                // ta.append(cow+",,,"+col+"|||"+fromWhich+"\n");
                                gui.updateImage(matrix);
                                ta.append("Transmission complete.\n");
                            }
                        }

                        else if (command_name.equals("message")) {
                            String username=received.get("which").toString();
                            if(username.equals("0")) {
                                ta2.append("Manager: ");
                                ta2.append(received.get("content").toString()+"\n");
                            }
                            else {
                                ta2.append("User "+username+": ");
                                ta2.append(received.get("content").toString()+"\n");
                            }
                        }

                    }
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("You didn't enter the correct ip address or port number.");
            JOptionPane.showMessageDialog(null, "You didn't enter the correct ip address or port number.. Closing the application.");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Connection failed.");
            JOptionPane.showMessageDialog(null, "Connection Failed. Closing the application.");
            System.exit(0);
        }
    }

    public User() {
        ClientGUI();
    }

    private void setPort(String port) {
        if (port == null) {
            ta.append("Didn't enter port number. Please try again. \n");
            JOptionPane.showMessageDialog(null, "Didn't enter port number. Please try again.");
            System.exit(0);
        } else {
            try {
                this.port = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                ta.append(
                        "invalid port number. The range of port number is from 1025 to 65534. Please try again. \n");
                JOptionPane.showMessageDialog(null, "Invalid port number. The range of port number is from 1025 to 65534. Please try again. ");
                System.exit(0);
            }
            if (this.port <= 1024 || this.port >= 65535) {
                ta.append(
                        "invalid port number. The range of port number is from 1025 to 65534. Please try again. \n");
                JOptionPane.showMessageDialog(null, "Invalid port number. The range of port number is from 1025 to 65534. Please try again. ");
                System.exit(0);

            }

        }

    }

    public void ClientGUI() {
        JFrame f = new JFrame();
        Font ft = new Font("TimesRoman", Font.PLAIN, 15);
        f.setTitle("Online Chatbox (Client Version)");
        f.setSize(400, 680);
        f.setLayout(new FlowLayout());
        f.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

                newCommand = new JSONObject();
                newCommand.put("command_name", "quit");
                newCommand.put("client_ID", user_id);
                try {
                    if (!is_kicked) {
                        os.writeUTF(newCommand.toJSONString());
                        os.flush();
                        os.close();
                        is.close();
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
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

        });

        Label input = new Label("Message");
        input.setFont(ft);
        input.setBounds(300, 200, 50, 40);
        tf = new TextField(30);
        tf.setFont(ft);
        ta = new TextArea(9, 40);
        ta.setFont(ft);
        ua = new TextArea(9, 40);
        ua.setFont(ft);
        send=new JButton("Send");
        send.addActionListener(this);
        send.setFont(ft);
        ta2=new TextArea(14,40);
        ta2.setFont(ft);
        f.add(input);
        f.add(tf);
        f.add(send);

        f.add(ta);
        f.add(ua);
        f.add(ta2);

        f.setVisible(true);
        ta.append("Command Status");
        ta.append("--------------------------------\n");
        ua.append("Online User List");
        ua.append("--------------------------------\n");
        ta2.append("Chat Window");
        ta2.append("--------------------------------\n");

    }

    // action of button
    @Override
    public void actionPerformed(ActionEvent e) {
        newCommand = new JSONObject();
        try {
            if(e.getSource() == send) {
                if(tf.getText().length()!=0 && tf.getText()!=null) {
                    newCommand.put("command_name","message");
                    newCommand.put("which",user_id);
                    newCommand.put("content",tf.getText());
                    os.writeUTF(newCommand.toString());
                    os.flush();
                    ta2.append("You: "+tf.getText()+"\n");
                }
                else {
                    //ta.append("You cannot enter empty input.\n");
                    JOptionPane.showMessageDialog(null, "You cannot enter empty input. ");
                }


            }
        } catch (Exception ex) {
            ta.append("Connection Lost! Please close the client and reconnect to server. \n");
            JOptionPane.showMessageDialog(null, "Connection Lost. Closing the application. Please, try to reconnect to server");
            System.exit(0);
        }

    }

    public DataOutputStream getOS() {
        return this.os;
    }
}