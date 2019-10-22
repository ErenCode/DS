
/*
 * author: Linchu Liu
 * ID: 978006
 */
import javax.swing.JButton;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Manager implements ActionListener {

	private TextField tf = null;
	private String address;
	private int port = 0;
	private TextArea ta = null;
	private TextArea ua = null;
	private JButton kick = null;
	private JButton list = null;
	private JButton accept = null;
	private JButton reject = null;
	private DataInputStream is = null;
	private DataOutputStream os = null;
	private Socket client = null;
	private JSONObject newCommand = null;
	private JFrame f2;
	private String join_client_ID = null;
	private JSONObject received = null;
	private Font ft;
	private ArrayList<Integer> user_array = null;
	DS_GUI gui = null;
	private String newMatrix = "";
	private int[][] matrix1 = null;
	private int[][] matrix2 = null;
	private boolean canAccept = true;

	public static void main(String[] args) {
		Manager client_obj = new Manager();
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client_obj.gui = new DS_GUI(client_obj);
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
//			int[][] matrix = gui.getMatrixofImage();

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
//			count1++;

//			String row = String.valueOf(matrix.length);
//			String col = String.valueOf(matrix[1].length);
//			if (gui.getAction()==1) {
//				gui.setAction(0);
//				try {
//					String matrixString = convertToString(matrix, matrix.length, matrix[0].length);
//
//					int bufferSize = 60000;
//					int i = 0;
//					int sum = 0;
//					int count = 0;
//					while (i < matrixString.length()) {
//						int endIdx = java.lang.Math.min(matrixString.length(), i + bufferSize);
//						String partMatrixString = matrixString.substring(i, endIdx);
//						JSONObject drawUpdate = new JSONObject();
//						drawUpdate.put("command_name", "drawUpdate");
//						drawUpdate.put("row", row);
//						drawUpdate.put("col", col);
//						drawUpdate.put("which", "0"); // manager id
//						drawUpdate.put("content", partMatrixString);
//						if (count == 0) {
//							drawUpdate.put("identifier", "start");
//						} else {
//							drawUpdate.put("identifier", "notStart");
//						}
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
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}

//	}

	// start client
	private void start() {
		try {

			// if the port is valid

			while (true) {

				if (is.available() > 0) {
					newCommand = new JSONObject();
					JSONParser parser = new JSONParser();
					received = (JSONObject) parser.parse(is.readUTF());
					if (received.containsKey("command_name")) {
						String command_name = received.get("command_name").toString();

						if (command_name.equals("create")) {
							String response = received.get("response").toString();
							if (response.equals("success")) {
								ta.append("Manager create a new white board successfully.\n");
							} else {
								ta.append("Cannot create a new white board.\n");
								ta.append("Please close the manager GUI.\n");
								break;
							}
						}

						else if (command_name.equals("join_resquest")) {
							if (canAccept) {
								join_client_ID = received.get("client_ID").toString();
								ta.append("User " + join_client_ID
										+ " wants to share your whiteboard, please click accept or reject button.\n");
							} else {
								String an_client_ID = received.get("client_ID").toString();
								newCommand.put("command_name", "join_response");
								newCommand.put("client_ID", an_client_ID);
								newCommand.put("response", "NO");
								os.writeUTF(newCommand.toJSONString());
								os.flush();
								ta.append("User " + an_client_ID
										+ "'s connection is refused automatically.\n");
							}
							if (canAccept == true) {
								canAccept = false;
							}

						} else if (command_name.equals("list")) {
							user_array = new ArrayList<Integer>();
							ua.setText("");
							ua.append("User list: total 1 manager and "
									+ received.get("user_count").toString() + " users.\n");
							ua.append("-------------------------------------------\n");
							ua.append("#ManagerName: user 0\n");
							StringTokenizer user_list = new StringTokenizer(
									received.get("user").toString());
							for (int i = 0; i < Integer
									.parseInt(received.get("user_count").toString()); i++) {
								String user = user_list.nextToken();
								ua.append("#Username: user " + user + "\n");
								user_array.add(Integer.parseInt(user));
							}
							ua.append("-------------------------------------------\n");
						}

						else if (command_name.equals("quit")) {
							ta.append(
									"User " + received.get("client_ID").toString() + " quits.\n");
						} else if (command_name.equals("drawUpdate")) {
							if (received.get("which").equals("0")) {
								continue;
							}
							if (received.get("identifier").equals("start")) {
								newMatrix = "";
								String received_user_id = received.get("which").toString();
								newMatrix += received.get("content").toString();

							} else {
								newMatrix += received.get("content").toString();
							}
							if (received.get("finish").equals("True")) {
								// ta.append(String.valueOf(newMatrix.length())+"!!!!");
								int cow = Integer.parseInt(received.get("row").toString());
								int col = Integer.parseInt(received.get("col").toString());
								int[][] matrix = convertToArray(newMatrix, cow, col);
								int realCow = matrix.length;
								int realCol = matrix[0].length;
								String fromWhich = received.get("which").toString();
								// ta.append(realCow+",,,"+realCol+"|||"+fromWhich+"\n");
								// ta.append(String.valueOf(matrix[0][0])+"\n");
								gui.updateImage(matrix);

							}
						}

						else if (command_name.equals("send")) {
							ta.append("1");
						}

						else if (command_name.equals("draw")) {

							gui.paint(received);

						}
					}
				}
			}

		} catch (IOException e) {
			ta.append(
					"connection failed. Didn't enter the correct server address or port number. \n Please try again. \n");
		} catch (ParseException e) {

			e.printStackTrace();
		}
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

	public Manager() {
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

	public TextArea getTextArea() {
		return ta;
	}

	public void ClientGUI() {
		JFrame f = new JFrame();
		ft = new Font("Monospaced", Font.PLAIN, 20);
		f.setTitle("Manager");
		f.setBounds(400, 200, 800, 1000);
		f.setLayout(new FlowLayout());
		f.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				newCommand = new JSONObject();
				newCommand.put("command_name", "close");
				try {
					os.writeUTF(newCommand.toJSONString());
					os.flush();
					os.close();
					is.close();
					client.close();
				} catch (IOException e2) {

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
		kick = new JButton("kick");
		kick.addActionListener(this);
		kick.setFont(ft);
		kick.setActionCommand("kick");
		accept = new JButton("accept");
		accept.addActionListener(this);
		accept.setFont(ft);
		reject = new JButton("reject");
		reject.addActionListener(this);
		reject.setFont(ft);
		ta = new TextArea(20, 70);
		ta.setFont(ft);
		ua = new TextArea(10, 70);
		ua.setFont(ft);

		f.add(input);
		f.add(tf);
		f.add(kick);
		f.add(accept);
		f.add(reject);
		f.add(ta);
		f.add(ua);

		f.setVisible(true);
	}

	// action of button
	@Override
	public void actionPerformed(ActionEvent e) {
		newCommand = new JSONObject();

		String output = null;
		String input = tf.getText();

		try {

			// kick
			if (e.getSource() == kick) {

				f2 = new JFrame();
				f2.setBounds(400, 200, 800, 200);
				f2.setLayout(new FlowLayout());
				TextField tf2 = new TextField(60);
				tf2.setFont(ft);
				f2.add(tf2);
				JButton finish = new JButton("Finish");
				finish.setFont(ft);
				f2.add(finish);
				TextArea ta2 = new TextArea(5, 50);
				ta2.append("Please enter the client ID\n");
				ta2.setFont(ft);
				f2.add(ta2);
				f2.setVisible(true);
				finish.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						f2.setVisible(false);
						String client_ID = tf2.getText();
						String pattern = "[1-9][0-9]*";
						if (client_ID == null || client_ID.length() == 0) {
							ta.append("The user ID cannot be empty. Please try again. \n");
							return;
						}
						if (!client_ID.matches(pattern)) {
							ta.append("The user ID must be numbers. Please try again. \n");
							return;
						}
						boolean is_user = false;
						if (user_array != null) {
							for (int user_ID : user_array) {
								if (Integer.parseInt(client_ID) == user_ID) {
									ta.append("Kick user successful.\n");
									is_user = true;
									break;
								}
							}
						}
						if (!is_user) {
							ta.append("This user id is not in the user list. Please try again.\n");
							return;
						}
						newCommand.put("command_name", "kick");
						newCommand.put("client_ID", client_ID);
						try {
							os.writeUTF(newCommand.toString());
							os.flush();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
			}

			// accept
			else if (e.getSource() == accept) {
				if (!canAccept) {
					newCommand.put("command_name", "join_response");
					newCommand.put("client_ID", join_client_ID);
					newCommand.put("response", "YES");
					os.writeUTF(newCommand.toJSONString());
					os.flush();
					ta.append("User " + join_client_ID + " is connected.\n");
				}
				if (canAccept == false) {
					canAccept = true;
				}

			}

			else if (e.getSource() == reject) {
				if (!canAccept) {
					newCommand.put("command_name", "join_response");
					newCommand.put("client_ID", join_client_ID);
					newCommand.put("response", "NO");
					os.writeUTF(newCommand.toJSONString());
					os.flush();
					ta.append("User " + join_client_ID + "'s connection is refused.\n");
				}
				if (canAccept == false) {
					canAccept = true;
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