package beam.turbocar.ai;

import beam.turbocar.*;

import java.util.Stack;

public class AutoDrive extends Thread {
    TurboCarComponent turboCarComponent;
    GameMap gameMap;
    Car car;

    Stack<Rout> routStack = new Stack<>();

    boolean turnBack = false;

    public AutoDrive(TurboCarComponent turboCarComponent) {
        gameMap = turboCarComponent.getGameMap();
        car = turboCarComponent.getCar();
        this.turboCarComponent = turboCarComponent;
    }

    @Override
    public void run() {

        int nothing = 0;
        Rout fromStack;
        while (turboCarComponent != null) {
           if (struck()) turnBack();

            if (!routStack.empty()) {
                fromStack = routStack.pop();
                if (fromStack.row == car.getRow() && fromStack.col == car.getColumn()) {
                    switch (fromStack.direction) {
                        case Rout.DIRECTION_UP:
                            turboCarComponent.upPressed = true;
                            break;
                        case Rout.DIRECTION_DOWN:
                            turboCarComponent.downPressed = true;
                            break;
                        case Rout.DIRECTION_LEFT:
                            turboCarComponent.leftPressed = true;
                            break;
                        case Rout.DIRECTION_RIGHT:
                            turboCarComponent.rightPressed = true;
                            break;
                    }
                } else {

                    routStack.push(fromStack);
                }


            }

           // checkCrossRoad();

            try {
                sleep(50);
            } catch (InterruptedException e) {
                System.out.println("AI AutoDrive sleep error.");
            }
        }
    }

    public void checkCrossRoad() {
        int r = car.getRow();
        int c = car.getColumn();

        int rr = -1;
        int cc = -1;

        Rout fromStack;

        System.out.println(routStack.size());

        if (!routStack.empty()) {
            fromStack = routStack.peek();
            rr = fromStack.row;
            cc = fromStack.col;
        }

        if (rr==r && cc ==c)
            return;;

        if (gameMap.getMapData()[r - 1][c] == Block.TYPE_BACKGROUND) {
            routStack.push(new Rout(r, c, Rout.DIRECTION_UP));

        }
        if (gameMap.getMapData()[r + 1][c] == Block.TYPE_BACKGROUND) {
            routStack.push(new Rout(r, c, Rout.DIRECTION_DOWN));
        }
        if (gameMap.getMapData()[r][c - 1] == Block.TYPE_BACKGROUND) {
            routStack.push(new Rout(r, c, Rout.DIRECTION_LEFT));
        }
        if (gameMap.getMapData()[r][c + 1] == Block.TYPE_BACKGROUND) {
            routStack.push(new Rout(r, c, Rout.DIRECTION_RIGHT));
        }

    }

    public boolean struck() {
        if (car.getHeadAngle() == 0 && car.getRow() > 0 && gameMap.getMapData()[car.getRow() - 1][car.getColumn()] == Block.TYPE_WALL)
            return true;
        if (car.getHeadAngle() == 180 && car.getRow() < gameMap.getRowSize() - 1 && gameMap.getMapData()[car.getRow() + 1][car.getColumn()] == Block.TYPE_WALL)
            return true;
        if (car.getHeadAngle() == 90 && car.getColumn() < gameMap.getColumnSize() - 1 && gameMap.getMapData()[car.getRow()][car.getColumn() + 1] == Block.TYPE_WALL)
            return true;
        if (car.getHeadAngle() == 270 && car.getColumn() > 0 && gameMap.getMapData()[car.getRow()][car.getColumn() - 1] == Block.TYPE_WALL)
            return true;

        return false;
    }

    public void turnBack() {
        if (car.getHeadAngle() == 0) {
            turboCarComponent.upPressed = false;
            turboCarComponent.downPressed = true;
            return;
        }

        if (car.getHeadAngle() == 180) {
            turboCarComponent.upPressed = true;
            turboCarComponent.downPressed = false;
            return;
        }

        if (car.getHeadAngle() == 90) {
            turboCarComponent.leftPressed = true;
            turboCarComponent.rightPressed = false;
            return;
        }

        if (car.getHeadAngle() == 270) {
            turboCarComponent.leftPressed = false;
            turboCarComponent.rightPressed = true;
            return;
        }
    }

    public class Rout {
        public static final int DIRECTION_UP = 0;
        public static final int DIRECTION_DOWN = 1;
        public static final int DIRECTION_LEFT = 2;
        public static final int DIRECTION_RIGHT = 3;

        int row;
        int col;
        int direction = DIRECTION_UP;

        public Rout(int row, int col, int direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }
    }
}
