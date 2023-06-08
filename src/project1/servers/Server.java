package project1.servers;

import java.io.IOException;

import project1.Features;

public interface Server extends Features {

  void readFromFile();

  ValidationCode isValidRequest(String[] req);

  String handleRequest(String[] req) throws IOException;

  void writeToFile() throws IOException;

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
