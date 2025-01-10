package guiComponents.dragDrop;

public abstract class AbstractDragAndDropListener implements DragAndDropListener {

    @Override
    public abstract Draggable onDragDetected( DragEvent event ); 

    @Override
    public void onDragEnter( DragEvent event ) {
    }

    @Override
    public void onDragExit( DragEvent event ) {
    }
    
    @Override
    public abstract void onDragOver( DragEvent event );  
 
    @Override
    public abstract void onDrop( DragEvent event );
 
    @Override
    public abstract void onDragDone( DragEvent event );

}