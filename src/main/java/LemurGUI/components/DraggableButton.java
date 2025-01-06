package LemurGUI.components;

import LemurGUI.dragDrop.DragAndDropControl;
import com.simsilica.lemur.Button;
import lombok.Getter;
import lombok.Setter;

public abstract class DraggableButton extends Button implements Draggable {

    @Getter
    @Setter
    protected boolean clickDisabled; // disables the button temporarily

    protected TooltipMouseListener tooltipListener;
    
    @Getter
    protected DragAndDropControl dragAndDropControl;
    public DraggableButton(String s,DragAndDropControl dragAndDropControl, TooltipMouseListener tooltipListener) {
        super(s);
        this.addMouseListener(tooltipListener);
        this.addControl(dragAndDropControl);
        this.tooltipListener = tooltipListener;
        this.dragAndDropControl = dragAndDropControl;
    }
}
