package io.jbnu.test;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
public class Checkpoint {
    private final Rectangle bounds;
    private boolean activated;
    public Checkpoint(float x,float y){bounds=new Rectangle(x,y,24,115);}
    public Rectangle getBounds(){return bounds;}
    public boolean isActivated(){return activated;}
    public void activate(){activated=true;}
    public void draw(ShapeRenderer sr){
        sr.setColor(activated?0.2f:0.9f,activated?0.9f:0.7f,0.25f,1);
        sr.rect(bounds.x,bounds.y,5,bounds.height);
        sr.triangle(bounds.x+5,bounds.y+bounds.height,bounds.x+47,bounds.y+bounds.height-17,bounds.x+5,bounds.y+bounds.height-34);
    }
}
