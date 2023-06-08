package project1.clients;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Class the simulates a client that interacts witch the serve using TCP connection.
 */
public class TCPClient extends AbstractClient {
  private Socket clientSocket;
  private DataInputStream din;
  private DataOutputStream dout;

  /**
   * Constructor to initialize the client socket, ip address of host, destination port number
   * and the timout limit.
   *
   * @param name    host name
   * @param port    destination port number
   * @param timeout time limit in milli-sec to wait before saying socket is not receiving back a response.
   */
  public TCPClient(String name, int port, int timeout) throws IOException {
    InetAddress ip = InetAddress.getByName(name);
    this.clientSocket = new Socket(ip, port);
    showInfo("Connection established\n\n");
    clientSocket.setSoTimeout(timeout);

    din = new DataInputStream(clientSocket.getInputStream());
    dout = new DataOutputStream(clientSocket.getOutputStream());
  }

  private TCPClient() {
  }

  @Override
  protected void handleRequestsAndResponses(String request) throws IOException {
    dout.writeUTF(request);
    dout.flush();

    String res = din.readUTF();
    showResponse(res);
  }

  @Override
  protected void closeEverything() {
    try {
      din.close();
      dout.close();
      clientSocket.close();
    } catch (IOException e) {
      showError(e.getMessage());
    }
  }

  @Override
  protected Client getClientInstance(String name, int port, int timeout) throws IOException {
    return new TCPClient(name, port, timeout);
  }

  /**
   * Driver method that is the entry point of the program.
   * This method is executed when we run the program where it validates the cli arguments received
   * and calls the required methods to proceed further in the program execution.
   *
   * @param args String array for command line arguments to be passed when running the program.
   *             For this program this array should have exactly 2 elements which are the
   *             name and port number respectively for the client to run on.
   */
  public static void main(String[] args) {
    new TCPClient().validateArgs(args);
  }
}
