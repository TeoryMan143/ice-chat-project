package chat.server;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class ServerMain {
  public static void main(String[] args) {
    int exitCode = 0;

    try (Communicator com = Util.initialize(args)) {
      ObjectAdapter adapter = com.createObjectAdapterWithEndpoints("ChatAdapter", "default -p 5000");
      ChatRoomImp serv = new ChatRoomImp();

      adapter.add(serv, Util.stringToIdentity("ChatService"));
      adapter.activate();

      System.out.println(" ================================================= ");
      System.out.println(" SERVIDOR ZEROC ICE INITIALIZED SUCCESSFULLY ");
      System.out.println(" TCP Port : 10000 | Endpoint : default -p 10000 ");
      System.out.println(" Service Identity : ChatService ");
      System.out.println(" ================================================= ");
      System.out.println(" Waiting for connection from the clients... ");

      com.waitForShutdown();
    } catch (Exception e) {
      System.err.println("[SERVER ERROR] Critical error" + e.getMessage());
      e.printStackTrace();
      exitCode = 1;
    }
    System.exit(exitCode);
  }
}
