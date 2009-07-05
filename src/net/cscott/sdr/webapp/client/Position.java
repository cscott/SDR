package net.cscott.sdr.webapp.client;

/** A dancer position: x and y coordinates (in 'dance floor units' and a
 *  rotation (in radians). */
public class Position {
    final double x, y, rot;
    public Position(double x, double y, double rot) {
        this.x=x; this.y=y; this.rot=rot;
    }
}
