package LemurGUI.dragDrop;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector2f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

import com.simsilica.lemur.event.AbstractCursorEvent;

public class DragEvent {
    
    private DragSession session;
    private AbstractCursorEvent cursorEvent;
    private CollisionResult collision;
    
    public DragEvent( DragSession session, AbstractCursorEvent cursorEvent ) {
        this(session, cursorEvent, cursorEvent.getCollision());
    }
    
    public DragEvent( DragSession session, AbstractCursorEvent cursorEvent, 
                      CollisionResult collision ) {
        this.session = session;
        this.cursorEvent = cursorEvent;
        this.collision = collision;                      
    }
    
    public DragSession getSession() {
        return session;
    }
    
    public float getX() {
        return cursorEvent.getX();
    }
    
    public float getY() {
        return cursorEvent.getY();
    }
    
    public Vector2f getLocation() {
        return new Vector2f(getX(), getY());
    }
      
    public CollisionResult getCollision() {
        return collision;
    }
    
    public ViewPort getViewPort() {
        return cursorEvent.getViewPort(); 
    }
    
    public Spatial getTarget() {
        return cursorEvent.getTarget();
    }
 
}
