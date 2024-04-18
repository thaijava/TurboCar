package beam.turbocar;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;

class CommandProcessor extends Thread {
    public static int PS_ID;
    private Socket clientSocket;
    GameServer server;

    Car car;

    int pid;

    boolean isActive = true;

    public CommandProcessor(Socket clientSocket, GameServer gameServer) {
        this.clientSocket = clientSocket;
        this.server = gameServer;

        pid = PS_ID;
        PS_ID++;
    }

    @Override
    public void run() {   //  run of CommandProcessor
        Command retCommand;

        try {
            ObjectOutputStream ooOut;
            ObjectInputStream ooIn = new ObjectInputStream(clientSocket.getInputStream());
            Command cc = (Command) ooIn.readObject();
            System.out.println(cc.command);
            while (cc.command != null) {
                switch (cc.command) {
                    case GameServer.COMMAND_REQ_MAP:

                        System.out.println("request map success");
                        break;

                    case GameServer.COMMAND_REG_CAR:
                        car = (Car) cc.p1;
                        Random r = new Random();
                        int randomCol = r.nextInt(server.getGameMapObject().getColumnSize());
                        int randomRow = r.nextInt(server.getGameMapObject().getRowSize());

                        while (!server.getGameMapObject().isBackgroundType(randomRow, randomCol)) {
                            randomCol = r.nextInt(server.getGameMapObject().getColumnSize());
                            randomRow = r.nextInt(server.getGameMapObject().getRowSize());

                        }
                        car.setLocation(randomRow, randomCol);

                        server.carList.add(this);

                        retCommand = new Command("register success.", car, server.getGameMapObject().getMapData(), null);
                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;

                    case GameServer.COMMAND_MOVE_TO:
                        long id = (long) cc.p1;
                        int row = (int) cc.p2;
                        int col = (int) cc.p3;
                        int headAngle = (int) cc.p4;
                        System.out.println("car moving ID:" + id + " row:" + row + " col:" + col);
                        this.car.setLocation(row, col);
                        this.car.headAngle = headAngle;
                        break;

                    case GameServer.COMMAND_GET_ALL_FRIENDS:
                        System.out.println("GET FULLY FRIENDS");
                        Car carList[] = this.extractCar();
                        retCommand = new Command("success", carList, null, null);
                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;

                    case GameServer.COMMAND_GET_FRIENDS:
                        System.out.println("HELLO GET FRIEND");
                        CarPos[] carPosList = this.extractCarPos();
                        retCommand = new Command("success", carPosList, null, null);

                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;
                    default:
                        System.out.println(">>>> MAP SERVER: UNKNOWN COMMAND, " + cc.command);

                }

                cc = (Command) ooIn.readObject();
            }
        } catch (IOException e) {
            System.out.println("Player disconnect. " + pid);
        } catch (ClassNotFoundException e) {
            System.out.println(">>>> CommandProcessor: HANDLE CLIENT COMMAND FAIL: Class not found." + pid);
            System.out.println(" fail class not found");
        }

        isActive = false;
    }

    private CarPos[] extractCarPos() {
        Iterator<CommandProcessor> i = server.carList.iterator();
        CarPos ret[] = new CarPos[server.carList.size()];
        int k = 0;
        while (i.hasNext()) {
            CommandProcessor e = i.next();
            if (e.isActive) {
                ret[k++] = e.car.getPos();
            }
        }

        return ret;
    }
    private Car[] extractCar() {
        Iterator<CommandProcessor> i = server.carList.iterator();
        Car ret[] = new Car[server.carList.size()];
        int k = 0;
        while (i.hasNext()) {
            CommandProcessor e = i.next();
            if (e.isActive && e.car != null) {
                ret[k++] = e.car;
            }
        }

        return ret;
    }

}
