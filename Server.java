/*
 * author: Linchu Liu
 * ID: 978006
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private ServerSocket serverSocket = null;
    //	ArrayList<WhiteBoard> whiteboard_list = new ArrayList<WhiteBoard>();
    private ArrayList<Socket> client_list = new ArrayList<Socket>();
    //store the current users and its id.
    private HashMap<Integer, Socket> user_map = new HashMap<Integer, Socket>();
    private Socket manager;
    private boolean hasWB = false;

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            Server server = new Server();
            server.start(server,port);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void start(Server server,int port) throws IOException {
        serverSocket = new ServerSocket(port);
        int client_identifier = 0;
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread thread = new Thread(new ServerThread(clientSocket, server, client_identifier));
            client_identifier++;
            thread.start();


        }
    }

    public void closeServer() {
        System.out.println("Server is closed.");
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void setManager(Socket manager) {
        this.manager = manager;
    }

    public Socket getManager() {
        return manager;
    }

    public void setHasWB(boolean hasWB) {
        this.hasWB = hasWB;
    }

    public boolean getHasWB() {
        return hasWB;
    }

    public HashMap<Integer, Socket> getUser_map() {
        return user_map;
    }

//	public ArrayList<WhiteBoard> getWB_list() {
//		return whiteboard_list;
//	}

    public ArrayList<Socket> getClient_list() {
        return client_list;
    }
}
