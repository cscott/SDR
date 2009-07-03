package net.cscott.sdr.webapp.client;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.Program;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The {@link Model} for the UI marries a {@link Sequence} with corresponding
 * {@link EngineResults} (if any), and holds the event mechanisms to tie
 * everything together.  It also holds a few other non-save-able bits of
 * UI state, such as whether the sequence is currently playing, what call
 * is currently highlighted, and where the slider is.
 *
 * @author C. Scott Ananian
 */
public class Model implements HasHandlers {
    private SequenceInfo _sequenceInfo;
    private Sequence _sequence;
    private EngineResults _engineResults;
    private int _sequenceChangeIndex = 0;

    private boolean _isDirty = false;
    private boolean _isPlaying = false;
    private double _sliderPos = 0;
    int highlightedCall;
    int insertionPoint = -1;

    // constructor
    public Model(final DanceEngineServiceAsync danceEngine) {
        // when the sequence changes, update the engine results
        this.addSequenceChangeHandler(new SequenceChangeHandler() {
            public void onSequenceChange(SequenceChangeEvent sce) {
                _sequenceChangeIndex++;
                danceEngine.dance(_sequence, _sequenceChangeIndex,
                                  new AsyncCallback<EngineResults>() {
                    public void onFailure(Throwable caught) {
                        handleFailure(caught);
                    }
                    public void onSuccess(EngineResults result) {
                        if (result.sequenceNumber != _sequenceChangeIndex)
                            return; // stale results, discard
                        Model.this._engineResults = result;
                        fireEvent(new EngineResultsChangeEvent());
                    }});
            }});
        // initialize
        this.newSequence();
    }
    // implement this in a subclass to provide better (ie, any) error handling
    public void handleFailure(Throwable caught) {
        /* Do nothing. That's not very good error handling. */
    }

    // accessor methods
    public SequenceInfo getSequenceInfo() { return this._sequenceInfo; }
    public Sequence getSequence() { return this._sequence; }
    public EngineResults getEngineResults() { return this._engineResults; }
    public boolean isDirty() { return this._isDirty; }
    public boolean isPlaying() { return this._isPlaying; }
    public double getSliderPos() { return this._sliderPos; }

    // mutation methods
    public void addCallAtPoint(String s) {
        // XXX insert it at insertionPoint if set
        this._sequence.calls.add(s);
        this._isDirty = true;
        this.fireEvent(new SequenceChangeEvent());
    }
    public void removeCallAt(int index) {
        this._sequence.calls.remove(index);
        this._isDirty = true;
        this.fireEvent(new SequenceChangeEvent());
    }
    public void setProgram(Program p) {
        if (p == _sequence.program) return;
        // set new program
        _sequence.program = p;
        if (!this._sequence.calls.isEmpty())
            // don't force save if new program is only state change
            this._isDirty = true;
        this.regenerateTags(); // may fire SequenceInfoChangeEvent
        this.fireEvent(new SequenceChangeEvent());
    }
    public void setTitle(String title) {
        if (_sequenceInfo.title.equals(title))
            return; // no change
        _sequenceInfo.title = title;
        this.fireEvent(new SequenceInfoChangeEvent());
    }
    public void newSequence() {
        // throw away current sequence, start a new one.
        this.load(new SequenceInfo(SequenceInfo.UNTITLED), new Sequence());
        this._isPlaying = false;
        this._sliderPos = 0;
        this.fireEvent(new PlayStatusChangeEvent());
    }
    public void load(SequenceInfo info, Sequence sequence) {
        this._sequenceInfo = info;
        this._sequence = sequence;
        this._isDirty = false; // nothing to save yet
        this.regenerateTags(); // may fire SequenceInfoChangeEvent
        this.fireEvent(new SequenceInfoChangeEvent());
        this.fireEvent(new SequenceChangeEvent());
    }
    public void clean() {
        this._isDirty = false;
    }
    public void setPlaying(boolean isPlaying) {
        if (this._isPlaying == isPlaying)
            return; /* no change */
        this._isPlaying = isPlaying;
        this.fireEvent(new PlayStatusChangeEvent());
    }
    public void setSliderPos(double sliderPos) {
        this._sliderPos = sliderPos;
        this.fireEvent(new PlayStatusChangeEvent());
    }

