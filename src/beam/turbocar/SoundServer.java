package beam.turbocar;

import javax.sound.sampled.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SoundServer extends Thread {
    public static final String SOUND_DING = "SOUND_DING";
    public static final String SOUND_BG = "SOUND_BG";

    String serverIP = "localhost";
    static int port = 3508;

    private Clip CLIP1, CLIP2, CLIP3, CLIP4;

    public SoundServer() {
        loadSound();
    }

    private void loadSound() {
        System.out.println("SoundServer.java loading sound...");

        try {
            AudioInputStream s1 = AudioSystem.getAudioInputStream(new File("asset/ding1.wav"));
            CLIP1 = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            CLIP1.open(s1);

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("success - load sound.");
    }


    public static void playDing() {
        try {
            Socket socket = new Socket("localhost", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(SOUND_DING);
        } catch (IOException e) {
            System.out.println("SoundServer connectoin fail.");
        }

    }


    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            PrintWriter out;
            BufferedReader in;
            System.out.println("SoundServer started");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String command = in.readLine();
                System.out.println("SOUND SERVER ACCEPT:" + command);
                switch (command) {
                    case SOUND_DING:
                        if (CLIP1.isRunning())
                            CLIP1.stop();
                        CLIP1.setFramePosition(0);
                        CLIP1.start();
                        break;
                    case SOUND_BG:
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SoundServer sound = new SoundServer();
        sound.start();

        SoundServer.playDing();
    }
}
