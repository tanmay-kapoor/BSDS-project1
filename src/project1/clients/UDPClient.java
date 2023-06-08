package project1.clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class that simulates a client for UDP data transmission.
 * This class' purpose is to send requests to the server, and inform the user of the response
 * sent back by the server.
 * This class allows users to send requests to the server like get, put or delete requests and
 * the client keeps running until the user explicitly intervenes or enters stop (case-insensitive).
 * It simulates a client that uses UDP protocol.
 */
public class UDPClient extends AbstractClient {
  private DatagramSocket clientSocket;
  private InetAddress ip;
  private final int port;

  /**
   * Constructor to initialize the client socket, ip address of host, destination port number
   * and the timout limit.
   *
   * @param name    host name
   * @param port    destination port number
   * @param timeout time limit in milli-sec to wait before saying socket is not receiving back a response.
   */
  public UDPClient(String name, int port, int timeout) throws IOException {
    super();
    this.port = port;
    this.clientSocket = new DatagramSocket();
    clientSocket.setSoTimeout(timeout);
    this.ip = InetAddress.getByName(name);
  }

  private UDPClient() {
    this.port = 0;
  }

  @Override
  protected void handleRequestsAndResponses(String request) throws IOException {
    byte[] sendData;
    byte[] receiveData = new byte[1024];
    sendData = request.getBytes();

    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
    clientSocket.send(sendPacket);

    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    showResponse(new String(receivePacket.getData()));
  }

  @Override
  protected void closeEverything() {
    clientSocket.close();
  }

  @Override
  protected Client getClientInstance(String name, int port, int timeout) throws IOException {
    return new UDPClient(name, port, timeout);
  }

  /**
   * Driver method that is the entry point of the program.
   * This method is executed when we run the program where it validates the cli arguments received
   * and calls the required methods to proceed further in the program execution.
   *
   * @param args String array for command line arguments to be passed when running the program.
   *             For this program this array should have exactly 2 elements which are the
   *             name and port number respectively for the client to run on.
   * @throws IllegalArgumentException in case of incorrect number of cli arguments or incorrect
   *                                  values for name and port number.
   */
  public static void main(String[] args) {
    new UDPClient().validateArgs(args);
  }
}
