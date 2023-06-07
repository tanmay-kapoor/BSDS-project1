package servers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractServer implements Server {
  protected final String fileName;
  protected final Map<String, String> map;
  protected boolean reqStatus;

  protected abstract boolean serveRequests() throws IOException;

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
  protected UDPServer.ValidationCode isValidRequest(String[] req) {
    req[0] = req[0].toUpperCase();
    for (int i = 0; i < req.length; i++) {
      req[i] = req[i].trim();
    }

    switch (req[0]) {
      case "GET":
      case "DELETE":
        return req.length == 2 ? UDPServer.ValidationCode.VALID_REQUEST_TYPE : UDPServer.ValidationCode.INCORRECT_PARAMETER_COUNT;

      case "PUT":
        return req.length == 3 ? UDPServer.ValidationCode.VALID_REQUEST_TYPE : UDPServer.ValidationCode.INCORRECT_PARAMETER_COUNT;

      default:
        return UDPServer.ValidationCode.INVALID_REQUEST_TYPE;
    }
  }

  // Process the request once it has been validated.
  protected String handleRequest(String[] req) {
    reqStatus = false;

    switch (req[0]) {
      case "GET":
        if (map.containsKey(req[1])) {
          this.reqStatus = true;
          return map.get(req[1]);
        }
        return "Invalid request. Can't get key that doesn't exist.";

      case "PUT":
        map.put(req[1], req[2]);
        reqStatus = true;
        return "put successful";

      case "DELETE":
        if (map.containsKey(req[1])) {
          map.remove(req[1]);
          reqStatus = true;
          return "delete successful";
        }
        return "Invalid request. Can't delete key that doesnt exist.";

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

  // Helper method to print request that the server needs to serve.
  protected void showRequest(String req) {
    System.out.println("\nREQ to process: " + req);
  }

  // Helper method to print response that the server will send to the client.
  protected void showResponse(String res) {
    System.out.println("RES to send: " + res);
  }

  // Helper method to print any errors that happen while the server is running.
  protected void showError(String msg) {
    System.out.println("ERROR: " + msg);
  }

  // Helper method to print any info to be read by the user
  protected void showInfo(String msg) {
    System.out.print(msg);
  }

  protected String prepareDataToSend(String[] req) {
    String res = handleRequest(req);
    if (!reqStatus) {
      long timestamp = System.currentTimeMillis();
      String msg = "timestamp=" + timestamp + ". " + res;
      showError(msg);
    } else {
      showResponse(res);
    }
    return res;
  }

  protected void handleInvalidRequest(ValidationCode validationCode) {
    long timestamp = System.currentTimeMillis();
    String res = "timestamp=" + timestamp + ". Malformed request, ";
    if (validationCode == ValidationCode.INCORRECT_PARAMETER_COUNT) {
      res += "incorrect parameter count";
    } else {
      res += "invalid request type. Must be GET, PUT or DELETE only.";
    }
    showResponse(res);
  }

  /**
   * Enum for association with request processing. When a request is sent to validateRequest method,
   * it returns one of the below-mentioned values indicating whether it should be processed further
   * or not.
   */
  protected enum ValidationCode {
    INCORRECT_PARAMETER_COUNT, INVALID_REQUEST_TYPE, VALID_REQUEST_TYPE
  }

  public void start() throws IOException {
    populateMap();
    while (true) {
      try {
        if (serveRequests()) {
          break;
        }
      } catch (IOException e) {
        showError("Connection lost");
        break;
      }
    }

    try {
      writeToFile();
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
      System.out.println("ERROR: " + e.getMessage());
    }
  }
}
