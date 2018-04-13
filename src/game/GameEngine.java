package game;

import framework.Board;
import framework.Framework;
import reversi.Reversi;
import ticTacToe.TicTacToe;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.ini4j.Wini;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;




public class GameEngine {

    private  boolean gamestart;
    private String game;
    private Wini ini;
    private String name;
    protected int[][] field;
    private int numberofstates = 3;
    protected String[] states = new String[100];
    private CommandCenter jack;
    private Framework framework;
    private Reversi reversi;
    private int[] move;
    private int calculatedMove;
    private Board board;
    private java.lang.reflect.Method method;
    public GameEngine(Map<String, String> optionlist, CommandCenter commandCenter, boolean start, Stage stage) {
        game = optionlist.get("game");
        name = optionlist.get("name");
        if (game.contains("Reversi")) {
            setField(8, 8);
            board = new Board();
            String name = optionlist.get("name");
            stage.setTitle(name);
            try {
                board.start(stage, field, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            reversi = new Reversi(field, board);
        } else if (game.contains("Tic-tac-toe")) {
            setField(3, 3);
            board = new Board();
            try {
                String name = optionlist.get("name");
                board.start(stage, field, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            framework = new TicTacToe(board);
            }else{
            System.out.println("Hopscotch");
        }

        gamestart = start;
        //showField();
        jack = commandCenter;
        new Thread(new Runnable() {
            public void run() {
                // receivedCommand houdt het ontvangen command van de server0
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (gamestart) {
                    String s = jack.ReadReceived();
                    System.out.println(s);
                    String parse = jack.commandHandling(s, name);

                    if (parse != null) {
                        int pos = Integer.valueOf(parse);
                        int[] work = calculateMoveToCoordinates(pos);
                        //if(field[work[0]][work[1]] == 0){
                            boolean valid = setState(work[0], work[1], 2);
                            if (valid){
                                field[work[0]][work[1]] = 2;
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    board.drawBoard(field);
                                    showField();
                                }
                            });
                        //}
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(gamestart) {

                    try {
                        method = board.getClass().getMethod("getMoveMade");
                        try {
                            boolean mup = (boolean) method.invoke(board);
                            if (mup) {
                                try {
                                    doMove();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    public void doMove() throws IOException {

            System.out.println("hij doet dit");
            int[] coordinates = board.getMove();
            calculatedMove = calculateMoveToPosition(coordinates);
            boolean exec = setState(coordinates[0], coordinates[1], 1);
            if (exec) {
                if(game.equals("Reversi")){ ;
                    field = reversi.doMove(getField(), calculatedMove);
                    jack.doMove(calculatedMove);
                    Platform.runLater(() -> board.drawBoard(field));
                }else {
                    field[coordinates[0]][coordinates[1]] = 1;
                jack.doMove(calculatedMove);
                Platform.runLater(() -> board.drawBoard(field));

            }
        }
    }

    public void setField(int x, int y) {
        field = new int[x][y];
    }



    private int calculateMoveToPosition(int[] move) {
        return (((move[1]) * field.length) + move[0]);
    }
    private int[] calculateMoveToCoordinates(int move) {
        int y = (move/(field.length));
        int x = move%(field.length);
        return new int[] {x, y};
    }


    public int[][] getField() {
        return field;
    }

    public boolean setState(int x, int y, int value) {
        if (x >= field.length) {
            System.out.println("error: the given position does not exist on this board");
            return false;
        } else {
            if (y >= field[1].length) {
                System.out.println("error: the given position does not exist on this board");
                return false;
            } else {
                if (value >= numberofstates) {
                    System.out.println("Error: given state is not supported");
                    return false;
                } else {
                    if (getState(x, y) == 2) {
                        System.out.println("vijandig");
                        field[x][y] = 2;
                        return false;
                    } else {
                        if (value == getState(x, y)) {
                            System.out.println("!: Dit vakje is al van jou, probeer een ander vakje");
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
    }

    public int getState(int x, int y) {
        return field[x][y];
    }



    public void showField() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                System.out.println("Values at arr[" + i + "][" + j + "] is " + field[i][j]);
            }
        }
    }
}