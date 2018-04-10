package Framework;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Board extends Application {
    GridPane gridPane;
    Image image = new Image(getClass().getResourceAsStream("o.png"));


    private void fieldClicked(Rectangle rect){
        System.out.println(rect.getX() + " " + rect.getY());
        ImageView iv = new ImageView(image);

        iv.setPreserveRatio(true);
        ImagePattern imagePattern = new ImagePattern(image);
        rect.setFill(imagePattern);
    }

    private void drawBoard(GraphicsContext gc){
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++) {
                Rectangle rect = new Rectangle(x, y, 200, 200);
                rect.setOnMouseClicked((e) -> fieldClicked(rect));
                rect.setFill(Color.WHITE);
                rect.setStroke(Color.BLACK);
                gridPane.add(rect, x, y);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        gridPane = new GridPane();
        Canvas canvas = new Canvas(600, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawBoard(gc);
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
