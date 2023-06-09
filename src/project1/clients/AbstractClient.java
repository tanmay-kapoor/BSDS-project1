package project1.clients;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.Scanner;

abstract class AbstractClient implements Client {
  protected final Scanner sc;

  protected abstract void handleRequestsAndResponses(String request) throws IOException;

  protected abstract void closeEverything();

  protected abstract Client getClientInstance(String name, int port, int timeout) throws IOException;

  protected AbstractClient() {
    sc = new Scanner(System.in);
  }

  protected String getTimestamp() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return "[" + timestamp + "]";
  }

  @Override
  public void showRequest(String req) {
    System.out.println(getTimestamp() + " REQ: " + req);
  }

  @Override
  public void showResponse(String res) {
    System.out.println(getTimestamp() + " RES received: " + res);
  }

  @Override
  public void showError(String msg) {
    System.out.println(getTimestamp() + " ERROR: " + msg);
  }

  @Override
  public void showInfo(String msg) {
    System.out.print(msg);
  }

  @Override
  public void validateArgs(String[] args) {
    try {
      if (args.length != 2) {
        throw new IllegalArgumentException("Invalid number of arguments. Should be exactly 2.");
      }
      String name = args[0];
      int port = Integer.parseInt(args[1]);
      if (port < 0 || port > 65535) {
        throw new IllegalArgumentException("Invalid port number. Must be in range 0-65535.");
      }
      int timeout = 1000;

      Client client = getClientInstance(name, port, timeout);
      client.start();
    } catch (IOException | IllegalArgumentException e) {
      System.out.println(getTimestamp() + "ERROR: " + e.getMessage());
    }
  }

  @Override
  public void serveRequests(String request) throws IOException {
    handleRequestsAndResponses(request);
  }

  @Override
  public void close() {
    closeEverything();
  }

  @Override
  public void start() {
    showInfo(
            "All valid request formats:\n\n" +
                    "GET x\n" +
                    "PUT x y\n" +
                    "DELETE x\n" +
                    "STOP\n\n" +
                    "Requests are tab separated. eg : PUT \\t This is the key \\t This is the value\n");

    while (true) {
      showInfo("REQ to send: ");
      String request = sc.nextLine();
      try {
        serveRequests(request);
        if (request.trim().equalsIgnoreCase("stop")) {
          break;
        }
      } catch (SocketTimeoutException e) {
        showError(e.getMessage() + ". Check server log or server might be down.");
      } catch (IOException e) {
        showError(e.getMessage());
      }
    }

    this.close();
  }
}
