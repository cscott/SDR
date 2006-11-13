package net.cscott.sdr.anim;

import java.awt.Color;
import java.text.DecimalFormat;

import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;

public class DebugState extends BaseState {
    Gauge memoryGauge;
    TextureText fps;
    public DebugState() {
        super("DebugState");
        memoryGauge = new Gauge("memory gauge", false,
                new Color[] { Color.green, Color.yellow, Color.red }, 0.5f);
	memoryGauge.getLocalTranslation().set(x(640-128), y(56), 0);
        rootNode.attachChild(memoryGauge);

        fps = mkText("fps:","0",64,JustifyX.CENTER,JustifyY.BOTTOM,x(320),y(0),x(320),y(16));
        
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }

    @Override
    protected void onActivate() { /* do nothing */ }
    
    @Override
    protected void stateUpdate(float tpf) {
        if (counter!=0) { counter--; return; } // don't update every frame
        counter = 20; // update every 20 frames.
        
        Runtime r = Runtime.getRuntime();
        memoryGauge.update(1-(r.freeMemory()/(float)r.totalMemory()));
        
        fps.setText(fpsFormat.format(1./tpf)+" fps");
    }
    private int counter = 0;
    private static final DecimalFormat fpsFormat =new DecimalFormat("#,###.#");
} 
