
/*
 * author: Linchu Liu
 * ID: 978006
 */
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import javax.swing.JFrame;
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
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class User implements ActionListener {

	private TextField tf = null;
	private String address;
	private int port = 0;
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
		} catch (ArrayIndexOutOfBoundsException e) {
			client_obj.ta.append("Didn't enter address, \n");
			client_obj.ta.append("or didn't enter port number. Please try again. \n");
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

		} catch (IOException e1) {
			client_obj.ta.append(
					"connection failed. Didn't enter the correct server address or port number. \n Please try again. \n");
		}
		client_obj.gui = new Client_GUI(client_obj);
		client_obj.gui.setBounds(100, 100, 600, 437);
		client_obj.gui.setTitle("Shared Whiteboard");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int count1 = 0;

//		while (true) {
//			
//			gui.send();
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}
//			int[][] matrix = gui.getMatrixofImage();
//			String row = String.valueOf(matrix.length);
//			String col = String.valueOf(matrix[1].length);

//			if (count1 % 2 == 0) {
//
//				matrix1 = matrix;
//
//			}
//
//			else {
//
//				matrix2 = matrix;
//
//			}
//
//			boolean same = true;
//
//			if (matrix1 != null && matrix2 != null) {
//
//				for (int i = 0; i < matrix1.length; i++) {
//
//					for (int j = 0; j < matrix1[1].length; j++) {
//
//						if (matrix1[i][j] != matrix2[i][j]) {
//
//							same = false;
//						}
//					}
//				}
//			}
//
//			count1++;
//			if (gui.getAction()==1) {
//				gui.setAction(0);
//				try {
//					String matrixString = convertToString(matrix, matrix.length, matrix[1].length);
//					int bufferSize = 60000;
//					int i = 0;
//					int sum = 0;
//					int count = 0;
//
//					while (i < matrixString.length()) {
//						int endIdx = java.lang.Math.min(matrixString.length(), i + bufferSize);
//						String partMatrixString = matrixString.substring(i, endIdx);
//						JSONObject drawUpdate = new JSONObject();
//						drawUpdate.put("command_name", "drawUpdate");
//						drawUpdate.put("row", row);
//						drawUpdate.put("col", col);
//						drawUpdate.put("which", user_id);
//						drawUpdate.put("content", partMatrixString);
//						if (count == 0) {
//							drawUpdate.put("identifier", "start");
//						} else {
//							drawUpdate.put("identifier", "notStart");
//						}
//
//						sum += partMatrixString.length();
//						i += bufferSize;
//						count++;
//						if (sum == matrixString.length()) {
//							drawUpdate.put("finish", "True");
//						} else {
//							drawUpdate.put("finish", "False");
//						}
//						os.writeUTF(drawUpdate.toString());
//						os.flush();
//					}
//					assert sum == matrixString.length();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
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
									ta.append("Your user_id is " + Integer.parseInt(user_id) + ".\n");
								}
							} else {
								ta.append(
										"Cannot join the white board. Connection is refused by the manager.\n");
								ta.append("Please close the application.\n");
								break;
							}
						}

						else if (command_name.equals("notified")) {
							if (received.get("reason").equals("kick")) {
								is_kicked = true;
								ta.append(
										"You are kicked by manager. Please close the application.\n");
								ua.setText("");
							}
							if (received.get("reason").equals("close")) {
								ta.append("Manager is closed. Please close the application.\n");
								ua.setText("");
							}

						}

						else if (command_name.equals("list")) {

							ua.setText("");
							ua.append("User list: total 1 manager and "
									+ received.get("user_count").toString() + " users\n");
							ua.append("-------------------------------------------\n");
							ua.append("#ManagerName: user 0\n");
							StringTokenizer user_list = new StringTokenizer(
									received.get("user").toString());
							for (int i = 0; i < Integer
									.parseInt(received.get("user_count").toString()); i++) {
								ua.append("#Username: user " + user_list.nextToken() + "\n");
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
							ta2.append("user "+username+": ");
							ta2.append(received.get("content").toString()+"\n");
							}
						}

					}
				}
			}
		} catch (IOException e) {
			ta.append(
					"connection failed. Didn't enter the correct server address or port number. \n Please try again. \n");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public User() {
		ClientGUI();
	}

	private void setPort(String port) {
		if (port == null) {
			ta.append("Didn't enter port number. Please try again. \n");
		} else {
			try {
				this.port = Integer.parseInt(port);
			} catch (NumberFormatException e) {
				ta.append(
						"invaild port number. The range of port number is from 1025 to 65534. Please try again. \n");
			}
			if (this.port <= 1024 || this.port >= 65535) {
				ta.append(
						"invaild port number. The range of port number is from 1025 to 65534. Please try again. \n");
			}

		}

	}

	public void ClientGUI() {
		JFrame f = new JFrame();
		Font ft = new Font("Monospaced", Font.PLAIN, 20);
		f.setTitle("client");
		f.setBounds(400, 200, 800, 1000);
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
				} catch (IOException e2) {
					e2.printStackTrace();
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

		Label input = new Label("input");
		input.setFont(ft);
		input.setBounds(20, 70, 60, 30);
		tf = new TextField(100);

		ta = new TextArea(20, 70);
		ta.setFont(ft);
		ua = new TextArea(10, 70);
		ua.setFont(ft);
		send=new JButton("send");
		send.addActionListener(this);
		send.setFont(ft);
		ta2=new TextArea(20,70);
		ta2.setFont(ft);
		f.add(input);
		f.add(tf);
		f.add(send);
		
		f.add(ta);
		f.add(ua);
		f.add(ta2);

		f.setVisible(true);
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
					ta.append("You cannot enter empty input.\n");
				}
				
				
			}
		} catch (Exception ex) {
			ta.append("Lose connection, Please close the client and reconnect to server. \n");
		}

	}

	public DataOutputStream getOS() {
		return this.os;
	}
}