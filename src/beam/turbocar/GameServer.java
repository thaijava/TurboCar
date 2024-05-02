package beam.turbocar;


import java.io.*;
import java.net.*;
import java.util.*;


public class GameServer extends Thread {

    String hostName = "localhost";
    static final int PORT = 8888;
    GameMap gameMap;

    Collection<CommandProcessor> carList = new ArrayList<>();

    Rule rule;

    public GameServer(GameMap gameMap) throws IOException {
        this.gameMap = gameMap;
        rule = new Rule(this);

    }


    public GameMap getGameMapObject() {
        return gameMap;
    }

    public void run() {                                     /////////////////        run() of MapServer


        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println(">>>> GAME SERVER STARTED..." + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                System.out.println(">>>> GAMESERVER HANDLE CONNECTION: " + clientSocket);

                CommandProcessor ccc = new CommandProcessor(clientSocket, this);
                ccc.start();

                carList.removeIf(e -> e.activeFlag == false);
                System.gc();

                System.out.println("\n Active processor:" + carList.size());

            } catch (Exception e) {
                System.out.println(">>>> MAPSERVER:  LOOP ACCEPT CONNECTION ERROR.");
                e.printStackTrace();
            }
        }
    }

    public String getHost() {
        return hostName;
    }

    public static void main(String[] args) {

        try {
            GameMap map = new GameMap();
            GameServer server = new GameServer(map);
            server.start();

            Socket socketToHost = new Socket("192.168.0.138", 8888);

            ObjectOutputStream ooOut = new ObjectOutputStream(socketToHost.getOutputStream());

            ooOut.writeObject(Command.COMMAND_REG_CAR + ", 100, 111, 222");

            System.out.println("Write success");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
