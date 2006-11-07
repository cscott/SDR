package net.cscott.sdr.anim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

/** This class is a simple test harness to develop the text texture-generation
 * code.
 * @author C. Scott Ananian
 * @version $Id: TextTest.java,v 1.3 2006-11-07 18:11:41 cananian Exp $
 */
public class TextTest extends JApplet {

    private BufferedImage bi;
    private final String text = "TIMING & FLOW (oh my!)";
    private final int pointSize = 24;
    private final int textureSize = 512;

    public void init() {
        setBackground(Color.white);
        bi = new BufferedImage(textureSize,textureSize,BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(Color.white);

        Font f= font.deriveFont((float)pointSize);
        g2.setFont(f);
        
        TextLayout layout = new TextLayout(text, f, g2.getFontRenderContext());
        // okay, get aspect ratio
        Rectangle2D bounds = layout.getBounds();
        int nStrips = (int) Math.ceil(Math.sqrt(bounds.getWidth() / bounds.getHeight()));
        int stripHeight = (int) Math.floor(textureSize/(double)nStrips);
        int stripWidth = nStrips*textureSize;

        float ox = (float)-bounds.getX(), oy = (float)-bounds.getY();
        double sx = stripWidth / bounds.getWidth();
        double sy = stripHeight / bounds.getHeight();
        // Write the text in strips to our image.
        for (int i=0; i<=nStrips; i++) {
            AffineTransform oat = g2.getTransform();
            if (i<nStrips)
                g2.translate(-i*textureSize,0);
            else
                g2.translate(-(i-1)*textureSize,-textureSize);
            g2.shear(0,stripHeight/(double)textureSize);
            g2.scale(sx,sy);
            g2.drawString(text, ox, oy);
            g2.setTransform(oat);
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
        TextLayout tl = new TextLayout("Text Texture Image:", g2.getFont(),g2.getFontRenderContext());
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
            // The .registerFont() method is only available in 1.6.
            //GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public static void main(String s[]) {
        JFrame f = new JFrame("Text Texture Test");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        JApplet applet = new TextTest();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(550,560));
        f.setVisible(true);
    }

}
