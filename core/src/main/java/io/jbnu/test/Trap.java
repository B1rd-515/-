
package io.jbnu.test;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Trap {

    private Rectangle bounds;

    public Trap(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
    }

    // 함정 그리기
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 0, 0, 1);

        float x = bounds.x;
        float y = bounds.y;
        float width = bounds.width;
        float height = bounds.height;

        // 삼각형 가시 그리기
        shapeRenderer.triangle(
            x, y,
            x + width / 2, y + height,
            x + width, y
        );
    }

    // 함정 영역 반환
    public Rectangle getBounds() {
        return bounds;
    }
}
