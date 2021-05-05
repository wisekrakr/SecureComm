package com.wisekrakr.wisesecurecomm.fx.screens.login;

import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;

import java.awt.*;

public class LoginGUI extends AbstractGUI {

    public static final int DESIRED_HEIGHT = 420;
    public static final int DESIRED_WIDTH = 350;
    private final EventManager eventManager;
    private LoginController controller;

    public LoginGUI(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void prepareGUI() {

        //Optional. This project has the ability to drag undecorated JFXPanels without a stage attached.
        setUndecorated(true);
        setBounds(getScreenSize().width + DESIRED_WIDTH, getScreenSize().height, DESIRED_WIDTH/2, DESIRED_HEIGHT/2);
        setPreferredSize(new Dimension(DESIRED_WIDTH, DESIRED_HEIGHT));
        setLocationRelativeTo(null);
        setTitle("Wise Secure Communication : Client version 0.7");
        setResizable(false);


        //Load your fxml file from resources. Pass parameters to the controller.
        controller = (LoginController) new LoginController(this, eventManager).initialize("/login.fxml");
        //Init the components so that they are filled before the frame is shown,
        controller.initComponents();
        //Add the controller to the JFrame and set it in the center.
        add(controller, BorderLayout.CENTER);
    }

    @Override
    public void showGUI() {
        pack();
        setVisible(true);
    }

    @Override
    public LoginController getController() {
        return controller;
    }

    @Override
    public void hideGUI() {
        setVisible(false);
    }
}
