package io.jbnu.test;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
public class DynamicTrap {
    private final Rectangle bounds;
    private final float period, activeDuration;
    private float elapsed;
    public DynamicTrap(float x,float y,float w,float h,float period,float activeDuration,float offset){
        bounds=new Rectangle(x,y,w,h);this.period=period;this.activeDuration=activeDuration;elapsed=offset;
    }
    public void update(float dt){elapsed+=dt;}
    public boolean isActive(){return (elapsed%period)<activeDuration;}
    public Rectangle getBounds(){return bounds;}
    public void draw(ShapeRenderer sr){
        if(isActive())sr.setColor(1f,0.25f,0.1f,1f);
        else sr.setColor(0.35f,0.35f,0.35f,1f);
        sr.rect(bounds.x,bounds.y,bounds.width,bounds.height);
    }
}
