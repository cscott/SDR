package net.cscott.sdr.webapp.client;

import java.util.List;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.webapp.client.Model.EngineResultsChangeEvent;
import net.cscott.sdr.webapp.client.Model.EngineResultsChangeHandler;
import net.cscott.sdr.webapp.client.Model.PlayStatusChangeEvent;
import net.cscott.sdr.webapp.client.Model.PlayStatusChangeHandler;
import net.cscott.sdr.webapp.client.Model.SequenceChangeEvent;
import net.cscott.sdr.webapp.client.Model.SequenceChangeHandler;
import net.cscott.sdr.webapp.client.Model.SequenceInfoChangeEvent;
import net.cscott.sdr.webapp.client.Model.SequenceInfoChangeHandler;

import com.google.gwt.animation.client.Animation;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
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
public class SDRweb implements EntryPoint, SequenceChangeHandler, PlayStatusChangeHandler {
    public static final double BPM = 128;

    final CallOracle callOracle = new CallOracle();
    final SuggestBox callEntry = new SuggestBox(callOracle);
    final FlexTable callList = new FlexTable();
    final Label currentCall = new Label();
    final Label errorMsg = new Label();
    final VerticalPanel topPanel = new VerticalPanel();
    final VerticalPanel canvasPanel = new VerticalPanel();
    final DanceFloor danceFloor = GWT.create(DanceFloor.class);
    final MenuItem sequenceTitle =
        new MenuItem(SequenceInfo.UNTITLED, (Command)null);
    final SliderBar playSlider = new SliderBar(0.0, 1.0);
    DockPanel playBar = new DockPanel();
    Animation animation = null;

