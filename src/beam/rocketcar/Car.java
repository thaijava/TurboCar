package beam.rocketcar;


import java.util.Random;


public class Car extends Block {

    int speed = GameMapComponent.TILE_SIZE / 15;           // SPEED

    int x, y;

    int headAngle = 0;

    long id = 0;


    public Car() {

        type = Block.TYPE_CAR;
        this.setFileImage("asset/redcar.png");

        id = System.nanoTime() * 100;
        id += (new Random()).nextInt(100);
    }

    public String toString() {
        return "id:" + id + " row:" + row + " col:" + column;
    }

    @Override
    public void setLocation(int row, int column) {
        super.setLocation(row, column);

        x = column * GameMapComponent.TILE_SIZE;
        y = row * GameMapComponent.TILE_SIZE;
    }

    public void xyToRowColumn() {
        int refX = column * GameMapComponent.TILE_SIZE;
        int refY = row * GameMapComponent.TILE_SIZE;
        int half = GameMapComponent.TILE_SIZE / 3;

        int boundRight = refX + half;
        int boundLeft = refX - half;

        int boundUpper = refY - half;
        int boundLower = refY + half;

        if (x >= boundRight) {
            column += 1;
        } else if (x < boundLeft) {
            column -= 1;
        } else if (y <= boundUpper) {
            row -= 1;
        } else if (y > boundLower) {
            row += 1;
        }

    }

    public int getHeadAngle() {
        return headAngle;
    }

    public long getId() {
        return id;
    }

    public CarPos getPos() {

        return new CarPos(id, row, column);
    }
}
