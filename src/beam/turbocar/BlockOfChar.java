package beam.turbocar;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BlockOfChar extends Block {

    String charString;

    public BlockOfChar(String charString, int row, int column) {
        super();
        this.row = row;
        this.column = column;
        this.charString = charString;
    }

    public String toString() {
        return charString + "," + row + "," + column;
    }

    public void drawImage(Graphics g, double rescaleX, double rescaleY) {
        if(charString.length() == 0) return;

        g.setColor(Color.white);
        int posx = (int) (column * TurboCarComponent.TILE_SIZE / rescaleX);
        int posy = (int) (row * TurboCarComponent.TILE_SIZE / rescaleY);

        BufferedImage img = TurboCarComponent.myCharacterFace.getFaceImage(charString);
        int xxx = (int) (TurboCarComponent.TILE_SIZE / rescaleX);
        int  yyy = (int) (TurboCarComponent.TILE_SIZE / rescaleY);

        g.drawImage(img, posx, posy, xxx, yyy, null);
    }

    public void drawImageWithBlink(Graphics g, double rescaleX, double rescaleY) {
        if(charString.length() == 0) return;

        g.setColor(Color.white);
        int posx = (int) (column * TurboCarComponent.TILE_SIZE / rescaleX);
        int posy = (int) (row * TurboCarComponent.TILE_SIZE / rescaleY);

        BufferedImage img = TurboCarComponent.myCharacterFace.getFaceImage(charString);
        int xxx = (int) (TurboCarComponent.TILE_SIZE / rescaleX);
        int  yyy = (int) (TurboCarComponent.TILE_SIZE / rescaleY);

        g.drawImage(img, posx, posy, xxx, yyy, null);
        if(Updater.BLINK_FLAG) {
            Graphics2D g2 = (Graphics2D) g;
            Stroke ss = g2.getStroke();
            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.red);
            g2.drawArc(posx, posy, xxx, yyy, 0, 360);

            g2.setStroke(ss);
        }
    }

}
