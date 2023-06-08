package servers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractServer implements Server {
  protected final String fileName;
  protected final Map<String, String> map;
  protected boolean reqStatus;

  protected abstract String receiveDataFromClient() throws IOException;

  protected abstract void sendDataToClient(String res) throws IOException;

  protected abstract boolean handleServeRequestError(Exception e);

  protected abstract void closeEverything() throws IOException;

  protected abstract Server getServerInstance(int port) throws IOException;

  protected AbstractServer() {
    this.fileName = "contents.json";
    this.map = new HashMap<>();
    this.reqStatus = false;
  }

  // Helper method to read json file and populate the key-value pairs into the map.
  protected void populateMap() {
    try {
      FileReader reader = new FileReader(fileName);
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
      JSONArray data = (JSONArray) jsonObject.get("data");
      for (Object pair : data) {
        JSONObject jsonPair = (JSONObject) pair;
        String key = (String) jsonPair.get("key");
        String value = (String) jsonPair.get("value");
        map.put(key, value);
      }
    } catch (FileNotFoundException ignored) {
      // file does not exist. But hashmap is already initialized hence ignore.
    } catch (IOException | ParseException e) {
      showError(e.getMessage());
    }
  }

  //  Checks whether request from client is valid or not.
  //  GET and DELETE request should have only 1 parameter, PUT should have 2.
  protected ValidationCode isValidRequest(String[] req) {
    req[0] = req[0].toUpperCase();
    for (int i = 0; i < req.length; i++) {
      req[i] = req[i].trim();
    }

    switch (req[0]) {
      case "STOP":
        return req.length == 1 ? ValidationCode.VALID_REQUEST_TYPE : ValidationCode.INCORRECT_PARAMETER_COUNT;

      case "GET":
      case "DELETE":
        return req.length == 2 ? ValidationCode.VALID_REQUEST_TYPE : ValidationCode.INCORRECT_PARAMETER_COUNT;

      case "PUT":
        return req.length == 3 ? ValidationCode.VALID_REQUEST_TYPE : ValidationCode.INCORRECT_PARAMETER_COUNT;

      default:
        return ValidationCode.INVALID_REQUEST_TYPE;
    }
  }

  // Process the request once it has been validated.
  protected String handleRequest(String[] req) throws IOException {
    reqStatus = true;

    switch (req[0]) {
      case "GET":
        if (map.containsKey(req[1])) {
          return map.get(req[1]);
        }
        reqStatus = false;
        return "Invalid request. Can't get key that doesn't exist.";

      case "PUT":
        map.put(req[1], req[2]);
        return "put successful";

      case "DELETE":
        if (map.containsKey(req[1])) {
          map.remove(req[1]);
          return "delete successful";
        }
        reqStatus = false;
        return "Invalid request. Can't delete key that doesnt exist.";

      case "STOP":
        writeToFile();
        return "Updated " + fileName + " with latest data.";

      default:
        return "never gonna happen";
    }
  }

  // Write contents of hashmap to contents.json file when program has finished executing.
  protected void writeToFile() throws IOException {
    JSONObject jsonObject = new JSONObject();
    JSONArray data = new JSONArray();
    for (String key : map.keySet()) {
      JSONObject details = new JSONObject();
      details.put("key", key);
      details.put("value", map.get(key));
      data.add(details);
    }
    jsonObject.put("data", data);

    FileWriter file = new FileWriter(fileName);
    file.write(jsonObject.toJSONString());
    file.close();
  }

  protected String getTimestamp() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return "[" + timestamp + "]";
  }

  // Helper method to print request that the server needs to serve.
  protected void showRequest(String req) {
    System.out.println(getTimestamp() + " REQ to process: " + req);
  }

  // Helper method to print response that the server will send to the client.
  protected void showResponse(String res) {
    System.out.println(getTimestamp() + " RES to send: " + res);
  }

  // Helper method to print any errors that happen while the server is running.
  protected void showError(String msg) {
    System.out.println(getTimestamp() + " ERROR: " + msg);
  }

  // Helper method to print any info to be read by the user
  protected void showInfo(String msg) {
    System.out.print(msg);
  }

  protected String handleInvalidRequest(ValidationCode validationCode) {
    String res = "Malformed request, ";
    if (validationCode == ValidationCode.INCORRECT_PARAMETER_COUNT) {
      res += "incorrect parameter count";
    } else {
      res += "invalid request type. Must be GET, PUT, DELETE or STOP only.";
    }
    return res;
  }

  /**
   * Enum for association with request processing. When a request is sent to validateRequest method,
   * it returns one of the below-mentioned values indicating whether it should be processed further
   * or not.
   */
  protected enum ValidationCode {
    INCORRECT_PARAMETER_COUNT, INVALID_REQUEST_TYPE, VALID_REQUEST_TYPE
  }

  public void start() {
    populateMap();

    while (true) {
      try {
        String request = receiveDataFromClient();
        showRequest(request);
        String[] req = request.split("\\t+");

        ValidationCode validationCode = isValidRequest(req);
        String res;
        if (validationCode == ValidationCode.VALID_REQUEST_TYPE) {
          res = handleRequest(req);
          if (!reqStatus) {
            showError(res);
          } else {
            showResponse(res);
          }
        } else {
          res = handleInvalidRequest(validationCode);
          showResponse(res);
        }
        sendDataToClient(res);
      } catch (IOException e) {
        boolean shouldBreak = handleServeRequestError(e);
        if (shouldBreak)
          break;
      }
    }

    try {
      closeEverything();
    } catch (IOException e) {
      showError(e.getMessage());
    }
  }

  protected void validateArgs(String[] args) {
    try {
      if (args.length != 1) {
        throw new IllegalArgumentException("Invalid number of arguments. Should be exactly 1.");
      }

      int port = Integer.parseInt(args[0]);
      if (port < 0 || port > 65535) {
        throw new IllegalArgumentException("Invalid port number. Must be in range 0-65535.");
      }

      Server server = getServerInstance(port);
      server.start();
    } catch (IOException | IllegalArgumentException e) {
      System.out.println(getTimestamp() + "ERROR: " + e.getMessage());
    }
  }
}
