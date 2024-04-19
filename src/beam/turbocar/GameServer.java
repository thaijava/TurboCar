package beam.turbocar;


import java.io.*;
import java.net.*;
import java.util.*;


public class GameServer extends Thread {
    static final String COMMAND_REQ_MAP = "REQ_MAP:";
    static final String COMMAND_REG_CAR = "REG_CAR:";
    static final String COMMAND_MOVE_TO = "MOVE_TO:";
    static final String COMMAND_GET_FRIENDS = "GET_FRIENDS:";
    static final String COMMAND_GET_ALL_FRIENDS = "GET_ALL_FRIENDS:";
    String hostName = "localhost";
    int port = 8888;
    Socket socketToHost;

    ObjectOutputStream ooOut;
    ObjectInputStream ooIn;
    GameMap gameMap;

    Collection<CommandProcessor> carList = new ArrayList<>();

    public GameServer(GameMap gameMap, int port) throws IOException {
        this.gameMap = gameMap;
        this.port = port;

    }


    public void connectRemote(String host, int port) throws IOException {
        if(socketToHost != null) socketToHost.close();

        hostName = host;
        socketToHost = new Socket(hostName, port);
        ooOut = new ObjectOutputStream(socketToHost.getOutputStream());

    }

    public GameMap getGameMapObject() {
        return gameMap;
    }

    public void run() {                                     /////////////////        run() of MapServer

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);

            System.out.println(">>>> GAME SERVER STARTED..." + port);
            socketToHost = new Socket(hostName, port);
            ooOut = new ObjectOutputStream(socketToHost.getOutputStream());

            System.out.println(">>>> success");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                System.out.println(">>>> GAMESERVER HANDLE CONNECTION: " + clientSocket);

                CommandProcessor ccc = new CommandProcessor(clientSocket, this);
                ccc.start();

                carList.removeIf(e -> e.isActive == false);
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

    public void carMoveTo(long id, int row, int column, int headAngle) throws IOException {

        while (socketToHost == null) {                                // wait until socket ready
            try {
                System.out.println("  car move to wait socket...");
                sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(COMMAND_MOVE_TO,  id, row, column, headAngle ));
    }

    public Command registerCar(Car car) throws IOException, ClassNotFoundException{

        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(COMMAND_REG_CAR, car, null, null));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());
        Command retCommand = (Command) ooIn.readObject();
        Car localCar = new Car();
        Car retCar = (Car)retCommand.p1;
        retCar.setImageRawData(localCar.imageByte);
        retCommand.p3 = retCar;

        return retCommand;
    }

    public Car[] getFullyFriends()  throws IOException, ClassNotFoundException {
        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(COMMAND_GET_ALL_FRIENDS, null, null, null));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());

        Command retCommand = (Command) ooIn.readObject();
        Car carList[]  = (Car[]) retCommand.p1;
        System.out.println("======= player list =======");
        for (Car c: carList) {
            System.out.println("     " + c);
        }
        System.out.println("===========================");

        return carList;
    }

    public CarPos[] getFriends() throws IOException, ClassNotFoundException {
        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(COMMAND_GET_FRIENDS, null, null, null));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());
        Command retCommand = (Command) ooIn.readObject();

        CarPos[]  carPosList = (CarPos[]) retCommand.p1;
        System.out.println("/////// position list //////");
        for (CarPos c: carPosList) {
            System.out.println(c);
        }
        System.out.println("///////////////////////////");

        return carPosList;
    }

    public static void main(String[] args) {

        try {
            GameMap map = new GameMap();
            GameServer server = new GameServer(map, 8888);
            server.start();

            Socket socketToHost = new Socket("192.168.0.138", 8888);

            ObjectOutputStream ooOut = new ObjectOutputStream(socketToHost.getOutputStream());
            ooOut.writeObject(GameServer.COMMAND_REG_CAR + ", 100, 111, 222");

            System.out.println("Write success");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
