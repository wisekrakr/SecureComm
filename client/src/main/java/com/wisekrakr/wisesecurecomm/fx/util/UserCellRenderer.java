package com.wisekrakr.wisesecurecomm.fx.util;


import com.wisekrakr.wisesecurecomm.communication.user.User;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A Class for Rendering users images / name / status color on the user list.
 */
public class UserCellRenderer implements Callback<ListView<User>,ListCell<User>>{
    private final String status;

    public UserCellRenderer(String line) {
        this.status = line;
    }

    @Override
    public ListCell<User> call(ListView<User> p) {
        return new ListCell<User>(){
            @Override
            protected void updateItem(User user, boolean bln) {
                super.updateItem(user, bln);
                setGraphic(null);
                setText(null);
                if (user != null) {
                    HBox hBox = new HBox();

                    Text name = new Text(user.getName());

                    Circle pictureImage= new Circle(16);
                    pictureImage.setFill(new ImagePattern(new Image(user.getProfilePicture())));
                    pictureImage.setStrokeWidth(4);

                    Platform.runLater(() -> {
                        switch (status){
                            case "ONLINE":
                                pictureImage.setStroke(Color.GREEN);
                                break;
                            case "BUSY":
                                pictureImage.setStroke(Color.BEIGE);
                                break;
                            case "AWAY":
                                pictureImage.setStroke(Color.BLUE);
                                break;
                        }
                    });

                    hBox.getChildren().addAll(pictureImage, name);
                    name.setFill(Color.WHITE);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hBox);

                    this.hoverProperty().addListener((observable, oldValue, newValue) -> {
                        if(newValue){
                            this.setTooltip(new Tooltip("ID: " + user.getId()));
                        }
                    });
                }
            }
        };
    }
}
