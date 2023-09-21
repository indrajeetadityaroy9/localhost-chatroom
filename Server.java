package hw2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	static ArrayList<ClientServer> users = new ArrayList<ClientServer>();
	static Socket clientSocket;
	static String username = null;
	static String password = null;
	static DataInputStream inStream;
	static DataOutputStream outStream;

	public static void AccessCheck() throws IOException {
		inStream = new DataInputStream(clientSocket.getInputStream());
		outStream = new DataOutputStream(clientSocket.getOutputStream());

		password = inStream.readUTF();

		if (!password.equals("cs319Spring2020")) {
			outStream.writeUTF("Incorrect Access Code");
			outStream.writeUTF("Enter access code : ");
			AccessCheck();
		}
	}

	public static void main(String[] args) {

		try {
			ServerSocket serverSocket = new ServerSocket(4040);
			System.out.println("Server is online");

			while (true) {

				clientSocket = serverSocket.accept();
				inStream = new DataInputStream(clientSocket.getInputStream());
				username = inStream.readUTF();
				AccessCheck();

				if (password.contentEquals("cs319Spring2020")) {
					System.out.println(username + " has connected to server");

					ClientServer a = new ClientServer(clientSocket, username, password);
					users.add(a);

					Thread t = new Thread(a);
					t.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ClientServer implements Runnable {

	Socket socket;
	String name;
	String password;

	ClientServer(Socket socket, String username, String password) {
		this.socket = socket;
		name = username;
		this.password = password;
	}

	public void run() {

		if (password.contentEquals("cs319Spring2020")) {
			try {
				Server.inStream = new DataInputStream(this.socket.getInputStream());
				Server.outStream = new DataOutputStream(this.socket.getOutputStream());
				Server.outStream.writeUTF("You are connected \n");
				Server.outStream.writeUTF("Welcome to the chatroom" + " " + name);
				Server.outStream.flush();

				while (true) {
					String input = Server.inStream.readUTF();
					System.out.println(name + ": " + input);
					for (ClientServer t : Server.users) {
						if (!t.socket.equals(this.socket)) {
							DataOutputStream output = new DataOutputStream(t.socket.getOutputStream());
							output.writeUTF(name + ":  " + input);
							output.flush();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Server.AccessCheck();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
