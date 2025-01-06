package LemurGUI.components;

import com.jme3.collision.CollisionResult;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorMotionEvent;

public interface Draggable {
    
    void onDragStart(CursorButtonEvent event, CollisionResult collision, Spatial capture);
   
    void drag();
    
    void onDragStop(CursorButtonEvent event, CursorMotionEvent lastMotion, Spatial dragTarget, Spatial draggedSpatial);
    
}
