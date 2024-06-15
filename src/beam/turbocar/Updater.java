package beam.turbocar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Updater extends Thread {
    public static boolean BLINK_FLAG = true;

    Socket socketToHost;
    Car car;

    private final TurboCarComponent turboCarComponent;
  //  private Car car;
    private Car[] carList;
    private CarPos[] carPosList;

    private final HashMap<Long, BufferedImage> outfitCache = new HashMap<>();

    boolean offlineFlag = false;

    ObjectOutputStream ooOut;
    ObjectInputStream ooIn;

    int[][] mapData;

    private int gameState;

    private ArrayList<BlockOfChar>    blockList;

    public Updater(Socket socketToHost, TurboCarComponent turboCarComponent ) throws IOException {
        this.turboCarComponent = turboCarComponent;
        this.socketToHost = socketToHost;

        ooOut = new ObjectOutputStream(socketToHost.getOutputStream());

    }

    public String getGameState() {
        switch (gameState) {
            case Rule.STATE_END:
                return "Game End.";
            case Rule.STATE_COUNTDOWN:
                return "Count down...";

            case Rule.STATE_RUNNING:
                return "Running...";
        }

        return "unknown state.";
    }

    private  void command_carMoveTo() throws IOException, ClassNotFoundException {

        while (socketToHost == null) {                                // wait until socket ready
            try {
                System.out.println("  car move to wait socket...");
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(Command.COMMAND_MOVE_TO, car.getRow(), car.getColumn(), car.getHeadAngle()));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());
        Command retCommand = (Command) ooIn.readObject();
        String result = retCommand.command;
        if (result.equals("hit"))  SoundServer.playDing();

    }

    private Car[] command_getFullyFriends() throws IOException, ClassNotFoundException {
        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(Command.COMMAND_GET_ALL_FRIENDS, null, null, null));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());
        Command retCommand = (Command) ooIn.readObject();

        Car[] carList = (Car[]) retCommand.p1;
        int remainTime = (int) retCommand.p2;
        String currentVocab = (String) retCommand.p3;
        turboCarComponent.updateScreen(remainTime, currentVocab);


        System.out.println("======= player list =======");
        for (Car c : carList) {
            System.out.println("     " + c);
        }
        System.out.println("===========================");

        return carList;
    }

    private CarPos[] command_getFriends() throws IOException, ClassNotFoundException {
        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(Command.COMMAND_GET_FRIENDS, null, null, null));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());
        Command retCommand = (Command) ooIn.readObject();

        CarPos[] carPosList = (CarPos[]) retCommand.p1;      // p1: is list of car position
        gameState = (int) retCommand.p2;                     // p2: is game state.
        blockList = (ArrayList<BlockOfChar>) retCommand.p3;          // P3: is current Block

//        System.out.println("/////// position list //////");
//        for (CarPos c : carPosList) {
//            System.out.println(c);
//        }
//        System.out.println("///////////////////////////");

        return carPosList;
    }

    public Car command_registerCar() throws IOException, ClassNotFoundException {

        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        this.car = new Car();
        Car localCar = this.car;

        ooOut.writeObject(new Command(Command.COMMAND_REG_CAR, this.car, null, null));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());
        Command retCommand = (Command) ooIn.readObject();                   //// read object

        Car retCar = (Car) retCommand.p1;
        localCar.setLocation(retCar.getRow(), retCar.getColumn());
        localCar.id = retCar.getId();

        mapData = (int[][]) retCommand.p2;

        this.car = localCar;
        return localCar;
    }

    public void command_bye() throws IOException {
        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(Command.COMMAND_BYE_BYE, null, null, null));
    }

    public boolean command_restartGame() throws IOException, ClassNotFoundException {
        while (socketToHost == null) {                                // wait until socket ready
            try {
                sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ooOut.writeObject(new Command(Command.COMMAND_START_GAME, null, null, null));
        ooIn = new ObjectInputStream(socketToHost.getInputStream());
        Command retCommand = (Command) ooIn.readObject();
        String ans = retCommand.command;
        return !ans.equals("error");
    }

    public void run() {
        int oldRow = turboCarComponent.car.getRow();
        int oldCol = turboCarComponent.car.getColumn();

        int delayTick1 = 0;
        int delayTick2 = 0;
        int blinkDelay = 0;

        try {
            while (!offlineFlag) {
                try {

                    if (oldRow != turboCarComponent.car.getRow() || oldCol != turboCarComponent.car.getColumn()) {
                        this.command_carMoveTo();
                        oldCol = turboCarComponent.car.getColumn();
                        oldRow = turboCarComponent.car.getRow();

                    }
                    sleep(10);
                    delayTick1++;
                    delayTick2++;
                    blinkDelay++;

                    if (delayTick1 > 50) {
                        this.carList = this.command_getFullyFriends();   //
                        buildOutfitCache();
                        delayTick1 = 0;

                    }

                    if (delayTick2 > 10) {
                        this.carPosList = this.command_getFriends();    //
                        delayTick2 = 0;
                    }

                    if(blinkDelay >10) {
                        blinkDelay = 0;
                        BLINK_FLAG = !BLINK_FLAG;
                    }

                } catch (IOException e) {
                    System.out.println("!!! Server down...");
                    e.printStackTrace();
                    offlineFlag = true;

                } catch (ClassNotFoundException e) {
                    System.out.println("!!! updater.run()  object transfer error...");
                    offlineFlag = true;

                }
            }
        } catch (InterruptedException ex) {
            System.out.println("!!! update thread.run io error...");
        }
    }

    private void buildOutfitCache() {
        for (Car c : carList) {
            if (c != null) {
                if (outfitCache.get(c.getId()) == null)
                    outfitCache.put(c.getId(), c.getImage());
            }
        }
    }


    public Car[] getCarList() {
        return carList;
    }

    public CarPos[] getCarPosList() {
        return carPosList;
    }

    public BufferedImage getOutfit(long id) {
        return outfitCache.get(id);
    }

    public void drawItems(Graphics g, double rescaleX, double rescaleY, int carSizeX, int carSizeY) {

        if (carPosList == null) return;
        for (CarPos cp : carPosList) {   // draw multiplayer car
            if (cp == null) continue;
            if (cp.getId() == car.getId()) continue;
            BufferedImage img = outfitCache.get(cp.getId());
            if (img == null) continue;

            BufferedImage goodImage = Car.rotate(img, cp.headAngle);

            g.drawImage(goodImage, (int) (cp.column * TurboCarComponent.TILE_SIZE / rescaleX),
                    (int) (cp.row * TurboCarComponent.TILE_SIZE / rescaleY),
                    carSizeX, carSizeY, null);
        }


        if (blockList == null) return;
        if (blockList.size() == 0) return;
        BlockOfChar currentBlock = blockList.get(0);

        currentBlock.drawImageWithBlink(g, rescaleX, rescaleY);

        if (blockList.size() >= 2) {
            for (int i = 1; i < blockList.size() ; i++) {
                currentBlock = blockList.get(i);
                currentBlock.drawImage(g, rescaleX, rescaleY);
            }
        }
    }
}
