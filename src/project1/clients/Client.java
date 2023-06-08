package project1.clients;

import java.io.IOException;

import project1.Features;

/**
 * Interface that defines the methods a Client should perform whether it is TCP or UDP.
 */
public interface Client extends Features {

  /**
   * Method that sends requests to the server and listens for responses sent by the server.
   *
   * @param request The request to be sent to the server.
   * @throws IOException In case of errors during the communication between the client and server.
   */
  void serveRequests(String request) throws IOException;
}
