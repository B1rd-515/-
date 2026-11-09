package io.jbnu.test;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** 이동 발판은 시작 위치와 이동 방향을 독립적으로 설정한다. */
public class MovingPlatform extends Platform {
    private final float minX, maxX, minY, maxY, speed;
    private final boolean vertical;
    private int direction;
    private float deltaX, deltaY;

    public MovingPlatform(float x, float y, float w, float h, float distance, float speed) {
        this(x, y, w, h, distance, speed, false);
    }

    public MovingPlatform(float x, float y, float w, float h, float distance, float speed, boolean vertical) {
        this(x, y, w, h, distance, speed, vertical, 0f, 1);
    }


    public MovingPlatform(float x, float y, float w, float h, float distance, float speed,
                          boolean vertical, float phase, int initialDirection) {
        super(x, y, w, h);
        float travel = Math.max(0f, distance);
        minX = x;
        maxX = vertical ? x : x + travel;
        minY = y;
        maxY = vertical ? y + travel : y;
        this.speed = Math.max(0f, speed);
        this.vertical = vertical;
        direction = initialDirection < 0 ? -1 : 1;
        float p = Math.max(0f, Math.min(1f, phase));
        if (vertical) getBounds().y = minY + travel * p;
        else getBounds().x = minX + travel * p;
    }


    public void update(float dt) {
        deltaX = 0f;
        deltaY = 0f;
        if (dt <= 0f) return;

        // [1] 수평 또는 수직 이동 범위 설정
        float low = vertical ? minY : minX;
        float high = vertical ? maxY : maxX;
        float old = vertical ? getBounds().y : getBounds().x;
        float range = high - low;

        if (range <= 0f || speed <= 0f) return;

        // [2] 이동 속도와 프레임 시간으로 위치 계산
        float pos = old + speed * direction * dt;

        // [3] 이동 범위 끝에서 방향 반전
        while (pos > high || pos < low) {
            if (pos > high) {
                pos = high - (pos - high);
                direction = -1;
            }
            if (pos < low) {
                pos = low + (low - pos);
                direction = 1;
            }
        }

        // [4] 발판의 실제 위치와 이동량 갱신
        if (vertical) {
            getBounds().y = pos;
            deltaY = pos - old;
        } else {
            getBounds().x = pos;
            deltaX = pos - old;
        }
    }


    public float getDeltaX() { return deltaX; }
    public float getDeltaY() { return deltaY; }

    @Override public void draw(ShapeRenderer sr) {
        sr.setColor(0.2f, 0.65f, 0.85f, 1f);
        sr.rect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
    }
}
