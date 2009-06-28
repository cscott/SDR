package net.cscott.sdr.webapp.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.util.Fraction;

/**
 * The {@link Model} for the UI marries a {@link Sequence} with corresponding
 * {@link EngineResults} (if any), and holds the event mechanisms to tie
 * everything together.  It also holds a few other non-savable bits of
 * UI state, such as whether the sequence is currently playing, what call
 * is currently highlighted, and where the slider is.
 *
 * @author C. Scott Ananian
 */
public class Model implements HasHandlers {
    private SequenceInfo _sequenceInfo;
    private Sequence _sequence;
    private EngineResults _engineResults;

    boolean _isDirty = false;
    boolean isPlaying;
    //Fraction sliderPos;
    int highlightedCall;
    int insertionPoint = -1;

    // accessor methods
    public SequenceInfo getSequenceInfo() { return this._sequenceInfo; }
    public Sequence getSequence() { return this._sequence; }
    public EngineResults getEngineResults() { return this._engineResults; }
    public boolean isDirty() { return this._isDirty; }

    // mutation methods
    public void addCallAtPoint(String s) {
        // XXX insert it at insertionPoint if set
        this._sequence.calls.add(s);
        this._isDirty = true;
        this.fireEvent(new SequenceChangeEvent());
    }
    public void removeCallAt(int index) {
        this._sequence.calls.remove(index);
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
    public void newSequence() {
        // throw away current sequence, start a new one.
        this._sequence = new Sequence();
        this._sequenceInfo = new SequenceInfo("Untitled");
        this._isDirty = false; // nothing to save yet
        this.regenerateTags(); // may fire SequenceInfoChangeEvent
        this.fireEvent(new SequenceInfoChangeEvent());
        this.fireEvent(new SequenceChangeEvent());
    }

    // generate automatic tags from sequence
    public void regenerateTags() {
        // copy old tags, so we can compare them later to the new tags
        List<String> oldTags = new ArrayList<String>
            (_sequenceInfo.automaticTags);
        // generate new automatic tag list.
        _sequenceInfo.automaticTags.clear();
        // 1) add tag based on program
        _sequenceInfo.automaticTags.add(_sequence.program.name().toLowerCase());
        // XXX: add tags based on starting level and resolution type
        // ie: 4-couple singer, 4-couple reverse-singer, unresolved, etc.

        // generate change event if tag list has changed
        if (!oldTags.equals(_sequenceInfo.automaticTags))
            this.fireEvent(new SequenceInfoChangeEvent());
    }

    // --- event infrastructure ---
    // events: sequence changed, results changed?
    //         playState changed, sliderPos changed, highlight changed?
    private final HandlerManager handlerManager = new HandlerManager(this);
    public void fireEvent(GwtEvent<?> event) {
        this.handlerManager.fireEvent(event);
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
    // initialize
    { this.newSequence(); }
}