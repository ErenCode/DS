/*
 * author: Linchu Liu
 * ID: 978006
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerThread implements Runnable {
	private Socket client = null;
	private JSONObject command = null;
	private JSONParser parser = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
//	private ArrayList<WhiteBoard> whiteboard_list = null;
	private JSONObject response;
	private Server server = null;
	private ArrayList<Socket> client_list = null;

	private int client_identifier;

	public ServerThread(Socket client, Server server, int client_identifier) {
//		this.whiteboard_list = server.getWB_list();
		this.client_list = server.getClient_list();
		this.client = client;
		this.server = server;
		this.client_identifier = client_identifier;
		try {
			input = new DataInputStream(this.client.getInputStream());
			output = new DataOutputStream(this.client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		parser = new JSONParser();
		try {
			while (true) {
				if (input.available() > 0) {
					response = new JSONObject();
					command = (JSONObject) parser.parse(input.readUTF());
					if (command.containsKey("command_name")) {
						String command_name = command.get("command_name").toString();
						if (command_name.equals("create")) {

							// current no white board
							if (server.getHasWB() == false) {
								// need to change parameters
//								WhiteBoard wb = new WhiteBoard();
//								whiteboard_list.add(wb);
								client_list.add(client);
								server.setManager(client);
								server.setHasWB(true);
								response.put("command_name", "create");
								response.put("response", "success");
								output.writeUTF(response.toJSONString());

							} else {
								response.put("response", "failure");
								output.writeUTF(response.toJSONString());
							}

						} else if (command_name.equals("join_request")) {
							if (server.getHasWB() == true) {
								response.put("command_name", "join_resquest");
								response.put("client_ID", client_identifier);
								client_list.add(client);
								// get manager
								DataOutputStream osManager = new DataOutputStream(
										server.getManager().getOutputStream());
								osManager.writeUTF(response.toJSONString());

							}

							else {
								response.put("command_name", "join_response");
								response.put("response", "failure");
								response.put("reason", "no_white_board");
								output.writeUTF(response.toJSONString());
								output.flush();
							}
						}

						else if (command_name.equals("join_response")) {
							if (command.containsKey("response")) {
								int client_ID = Integer
										.parseInt(command.get("client_ID").toString());

								DataOutputStream osClient = new DataOutputStream(
										client_list.get(client_ID).getOutputStream());
								// YES
								if (command.get("response").equals("YES")) {
									response.put("command_name", "join_response");
									response.put("response", "success");
									response.put("client_ID", client_ID);
									osClient.writeUTF(response.toJSONString());
									server.getUser_map().put(client_ID,
											client_list.get(client_ID));
									sendList();
								} else {
									response.put("command_name", "join_response");
									response.put("response", "failure");
									response.put("client_ID", client_ID);
									osClient.writeUTF(response.toJSONString());
									osClient.flush();
								}
							}
						}

						else if (command_name.equals("quit")) {
							int client_ID = 0;
							if (command.containsKey("client_ID")) {
								client_ID = Integer.parseInt(command.get("client_ID").toString());
							}
							if (server.getUser_map().containsKey(client_ID)) {
								server.getUser_map().remove(client_ID);

							}

							DataOutputStream managerOS = new DataOutputStream(
									server.getManager().getOutputStream());
							response.put("command_name", "quit");
							response.put("client_ID", command.get("client_ID").toString());
							managerOS.writeUTF(response.toJSONString());
							managerOS.flush();
							output.flush();
							sendList();

						}

						else if (command_name.equals("kick")) {
							int client_ID = Integer.parseInt(command.get("client_ID").toString());
							response.put("command_name", "notified");
							response.put("reason", "kick");
							DataOutputStream user_os = new DataOutputStream(
									server.getUser_map().get(client_ID).getOutputStream());
							user_os.writeUTF(response.toJSONString());
							user_os.flush();
							user_os.close();
							if (server.getUser_map().containsKey(client_ID)) {
								server.getUser_map().remove(client_ID);
							}
							output.flush();
							sendList();
						}

						else if (command_name.equals("close")) {

							server.setHasWB(false);

							response.put("command_name", "notified");
							response.put("reason", "close");

							for (Socket user : server.getUser_map().values()) {
								DataOutputStream user_os = new DataOutputStream(
										user.getOutputStream());
								user_os.writeUTF(response.toJSONString());
								user_os.flush();
								user_os.close();
							}
							client.close();
							server.closeServer();
							System.exit(0);

						} else if (command_name.equals("list")) {
							sendList();
						}
						else if(command_name.equals("drawUpdate")) {
			//				String matrix = command.get("content").toString();
			//				response.put("command_name", "drawUpdate");
			//				response.put("matrix", matrix);
							response=command;
							String user_ids=command.get("which").toString();
							if (user_ids.equals("0")){
								for (Socket user : server.getUser_map().values()) {
									DataOutputStream user_os = new DataOutputStream(
											user.getOutputStream());
									user_os.writeUTF(response.toJSONString());
									user_os.flush();
								}
							}
							else {
								DataOutputStream managerOS = new DataOutputStream(
										server.getManager().getOutputStream());
								managerOS.writeUTF(response.toJSONString());
								managerOS.flush();
								int user_idi=Integer.parseInt(user_ids);
								for(Integer key:server.getUser_map().keySet()){
							        if(key==user_idi) {
							        	continue;
							        }
							        Socket userSocket=server.getUser_map().get(key);
							        DataOutputStream user_os = new DataOutputStream(
											userSocket.getOutputStream());
									user_os.writeUTF(response.toJSONString());
									user_os.flush();	
							    }
								
							}

							
							
						}
					}
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

		}

	}
	
	public void sendList() {
		int count = 0;
		String user_list = "";
		for (int user_id : server.getUser_map().keySet()) {
			count++;
			user_list = user_list + String.valueOf(user_id) + " ";
		}
		System.out.println(count);
		response.put("command_name", "list");
		response.put("user", user_list);
		response.put("user_count", count);
		try {
			DataOutputStream managerOS = new DataOutputStream(server.getManager().getOutputStream());
			managerOS.writeUTF(response.toJSONString());
			managerOS.flush();
			for (Socket user : server.getUser_map().values()) {
				DataOutputStream user_os = new DataOutputStream(
						user.getOutputStream());
				user_os.writeUTF(response.toJSONString());
				user_os.flush();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
