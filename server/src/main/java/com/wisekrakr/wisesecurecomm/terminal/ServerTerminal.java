package com.wisekrakr.wisesecurecomm.terminal;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.wisekrakr.wisesecurecomm.ClientHandler;
import com.wisekrakr.wisesecurecomm.Server;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


public class ServerTerminal {

    private Terminal terminal;
    private TerminalScreen screen;
    private Server server;
    private TextGraphics graphics;

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


            boolean running = true;
            while (running){
                KeyStroke keyPressed = terminal.pollInput();

                if(keyPressed != null){
                    switch (keyPressed.getKeyType()){
                        case Escape:
                            running = false;
                            screen.stopScreen();
                            server.stopServer();
                            break;
                        case Enter:
                            screen.clear();
                            showMain();
                            screen.refresh();
                            break;
                        default:
                            System.out.println("Key has no function! -> " + keyPressed.getCharacter());
                    }
                }
            }


        }catch (Throwable t){
            throw new IllegalStateException("Could not start a new terminal window ", t);
        }
    }

    private void showIntro(){
        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);

        graphics.drawRectangle(
                new TerminalPosition(20,10),
                new TerminalSize(40,4),
                '*'
        );

        graphics.putString(22,11,"Welcome to the WISE Server Terminal");
        graphics.putString(22,16,"Enter to continue, Escape to quit");
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
    }

    public void refresh(Map<Long, ClientHandler> clientHandlers) {
        screen.clear();
        showServerHeader();

        int row = 10, col = 20;


        for (ClientHandler clientHandler: clientHandlers.values()){

            graphics.putString(col, row++,"client name: " + clientHandler.getUser().getName(), SGR.UNDERLINE);
            graphics.putString(col, row++, Long.toString(clientHandler.getUser().getId()), SGR.ITALIC);
            graphics.putString(col, row++, clientHandler.getUser().getStatus().toString(), SGR.BLINK);
            graphics.putString(col, row++, clientHandler.getClientSocket().getRemoteSocketAddress().toString(), SGR.BORDERED);
            graphics.putString(col, row++, "created at: " +
                    convertTime(clientHandler.getClientSocket().getSession().getCreationTime()), SGR.FRAKTUR);

            graphics.putString(col, row++, "---------------------------------------", SGR.BOLD);
        }

        try {
            screen.refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd MM yyyy HH:mm");
        return format.format(date);
    }
}
