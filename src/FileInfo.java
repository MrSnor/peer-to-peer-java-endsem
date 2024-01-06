import java.io.Serializable;

@SuppressWarnings("serial")

/**
 * Represents information about a file.
 */
public class FileInfo implements Serializable {
	public int peerid;
	public String fileName;
	public int portNumber;

	/**
	 * Default constructor for FileInfo.
	 */
	public FileInfo() {
	}

	/**
	 * Constructor for FileInfo.
	 * 
	 * @param fileName   The name of the file.
	 * @param peerid     The ID of the peer.
	 * @param portNumber The port number.
	 */
	public FileInfo(String fileName, int peerid, int portNumber) {
		this.peerid = peerid;
		this.fileName = fileName;
		this.portNumber = portNumber;
	}
}