package beam.turbocar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MyCharacterFace {

    int w = 45;
    int h = 45;

    int fontSize = 52;

    Font font;

    private String fileName="asset/CSChatThai.otf";
    private final String normalFace = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890.,/\\:;[]{}()#@!$%&*=+-" +
            "กขฃคฅฆงจฉชซฌญฎฏฐฑฒณดตถทธนบปผฝพฟภมยรลวศษสหฬอฮ";
    private final String spacialUpperFace = "  ่  ้  ๊  ๋  ็   ิ  ี  ึ   ื   ื  ั";
    private final String spacialLowerFace = "  ุ  ู ";

    private HashMap<String, BufferedImage> hashMap = new HashMap<>(100);

    Color bgColor = Color.yellow;

    public MyCharacterFace() throws FontFormatException, IOException {
        font = Font.createFont(Font.TRUETYPE_FONT, new File(fileName));

        init1();
        initUpper();
        initLower();
    }



    private void init1() {

        for (int i = 0; i < normalFace.length(); i++) {

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            String key = normalFace.substring(i, i + 1);
            Graphics g = image.getGraphics();
            g.setFont(font.deriveFont(Font.PLAIN, fontSize));
            g.setColor(bgColor);
            //   g.fillRect(0, 0, w, h);
            g.fillArc(0, 0, w,h, 0, 360);

            g.setColor(Color.black);
            FontMetrics metrics = g.getFontMetrics();
            int cHeight = metrics.getHeight();
            int cWidth = metrics.stringWidth(key);
            int xCenter = w / 2 - cWidth / 2;
            int yCenter = h / 2 - cHeight / 2 + metrics.getAscent();

            int ypad = cHeight/20;
            g.drawString(key, xCenter, yCenter +ypad);

            hashMap.put(key, image);
        }
    }
    private void initLower() {
        int xPad = 0;
        int yPad = 0;

        for (int i = 0; i < spacialLowerFace.length(); i++) {

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            String key = spacialLowerFace.substring(i, i + 1);
            Graphics g = image.getGraphics();
            g.setFont(font.deriveFont(Font.PLAIN, fontSize));
            g.setColor(bgColor);
            // g.fillRect(0, 0, w, h);
            g.fillArc(0, 0, w,h, 0, 360);

            g.setColor(Color.black);
            FontMetrics metrics = g.getFontMetrics();
            int cHeight = metrics.getHeight();
            int cWidth = metrics.stringWidth(key);
            int xCenter = w / 2 - cWidth / 2;
            int yCenter = h / 2 - cHeight / 2 + metrics.getDescent() ;

            xPad = w/8;
            yPad = h/8;

            g.drawString(key, xCenter + xPad, yCenter + yPad);

            hashMap.put(key, image);
        }
    }
    private void initUpper() {

        int xPad = 0;
        int yPad = 0;

        for (int i = 0; i < spacialUpperFace.length(); i++) {

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            String key = spacialUpperFace.substring(i, i + 1);
            Graphics g = image.getGraphics();
            g.setFont(font.deriveFont(Font.PLAIN, fontSize));
            g.setColor(bgColor);
            //g.fillRect(0, 0, w, h);

            g.fillArc(0, 0, w,h, 0, 360);

            g.setColor(Color.black);
            FontMetrics metrics = g.getFontMetrics();
            int cHeight = metrics.getHeight();
            int cWidth = metrics.stringWidth(key);
            int xCenter = w / 2 - cWidth / 2;
            int yCenter = h / 2 - cHeight / 2 + metrics.getAscent() + (metrics.getAscent() - metrics.getDescent());

            xPad = w/6;
            yPad = metrics.getDescent()/12 * -1;
            if(key.equals("็")) {
                xPad = w/3;
                yPad = metrics.getDescent()/6;
            }
            g.drawString(key, xCenter + xPad, yCenter - yPad);

            hashMap.put(key, image);
        }
    }


    public BufferedImage getFaceImage(String key) {
        key = key.trim();
        BufferedImage ret = hashMap.get(key);

        if (ret == null)
            return hashMap.get("?");

        return ret;
    }

}
