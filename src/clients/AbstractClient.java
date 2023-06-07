package clients;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

abstract class AbstractClient implements Client {
  protected final Scanner sc;

  protected abstract void handleRequestsAndResponses(String request) throws IOException;

  protected abstract void closeEverything();

  protected abstract Client getClientInstance(String name, int port, int timeout) throws IOException;

  protected AbstractClient() {
    sc = new Scanner(System.in);
  }

  // Helper method to print response that the client receives from the server.
  protected void showResponse(String res) {
    System.out.println("RES received: " + res);
  }

  // Helper method to print any errors during program execution.
  protected void showError(String msg) {
    System.out.println("ERROR: " + msg);
  }

  // Helper method to print any info to be read by the user
  protected void showInfo(String msg) {
    System.out.print(msg);
  }

  protected void validateArgs(String[] args) {
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
      System.out.println("ERROR: " + e.getMessage());
    }
  }

  // Handles all logic to keep the socket open, make requests to the server, receive responses
  // from the server and display to the user and also show errors that may occur or in case
  // the socket times out before receiving a response. Keeps running indefinitely until external
  // intervention.
  @Override
  public void start() {
    showInfo("All valid request formats:\n\n1. GET x\n2. PUT x y\n3. DELETE x\nType stop to exit\n");

    while (true) {
      showInfo("\nREQ to send: ");
      String request = sc.nextLine();
      if (request.trim().equalsIgnoreCase("stop")) {
        break;
      }

      try {
        handleRequestsAndResponses(request);
      } catch (SocketTimeoutException e) {
        showError(e.getMessage() + ". Check server log or server might be down.");
      } catch (IOException e) {
        showError(e.getMessage());
      }
    }
    closeEverything();
  }
}
