package clients;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient extends AbstractClient {
  private Socket clientSocket;
  private DataInputStream din;
  private DataOutputStream dout;

  public TCPClient(String name, int port, int timeout) throws IOException {
    super();
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

  public static void main(String[] args) {
    new TCPClient().validateArgs(args);
  }
}
