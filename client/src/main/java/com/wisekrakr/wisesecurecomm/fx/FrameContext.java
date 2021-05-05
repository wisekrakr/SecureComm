package com.wisekrakr.wisesecurecomm.fx;

public interface FrameContext {
    void prepareGUI();

    void hideGUI();

    void showGUI();

    ControllerContext getController();
}
