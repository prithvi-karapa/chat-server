package chat.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
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
    while(true) {
      try {
        DataInputStream inputSteam = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        System.out.println(inputSteam.readUTF());
      } catch (IOException e) {
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
