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
    showInfo("Server running\n");
  }

  private TCPServer() {
  }

  @Override
  protected boolean serveRequests() throws IOException {
    String request = din.readUTF();
    showRequest(request);

    // exit program if client says stop
    if (request.trim().equalsIgnoreCase("stop")) {
      return true;
    }

    String[] req = request.split("\\s+");

    ValidationCode validationCode = isValidRequest(req);
    if (validationCode == UDPServer.ValidationCode.VALID_REQUEST_TYPE) {
      String res = prepareDataToSend(req);
      dout.writeUTF(res);
      dout.flush();
    } else {
      handleInvalidRequest(validationCode);
    }
    return false;
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
