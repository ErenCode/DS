/*
 * author: Linchu Liu
 * ID: 978006
 */
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
            if (port >= 1025 && port <= 65534 ) {
                Server server = new Server();
                server.start(server,port);
            } else {
                System.out.println("You must input a valid port number in the range 1025 - 65534.");
                System.exit(0);
            }

        } catch (NumberFormatException e) {
            System.out.println("You must input a number as port number.");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("You must input one and only one number as port number.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Connection Lost! Please close the application.");
        }
    }

    private void start(Server server,int port) throws Exception {
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Connection Lost! Please close the application.");
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


    public ArrayList<Socket> getClient_list() {
        return client_list;
    }
}
