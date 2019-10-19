/*
 * author: Linchu Liu
 * ID: 978006
 */
import javax.swing.JButton;
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
import java.util.ArrayList;
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
	private ArrayList<Integer> user_array=null;

	public static void main(String[] args) {
		Manager client_obj = new Manager();

		try {
			client_obj.address = args[0];
			client_obj.setPort(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			client_obj.ta.append("Didn't enter address, \n");
			client_obj.ta.append("or didn't enter port number. Please try again. \n");
		}
		Thread CommunicationThread = new Thread(() -> client_obj.start());
		CommunicationThread.start();
	}

	// start client
	private void start() {
		try {
			client = new Socket(address, port);
			is = new DataInputStream(client.getInputStream());
			os = new DataOutputStream(client.getOutputStream());
			
			// if the port is valid
			if (port != 0) {
				ta.append("Connection succeed! \n");
				ta.append("Please enter word in the input field and then click the operation. \n");
			}
			newCommand = new JSONObject();
			newCommand.put("command_name", "create");
			os.writeUTF(newCommand.toJSONString());
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
							join_client_ID = received.get("client_ID").toString();
							ta.append("User " + join_client_ID
									+ " wants to share your whiteboard, please click accept or reject button.\n");

						} else if (command_name.equals("list")) {
							user_array=new ArrayList<Integer>();
							ua.setText("");
							ua.append("User list: total " + received.get("user_count").toString()
									+ " users\n");
							ua.append("-------------------------------------------\n");
							StringTokenizer user_list = new StringTokenizer(
									received.get("user").toString());
							for (int i = 0; i < Integer
									.parseInt(received.get("user_count").toString()); i++) {
								String user=user_list.nextToken();
								ua.append("Username: user " + user + "\n");
								user_array.add(Integer.parseInt(user));
							}
							ua.append("-------------------------------------------\n");
						}

						else if (command_name.equals("quit")) {
							ta.append(
									"User " + received.get("client_ID").toString() + " quits.\n");
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
		list = new JButton("list");
		list.addActionListener(this);
		list.setFont(ft);
		accept = new JButton("accept");
		accept.addActionListener(this);
		accept.setFont(ft);
		reject = new JButton("reject");
		reject.addActionListener(this);
		reject.setFont(ft);
		ta = new TextArea(20, 70);
		ta.setFont(ft);
		ua=new TextArea(10, 70);
		ua.setFont(ft);

		f.add(input);
		f.add(tf);
		f.add(kick);
		f.add(list);
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
			// list
			if (e.getSource() == list) {
				newCommand.put("command_name", "list");
				output = newCommand.toJSONString();
				os.writeUTF(output);
				os.flush();
				
			}

			// kick
			else if (e.getSource() == kick) {
				newCommand.put("command_name", "kick");

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
						boolean is_user=false;
						for(int user_ID:user_array) {
							if(Integer.parseInt(client_ID)==user_ID) {
								ta.append("Kick user successful.\n");
								is_user=true;
								break;
							}
						}
						if(!is_user) {
							ta.append("This user id is not in the user list. Please try again.\n");
							return;
						}
								
						
						newCommand.put("client_ID", client_ID);
						String output = newCommand.toJSONString();
						try {
							os.writeUTF(output);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				
				

			}

			// accept
			else if (e.getSource() == accept) {

				newCommand.put("command_name", "join_response");
				newCommand.put("client_ID", join_client_ID);
				newCommand.put("response", "YES");
				os.writeUTF(newCommand.toJSONString());
				os.flush();
				ta.append("User " + join_client_ID + " is connected.\n");

			}

			else if (e.getSource() == reject) {
				newCommand.put("command_name", "join_response");
				newCommand.put("client_ID", join_client_ID);
				newCommand.put("response", "NO");
				os.writeUTF(newCommand.toJSONString());
				os.flush();
				ta.append("User " + join_client_ID + "'s connection is refused.\n");
			}

		} catch (Exception ex) {
			ta.append("Lose connection, Please close the client and reconnect to server. \n");
		}

	}

}