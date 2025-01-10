package guiComponents.dragDrop;

import com.jme3.math.Vector2f;

public interface Draggable {

    public void setLocation( float x, float y );

    public Vector2f getLocation();

    public void updateDragStatus( DragStatus status );

    public void release();
}
