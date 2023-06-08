package servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends AbstractServer {
  private ServerSocket serverSocket;
  private DataInputStream din;
  private DataOutputStream dout;
  private Socket socket;

  public TCPServer(int port) throws IOException {
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
  protected String receiveDataFromClient() throws IOException {
    return din.readUTF().trim();
  }

  @Override
  protected void sendDataToClient(String res) throws IOException {
    dout.writeUTF(res);
    dout.flush();
  }

  @Override
  protected void closeEverything() throws IOException {
    din.close();
    dout.flush();
    dout.close();
    socket.close();
    serverSocket.close();
  }

  @Override
  protected Server getServerInstance(int port) throws IOException {
    return new TCPServer(port);
  }

  public static void main(String[] args) {
    new TCPServer().validateArgs(args);
  }
}
