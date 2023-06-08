package project1;

/**
 * Interface that holds the common methods to be implemented for both Client and Server
 * for TCP/UDP.
 */
public interface Features {

  /**
   * Validates the correct number of argyments provided for running the respective class
   * for TCP/UDP server/client.
   *
   * @param args the command line arguments that should be validated
   */
  void validateArgs(String[] args);

  /**
   * Method to start the client/server and start listening for requests, send to/from the server
   * and receive responses.
   */
  void start();

  /**
   * Method to display the request that is to be sent to the client or has to be
   * processed by the server.
   *
   * @param req The request as a String (eg: DELETE 75).
   */
  void showRequest(String req);

  /**
   * Method to show the response that the client receives after sending a request to server
   * or the response that the server is going to send back o the client.
   *
   * @param res The response as a String.
   */
  void showResponse(String res);

  /**
   * Method to display any errors encountered throughout the execution of all server/clients.
   *
   * @param msg The error message as a String.
   */
  void showError(String msg);

  /**
   * Method to display any additional info that may need ti be displayed to the user at any given
   * time by the server/client.
   *
   * @param msg The info as a String.
   */
  void showInfo(String msg);

  /**
   * Method to close the connections after the user has requested to exit the program,
   * i.e., client wants to leave or demolish the connection that was established.
   */
  void close();
}