    final Model model = new Model(GWT.<DanceEngineServiceAsync>create
                                  (DanceEngineService.class)) {
        @Override
        public void handleFailure(Throwable caught) {
            Window.alert(caught.toString());
        }
    };
    SequenceStorageServiceAsync storageService =
        GWT.create(SequenceStorageService.class);

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
                doOpen();
            }
        });
        fileMenu.addItem("Save", new Command() {
            public void execute() { doSave(); }
        });
        fileMenu.addItem("Print", new Command() {
            public void execute() {
                // XXX: in the future we'd open a window with a
                // better-formatted version, and print *that*
                Window.print();
            }});
        fileMenu.addItem("Logout", new Command() {
            public void execute() {
                storageService.logout(new EAsyncCallback<String>() {
                    public void onSuccess(String logoutURL) {
                        new SdrPopup("Logout from Google", logoutURL) {
                            @Override
                            public void onClose() {
                                Window.alert("You are now logged out");
                            }};
                    }});
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

        model.addSequenceInfoChangeHandler(new SequenceInfoChangeHandler() {
            public void onSequenceInfoChange(SequenceInfoChangeEvent sce) {
                sequenceTitle.setText(sce.getSource().getSequenceInfo().title);
            }});

        // Make a new menu bar, adding a few cascading menus to it.
        MenuBar menu = new MenuBar();
        menu.addItem("File", fileMenu);
        menu.addItem("Program", programMenu);
        menu.addSeparator();
        menu.addItem(sequenceTitle);
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

        final Button playButton = new Button("");
        model.addPlayStatusChangeHandler(new PlayStatusChangeHandler() {
            public void onPlayStatusChange(PlayStatusChangeEvent sce) {
                String icon = model.isPlaying() ? "Pause" : "Play";
                playButton.setHTML("<img src=\"/icons/stock_media-"+
                                   icon.toLowerCase()+".png\" "+
                                   "alt=\""+icon+"\" title=\""+icon+"\" />");
            }});
        playButton.addStyleName("playButton");
        playButton.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event) {
                // if we pressed play, but were already at the end, jump to the start
                if ((!model.isPlaying()) && model.getEngineResults()!=null &&
                    model.getEngineResults().totalBeats - model.getSliderPos() < 0.5)
                    model.setSliderPos(0);
                // toggle play status
                model.setPlaying(!model.isPlaying());
            }});
        playSlider.setStepSize(0.1);
        playSlider.setCurrentValue(0);
        playSlider.setNumTicks(1);
        playSlider.setNumLabels(1);
        playSlider.setWidth("100%");
        playSlider.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                model.setSliderPos(playSlider.getCurrentValue());
            }});
        model.addEngineResultsChangeHandler(new EngineResultsChangeHandler() {
            public void onEngineResultsChange(EngineResultsChangeEvent sce) {
                double totalBeats = model.getEngineResults().totalBeats;
                playSlider.setMaxValue(totalBeats);
                playSlider.setNumTicks((int)Math.round(totalBeats));
            }});
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
        model.addPlayStatusChangeHandler(this);
        model.addSequenceChangeHandler(this);
        model.addSequenceChangeHandler(new SequenceChangeHandler() {
            public void onSequenceChange(SequenceChangeEvent sce) {
                callOracle.setProgram(sce.getSource().getSequence().program);
            }});
        // initialize all the model-dependent fields
        model.fireEvent(new SequenceInfoChangeEvent());
        model.fireEvent(new SequenceChangeEvent());
        // trigger resize & focus shortly after load
        Timer postLoadTimer = new Timer() {
            @Override
            public void run() { doResize(); callEntry.setFocus(true); }
        };
        postLoadTimer.schedule(1);
    }

    void selectSequence(AsyncCallback<SequenceInfo> cb) {
        Window.alert("select one");
    }
    void doOpen() {
        selectSequence(new EAsyncCallback<SequenceInfo>() {
            public void onSuccess(final SequenceInfo info) {
                storageService.load(info.id, new EAsyncCallback<Sequence>(){
                    public void onSuccess(Sequence sequence) {
                        model.load(info, sequence);
                    }});
            }});
    }
    void doSave() {
        model.regenerateTags(); // ensure automatic tags are up-to-date
        if (model.getSequenceInfo().title.equals(SequenceInfo.UNTITLED))
            model.setTitle(Window.prompt("Title for this sequence",
                                         model.getSequenceInfo().title));
        // XXX ask for tags; use nicer interface
        storageService.save(model.getSequenceInfo(), model.getSequence(),
                            new LAsyncCallback<Long>() {
            @Override
            public void onSuccess(Long result) {
                model.getSequenceInfo().id = result;
                model.clean();
                Window.alert("Saved!");
            }
            @Override
            public void retry() { doSave(); /* retry */ }
        });
    }
    /** Create a new popup which logs into/out of Google and then closes. */
    public static abstract class SdrPopup extends PopupPanel {
        public SdrPopup(String caption, String loginUrl) {
            CaptionPanel cp = new CaptionPanel
                (caption+" (<a href=\"javascript:cancelPopup()\">close</a>)",
                 true);
            Frame frame = new Frame(loginUrl);
            cp.add(frame);
            setTitle("Login with your Google ID");
            cp.setWidth((Window.getClientWidth()*3/4)+"px");
            cp.setHeight((Window.getClientHeight()*3/4)+"px");
            frame.setStyleName("login-popup-frame");
            cp.addStyleName("login-popup-caption");
            this.addStyleName("login-popup");
            setWidget(cp);
            setPopup(this);
            this.center(); // and show
        }
        public final void closeMe() {
            this.hide();
            // okay, proceed.
            onClose();
        }
        public final void cancelMe() {
            this.hide();
            // but don't call onClose
        }
        /* called after login */
        protected abstract void onClose();
    }
    // stash a reference to the static 'hidePopup' method in a place where the
    // inner iframe can get to it.
    public static native void setPopup(SdrPopup p) /*-{
        $wnd.cancelPopup = function() {
            p.@net.cscott.sdr.webapp.client.SDRweb.SdrPopup::cancelMe()();
        };
        $wnd.hidePopup = function() {
            p.@net.cscott.sdr.webapp.client.SDRweb.SdrPopup::closeMe()();
        };
    }-*/;
    // --- end popup support

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
    public void onPlayStatusChange(PlayStatusChangeEvent sce) {
        if (model.isPlaying()) {
            if (this.animation == null)
                this.newAnimation();
        } else {
            if (this.animation != null)
                this.animation.cancel();
            this.animation = null;
        }
    }
    private void newAnimation() {
        if (this.animation!=null)
            this.animation.cancel();
        if (model.getEngineResults()==null) {
            model.setPlaying(false);
            return;
        }
        final double start = model.getSliderPos();
        final double end = model.getEngineResults().totalBeats;
        final double totalMillis = (end-start)*(60*1000/*one minute*/)/BPM;
        if (totalMillis < 100) {
            model.setPlaying(false);
            return;
        }
        this.animation = new Animation() {
            @Override
            protected double interpolate(double progress) {
                return start + progress * (end-start);
            }
            @Override
            protected void onUpdate(double beat) {
                boolean last = false;
                if (beat > model.getEngineResults().totalBeats) {
                    // whoops, sequence has shrunk!
                    beat = model.getEngineResults().totalBeats;
                    last = true;
                }
                playSlider.setCurrentValue(beat, false);
                model.setSliderPos(beat);
                // XXX: update the dancers (via playstatus event?)
                if (last) newAnimation();
            }
            @Override
            protected void onComplete() {
                // keep playing, maybe the sequence has grown.
                newAnimation();
            }
            @Override
            protected void onCancel() {
                /* do nothing, just stop */
            }};
        this.animation.run((int)totalMillis);
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
    /** Callback interface which handles errors (in a very simplistic way). */
    static abstract class EAsyncCallback<T> implements AsyncCallback<T> {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }
        public abstract void onSuccess(T result);
    }
    /** Callback interface which retries if the user is not yet logged in. */
    abstract class LAsyncCallback<T> extends EAsyncCallback<T> {
        @Override
        public final void onFailure(Throwable error) {
            if (error instanceof NotLoggedInException) {
                NotLoggedInException nlie = (NotLoggedInException) error;
                new SdrPopup("Login to Google", nlie.loginUrl) {
                    @Override
                    public void onClose() { retry(); }
                };
                return;
            }
            super.onFailure(error);
        }
        public abstract void retry();
    }
}
