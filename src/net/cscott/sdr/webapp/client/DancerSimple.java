package net.cscott.sdr.webapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Widget;

/** Implementation of the Dancer that just uses a pre-composed image. */
public class DancerSimple implements Dancer {
    final Image image = new Image();
    { image.addStyleName("dancer-background"); }

    public Widget widget() { return image; }
    public void drawDancer(int coupleNum, boolean isBoy, double rotation) {
        boolean isBlue = (coupleNum % 2) == 0;
        int rot = (int) Math.round(8*rotation/(2*Math.PI));
        while (rot < 0) rot += 8;
        rot = rot % 8;
        getImage(isBoy, isBlue, rot).applyTo(image);
    }
    private AbstractImagePrototype getImage(boolean isBoy,
					    boolean isBlue,
					    int rot) {
        if (isBoy) {
            switch (rot) {
            default:
            case 0:
                return isBlue? imageBundle.boy_0_bl(): imageBundle.boy_0_rd();
            case 1:
                return isBlue? imageBundle.boy_1_bl(): imageBundle.boy_1_rd();
            case 2:
                return isBlue? imageBundle.boy_2_bl(): imageBundle.boy_2_rd();
            case 3:
                return isBlue? imageBundle.boy_3_bl(): imageBundle.boy_3_rd();
            case 4:
                return isBlue? imageBundle.boy_4_bl(): imageBundle.boy_4_rd();
            case 5:
                return isBlue? imageBundle.boy_5_bl(): imageBundle.boy_5_rd();
            case 6:
                return isBlue? imageBundle.boy_6_bl(): imageBundle.boy_6_rd();
            case 7:
                return isBlue? imageBundle.boy_7_bl(): imageBundle.boy_7_rd();
            }
        } else {
            switch (rot) {
            default:
            case 0:
                return isBlue? imageBundle.girl_0_bl(): imageBundle.girl_0_rd();
            case 1:
                return isBlue? imageBundle.girl_1_bl(): imageBundle.girl_1_rd();
            case 2:
                return isBlue? imageBundle.girl_2_bl(): imageBundle.girl_2_rd();
            case 3:
                return isBlue? imageBundle.girl_3_bl(): imageBundle.girl_3_rd();
            case 4:
                return isBlue? imageBundle.girl_4_bl(): imageBundle.girl_4_rd();
            case 5:
                return isBlue? imageBundle.girl_5_bl(): imageBundle.girl_5_rd();
            case 6:
                return isBlue? imageBundle.girl_6_bl(): imageBundle.girl_6_rd();
            case 7:
                return isBlue? imageBundle.girl_7_bl(): imageBundle.girl_7_rd();
            }
        }
    }
    private final DancerImageBundle imageBundle =
        GWT.create(DancerImageBundle.class);
    public interface DancerImageBundle extends ImageBundle {
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-0-bl.png")
        public AbstractImagePrototype boy_0_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-1-bl.png")
        public AbstractImagePrototype boy_1_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-2-bl.png")
        public AbstractImagePrototype boy_2_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-3-bl.png")
        public AbstractImagePrototype boy_3_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-4-bl.png")
        public AbstractImagePrototype boy_4_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-5-bl.png")
        public AbstractImagePrototype boy_5_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-6-bl.png")
        public AbstractImagePrototype boy_6_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-7-bl.png")
        public AbstractImagePrototype boy_7_bl();

        @Resource("net/cscott/sdr/webapp/client/dancers/boy-0-rd.png")
        public AbstractImagePrototype boy_0_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-1-rd.png")
        public AbstractImagePrototype boy_1_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-2-rd.png")
        public AbstractImagePrototype boy_2_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-3-rd.png")
        public AbstractImagePrototype boy_3_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-4-rd.png")
        public AbstractImagePrototype boy_4_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-5-rd.png")
        public AbstractImagePrototype boy_5_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-6-rd.png")
        public AbstractImagePrototype boy_6_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/boy-7-rd.png")
        public AbstractImagePrototype boy_7_rd();

        @Resource("net/cscott/sdr/webapp/client/dancers/girl-0-bl.png")
        public AbstractImagePrototype girl_0_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-1-bl.png")
        public AbstractImagePrototype girl_1_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-2-bl.png")
        public AbstractImagePrototype girl_2_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-3-bl.png")
        public AbstractImagePrototype girl_3_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-4-bl.png")
        public AbstractImagePrototype girl_4_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-5-bl.png")
        public AbstractImagePrototype girl_5_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-6-bl.png")
        public AbstractImagePrototype girl_6_bl();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-7-bl.png")
        public AbstractImagePrototype girl_7_bl();

        @Resource("net/cscott/sdr/webapp/client/dancers/girl-0-rd.png")
        public AbstractImagePrototype girl_0_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-1-rd.png")
        public AbstractImagePrototype girl_1_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-2-rd.png")
        public AbstractImagePrototype girl_2_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-3-rd.png")
        public AbstractImagePrototype girl_3_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-4-rd.png")
        public AbstractImagePrototype girl_4_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-5-rd.png")
        public AbstractImagePrototype girl_5_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-6-rd.png")
        public AbstractImagePrototype girl_6_rd();
        @Resource("net/cscott/sdr/webapp/client/dancers/girl-7-rd.png")
        public AbstractImagePrototype girl_7_rd();
    }
}