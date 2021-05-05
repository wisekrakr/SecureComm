package com.wisekrakr.wisesecurecomm.fx.screens.voice;

import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;

import java.awt.*;

public class VoicePlayerGUI extends AbstractGUI {

    private static final int DESIRED_HEIGHT = 252;
    private static final int DESIRED_WIDTH = 290;
    private VoicePlayerController controller;
    private final EventManager eventManager;

    public VoicePlayerGUI(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void prepareGUI() {
        setUndecorated(true);
        setBounds(getScreenSize().width + DESIRED_WIDTH, getScreenSize().height, DESIRED_WIDTH/2, DESIRED_HEIGHT/2);
//
        setPreferredSize(new Dimension(DESIRED_WIDTH, DESIRED_HEIGHT));
        setLocationRelativeTo(null);
        setResizable(false);
        //Load your fxml file from resources. Pass parameters to the controller.
        controller = (VoicePlayerController) new VoicePlayerController(this,eventManager).initialize("/voice.fxml");
        //Init the components so that they are filled before the frame is shown,
        controller.initComponents();
        //Add the controller to the JFrame and set it in the center.
        add(controller, BorderLayout.CENTER);
    }

    @Override
    public void hideGUI() {
        setVisible(false);
        dispose();
    }

    @Override
    public void showGUI() {
        pack();
        setVisible(true);
    }

    @Override
    public VoicePlayerController getController() {
        return controller;
    }
}
