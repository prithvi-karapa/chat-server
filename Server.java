import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;

public class Server {
	private static final int PORT_NUMBER = 4000; //TODO do this a better way

	public static void main(String[] args) {
    try {
      ServerSocket listeningServer = new ServerSocket(PORT_NUMBER); //We need the ServerSocket to detect the incoming connections
      Socket socket = listeningServer.accept(); //We need this socket to facilitate communication
      System.out.println(socket);

      DataInputStream inputSteam = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

      while(true) {
        String data;
        data = inputSteam.readLine();
        if (data.equals("exit")) {
          socket.close();
          inputSteam.close();
          break;
        }

        System.out.println(data);
      } 
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
