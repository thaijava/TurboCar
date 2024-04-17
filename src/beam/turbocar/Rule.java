package beam.turbocar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Rule  implements Runnable {
    public static final int GAME_TIME = 180;
    public static final int STATE_END = 0;
    public static final int STATE_COUNTDONW = 1;
    public static final int STATE_RUNNING = 2;

    String fileName = "asset/wordlist.txt";

    Thread clocker;

    int currentState = STATE_END;
    int remainTime = 0;

    List<String> wordList;
    public Rule(){

        clocker = new Thread(this);
        clocker.start();
    }


    public boolean startNewGame()  {
        // random word;
        wordList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while (line != null) {
                if(line.length() > 0) wordList.add(line);
                line = br.readLine();
            }

            br.close();
        } catch (IOException e) {
            System.out.println(">>>> START GAME ERROR: read file..." + fileName);
            return false;
        }

        for(String e: wordList) {
            System.out.println(e);
        }


        remainTime = GAME_TIME;
        currentState = STATE_COUNTDONW;

        return true;

    }

    public void run() {
        try {
            while (true) {
                switch (currentState) {
                    case STATE_END:
                        Thread.sleep(10);
                        break;
                    case STATE_COUNTDONW:
                        int toZero = 5;
                        while (toZero > 0) {
                            System.out.println("Countdown..." + toZero);
                            Thread.sleep(1000);
                            toZero--;
                        }

                        currentState = STATE_RUNNING;
                        break;
                    case STATE_RUNNING:


                        Thread.sleep(1000);
                        remainTime--;
 //                       System.out.println("Game running..." + remainTime);
                        if(remainTime == 0) {
                            currentState = STATE_END;
                        }
                        break;
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Rule rul = new Rule();

        rul.startNewGame();

    }

}
