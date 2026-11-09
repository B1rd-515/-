package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** 몬스터 도트 이미지를 그리며 이동 및 충돌은 몬스터 클래스에서 처리한다. */
public final class EnemyVisuals {
    private final Texture ground;
    private final Texture flying;

    public EnemyVisuals() {
        ground = new Texture(Gdx.files.internal("enemies/ground_enemy.png"));
        flying = new Texture(Gdx.files.internal("enemies/flying_enemy.png"));
        ground.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        flying.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    private static final float VISUAL_SCALE = 2f;

    public void draw(SpriteBatch batch, StageManager manager) {
        for (Enemy enemy : manager.getEnemies()) {
            if (!enemy.isAlive()) continue;
            Rectangle b = enemy.getBounds();
            // 지상 몬스터 이미지를 충돌 영역의 아래쪽 중앙에 맞춰 그린다.
            float w = b.width * VISUAL_SCALE;
            float h = b.height * VISUAL_SCALE;
            batch.draw(ground, b.x + (b.width - w) / 2f, b.y, w, h);
        }
        for (FlyingEnemy enemy : manager.getFlyingEnemies()) {
            if (!enemy.isAlive()) continue;
            Rectangle b = enemy.getBounds();
            // 비행 몬스터 이미지를 충돌 영역의 중앙에 맞춰 그린다.
            float w = b.width * VISUAL_SCALE;
            float h = b.height * VISUAL_SCALE;
            batch.draw(flying, b.x + (b.width - w) / 2f,
                    b.y + (b.height - h) / 2f, w, h);
        }
    }

    public void dispose() {
        ground.dispose();
        flying.dispose();
    }
}
