package project1;

public interface Features {

  void validateArgs(String[] args);

  /**
   * Method to start the client/server and start listening for requests, send to/from the server
   * and receive responses.
   */
  void start();

  void showRequest(String req);

  void showResponse(String res);

  void showError(String msg);

  void showInfo(String msg);

  void close();
}
