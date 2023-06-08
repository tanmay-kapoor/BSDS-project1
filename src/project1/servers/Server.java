package project1.servers;

import java.io.IOException;

import project1.Features;

/**
 * Interface that defines the methods a Server should perform whether it is TCP or UDP.
 */
public interface Server extends Features {

  /**
   * Reads a file called contents.json and populates the hashmap with the
   * key-value pairs stored in this file.
   */
  void readFromFile();

  /**
   * Checks whether the request to be processed is valid or not. Requests are processed only
   * if they match the criteria described.
   * stop should have 0 parameters, GET and DELETE request should have only 1 parameter,
   * PUT should have 2.
   *
   * @param req The request split into words as a String array.
   * @return enum that tells whether the request is of a valid format or not.
   */
  ValidationCode isValidRequest(String[] req);

  /**
   * Processes the request once it has been validated to generate the suitable response.
   *
   * @param req The request split into words as a String array.
   * @return The response to be sent to the client as a String.
   * @throws IOException in case of any errors while writing to the file when client says STOP.
   */
  String handleRequest(String[] req) throws IOException;

  /**
   * Write the contents of the hashmap into a file called contents.json for future references.
   *
   * @throws IOException in case of any errors while writing to the file when client says STOP.
   */
  void writeToFile() throws IOException;

  /**
   * Generates the suitable response if the request to be processed is not of a valid format.
   *
   * @param validationCode code of the requests that tells the type of incorrect request.
   * @return the response to be sent to the client as a String.
   */
  String handleInvalidRequest(ValidationCode validationCode);

  /**
   * Enum for association with request processing. When a request is sent to validateRequest method,
   * it returns one of the below-mentioned values indicating whether it should be processed further
   * or not.
   */
  enum ValidationCode {
    INCORRECT_PARAMETER_COUNT, INVALID_REQUEST_TYPE, VALID_REQUEST_TYPE
  }
}
