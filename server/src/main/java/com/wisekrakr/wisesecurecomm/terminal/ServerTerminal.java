package com.wisekrakr.wisesecurecomm.terminal;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.wisekrakr.wisesecurecomm.ClientHandler;
import com.wisekrakr.wisesecurecomm.Server;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;



public class ServerTerminal {

    private Terminal terminal;
    private TerminalScreen screen;
    private final Server server;
    private TextGraphics graphics;
    private Table<String> table;
    private boolean running = true;
    private MultiWindowTextGUI gui;
    private Map<Long, Integer>userTablePosition = new HashMap<>();
    private int count = 0;

    public ServerTerminal(Server server) {

        this.server = server;
    }

    public void create() {
        try {
            terminal = new DefaultTerminalFactory().createTerminal();

            screen = new TerminalScreen(terminal);
            screen.startScreen();

        } catch (Throwable t) {
            throw new IllegalStateException("Could not create new Server Terminal", t);
        }
    }

//    public void start() {
//        try {
//            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
//            TextGraphics graphics = screen.newTextGraphics();
//            Window window = new BasicWindow("WISE Server Terminal");
//            Panel contentPanel = new Panel(new GridLayout(2));
//            GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
//            gridLayout.setHorizontalSpacing(3);
//
//            Label title = new Label("This is a label that spans two columns");
//            title.setLayoutData(GridLayout.createLayoutData(
//                    GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
//                    GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
//                    true,       // Give the component extra horizontal space if available
//                    false,        // Give the component extra vertical space if available
//                    2,                  // Horizontal span
//                    1));                  // Vertical span
//            contentPanel.addComponent(title);
//
//            contentPanel.addComponent(new Label("Text Box (aligned)"));
//            contentPanel.addComponent(
//                    new TextBox()
//                            .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));
//
//            contentPanel.addComponent(new Label("Password Box (right aligned)"));
//            contentPanel.addComponent(
//                    new TextBox()
//                            .setMask('*')
//                            .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
//
//            contentPanel.addComponent(new Label("Read-only Combo Box (forced size)"));
//            List<String> timezonesAsStrings = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
//            ComboBox<String> readOnlyComboBox = new ComboBox<>(timezonesAsStrings);
//            readOnlyComboBox.setReadOnly(true);
//            readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
//            contentPanel.addComponent(readOnlyComboBox);
//
//            contentPanel.addComponent(new Label("Editable Combo Box (filled)"));
//            contentPanel.addComponent(
//                    new ComboBox<>("Item #1", "Item #2", "Item #3", "Item #4")
//                            .setReadOnly(false)
//                            .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));
//
//            contentPanel.addComponent(new Label("Button (centered)"));
//            contentPanel.addComponent(new Button("Button", () -> MessageDialog.showMessageDialog(
//                    textGUI,
//                    "MessageBox",
//                    "This is a message box", MessageDialogButton.OK))
//                        .setLayoutData(
//                                GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)
//                        )
//            );
//
//            contentPanel.addComponent(
//                    new EmptySpace()
//                            .setLayoutData(
//                                    GridLayout.createHorizontallyFilledLayoutData(2)));
//            contentPanel.addComponent(
//                    new Separator(Direction.HORIZONTAL)
//                            .setLayoutData(
//                                    GridLayout.createHorizontallyFilledLayoutData(2)));
//            contentPanel.addComponent(
//                    new Button("Close", window::close).setLayoutData(
//                            GridLayout.createHorizontallyEndAlignedLayoutData(2)));
//
//            window.setComponent(contentPanel);
//
//            textGUI.addWindowAndWait(window);
//        } catch (Throwable t) {
//            throw new IllegalStateException("Could not start a new terminal window ", t);
//        }finally {
//            if(screen != null) {
//                try {
//                    screen.stopScreen();
//                    server.stopServer();
//                }catch (Throwable t) {
//                    //
//                }
//
//            }
//        }
//    }
//
    public void start(){

        try {
            graphics = screen.newTextGraphics();

            showIntro();
            screen.refresh();


            while (running){
                KeyStroke keyPressed = terminal.pollInput();

                if(keyPressed != null){
                    screen.clear();
                    switch (keyPressed.getKeyType()){
                        case F10:
                            exit();
                            break;
                        case Enter:
                            showMain();
                            break;
                        default:
                            System.out.println("Key has no function! -> " + keyPressed.getCharacter());
                    }
                    screen.refresh();
                }
            }


        }catch (Throwable t){
            throw new IllegalStateException("Could not start a new terminal window ", t);
        }
    }

