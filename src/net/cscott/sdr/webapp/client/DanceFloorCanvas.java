package net.cscott.sdr.webapp.client;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class DanceFloorCanvas implements DanceFloor {
    final GWTCanvas canvas = new GWTCanvas();
    public DanceFloorCanvas() {
        // just scribble in a bit of green to let us know it's working
        canvas.setLineWidth(1);
        canvas.setStrokeStyle(Color.GREEN);

        canvas.beginPath();
        canvas.moveTo(1,1);
        canvas.lineTo(1,50);
        canvas.lineTo(50,50);
        canvas.lineTo(50, 1);
        canvas.closePath();
        canvas.stroke();
    }
    public Widget widget() { return canvas; }
}
