package guiComponents.dragDrop;

public interface DragAndDropListener {

    public Draggable onDragDetected( DragEvent event ); 

    public void onDragEnter( DragEvent event );

    public void onDragExit( DragEvent event );

    public void onDragOver( DragEvent event );  

    public void onDrop( DragEvent event );

    public void onDragDone( DragEvent event );  
}

