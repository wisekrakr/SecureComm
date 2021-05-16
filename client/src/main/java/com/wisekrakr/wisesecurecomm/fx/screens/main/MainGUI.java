package com.wisekrakr.wisesecurecomm.fx.screens.main;

import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.connection.AudioManager;
import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;

import java.awt.*;

public class MainGUI extends AbstractGUI {
    public static final int DESIRED_HEIGHT = 600;
    public static final int DESIRED_WIDTH = 900;
    private final EventManager eventManager;
    private final User user;
    private final AudioManager audioManager;
    private MainController controller;

    public MainGUI(EventManager eventManager, User user, AudioManager audioManager) {
        this.eventManager = eventManager;
        this.user = user;
        this.audioManager = audioManager;
    }

    @Override
    public void prepareGUI() {
        setUndecorated(true);
        setBounds(getScreenSize().width + DESIRED_WIDTH, getScreenSize().height, DESIRED_WIDTH/2, DESIRED_HEIGHT/2);

        setPreferredSize(new Dimension(DESIRED_WIDTH, DESIRED_HEIGHT));
        setLocationRelativeTo(null);
        setResizable(false);

        //Load your fxml file from resources. Pass parameters to the controller.
        controller = (MainController) new MainController(this, eventManager, user).initialize("/main.fxml");
        //Init the components so that they are filled before the frame is shown,
        controller.initComponents();
        //Add the controller to the JFrame and set it in the center.
        add(controller, BorderLayout.CENTER);

        //Initialize audiohandler. Init here so we can get a notification if there is no audio.
        audioManager.initialize(eventManager);
    }

    @Override
    public void showGUI() {
        pack();
        setVisible(true);
    }

    @Override
    public MainController getController() {
        return controller;
    }

    @Override
    public void hideGUI() {
        setVisible(false);
        dispose();
    }
}
