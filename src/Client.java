
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	@SuppressWarnings({ "unchecked", "rawtypes", "resource", "unused" })

	public Client() {
		Socket socket;
		ArrayList<FileInfo> arrList = new ArrayList<FileInfo>();
		ObjectInputStream objectInputStream;
		ObjectOutputStream objectOutputStream;
		String string;
		Object o, b;
		String directoryPath = null;
		int peerServerPort = 0;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Welcome to the Client ::");
			System.out.println(" ");
			System.out.println("Enter the directory that contain the files -->");
			directoryPath = br.readLine();

			System.out.println("Enter the port number on which this peer should act as server ::");
			peerServerPort = Integer.parseInt(br.readLine());

			ServerDownload objServerDownload = new ServerDownload(peerServerPort, directoryPath);
			objServerDownload.start();

			InetAddress routerAddress = InetAddress.getLocalHost();

			socket = new Socket(routerAddress.getHostAddress(), 7799);

			InetAddress clientAddress = socket.getInetAddress();
			System.out.println("\nConnected to indexing server at: " + clientAddress.getHostAddress() + "\n");

			// Receive affirmation from the server
			// can use to check if clients are getting connected but dont uncomment while
			// use gives error
			// InputStream inputStream = socket.getInputStream();
			// byte[] buffer = new byte[1024];
			// int bytesRead = inputStream.read(buffer);
			// String affirmationMessage = new String(buffer, 0, bytesRead);
			// System.out.println("Received affirmation: " + affirmationMessage);

			objectInputStream = new ObjectInputStream(socket.getInputStream());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

			System.out.println("Enter the peerid for this directory ::");
			int readpid = Integer.parseInt(br.readLine());

			File folder = new File(directoryPath);
			File[] listofFiles = folder.listFiles();
			FileInfo currentFile;
			File file;

			// Iterating over an array of files in a directory and creating a `FileInfo`
			// object for each file.
			for (int i = 0; i < listofFiles.length; i++) {
				currentFile = new FileInfo();
				file = listofFiles[i];
				currentFile.fileName = file.getName();
				currentFile.peerid = readpid;
				currentFile.portNumber = peerServerPort;
				arrList.add(currentFile);
			}

			objectOutputStream.writeObject(arrList);

			System.out.println(
					"Enter the desired file name that you want to downloaded from the list of the files shown in the Index Server ::");
			String fileNameToDownload = br.readLine();
			objectOutputStream.writeObject(fileNameToDownload);

			System.out.println("Waiting for the reply from Server...!!");

			ArrayList<FileInfo> peers = new ArrayList<FileInfo>();
			peers = (ArrayList<FileInfo>) objectInputStream.readObject();

			for (int i = 0; i < peers.size(); i++) {
				int result = peers.get(i).peerid;
				int port = peers.get(i).portNumber;
				System.out.println("The file is stored at peer id " + result + " on port " + port);
			}

			System.out.println("Enter the respective port number of the above peer id :");
			int clientAsServerPortNumber = Integer.parseInt(br.readLine());

			System.out.println("Enter the desired peer id from which you want to download the file from :");
			int clientAsServerPeerid = Integer.parseInt(br.readLine());

			clientAsServer(clientAsServerPeerid, clientAsServerPortNumber, fileNameToDownload, directoryPath);
		} catch (Exception e) {
			System.out.println("Error in establishing the Connection between the Client and the Server!! ");
			System.out.println("Please cross-check the host address and the port number..");
		}
	}

	/**
	 * Downloads a file from a peer server and saves it to the specified directory.
	 *
	 * @param clientAsServerPeerid     the peer id of the server from which to
	 *                                 download the file
	 * @param clientAsServerPortNumber the port number of the server from which to
	 *                                 download the file
	 * @param fileName                 the name of the file to download
	 * @param directoryPath            the directory path where the file should be
	 *                                 saved
	 * @throws ClassNotFoundException if the class of a serialized object could not
	 *                                be found
	 */
	public static void clientAsServer(int clientAsServerPeerid, int clientAsServerPortNumber, String fileName,
			String directoryPath) throws ClassNotFoundException {
		try {
			@SuppressWarnings("resource")
			InetAddress routerAddress = InetAddress.getLocalHost();

			Socket clientAsServersocket = new Socket(routerAddress.getHostAddress(), clientAsServerPortNumber);

			System.out.println("Setting up a peer connection at :" + clientAsServersocket.getInetAddress().getHostAddress());

			ObjectOutputStream clientAsServerOOS = new ObjectOutputStream(clientAsServersocket.getOutputStream());
			ObjectInputStream clientAsServerOIS = new ObjectInputStream(clientAsServersocket.getInputStream());

			clientAsServerOOS.writeObject(fileName);
			int readBytes = (int) clientAsServerOIS.readObject();

			// System.out.println("Number of bytes that have been transferred are
			// ::"+readBytes);

			// Reading the file data from the one peer and saving it to a file on the
			// requesting peer's machine.
			byte[] b = new byte[readBytes];
			clientAsServerOIS.readFully(b);
			OutputStream fileOPstream = new FileOutputStream(directoryPath + "//" + fileName);

			@SuppressWarnings("resource")

			BufferedOutputStream BOS = new BufferedOutputStream(fileOPstream);
			BOS.write(b, 0, (int) readBytes);

			System.out.println(
					"Requested file - " + fileName + ", has been downloaded to your desired directory " + directoryPath);
			System.out.println(" ");
			System.out.println("Display file " + fileName);

			BOS.flush();
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
