package io.jbnu.test;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
public class Enemy {
    private final Rectangle bounds;
    private final float minX, maxX, speed;
    private int direction = 1;
    private boolean alive = true;
    public Enemy(float x,float y,float width,float height,float distance,float speed) {
        bounds = new Rectangle(x,y,width,height);
        minX=x; maxX=x+distance; this.speed=speed;
    }
    public void update(float dt) {
        if (!alive) return;
        bounds.x += speed*direction*dt;
        if(bounds.x>=maxX){bounds.x=maxX;direction=-1;}
        if(bounds.x<=minX){bounds.x=minX;direction=1;}
    }
    public Rectangle getBounds(){return bounds;}
    public boolean isAlive(){return alive;}
    public void defeat(){alive=false;}
    public void draw(ShapeRenderer sr){if(!alive)return;sr.setColor(0.8f,0.15f,0.15f,1);sr.rect(bounds.x,bounds.y,bounds.width,bounds.height);}
}
