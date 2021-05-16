package com.wisekrakr.wisesecurecomm.fx.screens.dm;

import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;

import java.awt.*;

public class DirectMessagingGUI extends AbstractGUI {

    private static final int DESIRED_HEIGHT = 400;
    private static final int DESIRED_WIDTH = 300;
    private final DirectMessagingController controller;

    public DirectMessagingGUI(EventManager eventManager, User user, User other) {

        //Load your fxml file from resources. Pass parameters to the controller.
        controller = (DirectMessagingController) new DirectMessagingController(this, eventManager, user,other).initialize("/dm.fxml");
        //Init the components so that they are filled before the frame is shown,
        controller.initComponents();
        //Add the controller to the JFrame and set it in the center.
        add(controller, BorderLayout.CENTER);
    }

    @Override
    public void prepareGUI() {
        setUndecorated(true);
        setBounds(0, 0, DESIRED_WIDTH, DESIRED_HEIGHT);

        setPreferredSize(new Dimension(DESIRED_WIDTH, DESIRED_HEIGHT));
        setLocationRelativeTo(null);
        setResizable(false);
    }

    @Override
    public DirectMessagingController getController() {
        return controller;
    }

    @Override
    public void showGUI() {
        pack();
        setVisible(true);
    }

    @Override
    public void hideGUI() {
        setVisible(false);
    }
}
