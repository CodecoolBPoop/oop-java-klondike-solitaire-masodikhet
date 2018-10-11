package com.codecool.klondike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;


public class Klondike extends Application {

    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Game game = new Game();
        game.setTableBackground(new Image("/table/green.png"));

        Button theme1 = new Button();
        Button theme2 = new Button();
        Button theme3 = new Button();


        game.getChildren().add(theme3);
        game.getChildren().add(theme2);
        game.getChildren().add(theme1);


        theme2.setText("Theme2");
        theme2.setLayoutY(100);
        theme2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Card.loadCardImages("theme2/card_back.png");
                start(primaryStage);

            }
        });

        theme1.setText("Theme1");
        theme1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Card.loadCardImages("card_images/card_back.png");
                start(primaryStage);

            }
        });

        theme3.setText("Theme3");
        theme3.setLayoutY(200);
        theme3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Card.loadCardImages("theme3/card_back.png");
                start(primaryStage);

            }
        });

        primaryStage.setTitle("Klondike Solitaire");
        primaryStage.setScene(new Scene(game, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();

    }
}
