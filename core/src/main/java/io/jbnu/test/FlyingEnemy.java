package io.jbnu.test;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/** 비행 몬스터는 좌우로 이동하면서 상하로 흔들린다. */
public final class FlyingEnemy {
    private final Rectangle bounds;
    private final float originY, minX, maxX, speed, amplitude, frequency;
    private float elapsed;
    private int direction = 1;
    private boolean alive = true;

    /** distance는 수평 이동 거리, frequency는 초당 상하 진동 횟수이다. */
    public FlyingEnemy(float x, float y, float width, float height,
                       float distance, float speed, float amplitude, float frequency) {
        bounds = new Rectangle(x, y, width, height);
        originY = y;
        minX = x;
        maxX = x + Math.max(0, distance);
        this.speed = Math.max(0, speed);
        this.amplitude = Math.max(0, amplitude);
        this.frequency = Math.max(0, frequency);
    }

    public void update(float dt) {
        if (!alive) return;
        elapsed += dt;
        bounds.x += speed * direction * dt;
        if (bounds.x >= maxX) { bounds.x = maxX; direction = -1; }
        if (bounds.x <= minX) { bounds.x = minX; direction = 1; }
        bounds.y = originY + (float)Math.sin(elapsed * frequency * Math.PI * 2) * amplitude;
    }

    public Rectangle getBounds() { return bounds; }
    public boolean isAlive() { return alive; }
    public void defeat() { alive = false; }
    public void draw(ShapeRenderer sr) {
        if (!alive) return;
        float x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
        sr.setColor(0.65f, 0.32f, 0.80f, 1f);
        sr.ellipse(x, y, w, h);
        sr.setColor(0.75f, 0.87f, 1f, 1f);
        sr.triangle(x + w * .25f, y + h * .58f, x - w * .30f, y + h * 1.15f, x + w * .38f, y + h * .82f);
        sr.triangle(x + w * .75f, y + h * .58f, x + w * 1.30f, y + h * 1.15f, x + w * .62f, y + h * .82f);
    }
}
