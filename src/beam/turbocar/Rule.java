package beam.turbocar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Rule implements Runnable {

    public static final int COUNT_DOWN = 3;
    public static final int GAME_TIME = 180;
    public static final int STATE_END = 0;
    public static final int STATE_COUNTDONW = 1;
    public static final int STATE_RUNNING = 2;
    GameServer server;

    String fileName = "asset/wordlist.txt";

    Thread clocker;

    int currentState = STATE_END;
    int remainTime = 0;

    private StringTokenizer wordList;

    private int blockPointer=0;
    private boolean fetchNext = true;
    private int sumOfTick=0;

    String currentGamingWord;
    String currentGamingMeaning;

    ArrayList<BlockOfChar> blockList;

    public Rule(GameServer gameServer) {
        server = gameServer;
        clocker = new Thread(this);
        clocker.start();
    }


    public boolean startNewGame() {
        // random word;

        String content = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while (line != null) {
                if (line.length() > 0) content = content + line + "##$**";

                line = br.readLine();
            }

            System.out.println(content);
            br.close();
        } catch (IOException e) {
            System.out.println(">>>> START GAME ERROR: read file..." + fileName);
            return false;
        }

        wordList = new StringTokenizer(content, "##$**");
        fetchNext = true;

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
                        int toZero = COUNT_DOWN;
                        while (toZero > 0) {
                            currentGamingWord = ""+toZero;
                            currentGamingMeaning = "";
                            this.scambleBlock();

                            System.out.println("Countdown..." + this.getCurrentBlock());
                            Thread.sleep(1000);
                            toZero--;
                        }

                        currentState = STATE_RUNNING;
                        break;
                    case STATE_RUNNING:
                        if(fetchNext) {
                            String ttt = nextWord();
                            int jjj = ttt.indexOf(" ");
                            if(jjj > 0) {
                                currentGamingWord = ttt.substring(0, jjj).trim();
                                currentGamingMeaning = ttt.substring(jjj).trim();
                            } else {
                                currentGamingWord = ttt.trim();
                                currentGamingMeaning = "no meaning found.";
                            }

                            scambleBlock();
                            for (BlockOfChar block: blockList ) {
                                System.out.println(block);

                            }
                            fetchNext = false;
                        }


                        Thread.sleep(100);
                        sumOfTick++;
                        if(sumOfTick >= 10) {
                            sumOfTick = 0;
                            remainTime--;

                            System.out.println("Game running..." + remainTime + "--->"+currentGamingWord + " -- " + currentGamingMeaning);
                            BlockOfChar currentBlock = this.getCurrentBlock();
                            System.out.println(currentBlock);

                            consume();
                        }

                        if (remainTime == 0) {
                            currentState = STATE_END;
                        }
                        break;
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void consume() {

        blockPointer++;
        if (blockPointer >= currentGamingWord.length()) {
            blockPointer =0;
            fetchNext = true;
        }

    }

    public BlockOfChar getCurrentBlock() {
        return blockList.get( blockPointer );
    }

    private void scambleBlock() {

        blockList = new ArrayList<>();
        Random random = new Random();
        int i=0;
        while (i < currentGamingWord.length()){
            int row = random.nextInt(server.gameMap.getRowSize());
            int col = random.nextInt(server.gameMap.getColumnSize());
            if(server.gameMap.mapData[row][col] == Block.TYPE_BACKGROUND) {
                String st1 = currentGamingWord.substring(i, i+1);
                blockList.add(new BlockOfChar(st1, row, col));
                i++;
            }
        }

        fetchNext = false;
    }

    private String nextWord() {
        if (!wordList.hasMoreElements())
            return "EndOfWord END OF WORD TO COLLECT";

        return wordList.nextToken();
    }

    public static void main(String[] args) {
        try {
            GameMap map = new GameMap();
            GameServer server = new GameServer(map, 8888);

            Rule rul = new Rule(server);

            rul.startNewGame();

            Thread.sleep(1000);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
