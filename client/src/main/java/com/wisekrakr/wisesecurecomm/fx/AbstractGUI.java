package com.wisekrakr.wisesecurecomm.fx;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractGUI extends JFrame implements FrameContext {
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public Dimension getScreenSize() {
        return screenSize;
    }

}
