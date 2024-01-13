
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ServerDownload class represents a server that handles file downloads.
 * It extends Thread to allow for multi-threading.
 */
public class ServerDownload extends Thread {
    int peerServerPort;
    String directoryPath = null;
    ServerSocket dwldServerSocket;
    Socket dwldSocket = null;

    /**
     * Constructor for the ServerDownload class.
     * 
     * @param peerServerPort   The port number for the server to listen on.
     * @param directoryPath    The path to the directory where the downloaded files will be stored.
     */
    ServerDownload(int peerServerPort, String directoryPath) {
        this.peerServerPort = peerServerPort;
        this.directoryPath = directoryPath;
    }

    /**
     * The run method of the ServerDownload class.
     * It listens for incoming connections and starts a new thread to handle each connection.
     */
    public void run() {
        try {
            dwldServerSocket = new ServerSocket(peerServerPort);
            dwldSocket = dwldServerSocket.accept();
            new ServerDownloadThread(dwldSocket, directoryPath).start();
        } catch (IOException ex) {
            Logger.getLogger(ServerDownload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/**
 * This class represents a thread that handles server-side file downloads.
 */
class ServerDownloadThread extends Thread {
    Socket dwldThreadSocket; // The socket for the download thread
    String directoryPath; // The path to the directory where the files are stored

    /**
     * Constructs a ServerDownloadThread object.
     *
     * @param dwldThreadSocket The socket for the download thread
     * @param directoryPath The path to the directory where the files are stored
     */
    public ServerDownloadThread(Socket dwldThreadSocket, String directoryPath) {
        this.dwldThreadSocket = dwldThreadSocket;
        this.directoryPath = directoryPath;
    }

    /**
     * Runs the server-side file download logic.
     */
    @SuppressWarnings({ "unused", "resource" })
    public void run() {
        try {
            ObjectOutputStream objOS = new ObjectOutputStream(dwldThreadSocket.getOutputStream());
            ObjectInputStream objIS = new ObjectInputStream(dwldThreadSocket.getInputStream());

            String fileName = (String) objIS.readObject();
            String fileLocation; // Stores the directory name
            
            while (true) {
                File myFile = new File(directoryPath + "//" + fileName);
                long length = myFile.length();

                byte[] byte_arr = new byte[(int) length];

                objOS.writeObject((int) myFile.length());
                objOS.flush();

                FileInputStream FIS = new FileInputStream(myFile);
                BufferedInputStream objBIS = new BufferedInputStream(FIS);
                objBIS.read(byte_arr, 0, (int) myFile.length());

                // System.out.println("Sending the file of " +byte_arr.length+ " bytes");

                objOS.write(byte_arr, 0, byte_arr.length);

                objOS.flush();
            }
        } catch (Exception e) {

        }
    }
}