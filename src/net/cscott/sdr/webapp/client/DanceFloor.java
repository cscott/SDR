package net.cscott.sdr.webapp.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * Interface to the DanceFloor component, so that we can swap out the
 * {@link DanceFloorCanvas} implementation on platforms which don't
 * support the canvas tag (like in hosted mode).
 * @author C. Scott Ananian
 */
public interface DanceFloor {
    Widget widget();
}
