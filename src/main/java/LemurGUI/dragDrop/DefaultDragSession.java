package LemurGUI.dragDrop;

import java.util.*;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;

public class DefaultDragSession implements DragSession {
       
    private Spatial source;
    private Map<String, Object> attributes;
    private Draggable draggable;
    private DragStatus status;
    private Spatial dropTarget;
    private Vector2f dragLocation;
    private CollisionResult collision;

    public DefaultDragSession( Spatial source, Vector2f dragLocation ) {
        this.source = source;
        this.dragLocation = dragLocation;
    }
 
    @Override
    public void set( String name, Object attribute ) {
        if( attributes == null ) {
            if( attribute == null ) {
                return;
            }
            this.attributes = new LinkedHashMap<>();
        }
        attributes.put(name, attribute);
    }
    
    @Override
    @SuppressWarnings("unchecked") 
    public <T> T get( String name, T defaultValue ) {
        if( attributes == null ) {
            return defaultValue;
        }
        T result = (T)attributes.get(name);
        return result != null ? result : defaultValue;
    }
 
    @Override
    public boolean hasAttribute( String name ) {
        if( attributes == null ) {
            return false;
        }
        return attributes.containsKey(name);
    }

    @Override   
    public Spatial getDragSource() {
        return source;
    }
    
    protected void setDraggable( Draggable draggable ) {
        if( this.draggable != null ) {
            throw new IllegalStateException("Session already has a draggable defined.");
        }
        this.draggable = draggable; 
    }

    @Override   
    public Draggable getDraggable() {
        return draggable;
    }
 
    @Override   
    public void setDragStatus( DragStatus status ) {
        if( this.status == status ) {
            return;
        }
        this.status = status;
        draggable.updateDragStatus(status);
    }
    
    @Override   
    public DragStatus getDragStatus() {
        return status;
    }
    
    protected void close( DragEvent event ) {
        // Let the target know the drop is done
        if( dropTarget != null ) {
            if( status == DragStatus.ValidTarget ) {
                // Notify it about the drop.  It gets one last say as to
                // whether the drop was successful by resetting the status
                // if it wants to.        
                dropTarget.getControl(DragAndDropControl.class).fireDrop(event);
            }
            // Either way, we're closing so let it know we exited
            dropTarget.getControl(DragAndDropControl.class).fireExit(event);
               
            if( status != DragStatus.ValidTarget ) {
                // The drag status indicates that the target is no longer valid
                // so we'll clear it for the 'done' event we send to the source
                dropTarget = null;
            } 
        }
        
        // Let the source know the drop is done 
        if( source != null ) {
            source.getControl(DragAndDropControl.class).fireDone(event);
        }
        
        // And finally cleanup the draggable
        draggable.release();
    } 
 
    protected void setDropTarget( Spatial dropTarget, DragEvent event ) {
        if( this.dropTarget == dropTarget ) {
            return;
        }
        if( this.dropTarget != null ) {
            this.dropTarget.getControl(DragAndDropControl.class).fireExit(event);
        }
        this.dropTarget = dropTarget;
        if( this.dropTarget != null ) {
            // Set an initial status to indicate the we're over a target
            // but we'll let the listeners decide how valid it is
            setDragStatus(DragStatus.InvalidTarget);
            this.dropTarget.getControl(DragAndDropControl.class).fireEnter(event);
        } else {
            // There is no target so we'll clear any status
            setDragStatus(DragStatus.NoTarget);
        }
    }
    
    @Override   
    public Spatial getDropTarget() {
        return dropTarget;
    }
    
    @Override   
    public Vector2f getDragLocation() {
        return dragLocation;
    }
 
    protected void setDropCollision( CollisionResult collision ) {
        this.collision = collision;
    } 
    
    @Override   
    public CollisionResult getDropCollision() {
        return collision;
    }   
}