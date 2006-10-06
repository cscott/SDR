/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.cscott.sdr.anim;

import com.jme.input.InputHandler;
import com.jme.input.KeyboardLookHandler;
import com.jme.input.MouseLookHandler;
import com.jme.input.RelativeMouse;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.input.action.MouseLook;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>FirsPersonController</code> defines an InputHandler that sets
 * input to be controlled similar to First Person Shooting games. By default the
 * commands are, WSAD moves the camera forward, backward and strafes. The
 * arrow keys rotate and tilt the camera and the mouse also rotates and tilts
 * the camera. <br>
 * This is a handler that is composed from {@link KeyboardLookHandler} and {@link MouseLookHandler}.
 * @author Mark Powell
 * @version $Id: SdrFirstPersonHandler.java,v 1.1 2006-10-06 21:29:08 cananian Exp $
 */
public abstract class SdrFirstPersonHandler extends InputHandler {
    private InputHandler mouseLookHandler;
    private KeyboardLookHandler keyboardLookHandler;

    /**
     * Creates a first person handler.
     * @param cam The camera to move by this handler.
     * @param moveSpeed action speed for move actions
     * @param turnSpeed action speed for rotating actions
     */
    public SdrFirstPersonHandler(final Camera cam, float moveSpeed, final float turnSpeed){
        mouseLookHandler = new InputHandler() {
            { /* constructor */
            final RelativeMouse m = new RelativeMouse("Mouse Input");
            m.registerWithInputHandler( this );

            MouseLook mouseLook = new MouseLook(m, cam, turnSpeed );
            mouseLook.setLockAxis(new Vector3f(cam.getUp()));
            addAction(mouseLook);
            addAction(new MouseInputAction() {
                { setMouse(m); }
                @Override
                public void performAction(InputActionEvent evt) {
                    if (mouse.getLocalTranslation().x!=0 ||
                        mouse.getLocalTranslation().y!=0)
                        onAction();
                }
            });
            }
        };
        addToAttachedHandlers( mouseLookHandler );
        keyboardLookHandler = new KeyboardLookHandler( cam, moveSpeed, turnSpeed ) {
            @Override
            public void addAction( final InputAction inputAction, String triggerCommand, boolean allowRepeats ) {
                InputAction wrapperAction = new InputAction() {
                    { setSpeed(inputAction.getSpeed()); }
                    @Override
                    public void performAction(InputActionEvent evt) {
                        inputAction.performAction(evt);
                        onAction();
                    }
                };
                // if the action is performed, set the 'last moved' variable.
                super.addAction(wrapperAction, triggerCommand, allowRepeats);
            }
        };
        addToAttachedHandlers( keyboardLookHandler );
    }
    /** This method is called whenever an appropriate keyboard or mouse action
     * is performed (that is, whenever this handler changes the camera
     * position or angle.  Override in a subclass in order to register/react
     * to this event.
     */
    public abstract void onAction();
}
