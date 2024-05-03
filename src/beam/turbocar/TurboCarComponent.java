package beam.turbocar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;


public class TurboCarComponent extends JPanel implements Runnable, KeyListener, ComponentListener {

    static MyCharacterFace myCharacterFace;

    public static final String VOCAB_CHANGED = "VOCAB_CHANGED";
    public static final String REMAIN_TIME_CHANGED = "REMAIN_TIME_CHANGED";

    int fps = 110;                                                     //      FPS
    public static int BASE_TILE_SIZE = 16;                            //     one block 16x16
    public static final double scale = 1;                             //     SCALE
    public static int TILE_SIZE = (int) (BASE_TILE_SIZE * scale);

    public static final double INITIAL_SCALE = 2.5;

    boolean upPressed = false;
    boolean downPressed = false;
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean switch01 = false;

    int TARGET_TIME = 1000000000 / fps;

    String serverName = "localhost";

    Updater updater;

    Car car;
    BufferedImage carImage = null;
    Thread mainLoopThread;
    GameMap gameMap = new GameMap();
    GameServer server;

    Dimension selfSize = new Dimension(400, 400);

    Socket socketToHost;


    public TurboCarComponent() throws IOException, ClassNotFoundException, FontFormatException{
        myCharacterFace = new MyCharacterFace();
        gameMap = new GameMap();
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
        this.addKeyListener(this);
        this.addComponentListener(this);


        server = new GameServer(gameMap);
        server.start();
        // 1. start server

        socketToHost = new Socket(serverName, GameServer.PORT);
        // 2. crate socket communication

        updater = new Updater(socketToHost, this);
        car = updater.command_registerCar();
        //3. register 1 car

        System.out.println("reg. car success " + car);

        updater.start();
    }

    public TurboCarComponent(String testMode) throws IOException, ClassNotFoundException, FontFormatException{
        myCharacterFace = new MyCharacterFace();
        gameMap = new GameMap();
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
        this.addKeyListener(this);
        this.addComponentListener(this);

        server = new GameServer(gameMap);

        socketToHost = new Socket(serverName, GameServer.PORT);
        // 2. crate socket communication

        updater = new Updater(socketToHost, this);
        car = updater.command_registerCar();
        //3. register 1 car

        System.out.println("reg. car success " + car);

        updater.start();
    }



    public Dimension getPrefferedSize() {
        int minX = (int) (TILE_SIZE * gameMap.getColumnSize() * INITIAL_SCALE);
        int minY = ((int) (TILE_SIZE * gameMap.getRowSize() * INITIAL_SCALE));

        return new Dimension(minX, minY);
    }


