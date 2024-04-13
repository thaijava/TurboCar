package beam.rocketcar;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;

class CommandProcessor extends Thread {
    public static int PS_ID;
    private Socket clientSocket;
    MapServer mapServer;

    Car car;

    int pid;

    boolean isActive = true;

    public CommandProcessor(Socket clientSocket, MapServer mapServer) {
        this.clientSocket = clientSocket;
        this.mapServer = mapServer;

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
                    case MapServer.COMMAND_REQ_MAP:
//                        ooOut =  new ObjectOutputStream(clientSocket.getOutputStream());
//                        int xxx[][] = mapServer.getGameMapObject().getMapData();
//                        ooOut.writeObject(xxx);
                        System.out.println("request map success");
                        break;

                    case MapServer.COMMAND_REG_CAR:
                        car = new Car();
                        Random r = new Random();
                        int randomCol = r.nextInt(mapServer.getGameMapObject().getColumnSize());
                        int randomRow = r.nextInt(mapServer.getGameMapObject().getRowSize());

                        while (!mapServer.getGameMapObject().isBackgroundType(randomRow, randomCol)) {
                            randomCol = r.nextInt(mapServer.getGameMapObject().getColumnSize());
                            randomRow = r.nextInt(mapServer.getGameMapObject().getRowSize());

                        }
                        car.setLocation(randomRow, randomCol);

                        mapServer.carList.add(this);
                        mapServer.getGameMapObject().getMapData()[1][r.nextInt(10)] = 1;
                        retCommand = new Command("register success.", car, mapServer.getGameMapObject().getMapData(), null);
                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;

                    case MapServer.COMMAND_MOVE_TO:
                        long id = (long) cc.p1;
                        int row = (int) cc.p2;
                        int col = (int) cc.p3;
                        System.out.println("commandProcessor car move to ID:" + id + " row:" + row + " col:" + col);
                        break;

                    case MapServer.COMMAND_GET_FRIENDS:
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
            System.out.println(">>>> CommandProcessor: HANDLE CLIENT COMMAND FAIL:" + pid);
            System.out.println(" fail CommandProcessor");
        } catch (ClassNotFoundException e) {
            System.out.println(">>>> CommandProcessor: HANDLE CLIENT COMMAND FAIL: Class not found." + pid);
            System.out.println(" fail class not found");
        }

        isActive = false;
    }

    private CarPos[] extractCarPos() {

        Iterator<CommandProcessor> i = mapServer.carList.iterator();

        CarPos ret[] = new CarPos[mapServer.carList.size()];
        int k = 0;
        while (i.hasNext()) {
            CommandProcessor e = i.next();
            if (e.isActive) {
                ret[k++] = e.car.getPos();
            }
        }

        return ret;
    }

}
