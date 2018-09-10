package chat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Read off the socket, print messages, and exit when the socket is closed or an exception occurs
 */
public class ServerListener implements Runnable {
  Socket socket;
  Thread mainClientProcess;

  ServerListener(Socket socket, Thread mainClientProcess) {
    this.socket = socket;
    this.mainClientProcess = mainClientProcess;
  }

  @Override
  public void run() {
    ObjectInputStream inputStream;
    try {
      inputStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    while(true) {
      try {
        System.out.println(inputStream.readObject().toString());
      } catch (IOException | ClassNotFoundException e) {
        break;
      }
    }
    try {
      socket.close();
      System.in.close();
      mainClientProcess.interrupt();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
