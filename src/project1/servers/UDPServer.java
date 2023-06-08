package project1.servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class that simulates a server for UDP data transmission.
 * This class' purpose is to receive and process requests that are sent by the client.
 * This class can read json files to populate a hashmap, perform functions to get, put or delete
 * key-value pairs and also write the results into a json file to save it for future references.
 * It simulates a server that uses UDP protocol.
 */
public class UDPServer extends AbstractServer {
  private InetAddress ip;
  private int clientPort;
  private DatagramSocket serverSocket;

  /**
   * Constructor to initialize the file name to read json data from, map that stores the previous
   * and future key-value pairs and the request status as a boolean for each incoming request
   * which denotes whether a request has been processed or not which in turn helps in displaying
   * the proper message to the user.
   *
   * @param port port to use
   * @throws IOException in case of errors which creating the socket.
   */
  public UDPServer(int port) throws IOException {
    super();
    this.serverSocket = new DatagramSocket(port);
    showInfo("Server running\n\n");
  }

  private UDPServer() {
  }

  @Override
  protected boolean handleServeRequestError(Exception e) {
    showError(e.getMessage());
    return false;
  }

  @Override
  protected String getIp() {
    return String.valueOf(this.ip);
  }

  @Override
  protected String getPort() {
    return Integer.toString(this.clientPort);
  }

  @Override
  protected String receiveDataFromClient() throws IOException {
    byte[] receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    serverSocket.receive(receivePacket);
    ip = receivePacket.getAddress();
    clientPort = receivePacket.getPort();
    return new String(receivePacket.getData()).trim();
  }

  @Override
  protected void sendDataToClient(String res) throws IOException {
    byte[] sendData = res.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, clientPort);
    serverSocket.send(sendPacket);
  }

  @Override
  protected void closeEverything() {
    serverSocket.close();
  }

  @Override
  protected Server getServerInstance(int port) throws IOException {
    return new UDPServer(port);
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
    new UDPServer().validateArgs(args);
  }
}