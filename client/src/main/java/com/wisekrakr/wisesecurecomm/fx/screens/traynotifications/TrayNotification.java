package com.wisekrakr.wisesecurecomm.fx.screens.traynotifications;

import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.animations.*;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.models.CustomStage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public final class TrayNotification {

    @FXML
    private Label lblTitle, lblMessage, lblClose;
    @FXML
    private ImageView imageIcon;
    @FXML
    private Rectangle rectangleColor;
    @FXML
    private AnchorPane rootNode;

    private CustomStage stage;
    private AnimationType animationType;
    private EventHandler<ActionEvent> onDismissedCallBack, onShownCallback;
    private TrayAnimation animator;
    private AnimationProvider animationProvider;

    /**
     * Initializes an instance of the tray notification object
     * @param title The title text to assign to the tray
     * @param body The body text to assign to the tray
     * @param img The image to show on the tray
     * @param rectangleFill The fill for the rectangle
     */
    public TrayNotification(String title, String body, Image img, Paint rectangleFill) {
        initTrayNotification(title, body, TrayNotificationType.CUSTOM);
        setImage(img);
        setRectangleFill(rectangleFill);
    }


    /**
     * Initializes an instance of the tray notification object
     * @param title The title text to assign to the tray
     * @param body The body text to assign to the tray
     * @param trayNotificationType The notification type to assign to the tray
     */
    public TrayNotification(String title, String body, TrayNotificationType trayNotificationType) {
        initTrayNotification(title, body, trayNotificationType);
    }


    /**
     * Initializes an empty instance of the tray notification
     */
    public TrayNotification() {
        initTrayNotification("", "", TrayNotificationType.CUSTOM);
    }

    private void initTrayNotification(String title, String message, TrayNotificationType type) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/notification.fxml"));

            fxmlLoader.setController(this);
            fxmlLoader.load();

            initStage();
            initAnimations();

            setTray(title, message, type);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAnimations() {

        animationProvider =
            new AnimationProvider(new FadeAnimation(stage), new SlideAnimation(stage), new PopupAnimation(stage));

        //Default animation type
        setAnimationType(AnimationType.SLIDE);
    }

    private void initStage() {

        stage = new CustomStage(rootNode, StageStyle.UNDECORATED);
        stage.setScene(new Scene(rootNode));
        stage.setAlwaysOnTop(true);
        stage.setLocation(stage.getBottomRight());

        lblClose.setOnMouseClicked(e -> dismiss());
    }

    public void setNotificationType(TrayNotificationType nType) {

        URL imageLocation = null;
        String paintHex = null;

        switch (nType) {

            case INFORMATION:
                imageLocation = getClass().getClassLoader().getResource("images/info.png");
                paintHex = "#2C54AB";
                break;

            case NOTICE:
                imageLocation = getClass().getClassLoader().getResource("images/notice.png");
                paintHex = "#8D9695";
                break;

            case SUCCESS:
                imageLocation = getClass().getClassLoader().getResource("images/success.png");
                paintHex = "#009961";
                break;

            case WARNING:
                imageLocation = getClass().getClassLoader().getResource("images/warning.png");
                paintHex = "#E23E0A";
                break;

            case ERROR:
                imageLocation = getClass().getClassLoader().getResource("images/error.png");
                paintHex = "#CC0033";
                break;

            case CUSTOM:
                return;
        }

        setRectangleFill(Paint.valueOf(paintHex));
        if(imageLocation != null) setImage(new Image(imageLocation.toExternalForm()));
        else System.out.println("NO IMAGES");
    }

    public void setTray(String title, String message, TrayNotificationType type) {
        setTitle(title);
        setMessage(message);
        setNotificationType(type);
    }

    public void setTray(String title, String message, Image img, Paint rectangleFill, AnimationType animType) {
        setTitle(title);
        setMessage(message);
        setImage(img);
        setRectangleFill(rectangleFill);
        setAnimationType(animType);
    }

    public boolean isTrayShowing() {
        return animator.isShowing();
    }

    /**
     * Shows and dismisses the tray notification
     * @param dismissDelay How long to delay the start of the dismiss animation
     */
    public void showAndDismiss(Duration dismissDelay) {

        if (isTrayShowing()) {
            dismiss();
        } else {
            stage.show();

            onShown();
            animator.playSequential(dismissDelay);
        }

        onDismissed();
    }

    /**
     * Displays the notification tray
     */
    public void showAndWait() {

        if (! isTrayShowing()) {
            stage.show();

            animator.playShowAnimation();

            onShown();
        }
    }

    /**
     * Dismisses the notifcation tray
     */
    public void dismiss() {

        if (isTrayShowing()) {
            animator.playDismissAnimation();
            onDismissed();
        }
    }

    private void onShown() {
        if (onShownCallback != null)
            onShownCallback.handle(new ActionEvent());
    }

    private void onDismissed() {
        if (onDismissedCallBack != null)
            onDismissedCallBack.handle(new ActionEvent());
    }

    /**
     * Sets an action event for when the tray has been dismissed
     * @param event The event to occur when the tray has been dismissed
     */
    public void setOnDismiss(EventHandler<ActionEvent> event) {
        onDismissedCallBack  = event;
    }

    /**
     * Sets an action event for when the tray has been shown
     * @param event The event to occur after the tray has been shown
     */
    public void setOnShown(EventHandler<ActionEvent> event) {
        onShownCallback  = event;
    }

    /**
     * Sets a new task bar image for the tray
     * @param img The image to assign
     */
    public void setTrayIcon(Image img) {
        stage.getIcons().clear();
        stage.getIcons().add(img);
    }

    public Image getTrayIcon() {
        return stage.getIcons().get(0);
    }

    /**
     * Sets a title to the tray
     * @param txt The text to assign to the tray icon
     */
    public void setTitle(String txt) {
        lblTitle.setText(txt);
    }

    public String getTitle() {
        return lblTitle.getText();
    }

    /**
     * Sets the message for the tray notification
     * @param txt The text to assign to the body of the tray notification
     */
    public void setMessage(String txt) {
        lblMessage.setText(txt);
    }

    public String getMessage() {
        return lblMessage.getText();
    }

    public void setImage (Image img) {
        imageIcon.setImage(img);

        setTrayIcon(img);
    }

    public Image getImage() {
        return imageIcon.getImage();
    }

    public void setRectangleFill(Paint value) {
        rectangleColor.setFill(value);
    }

    public Paint getRectangleFill() {
        return rectangleColor.getFill();
    }

    public void setAnimationType(AnimationType type) {
        animator = animationProvider.findFirstWhere(a -> a.getAnimationType() == type);

        animationType = type;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }
}