    private void showIntro(){
        boolean introShown = true;
        while (!introShown){

            cursorWait(0, 666);
            typeLine(">_ WISE SERVER INTERFACE READY", 0);
            cursorWait(0, 666);
            typeLine(">_ INITIALIZING CLIENT SUPPORT DATA MATRIX", 4);
            cursorWait(0, 666);
            typeLine(">_ ....................................................", 6);
            cursorWait(0, 666);
            typeLine(">_ SECURECOMM WISE SERVER IS ALIVE!- CLIENT SYSTEM", 6);
            cursorWait(0, 666);

            introShown = true;
        }

        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);

        graphics.drawRectangle(
                new TerminalPosition(10,10),
                new TerminalSize(50,4),
                '*'
        );

        graphics.putString(12,11,"Welcome to the SecureComm WISE Server Terminal");
        graphics.putString(12,16,"Enter to continue, F10 to stop the server");
        cursorWait(0, 1111);
        typeLine(">_ ....................................................", 17);

    }


    private void showServerHeader(){
        graphics.drawRectangle(
                new TerminalPosition(3,4),
                new TerminalSize(40,4),
                '*'
        );

        graphics.putString(5,5,"Server Information: ");
    }

    private void showMain(){
        showServerHeader();

        graphics.putString(5, 10, "No clients connected", SGR.BLINK);

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.FIT_TERMINAL_WINDOW, Window.Hint.NO_DECORATIONS));

        // Create gui and start gui
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        table = new Table<>("Name", "ID", "IP", "Created At", "Status");
        table.setPosition(new TerminalPosition(10,10));
        window.setComponent(table);

        gui.addWindowAndWait(window);

    }

    public void addUser(ClientHandler clientHandler){
        try {
            if(table.getTableModel().getRowCount() != 0) count++;

            terminal.bell();
            table.getTableModel().addRow(
                    clientHandler.getUser().getName(),
                    String.valueOf(clientHandler.getUser().getId()),
                    String.valueOf(clientHandler.getClientSocket().getRemoteSocketAddress()),
                    convertTime(clientHandler.getClientSocket().getSession().getCreationTime()),
                    String.valueOf(clientHandler.getUser().getStatus())
            );
            userTablePosition.put(clientHandler.getUser().getId(), count);
        }catch (Exception e){
            // Exception for the system beep sound.
            e.printStackTrace();
        }

    }

    public void refresh(ClientHandler clientHandler, UserStatus userStatus) {
        try {
            terminal.bell();

//            screen.clear();
            showServerHeader();

            for(Map.Entry<Long, Integer>userPos: userTablePosition.entrySet()){
                if(clientHandler.getUser().getId() == userPos.getKey()){
                    switch (userStatus){
                        case UPDATE:
                            table.getTableModel().removeRow(userPos.getValue());
                            addUser(clientHandler);
                            break;
                        case REMOVE:
                            table.getTableModel().removeRow(userPos.getValue());
                            userTablePosition.remove(userPos.getKey());
                            break;
                        default:
                            MessageDialog.showMessageDialog(
                                    gui, "Error",
                                    clientHandler.getUser().getName()+ " could not be updated!",
                                    MessageDialogButton.OK
                            );
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            screen.refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void cursorWait(int row, int millis) {

        screen.setCursorPosition(null);
        screen.setCursorPosition(new TerminalPosition(0, row));

        try {
            screen.refresh();

            Thread.sleep(millis);
        } catch (Throwable t) {
            //
        }
    }

    private void typeLine(String msg, int row) {
        TextColor foregroundColor = TextColor.ANSI.GREEN;
        int interval = 11;

        for (int i = 0; i < msg.length(); i++) {
            screen.setCursorPosition(new TerminalPosition(i, row));
            screen.setCharacter( (i), row, new TextCharacter(msg.charAt(i), foregroundColor, TextColor.ANSI.BLUE));

            try {
                screen.refresh();
                Thread.sleep(ThreadLocalRandom.current().nextInt(interval*3));
            } catch (Throwable t) {
                //
            }
        }
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd MM yyyy HH:mm");
        return format.format(date);
    }

    private void exit(){
        running = false;

        screen.clear();
        cursorWait(2, 666);
        typeLine(">_ SYSTEM EXIT REQUESTED", 0);
        cursorWait(2, 1111);
        typeLine(">_ SERVER BYE TO CLIENTS", 1);
        cursorWait(2, 1111);
        typeLine(">_ SERVER TERMINATED", 2);
        cursorWait(2, 1111);

        try {
            screen.stopScreen();
            server.stopServer();
        }catch (Throwable t){
            throw new IllegalStateException("Error while closing server and shutting down terminal",t);
        }

    }

}
