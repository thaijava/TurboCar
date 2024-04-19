package beam.turbocar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class UpdateMeFriendAndItem extends Thread {
    GameServer gameServer;
    private Car car;
    private Car[] carList;
    private CarPos[] carPosList;

    private HashMap<Long, BufferedImage> outfitCache = new HashMap<>();

    boolean stopFlag = false;

    public UpdateMeFriendAndItem(Car car, GameServer gameServer) {
        this.gameServer = gameServer;
        this.car = car;

    }

    public void run() {
        int oldRow = car.getRow();
        int oldCol = car.getColumn();

        int delayTick1 =0;
        int delayTick2 =0;

        try {
            while (true && !stopFlag) {
                try {
                    if (oldRow != car.getRow() || oldCol != car.getColumn()) {
                        gameServer.carMoveTo(car.getId(), car.getRow(), car.getColumn(), car.getHeadAngle());
                        oldCol = car.getColumn();
                        oldRow = car.getRow();
                    }
                    sleep(50);
                    delayTick1++;
                    delayTick2++;

                    if(delayTick1 > 30) {
                        this.carList = gameServer.getFullyFriends();
                        buildOutfitCache();
                        System.out.println("build outfitcache done...");
                        delayTick1 = 0;
                    }

                    if(delayTick2 > 2) {
                        this.carPosList = gameServer.getFriends();
                        delayTick2 = 0;
                    }

                } catch (IOException e) {
                    System.out.println("!!! Server down...");
                    stopFlag = true;

                } catch (ClassNotFoundException e) {
                    System.out.println("!!! update items object transfer error...");
                    stopFlag = true;

                }
            }
        } catch (InterruptedException ex) {
            System.out.println("!!! update thread data comm error...");
        }
    }

    private void buildOutfitCache() {
        for(Car c: carList) {
            if(c!= null) {
                if(outfitCache.get(c.getId()) == null)
                 outfitCache.put(c.getId(), c.getImage());
            }
        }
    }


    public void setCar(Car c) {
        this.car = c;
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
        if(carPosList == null) return;

        for(CarPos cp: carPosList) {
            if(cp == null) continue;
            if(cp.getId() == car.getId()) continue;
            BufferedImage img = outfitCache.get(cp.getId());
            if(img == null) continue;

            BufferedImage goodImage = Car.rotate(img, cp.headAngle);
            g.drawImage(goodImage, (int) (cp.column * TurboCarComponent.TILE_SIZE/rescaleX),
                    (int) (cp.row * TurboCarComponent.TILE_SIZE/rescaleY),
                    carSizeX, carSizeY, null);
        }
    }
}
