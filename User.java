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

		Thread CommunicationThread = new Thread(() -> client_obj.start());
		CommunicationThread.start();
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
								ta.append("Join white board successfully.\n");
								ta.append("Your user_id is " + Integer.parseInt(user_id) + ".\n");
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
							ua.append("User list: total " + received.get("user_count").toString()
									+ " users\n");
							ua.append("-------------------------------------------\n");
							StringTokenizer user_list = new StringTokenizer(
									received.get("user").toString());
							for (int i = 0; i < Integer
									.parseInt(received.get("user_count").toString()); i++) {
								ua.append("Username: user " + user_list.nextToken() + "\n");
							}
							ua.append("-------------------------------------------\n");
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

		list = new JButton("list");
		list.addActionListener(this);
		list.setFont(ft);

		ta = new TextArea(20, 70);
		ta.setFont(ft);
		ua = new TextArea(10, 70);
		ua.setFont(ft);
		f.add(input);
		f.add(tf);

		f.add(list);

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
			}

		} catch (Exception ex) {
			ta.append("Lose connection, Please close the client and reconnect to server. \n");
		}

	}
}