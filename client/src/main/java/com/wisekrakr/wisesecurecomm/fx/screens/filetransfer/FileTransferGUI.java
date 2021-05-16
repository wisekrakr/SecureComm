package com.wisekrakr.wisesecurecomm.fx.screens.filetransfer;

import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;

import java.awt.*;
import java.util.List;


public class FileTransferGUI extends AbstractGUI {

    private static final int DESIRED_HEIGHT = 400;
    private static final int DESIRED_WIDTH = 600;
    private final EventManager eventManager;
    private final User user;
    private final List<User> recipients;
    private FileTransferController controller;

    public FileTransferGUI(EventManager eventManager, User user, List<User> recipients) {
        this.eventManager = eventManager;
        this.user = user;
        this.recipients = recipients;
    }

    @Override
    public void prepareGUI() {
        //Optional. This project has the ability to drag undecorated JFXPanels without a stage attached.
        setUndecorated(true);
        setBounds(getScreenSize().width + DESIRED_WIDTH, getScreenSize().height, DESIRED_WIDTH/2, DESIRED_HEIGHT/2);

        setPreferredSize(new Dimension(DESIRED_WIDTH, DESIRED_HEIGHT));
        setLocationRelativeTo(null);
        setResizable(false);

        //Load your fxml file from resources. Pass parameters to the controller.
        controller = (FileTransferController) new FileTransferController(
                this, eventManager, user, recipients).initialize("/filetransfer.fxml");
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
    public FileTransferController getController() {
        return controller;
    }

    @Override
    public void hideGUI() {
        setVisible(false);
        dispose();
    }
}
