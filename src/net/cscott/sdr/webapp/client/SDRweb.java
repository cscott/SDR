package net.cscott.sdr.webapp.client;

import java.util.List;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.webapp.client.Model.SequenceChangeEvent;
import net.cscott.sdr.webapp.client.Model.SequenceChangeHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

// incubator
import com.google.gwt.widgetideas.client.SliderBar;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SDRweb implements EntryPoint, SequenceChangeHandler {
    final CallOracle callOracle = new CallOracle();
    final SuggestBox callEntry = new SuggestBox(callOracle);
    final FlexTable callList = new FlexTable();
    final Label currentCall = new Label();
    final Label errorMsg = new Label();
    final VerticalPanel topPanel = new VerticalPanel();
    final VerticalPanel canvasPanel = new VerticalPanel();
    final DanceFloor danceFloor = GWT.create(DanceFloor.class);
    DockPanel playBar = new DockPanel();

    final Model model = new Model();

    public boolean confirmDiscard() {
        if (!model.isDirty()) return true; // nothing to save
        return Window.confirm("Are you sure you want to discard the "+
                              "current unsaved sequence?");
    }
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        topPanel.setWidth("100%");
        // Menu bar
        MenuBar fileMenu = new MenuBar(true);
        fileMenu.addItem("New", new Command() {
            public void execute() {
                if (!confirmDiscard()) return;
                model.newSequence();
            }});
        fileMenu.addItem("Open", new Command() {
            public void execute() {
                if (!confirmDiscard()) return;
                Window.alert("Open not yet implemented."); }
        });
        fileMenu.addItem("Save", new Command() {
            public void execute() { Window.alert("Save not yet implemented."); }
        });
        fileMenu.addItem("Print", new Command() {
            public void execute() {
                // XXX: in the future we'd open a window with a
                // better-formatted version, and print *that*
                Window.print();
            }});
        fileMenu.addItem("Close", new Command() {
            public native void execute() /*-{ $wnd.close(); }-*/;
            });
        Window.addWindowClosingHandler(new ClosingHandler() {
            public void onWindowClosing(ClosingEvent event) {
                if (model.isDirty())
                    event.setMessage("This will discard the current sequence.");
            }});

        MenuBar programMenu = new MenuBar(true);
        for (Program p: Program.values()) {
            final Program pp = p;
            programMenu.addItem(p.toTitleCase(), new Command() {
                public void execute() {
                    model.setProgram(pp);
                }});
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
        Button callGo = new Button("Add");
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
        // level label
        final Label levelLabel = new Label("--");
        levelLabel.addStyleName("levelLabel");
        model.addSequenceChangeHandler(new SequenceChangeHandler() {
            public void onSequenceChange(SequenceChangeEvent sce) {
                Program p = sce.getSource().getSequence().program;
                String s = (p==Program.MAINSTREAM)?"MS":p.name().toUpperCase();
                levelLabel.setText(s);
            }});
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
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                doResize(event.getWidth(), event.getHeight());
          }
        });

        // set up default text and handlers for callEntry
        //callEntry.setText("Type a square dance call");
        // Listen for keyboard events in the input box.
        callEntry.getTextBox().addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER &&
                    !callEntry.isSuggestionListShowing()) {
                    activate();
                }
            }});
        callEntry.getTextBox().addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                selectCall();
            }});
        // Listen for mouse events on the Add button.
        callGo.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            activate();
          }
        });
        // hook up model
        model.addSequenceChangeHandler(this);
        model.addSequenceChangeHandler(new SequenceChangeHandler() {
            public void onSequenceChange(SequenceChangeEvent sce) {
                callOracle.setProgram(sce.getSource().getSequence().program);
            }});
        // initialize all the model-dependent fields
        model.fireEvent(new SequenceChangeEvent());
        // trigger resize & focus shortly after load
        Timer postLoadTimer = new Timer() {
            @Override
            public void run() { doResize(); callEntry.setFocus(true); }
        };
        postLoadTimer.schedule(1);
    }

    void activate() {
        String newCall = callEntry.getText();
        this.selectCall();
        this.model.addCallAtPoint(newCall);
    }
    void selectCall() {
        callEntry.getTextBox()
            .setSelectionRange(0, callEntry.getText().length());
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
    public void onSequenceChange(SequenceChangeEvent sce) {
        // build the call list from the model
        final Model model = sce.getSource();
        FlexCellFormatter fcf = callList.getFlexCellFormatter();
        int row=1; // row number
        List<String> calls = model.getSequence().calls;
        for (int callIndex=0; callIndex<calls.size(); callIndex++, row++) {
            String call = calls.get(callIndex);
            fcf.setColSpan(row, 0, 1);
            callList.setText(row, 0, call);
            Button removeButton = new Button("X");
            removeButton.setStyleName("removeButton");
            final int ci = callIndex; // for use in click handler
            removeButton.addClickHandler(new ClickHandler(){
                public void onClick(ClickEvent event) {
                    model.removeCallAt(ci);
                }});
            fcf.setColSpan(row, 1, 1);
            callList.setWidget(row, 1, removeButton);
            if (row == model.insertionPoint) {
                row++;
                callList.removeCell(row, 1);
                fcf.setColSpan(row, 0, 2);
                callList.setHTML(row, 0, "<hr/>");
            }
        }
        // remove other rows
        for (int j=callList.getRowCount()-1; j>=row; j--)
            callList.removeRow(j);
        if (calls.isEmpty()) {
            // add a place holder for the actual calls
            callList.getFlexCellFormatter().setColSpan(1, 0, 2);
            callList.setHTML(1, 0, "<i>&nbsp;(no calls yet)&nbsp;</i>");
        }
        doResize();
    }
}
