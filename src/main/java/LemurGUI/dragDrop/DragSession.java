package LemurGUI.dragDrop;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;

public interface DragSession {
 
    public static final String ITEM = "item";

    public void set( String name, Object attribute );

    public <T> T get( String name, T defaultValue );

    public boolean hasAttribute( String name );

    public void setDragStatus( DragStatus status );

    public DragStatus getDragStatus();

    public Spatial getDragSource();

    public Spatial getDropTarget();

    public CollisionResult getDropCollision();

    public Draggable getDraggable();

    public Vector2f getDragLocation();
}
