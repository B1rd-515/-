
package io.jbnu.test;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Platform {

    private Rectangle bounds;

    // 발판 생성
    public Platform(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
    }

    // 발판 그리기
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.45f, 0.35f, 0.25f, 1);

        shapeRenderer.rect(
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height
        );
    }

    // 발판 충돌 영역 반환
    public Rectangle getBounds() {
        return bounds;
    }
}
