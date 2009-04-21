package net.cscott.sdr.webapp.client;

import net.cscott.sdr.calls.Program;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

// incubator
import com.google.gwt.widgetideas.client.SliderBar;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SDRweb implements EntryPoint {
    final SuggestBox callEntry = new SuggestBox(new CallOracle());
    final FlexTable callList = new FlexTable();
    final Label currentCall = new Label();
    final Label errorMsg = new Label();
    final VerticalPanel topPanel = new VerticalPanel();
    final VerticalPanel canvasPanel = new VerticalPanel();
    final DanceFloor danceFloor = GWT.create(DanceFloor.class);
    DockPanel playBar = new DockPanel();

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // Make a command that we will execute from all leaves.
        // XXX: WRITE ME
        Command cmd = new Command() {
          public void execute() {
              Window.alert("You selected a menu item!");
          }
        };

        topPanel.setWidth("100%");
        // Menu bar
        MenuBar fileMenu = new MenuBar(true);
        fileMenu.addItem("New", cmd);
        fileMenu.addItem("Open", cmd);
        fileMenu.addItem("Save", cmd);
        fileMenu.addItem("Print", cmd);
        fileMenu.addItem("Close", cmd);

        MenuBar programMenu = new MenuBar(true);
        for (Program p: Program.values()) {
            programMenu.addItem(p.toTitleCase(), cmd);
        }

        // Make a new menu bar, adding a few cascading menus to it.
        MenuBar menu = new MenuBar();
        menu.addItem("File", fileMenu);
        menu.addItem("Program", programMenu);
        topPanel.add(menu);

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
        topPanel.add(callBar);
        RootPanel.get("div-top").add(topPanel);

        // call list panel
        callList.setText(0, 0, "Sequence");
        callList.getFlexCellFormatter().setColSpan(0, 0, 2);
        callList.getRowFormatter().setStyleName(0, "callListHeader");
        callList.setStyleName("callList");
        for (int i=1; i<50; i++) {
            callList.setText(i,0,"Call #"+i);
            Button removeButton = new Button("X");
            removeButton.setStyleName("removeButton");
            callList.setWidget(i, 1, removeButton);
            if (i==25) {
                i++;
                callList.setHTML(i, 0, "<hr/>");
                callList.getFlexCellFormatter().setColSpan(i, 0, 2);
            }
        }
        RootPanel.get("div-calllist").add(callList);

	currentCall.setStyleName("currentCall");
	errorMsg.setStyleName("errorMsg");
        canvasPanel.add(currentCall);
	canvasPanel.add(errorMsg);

        Button playButton = new Button("Play"); // xxx replace with image
        SliderBar playSlider = new SliderBar(0.0, 1.0);
        playSlider.setStepSize(0.1);
        playSlider.setCurrentValue(0.5);
        playSlider.setNumTicks(10);
        playSlider.setNumLabels(10);
        playSlider.setWidth("100%");
        Label levelLabel = new Label("PLUS");
        levelLabel.addStyleName("levelLabel");
        playBar.add(playButton, DockPanel.LINE_START);
        playBar.add(levelLabel, DockPanel.LINE_END);
        playBar.add(playSlider, DockPanel.CENTER);
        playBar.setCellWidth(playSlider, "100%");
        playBar.setCellHorizontalAlignment(playSlider, DockPanel.ALIGN_CENTER);
        playBar.setCellVerticalAlignment(playButton, DockPanel.ALIGN_MIDDLE);
        playBar.setCellVerticalAlignment(levelLabel, DockPanel.ALIGN_MIDDLE);
        playBar.setCellVerticalAlignment(playSlider, DockPanel.ALIGN_MIDDLE);
        RootPanel.get("div-playbar").add(playBar);

        // canvas takes up all the rest of the space
        Widget canvas = danceFloor.widget();
        canvasPanel.add(canvas);
        canvasPanel.setCellHeight(canvas, "100%");
        RootPanel.get("div-canvas").add(canvasPanel);

        // we want to take advantage of the entire client area
        Window.setMargin("0px");
        // set a resize handler to keep all the dimensions in line.
        doResize();
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                doResize(event.getWidth(), event.getHeight());
          }
        });
        // trigger this shortly after load
        Timer resizeTimer = new Timer() {
            @Override
            public void run() { doResize(); }
        };
        resizeTimer.schedule(1);

        // set up default text and handlers for callEntry
        /*
        callEntry.setText("Type a square dance call");
        //callEntry.setSelectionRange(0, callEntry.getText().length());
        // Listen for keyboard events in the input box.
        callEntry.addKeyPressHandler(new KeyPressHandler() {
          public void onKeyPress(KeyPressEvent event) {
            if (event.getCharCode() == KeyCodes.KEY_ENTER) {
              activate();
            }
          }
        });
        */
        callEntry.setFocus(true);
        // Listen for mouse events on the Add button.
        callGo.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            activate();
          }
        });
    }
    void activate() {
        Window.alert("You entered a call");
    }
    void doResize() {
        doResize(Window.getClientWidth(), Window.getClientHeight());
    }
    void doResize(int width, int height) {
        int panelBottom = topPanel.getAbsoluteTop()+topPanel.getOffsetHeight();
        String style = "padding-top: "+panelBottom+"px;";
        RootPanel.get("div-calllist").getElement().setAttribute("style", style);
        RootPanel.get("div-canvas").getElement().setAttribute("style", style);
        // compensate for border of canvasPanel
        //width-=2; height-=2;
        int right = callList.getAbsoluteLeft()+callList.getOffsetWidth();
        right += 5; // padding on right of call list
        style = "padding-left: "+right+"px;";
        canvasPanel.getElement().setAttribute("style", style);
        playBar.getElement().setAttribute("style", style);
        canvasPanel.setHeight((height-panelBottom)+"px");
        if (false) { // iphone hack
            NodeList<Element> nodes = callList.getElement().getElementsByTagName("tbody");
            for (int i=0; i<nodes.getLength(); i++) {
                Element e = nodes.getItem(i);
                if (e!=null) e.setAttribute("style", "height: "+(height-panelBottom-4)+"px;");
            }
        }
    }
}
