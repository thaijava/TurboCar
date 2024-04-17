package beam.turbocar;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Car extends Block {

    int speed = TurboCarComponent.TILE_SIZE / 15;           // SPEED

    int x, y;

    int headAngle = 0;

     int score = 0;


    public Car() {
        super();

        type = Block.TYPE_CAR;
        this.setFileImage("asset/redcar.png");

    }

    public String toString() {
        return "id:" + id + " row:" + row +
                " col:" + column + " score:" + score;
    }

    @Override
    public void setLocation(int row, int column) {
        super.setLocation(row, column);

        x = column * TurboCarComponent.TILE_SIZE;
        y = row * TurboCarComponent.TILE_SIZE;
    }

    public void xyToRowColumn() {
        int refX = column * TurboCarComponent.TILE_SIZE;
        int refY = row * TurboCarComponent.TILE_SIZE;
        int half = TurboCarComponent.TILE_SIZE / 3;

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

        return new CarPos(id, row, column, headAngle);
    }

    public static BufferedImage rotate(BufferedImage img, int angle) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2 = newImage.createGraphics();
        g2.rotate(Math.toRadians(angle), img.getWidth() / 2, img.getHeight() / 2);
        g2.drawImage(img, null, 0, 0);

        return newImage;
    }
}
