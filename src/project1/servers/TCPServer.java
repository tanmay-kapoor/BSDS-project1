package project1.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that simulates a server for TCP data transmission.
 * This class' purpose is to receive and process requests that are sent by the client.
 * This class can read json files to populate a hashmap, perform functions to get, put or delete
 * key-value pairs and also write the results into a json file to save it for future references.
 * It simulates a server that uses TCP protocol.
 */
public class TCPServer extends AbstractServer {
  private ServerSocket serverSocket;
  private DataInputStream din;
  private DataOutputStream dout;
  private Socket socket;

  /**
   * Constructor to initialize the file name to read json data from, map that stores the previous
   * and future key-value pairs and the request status as a boolean for each incoming request
   * which denotes whether a request has been processed or not which in turn helps in displaying
   * the proper message to the user. Also, the data input/output streams for interacting with
   * sockets.
   *
   * @param port port to use
   * @throws IOException in case of errors which creating the socket.
   */
  public TCPServer(int port) throws IOException {
    super();
    serverSocket = new ServerSocket(port);
    socket = serverSocket.accept();
    din = new DataInputStream(socket.getInputStream());
    dout = new DataOutputStream(socket.getOutputStream());
    showInfo("Connection established\n\n");
  }

  private TCPServer() {
  }

  @Override
  protected boolean handleServeRequestError(Exception e) {
    showError("Connection lost");
    return true;
  }

  @Override
  protected String getIp() {
    return String.valueOf(socket.getInetAddress());
  }

  @Override
  protected String getPort() {
    return Integer.toString(socket.getPort());
  }

  @Override
  protected String receiveDataFromClient() throws IOException {
    return din.readUTF().trim();
  }

  @Override
  protected void sendDataToClient(String res) throws IOException {
    dout.writeUTF(res);
    dout.flush();
  }

  @Override
  protected void closeEverything() {
    try {
      din.close();
      dout.flush();
      dout.close();
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      showError(e.getMessage());
    }
  }

  @Override
  protected Server getServerInstance(int port) throws IOException {
    return new TCPServer(port);
  }

  /**
   * Driver method that is the entry point of the program.
   * This method is executed when we run the program where it validates the cli arguments received
   * and calls the required methods to proceed further in the program execution.
   *
   * @param args String array for command line arguments to be passed when running the program.
   *             For this program this array should have only one element which is the port number
   *             for the server.
   * @throws IllegalArgumentException in case of incorrect number of cli arguments or incorrect
   *                                  values for name and port number.
   */
  public static void main(String[] args) {
    new TCPServer().validateArgs(args);
  }
}
