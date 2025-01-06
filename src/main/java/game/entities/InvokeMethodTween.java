package game.entities;

import com.jme3.anim.tween.AbstractTween;

public class InvokeMethodTween extends AbstractTween {
    private final Runnable runnable;
    public InvokeMethodTween(Runnable runnable) {
        super(0);
        this.runnable = runnable;
    }

    @Override
    protected void doInterpolate(double d) {
        runnable.run();
    }
}
