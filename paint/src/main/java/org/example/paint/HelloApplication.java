package org.example.paint;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private final double lastUpdate = 0;
    GraphicsContext gc;
    Canvas canvas;



    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        canvas = (Canvas) scene.lookup("#surface");
        gc = canvas.getGraphicsContext2D();

        AnimationTimer renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000.0 / 60.0) {
                    render();
                }
            }
        };
        renderLoop.start();

        stage.setTitle("Paint by Oliwier!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * clears canvas and draws all the shapes (updates done by EventController)
     */
    private void render(){
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        for(int i = 0; i < Shapes.getInstance().getSize(); i++){
            Shapes.getInstance().get(i).draw(gc);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}