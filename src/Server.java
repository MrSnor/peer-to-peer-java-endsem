
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Integer;

@SuppressWarnings("unused")

public class Server {
	public static ArrayList<FileInfo> globalArray = new ArrayList<FileInfo>();

	@SuppressWarnings("resource")
	// public static void main(String args[])
	public Server() throws NumberFormatException, IOException {

		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(7799);
			InetAddress serverAddress = InetAddress.getLocalHost();
			System.out.println("Server started on: " + serverAddress.getHostAddress());
			// listening on port
			System.out.println("Server listening on port: " + serverSocket.getLocalPort());
			System.out.println(" ");
			System.out.println("Waiting for the Client to be connected ..");
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				System.out.println("Client connected: " + clientSocket);


				// can use to check if clients are getting connected but dont uncomment while use gives error
				// Send affirmation to the client
				// String affirmationMessage = "Connection successful Sir";
				// OutputStream outputStream = clientSocket.getOutputStream();
				// outputStream.write(affirmationMessage.getBytes());
				// outputStream.flush();
				// outputStream.close();

				// serverSocket.close();
			} catch (IOException e) {
				System.out.println("I/O error: " + e);
			} finally {
				if (clientSocket != null) {
					// clientSocket.close();
					new ServerTestClass(clientSocket, globalArray).start();
				}
			}
			// new ServerTestClass(socket, globalArray).start();
		}
	}
}

class ServerTestClass extends Thread {
	protected Socket socket;
	ArrayList<FileInfo> globalArray;

	public ServerTestClass(Socket clientSocket, ArrayList<FileInfo> globalArray) {
		this.socket = clientSocket;
		this.globalArray = globalArray;
	}

	ArrayList<FileInfo> filesList = new ArrayList<FileInfo>();
	ObjectOutputStream oos;
	ObjectInputStream ois;
	String str;
	int index;

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			InputStream is = socket.getInputStream();
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(is);
			filesList = (ArrayList<FileInfo>) ois.readObject();
			System.out.println("All the available files from the given directory have been recieved to the Server!");
			for (int i = 0; i < filesList.size(); i++) {
				globalArray.add(filesList.get(i));
			}

			System.out.println(
					"List of files at peer " + filesList.get(0).peerid + " (with port " + filesList.get(0).portNumber + "):");
			for (FileInfo fileInfo : filesList) {
				System.out.println("- " + fileInfo.fileName);
			}

			System.out
					.println("Total number of files available in the Server that are received from all the connected clients: "
							+ globalArray.size());

		}

		catch (IndexOutOfBoundsException e) {
			System.out.println("Index out of bounds exception");
		} catch (IOException e) {
			System.out.println("I/O exception");
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
		}

		try {
			str = (String) ois.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
		}

		ArrayList<FileInfo> sendingPeers = new ArrayList<FileInfo>();
		System.out.println("Searching for the file name...!!!");

		for (int j = 0; j < globalArray.size(); j++) {
			FileInfo fileInfo = globalArray.get(j);
			Boolean tf = fileInfo.fileName.equals(str);
			if (tf) {
				index = j;
				sendingPeers.add(fileInfo);
			}
		}

		try {
			oos.writeObject(sendingPeers);
		} catch (IOException ex) {
			Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
