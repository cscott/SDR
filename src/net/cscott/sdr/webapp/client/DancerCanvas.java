package net.cscott.sdr.webapp.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Widget;

/** Implementation which draws a dancer using a canvas. */
public class DancerCanvas implements Dancer {
    final Canvas canvas;
    final Context2d context;
    private final static CssColor[] DANCER_COLORS = new CssColor[] {
      CssColor.make("red"),
      CssColor.make("green"),
      CssColor.make("blue"),
      CssColor.make("yellow")
    };
    private final static CssColor WHITE = CssColor.make("white");
    private final static CssColor BLACK = CssColor.make("black");

    private final int size = 40;
    public DancerCanvas(Canvas c) {
        canvas = c;
        canvas.setHeight(size+"px");
        canvas.setWidth(size+"px");
        canvas.setCoordinateSpaceHeight(size);
        canvas.setCoordinateSpaceWidth(size);
        canvas.addStyleName("dancer-background");
        context = canvas.getContext2d();
    }
    public Widget widget() { return canvas; }
    public int getSize() { return size; }
    public void drawDancer(int coupleNum, boolean isBoy, double rotation) {
        CssColor c = DANCER_COLORS[coupleNum % 4];
        context.save();
        context.clearRect(0,0,size,size);
        context.translate(size/2., size/2.);
        context.scale(size/100., size/100.);
        context.rotate(rotation);
        context.translate(-50,-50);
        if (isBoy) drawBoy(c); else drawGirl(c);
        drawCenter();
        context.restore();
    }
    private void drawBoy(CssColor c) {
        double tw = 1.25;
        context.setFillStyle(c);
        context.setStrokeStyle("black");
        context.setLineWidth(2.5);
        context.beginPath();
        context.moveTo(20+tw,20+tw);
        context.lineTo(20+tw,80-tw);
        context.lineTo(80-tw,80-tw);
        context.lineTo(80-tw,20+tw);
        context.lineTo(60-tw,20+tw);
        context.lineTo(60-tw,5+tw);
        context.lineTo(40+tw,5+tw);
        context.lineTo(40+tw,20+tw);
        context.closePath();
        context.fill();
        context.stroke();
    }
    private void drawGirl(CssColor c) {
        double tw = 1.25;
        context.setFillStyle(c);
        context.setStrokeStyle(BLACK);
        context.setLineWidth(2.5);
        context.beginPath();
        double nose = Math.toRadians(17);
        context.arc(50,50,30+tw,3*Math.PI/2+nose,3*Math.PI/2-nose,false);
        context.lineTo(50,5+tw);
        context.closePath();
        context.fill();
        context.stroke();
    }
    private void drawCenter() {
        double tw = 1.25;
        context.setFillStyle(WHITE);
        context.setStrokeStyle(BLACK);
        context.setLineWidth(2.5);
        context.beginPath();
        context.moveTo(70,50);
        context.arc(50, 50, 20+tw, 0, 2*Math.PI, true);
        context.fill();
        context.stroke();
    }
}
