package net.cscott.sdr.webapp.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;

public class DanceFloor extends AbsolutePanel {
    public final int dancerSize = 40; /* pixels */
    private int width, height;

    private List<CompositeDancer> dancers = new ArrayList<CompositeDancer>(8);
    public DanceFloor() {
        addStyleName("dance-floor");
    }
    void updateCenter() {
        int oldWidth = width, oldHeight = height;
        this.width = getOffsetWidth();
        this.height = getOffsetHeight();
        if (width!=oldWidth || height!=oldHeight)
            for (CompositeDancer d: dancers)
                updatePosition(d);
    }

    void setNumDancers(int n) {
        while (dancers.size() < n) {
            int i = dancers.size();
            CompositeDancer d = new CompositeDancer(i/2, (i%2)==0);
            dancers.add(d);
            this.add(d, 0, 0);
        }
        while (dancers.size() > n) {
            dancers.remove(dancers.size()-1).removeFromParent();
        }
    }
    void update(int dancerNum, Position pos) {
        update(dancerNum, pos.x, pos.y, pos.rot);
    }
    void update(int dancerNum, double x, double y, double rotation) {
        // adjust x and y to dance floor coordinates, where
        // 2 units == 1 dancer size
        CompositeDancer d = dancers.get(dancerNum);
        d.update(rotation);
        d.x = x;
        d.y = y;
        updatePosition(d);
    }
    private void updatePosition(CompositeDancer d) {
        double cx = (this.width + d.x*dancerSize)/2.;
        double cy = (this.height - d.y*dancerSize)/2.;
        double offset = d.getSize()/2.;
        setWidgetPosition(d, (int)Math.round(cx-offset),
                          (int)Math.round(cy-offset));
    }

    static class CompositeDancer extends AbsolutePanel {
        /** The background image. */
        final Dancer dancer = GWT.create(Dancer.class);
        /** The dancer number, as text.  Must be a table element because
         * only td has a functional vertical-align property.
         */
        final Grid label = new Grid(1,1);
        /** Couple # */
        final int coupleNum;
        /** Is this a boy? */
        final boolean isBoy;
        /** Position in dance floor coordinates (not pixels) */
        double x, y;
        CompositeDancer(int coupleNum, boolean isBoy) {
            this.coupleNum = coupleNum;
            this.isBoy = isBoy;
            // first the image
            this.add(dancer.widget(), 0, 0);
            // then the label
            this.add(label, 0, 0);
            label.setHTML(0, 0, Integer.toString(coupleNum+1));
            label.addStyleName("dancer-number");
            this.addStyleName("dancer");
            this.setPixelSize(getSize(), getSize());
        }
        void update(double rotation) {
            this.dancer.drawDancer(coupleNum, isBoy, rotation);
        }
        int getSize() { return this.dancer.getSize(); }
    }
}
