package net.cscott.sdr.webapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SDRweb implements EntryPoint {
    final TextBox callEntry = new TextBox();
    final FlexTable callList = new FlexTable();
    final Label currentCall = new Label();

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        DockPanel layout = new DockPanel();
        layout.setWidth("100%");
        layout.setHeight("100%"); // XXX: not portable?

        // Make a command that we will execute from all leaves.
        // XXX: WRITE ME
        Command cmd = new Command() {
          public void execute() {
            Window.alert("You selected a menu item!");
          }
        };

        // Menu bar
        MenuBar fileMenu = new MenuBar(true);
        fileMenu.addItem("New", cmd);
        fileMenu.addItem("Open", cmd);
        fileMenu.addItem("Save", cmd);
        fileMenu.addItem("Close", cmd);

        MenuBar programMenu = new MenuBar(true);
        programMenu.addItem("Basic", cmd);
        programMenu.addItem("Mainstream", cmd);
        programMenu.addItem("Plus", cmd);

        // Make a new menu bar, adding a few cascading menus to it.
        MenuBar menu = new MenuBar();
        menu.addItem("File", fileMenu);
        menu.addItem("Program", programMenu);
        layout.add(menu, DockPanel.NORTH);

        DockPanel callBar = new DockPanel();
        callBar.setWidth("100%");
        Label callLabel = new Label("Call: ");
        callLabel.setHorizontalAlignment(Label.ALIGN_RIGHT);
        Button callGo = new Button("Go");
        callEntry.setWidth("100%");
        callEntry.setStyleName("callEntry");
        callBar.add(callLabel, DockPanel.LINE_START);
        callBar.add(callGo, DockPanel.LINE_END);
        callBar.add(callEntry, DockPanel.CENTER);
        callBar.setCellVerticalAlignment(callLabel, DockPanel.ALIGN_MIDDLE);
        callBar.setCellVerticalAlignment(callEntry, DockPanel.ALIGN_MIDDLE);
        callBar.setCellVerticalAlignment(callGo, DockPanel.ALIGN_MIDDLE);
        callBar.setCellWidth(callEntry, "100%");
        callBar.setSpacing(5);
        layout.add(callBar, DockPanel.NORTH);

        callEntry.setText("Type a square dance call");

        callList.setText(0, 0, "Sequence");
        callList.getFlexCellFormatter().setColSpan(0, 0, 2);
        callList.getRowFormatter().setStyleName(0, "callListHeader");
        callList.setStyleName("callList");
        for (int i=1; i<5; i++)
            callList.setText(i,0,"Call #"+i);
        HorizontalPanel mainPanel = new HorizontalPanel();
        mainPanel.setHeight("100%");
        DecoratorPanel dp = new DecoratorPanel();
        dp.add(callList);
        dp.setHeight("100%");
        dp.addStyleName("callListDecorator");
        mainPanel.add(dp);
        layout.add(mainPanel, DockPanel.CENTER);
        layout.setCellHeight(mainPanel, "100%");

        VerticalPanel canvasPanel = new VerticalPanel();
        canvasPanel.add(currentCall);

        DockPanel playBar = new DockPanel();
        playBar.setWidth("100%");
        Button playButton = new Button("Play"); // xxx replace with image
        Label playSlider = new Label("Slider"); // xxx replace with slider
        playBar.add(playButton, DockPanel.LINE_START);
        playBar.add(playSlider, DockPanel.CENTER);
        playBar.setCellWidth(playSlider, "100%");

        // canvas takes up all the rest of the space
        Label canvas = new Label("canvas");
        canvasPanel.add(canvas);
        canvasPanel.add(playBar);
        canvas.setHeight("100%");
        canvasPanel.setHeight("100%");
        mainPanel.add(canvasPanel);
        mainPanel.setHeight("100%");
        layout.setCellWidth(mainPanel, "100%");
        layout.setCellHeight(mainPanel, "100%"); // XXX: not portable?
        mainPanel.setWidth("100%");
        mainPanel.setCellWidth(canvasPanel, "100%");
        canvasPanel.setWidth("100%");
        canvasPanel.setCellHeight(canvas, "100%"); // XXX: not portable?
        canvasPanel.setCellWidth(canvas, "100%");
        layout.setSpacing(4);

        RootPanel.get("app").add(layout);
        // Get rid of scrollbars, and clear out the window's built-in margin,
        // because we want to take advantage of the entire client area.
        //Window.enableScrolling(false);
        Window.setMargin("0px");
    }
}