    private synchronized void readKeyDirection() {
        if (upPressed) {
            try {
                if (gameMap.isWallType(car.getRow() - 1, car.getColumn())) return;
            } catch (IndexOutOfBoundsException e) {
                car.row = server.getGameMapObject().getRowSize();
                car.y = car.row * TILE_SIZE;
            }

            car.headAngle = 0;
            int tmp = car.row - 1;
            tmp *= TILE_SIZE;

            long lastTime;
            while (car.y > tmp) {
                car.y -= car.speed;
                lastTime = System.nanoTime();
                repaint();

                long timeConsume = System.nanoTime() - lastTime;

                if (timeConsume < TARGET_TIME) {
                    long sleepTime = (TARGET_TIME - timeConsume) / 1000000;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            car.xyToRowColumn();

        } else if (downPressed) {
            try {
                if (gameMap.isWallType(car.getRow() + 1, car.getColumn())) return;
            } catch (IndexOutOfBoundsException e) {
                car.row = 0;
                car.y = 0;
            }

            car.headAngle = 180;
            int tmp = car.getRow() + 1;
            tmp *= TILE_SIZE;

            long lastTime;
            while (car.y < tmp) {
                car.y += car.speed;
                lastTime = System.nanoTime();
                repaint();

                long timeConsume = System.nanoTime() - lastTime;
                if (timeConsume < TARGET_TIME) {
                    long sleepTime = (TARGET_TIME - timeConsume) / 1000000;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            car.xyToRowColumn();

        } else if (leftPressed) {
            try {
                if (gameMap.isWallType(car.getRow(), car.getColumn() - 1)) return;
            } catch (IndexOutOfBoundsException e) {
                car.column = server.getGameMapObject().getColumnSize();
                car.x = car.column * TILE_SIZE;
            }

            car.headAngle = 270;
            int c = car.getColumn() - 1;
            c *= TILE_SIZE;
            long lastTime;
            while (car.x > c) {
                car.x -= car.speed;
                lastTime = System.nanoTime();
                repaint();

                long timeConsume = System.nanoTime() - lastTime;
                if (timeConsume < TARGET_TIME) {
                    long sleepTime = (TARGET_TIME - timeConsume) / 1000000;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            car.xyToRowColumn();

        } else if (rightPressed) {

            try {
                if (gameMap.isWallType(car.getRow(), car.getColumn() + 1)) return;
            } catch (IndexOutOfBoundsException e) {
                car.column = 0;
                car.x = 0;
            }

            car.headAngle = 90;
            int c = car.getColumn() + 1;
            c *= TILE_SIZE;
            long lastTime;
            while (car.x < c) {
                car.x += car.speed;
                lastTime = System.nanoTime();
                repaint();

                long timeConsume = System.nanoTime() - lastTime;
                if (timeConsume < TARGET_TIME) {
                    long sleepTime = (TARGET_TIME - timeConsume) / 1000000;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            car.xyToRowColumn();
        }


    }


    public void run() {

        long lastTime;

        while (mainLoopThread != null) {
            lastTime = System.nanoTime();

            readKeyDirection();

            repaint();

            long timeConsume = System.nanoTime() - lastTime;

            if (timeConsume < TARGET_TIME) {
                long sleepTime = (TARGET_TIME - timeConsume) / 1000000;
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    ///////////////////////////////   PAINT COMPONENT
    public void paintComponent(Graphics g) {

        g.drawImage(gameMap.getBackgroundImage(), 0, 0, selfSize.width, selfSize.height, null);

        if (carImage == null) carImage = car.getImage();

        double rescaleX = (TILE_SIZE * gameMap.getColumnSize()) / selfSize.getWidth();
        double rescaleY = (TILE_SIZE * gameMap.getRowSize()) / selfSize.getHeight();
        int carSizeX = (int) (TILE_SIZE / rescaleX);
        int carSizeY = (int) (TILE_SIZE / rescaleY);

        updater.drawItems(g, rescaleX, rescaleY, carSizeX, carSizeY);     //// updater draw draw and draw

        BufferedImage goodImage = Car.rotate(carImage, car.headAngle);
        int tmpx = (int) (car.x / rescaleX);
        int tmpy = (int) (car.y / rescaleY);
        g.drawImage(goodImage, tmpx, tmpy, carSizeX, carSizeY, null);
        g.setColor(Color.white);
        g.drawRect(tmpx, tmpy, carSizeX, carSizeY);
        g.drawString(car.name, tmpx, tmpy - 10);

        if (updater.offlineFlag) {
            g.drawString("Offline", 20, 15);
            g.setColor(Color.red);
        } else {
            g.drawString(updater.getGameState(), 20, 15);
            g.setColor(Color.green);
        }
        g.fillArc(5, 5, 10, 10, 0, 360);


        switch01 = !switch01;
    }

    public void actionConnect(String hostName) throws IOException, ClassNotFoundException {


        try {
            updater.command_bye();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updater.offlineFlag = true;

        socketToHost = new Socket(hostName, GameServer.PORT);
        updater = new Updater(socketToHost, this);
        car = updater.command_registerCar();
        gameMap = new GameMap(updater.mapData);
        updater.start();
    }

    public static void main(String[] args) {
//
//
//        JFrame f = new JFrame();
//
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.setLayout(new BorderLayout());
//
//        TurboCarComponent turboCarComponent;
//        try {
//            turboCarComponent = new TurboCarComponent();
//        } catch (IOException | ClassNotFoundException | FontFormatException e) {
//            throw new RuntimeException(e);
//        }
//        f.add(turboCarComponent, BorderLayout.CENTER);
//
//        f.setSize(800, 800);
//        f.setLocation(300, 0);
//        f.setVisible(true);
//        turboCarComponent.requestFocus();
    }


    @Override
    public void keyTyped(KeyEvent e) {
//        try {
//
//            if (e.getKeyChar() == 'y') {
//                updater.command_getFriends();
//            }
//
//        } catch (ConnectException ex) {
//            System.out.println(">>>> Error: Can't connect remote host.");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } catch (ClassNotFoundException ex) {
//            System.out.println(">>>> Error: Object serialize error.");
//        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
                rightPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

//        switch (e.getKeyCode()) {
//            case KeyEvent.VK_W:
//                upPressed = false;
//                break;
//            case KeyEvent.VK_S:
//                downPressed = false;
//                break;
//            case KeyEvent.VK_A:
//                leftPressed = false;
//                break;
//            case KeyEvent.VK_D:
//                rightPressed = false;
//                break;
 //       }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int w = e.getComponent().getWidth();
        int h = e.getComponent().getHeight();

        if (e.getComponent().getWidth() < 400)
            w = 400;
        if (e.getComponent().getHeight() < 400)
            h = 400;

        selfSize.setSize(w, h);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    public boolean actionRestart() throws IOException, ClassNotFoundException{
        return updater.command_restartGame();
    }


    public void updateScreen(int remainTime, String currentVocab) {

        firePropertyChange(TurboCarComponent.REMAIN_TIME_CHANGED, 0, remainTime);
        firePropertyChange(TurboCarComponent.VOCAB_CHANGED, 0, currentVocab);
    }
}