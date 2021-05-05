package com.wisekrakr.wisesecurecomm.fx.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

/**
 * This class is used to generate the animation on a gui, It will generate random ints to determine
 * the size, speed, starting points and direction of each shape.
 */
public class ShapeAnimations {

    public static void generateBallAnimation(int ballRadius, int velocity, int width, int height, BorderPane borderPane){
        Random rand = new Random();
        int radius = rand.nextInt(ballRadius) + 1;
        int speed = rand.nextInt(velocity) + velocity;
        int startXPoint = rand.nextInt(height);
        int startYPoint = rand.nextInt(width);
        int direction = rand.nextInt(6) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Circle circle = null;

        switch (direction){
            case 1 :
                // MOVE LEFT TO RIGHT
                circle = new Circle(0,startYPoint,radius);
                moveXAxis = new KeyValue(circle.centerXProperty(), width -  radius);

                circle.setFill(Color.web("#b0e4ea"));
                circle.setOpacity(0.3);
                break;
            case 2 :
                // MOVE TOP TO BOTTOM
                circle = new Circle(startXPoint,0,radius);
                moveYAxis = new KeyValue(circle.centerYProperty(), height - radius);

                circle.setFill(Color.web("#125f65"));
                circle.setOpacity(0.2);
                break;
            case 3 :
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                circle = new Circle(startXPoint,0,radius);
                moveXAxis = new KeyValue(circle.centerXProperty(), width -  radius);
                moveYAxis = new KeyValue(circle.centerYProperty(), height - radius);

                circle.setFill(Color.web("#115358"));
                circle.setOpacity(0.1);
                break;
            case 4 :
                // MOVE BOTTOM TO TOP
                circle = new Circle(startXPoint,height - radius ,radius);
                moveYAxis = new KeyValue(circle.centerYProperty(), 0);

                circle.setFill(Color.web("#800000"));
                circle.setOpacity(0.1);
                break;
            case 5 :
                // MOVE RIGHT TO LEFT
                circle = new Circle(height - radius,startYPoint,radius);
                moveXAxis = new KeyValue(circle.centerXProperty(), 0);

                circle.setFill(Color.web("#e62136"));
                circle.setOpacity(0.2);
                break;
            case 6 :
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                circle = new Circle(startXPoint,0,radius);
                moveXAxis = new KeyValue(circle.centerXProperty(), width +  radius);
                moveYAxis = new KeyValue(circle.centerYProperty(), height + radius);

                circle.setFill(Color.web("#00ffff"));
                circle.setOpacity(0.2);
                break;

            default:
                System.out.println("default");
        }

        KeyFrame keyFrame = new KeyFrame(Duration.millis(speed * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.setRate(10);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        borderPane.getChildren().add(borderPane.getChildren().size()-1,circle);
    }

    public static void generateSquareAnimation(int squareSize, int velocity, int width, int height, BorderPane borderPane){
        Random rand = new Random();
        int size = rand.nextInt(squareSize) + 1;
        int speed = rand.nextInt(velocity) + velocity;
        int startXPoint = rand.nextInt(height);
        int startYPoint = rand.nextInt(width);
        int direction = rand.nextInt(6) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Rectangle rect = null;

        switch (direction){
            case 1 :
                // MOVE LEFT TO RIGHT
                rect = new Rectangle(0,startYPoint,squareSize,squareSize);
                moveXAxis = new KeyValue(rect.xProperty(), width -  size);

                rect.setFill(Color.web("#b0e4ea"));
                rect.setOpacity(0.1);
                break;
            case 2 :
                // MOVE TOP TO BOTTOM
                rect = new Rectangle(startXPoint,0,squareSize,squareSize);
                moveYAxis = new KeyValue(rect.yProperty(), height - size);

                rect.setFill(Color.web("#125f65"));
                rect.setOpacity(0.1);
                break;
            case 3 :
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                rect = new Rectangle(startXPoint,0,squareSize,squareSize);
                moveXAxis = new KeyValue(rect.xProperty(), width -  size);
                moveYAxis = new KeyValue(rect.yProperty(), height - size);

                rect.setFill(Color.web("#115358"));
                rect.setOpacity(0.1);
                break;
            case 4 :
                // MOVE BOTTOM TO TOP
                rect = new Rectangle(startXPoint,height-squareSize ,squareSize,squareSize);
                moveYAxis = new KeyValue(rect.yProperty(), 0);

                rect.setFill(Color.web("#800000"));
                rect.setOpacity(0.1);
                break;
            case 5 :
                // MOVE RIGHT TO LEFT
                rect = new Rectangle(width-squareSize,startYPoint,squareSize,squareSize);
                moveXAxis = new KeyValue(rect.xProperty(), 0);

                rect.setFill(Color.web("#e62136"));
                rect.setOpacity(0.1);
                break;
            case 6 :
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                rect = new Rectangle(startXPoint,0,squareSize,squareSize);
                moveXAxis = new KeyValue(rect.xProperty(), width +  size);
                moveYAxis = new KeyValue(rect.yProperty(), height + size);

                rect.setFill(Color.web("#00ffff"));
                rect.setOpacity(0.1);
                break;

            default:
                System.out.println("default");
        }

        KeyFrame keyFrame = new KeyFrame(Duration.millis(speed * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        borderPane.getChildren().add(borderPane.getChildren().size()-1,rect);
    }
}
