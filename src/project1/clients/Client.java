package project1.clients;

import java.io.IOException;

import project1.Features;

/**
 * Interface that defines the methods a Client should perform whether it is TCP or UDP.
 */
public interface Client extends Features {
  void serveRequests(String request) throws IOException;
}
