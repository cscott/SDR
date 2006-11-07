package net.cscott.sdr.anim;
/*
 * @(#)ImageOps.java    1.2 98/07/09
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class TextTest extends JApplet {

    private BufferedImage bi;
    private final String text = "TIMING & FLOW";
    private final int pointSize = 24;
    private final int textureSize = 512;

    public void init() {
        setBackground(Color.white);
        bi = new BufferedImage(textureSize,textureSize,BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = bi.createGraphics();
        Font f= font.deriveFont((float)pointSize);
        
        TextLayout layout = new TextLayout(text, f, g2.getFontRenderContext());
        // okay, get aspect ratio
        Rectangle2D bounds = layout.getBounds();
        int nStrips = (int) Math.ceil(Math.sqrt(bounds.getWidth() / bounds.getHeight()));
        int stripHeight = (int) Math.floor(textureSize/(double)nStrips);
        int stripWidth = nStrips*textureSize;//textureSize*(int)Math.ceil(stripHeight*bounds.getWidth()/bounds.getHeight()/textureSize);

        BufferedImage wide = new BufferedImage(stripWidth,stripHeight,BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D wg2 = (Graphics2D) wide.getGraphics();
        wg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        wg2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        wg2.setColor(Color.white);
        double scale = stripHeight / bounds.getHeight();

        wg2.setFont(f);
        wg2.scale(scale,scale);
        wg2.drawString(text,(float)-bounds.getX(),(float)-bounds.getY());
        // now transfer strips to our parent image.
        for (int i=0; i<nStrips; i++) {
            BufferedImage sub = wide.getSubimage(i*textureSize,0,textureSize,stripHeight);
            g2.drawRenderedImage(sub,AffineTransform.getTranslateInstance(0,i*stripHeight));
        }
    }



    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        
        g2.setFont(font.deriveFont(10f));
        GradientPaint greygrad = new GradientPaint(0,0,Color.gray,400,400,Color.black);
        g2.setPaint(greygrad);
        g2.fill(new Rectangle2D.Double(4,14,514,514));
        
        int x = 0, y = 0;
        
        AffineTransform at = new AffineTransform();
        
        BufferedImageOp biop = null;
        
        x = 5; y = 15;
        biop = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BILINEAR);
        
        g2.drawImage(bi,biop,x,y); 
        TextLayout tl = new TextLayout("CSA!", g2.getFont(),g2.getFontRenderContext());
        g2.setColor(Color.black);
        tl.draw(g2, (float) x, (float) y-4);
    }

    protected URL getURL(String filename) {
        return TextTest.class.getClassLoader().getResource      
            ("net/cscott/sdr/anim/"+filename);
    }
    private static Font font = null;
    static {
        try {
            URL url = TextTest.class.getClassLoader().getResource      
            ("net/cscott/sdr/fonts/exprswy_free.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public static void main(String s[]) {
        JFrame f = new JFrame("ImageOps");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        JApplet applet = new TextTest();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(550,550));
        f.show();
    }

}
