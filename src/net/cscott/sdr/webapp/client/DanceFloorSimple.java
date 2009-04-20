package net.cscott.sdr.webapp.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/** Implementation of the DanceFloor that just uses a multiline label. */
public class DanceFloorSimple implements DanceFloor {
    final Label canvas = new Label("asdas");
    public Widget widget() { return canvas; }
}
