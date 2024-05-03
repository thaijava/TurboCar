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

    boolean stopFlag = false;

    boolean activeFlag = true;

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

            Command cc = (Command) ooIn.readObject();  // OBJECT READ COMMAND
            System.out.println(cc.command);
            while (cc.command != null && !stopFlag ) {
                switch (cc.command) {
                    case Command.COMMAND_REQ_MAP:

                        System.out.println("request map success");
                        break;

                    case Command.COMMAND_REG_CAR:
                        car = (Car) cc.p1;
                        Random r = new Random();
                        int randomCol = r.nextInt(server.getGameMapObject().getColumnSize());
                        int randomRow = r.nextInt(server.getGameMapObject().getRowSize());

                        while (!server.getGameMapObject().isBackgroundType(randomRow, randomCol)) {
                            randomCol = r.nextInt(server.getGameMapObject().getColumnSize());
                            randomRow = r.nextInt(server.getGameMapObject().getRowSize());

                        }
                        car.setLocation(randomRow, randomCol);
                        car.setName("player " + pid);

                        server.carList.add(this);

                        retCommand = new Command("register success.", car, server.getGameMapObject().getMapData(), null);
                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;

                    case Command.COMMAND_MOVE_TO:

                        int tmprow = (int) cc.p1;
                        int tmpcol = (int) cc.p2;
                        int tmphead = (int) cc.p3;

                        this.car.setLocation(tmprow, tmpcol);
                        this.car.headAngle = tmphead;

                        String result = "success";
                        BlockOfChar currentBlock = server.rule.getCurrentBlock();

                        if(currentBlock.getRow() == car.getRow() &&
                            currentBlock.getColumn() == car.getColumn()) {
                            result = "hit";
                            server.rule.consume();
                        }

                        retCommand = new Command(result, null, null, null);
                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;

                    case Command.COMMAND_GET_ALL_FRIENDS:
                        Car carList[] = this.extractCar();
                        retCommand = new Command("success", carList, server.rule.getRemainTime(), server.rule.getCurrentGameingWord());

                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;

                    case Command.COMMAND_GET_FRIENDS:
                        CarPos[] carPosList = this.extractCarPos();
                        retCommand = new Command("success", carPosList, server.rule.currentState, server.rule.getBlockList());

                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);

                        break;
                    case Command.COMMAND_START_GAME:
                        boolean success = server.rule.startNewGame();
                        if (success)
                            retCommand = new Command("success", null, null, null);
                        else
                            retCommand = new Command("error", null, null, null);

                        ooOut = new ObjectOutputStream(clientSocket.getOutputStream());
                        ooOut.writeObject(retCommand);
                        break;
                    case Command.COMMAND_BYE_BYE:
                        activeFlag = false;
                        break;
                    default:
                        System.out.println(">>>> MAP SERVER: UNKNOWN COMMAND, " + cc.command);

                }

                cc = (Command) ooIn.readObject();
            }

        } catch (IOException e) {
            System.out.println("Player disconnect. " + pid);
            e.printStackTrace();
            stopFlag = true;
        } catch (ClassNotFoundException e) {
            System.out.println(">>>> CommandProcessor: HANDLE CLIENT COMMAND FAIL: Class not found." + pid);
            System.out.println(" fail class not found");
        }

        activeFlag = false;
    }

    private CarPos[] extractCarPos() {
        Iterator<CommandProcessor> i = server.carList.iterator();
        CarPos ret[] = new CarPos[server.carList.size()];
        int k = 0;
        while (i.hasNext()) {
            CommandProcessor e = i.next();
            if (e.activeFlag) {
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
            if (e.activeFlag && e.car != null) {
                ret[k++] = e.car;
            }
        }

        return ret;
    }

}