    // generate automatic tags from sequence
    public void regenerateTags() {
        // copy old tags, so we can compare them later to the new tags
        List<String> oldTags = new ArrayList<String>(_sequenceInfo.tags);
        // clear all automatic tags from the list
        _sequenceInfo.tags.clear();
        // generate new automatic tag list.
        // 1) add manual tags
        _sequenceInfo.tags.addAll(_sequenceInfo.manualTags);
        // 2) add tag based on program
        _sequenceInfo.tags.add(_sequence.program.name().toLowerCase());
        // XXX: add tags based on starting level and resolution type
        // ie: 4-couple singer, 4-couple reverse-singer, unresolved, etc.

        // generate change event if tag list has changed
        if (!oldTags.equals(_sequenceInfo.tags))
            this.fireEvent(new SequenceInfoChangeEvent());
    }

    // --- event infrastructure ---
    // events: sequence changed, results changed?
    //         playState changed, sliderPos changed, highlight changed?
    private final HandlerManager handlerManager = new HandlerManager(this);
    public void fireEvent(GwtEvent<?> event) {
        this.handlerManager.fireEvent(event);
    }

    // play status change event
    public HandlerRegistration addPlayStatusChangeHandler(PlayStatusChangeHandler handler) {
        return this.handlerManager.addHandler(PlayStatusChangeEvent.TYPE, handler);
    }
    public static interface PlayStatusChangeHandler extends EventHandler {
        void onPlayStatusChange(PlayStatusChangeEvent sce);
    }
    static class PlayStatusChangeEvent extends GwtEvent<PlayStatusChangeHandler> {
        public static final GwtEvent.Type<PlayStatusChangeHandler> TYPE =
            new GwtEvent.Type<PlayStatusChangeHandler>();
        @Override
        public Model getSource() { return (Model) super.getSource(); }
        @Override
        protected void dispatch(PlayStatusChangeHandler handler) {
            handler.onPlayStatusChange(this);
        }
        @Override
        public GwtEvent.Type<PlayStatusChangeHandler> getAssociatedType() {
            return TYPE;
        }
    }

    // sequence info change event
    public HandlerRegistration addSequenceInfoChangeHandler(SequenceInfoChangeHandler handler) {
        return this.handlerManager.addHandler(SequenceInfoChangeEvent.TYPE, handler);
    }
    public static interface SequenceInfoChangeHandler extends EventHandler {
        void onSequenceInfoChange(SequenceInfoChangeEvent sce);
    }
    static class SequenceInfoChangeEvent extends GwtEvent<SequenceInfoChangeHandler> {
        public static final GwtEvent.Type<SequenceInfoChangeHandler> TYPE =
            new GwtEvent.Type<SequenceInfoChangeHandler>();
        @Override
        public Model getSource() { return (Model) super.getSource(); }
        @Override
        protected void dispatch(SequenceInfoChangeHandler handler) {
            handler.onSequenceInfoChange(this);
        }
        @Override
        public GwtEvent.Type<SequenceInfoChangeHandler> getAssociatedType() {
            return TYPE;
        }
    }

    // sequence change event
    public HandlerRegistration addSequenceChangeHandler(SequenceChangeHandler handler) {
        return this.handlerManager.addHandler(SequenceChangeEvent.TYPE, handler);
    }
    public static interface SequenceChangeHandler extends EventHandler {
        void onSequenceChange(SequenceChangeEvent sce);
    }
    static class SequenceChangeEvent extends GwtEvent<SequenceChangeHandler> {
        public static final GwtEvent.Type<SequenceChangeHandler> TYPE =
            new GwtEvent.Type<SequenceChangeHandler>();
        @Override
        public Model getSource() { return (Model) super.getSource(); }
        @Override
        protected void dispatch(SequenceChangeHandler handler) {
            handler.onSequenceChange(this);
        }
        @Override
        public GwtEvent.Type<SequenceChangeHandler> getAssociatedType() {
            return TYPE;
        }
    }

    // engine results change event
    public HandlerRegistration addEngineResultsChangeHandler(EngineResultsChangeHandler handler) {
        return this.handlerManager.addHandler(EngineResultsChangeEvent.TYPE, handler);
    }
    public static interface EngineResultsChangeHandler extends EventHandler {
        void onEngineResultsChange(EngineResultsChangeEvent sce);
    }
    static class EngineResultsChangeEvent extends GwtEvent<EngineResultsChangeHandler> {
        public static final GwtEvent.Type<EngineResultsChangeHandler> TYPE =
            new GwtEvent.Type<EngineResultsChangeHandler>();
        @Override
        protected void dispatch(EngineResultsChangeHandler handler) {
            handler.onEngineResultsChange(this);
        }
        @Override
        public GwtEvent.Type<EngineResultsChangeHandler> getAssociatedType() {
            return TYPE;
        }
    }
}