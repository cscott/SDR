package net.cscott.sdr.webapp.client;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

/** Implementation which draws a dancer using a canvas. */
public class DancerCanvas implements Dancer {
    final GWTCanvas canvas = new GWTCanvas();
    private final int size = 40;
    public DancerCanvas() {
        canvas.setHeight(size+"px");
        canvas.setWidth(size+"px");
        canvas.setCoordSize(size, size);
        canvas.addStyleName("dancer-background");
    }
    public Widget widget() { return canvas; }
    public void drawDancer(int coupleNum, boolean isBoy, double rotation) {
        Color c;
        switch (coupleNum % 4) {
        default:
        case 0: c=Color.RED; break;
        case 1: c=Color.GREEN; break;
        case 2: c=Color.BLUE; break;
        case 3: c=Color.YELLOW; break;
        }
        canvas.saveContext();
        canvas.clear();
        canvas.translate(size/2., size/2.);
        canvas.scale(size/100., size/100.);
        canvas.rotate(rotation);
        canvas.translate(-50,-50);
        if (isBoy) drawBoy(c); else drawGirl(c);
        drawCenter();
        canvas.restoreContext();
    }
    private void drawBoy(Color c) {
        double tw = 1.25;
        canvas.setFillStyle(c);
        canvas.setStrokeStyle(Color.BLACK);
        canvas.setLineWidth(2.5);
        canvas.beginPath();
        canvas.moveTo(20+tw,20+tw);
        canvas.lineTo(20+tw,80-tw);
        canvas.lineTo(80-tw,80-tw);
        canvas.lineTo(80-tw,20+tw);
        canvas.lineTo(60-tw,20+tw);
        canvas.lineTo(60-tw,5+tw);
        canvas.lineTo(40+tw,5+tw);
        canvas.lineTo(40+tw,20+tw);
        canvas.closePath();
        canvas.fill();
        canvas.stroke();
    }
    private void drawGirl(Color c) {
        double tw = 1.25;
        canvas.setFillStyle(c);
        canvas.setStrokeStyle(Color.BLACK);
        canvas.setLineWidth(2.5);
        canvas.beginPath();
        double nose = Math.toRadians(17);
        canvas.arc(50,50,30+tw,3*Math.PI/2+nose,3*Math.PI/2-nose,false);
        canvas.lineTo(50,5+tw);
        canvas.closePath();
        canvas.fill();
        canvas.stroke();
    }
    private void drawCenter() {
        double tw = 1.25;
        canvas.setFillStyle(Color.WHITE);
        canvas.setStrokeStyle(Color.BLACK);
        canvas.setLineWidth(2.5);
        canvas.beginPath();
        canvas.moveTo(70,50);
        canvas.arc(50, 50, 20+tw, 0, 2*Math.PI, true);
        canvas.fill();
        canvas.stroke();
    }
}
