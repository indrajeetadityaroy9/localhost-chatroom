package hw2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

	static BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
	private static String code;
	private static String name;

	public Client() throws IOException {

		System.out.print("Enter your name: ");
		name = read.readLine();
		System.out.print("Enter access code : ");
		code = read.readLine();

		try {
			Socket serverSocket = new Socket("localhost", 4040);

			DataOutputStream outStream = new DataOutputStream(serverSocket.getOutputStream());

			ClientIO a = new ClientIO(serverSocket);
			Thread t = new Thread(a);
			t.start();
			
			outStream.writeUTF(name);
			outStream.writeUTF(code);
			outStream.flush();

			while (true) {
				outStream.writeUTF(read.readLine());
				outStream.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		new Client();
	}
}

class ClientIO implements Runnable {

	Socket s;

	public ClientIO(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {
		try {
			DataInputStream input = new DataInputStream(s.getInputStream());
			while (true) {
				String inStr = input.readUTF();
				System.out.println(inStr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
