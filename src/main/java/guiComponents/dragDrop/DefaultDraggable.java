package guiComponents.dragDrop;

import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

public class DefaultDraggable implements Draggable {
 
    private Vector2f start;   
    private Spatial spatial;
    private Vector3f origin;
    private Vector3f xAxis;
    private Vector3f yAxis;
    
    private Vector2f currentLocation;

    public DefaultDraggable( Vector2f start, 
                             Spatial spatial, Vector3f origin, Vector3f xAxis, Vector3f yAxis ) {
        this.start = start.clone();
        this.spatial = spatial;
        this.origin = origin.clone();
        this.xAxis = xAxis.clone();
        this.yAxis = yAxis.clone();
        this.currentLocation = start.clone();
    }

    public DefaultDraggable( ViewPort view, Spatial spatial, Vector2f start ) {
        Camera cam = view.getCamera();
        Vector3f origin = spatial.getWorldTranslation();
        Vector3f screenPos = cam.getScreenCoordinates(origin);
        Vector2f xScreen = new Vector2f(screenPos.x + 1, screenPos.y);
        Vector2f yScreen = new Vector2f(screenPos.x, screenPos.y + 1);


        Vector3f xWorld = cam.getWorldCoordinates(xScreen, screenPos.z);
        Vector3f yWorld = cam.getWorldCoordinates(yScreen, screenPos.z);

        this.start = start.clone();
        this.spatial = spatial;
        this.origin = origin.clone();
        this.xAxis = xWorld.subtractLocal(origin);
        this.yAxis = yWorld.subtractLocal(origin);
        this.currentLocation = start.clone();
    }
    
    public Spatial getSpatial() {
        return spatial;
    }
 
    protected void updateTranslation() {
        float x = currentLocation.x - start.x;
        float y = currentLocation.y - start.y;
        Vector3f loc = origin.add(xAxis.mult(x)).addLocal(yAxis.mult(y));

        loc = spatial.getParent().worldToLocal(loc, loc);
                 
        spatial.setLocalTranslation(loc);
    }
    
    public void setLocation( float x, float y ) {
        currentLocation.set(x, y);
        updateTranslation();
    }
 
    public Vector2f getLocation() {
        return currentLocation;
    }
 
    public void updateDragStatus( DragStatus status ) {
    }
    
    public void release() {
        spatial.removeFromParent();
    }
}
