package beam.rocketcar;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Block implements  Serializable{
    static final int TYPE_BACKGROUND=0;
    static final int TYPE_WALL=1;
    static final int TYPE_CAR=9;
    byte[] imageByte;
    int row, column;
    int type = TYPE_BACKGROUND;

    public Block() {
    }

    public void setLocation(int row, int column){
        this.column =column;
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getType() {
        return type;
    }

    public void setFileImage(String filePath) {
        try {

            BufferedImage tmp = ImageIO.read(new File(filePath));
            ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();
            ImageIO.write(tmp, "png", bufferOutput);
            imageByte = bufferOutput.toByteArray();
        } catch (IOException e) {
            System.out.println(">>>> BLOCK NEW() ERROR:  can read image file:" + filePath);
            e.printStackTrace();
        }

    }

    public BufferedImage getImage() {
        BufferedImage ret =null;
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(imageByte);
            ret = ImageIO.read(byteStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
