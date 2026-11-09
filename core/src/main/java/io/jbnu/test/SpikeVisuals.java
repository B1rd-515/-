package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** 세 스테이지에 공통 금속 스파이크 이미지를 그린다. */
public final class SpikeVisuals {
    private static final float TILE_WORLD_WIDTH = 40f;
    private final Texture texture;

    public SpikeVisuals() {
        texture = new Texture(Gdx.files.internal("terrain/metal_spike_tile.png"));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void draw(SpriteBatch batch, StageManager manager) {
        for (Trap trap : manager.getTraps()) {
            Rectangle r = trap.getBounds();
            if (r.width <= 0 || r.height <= 0) continue;
            float end = r.x + r.width;
            for (float x = r.x; x < end - 0.001f; x += TILE_WORLD_WIDTH) {
                float width = Math.min(TILE_WORLD_WIDTH, end - x);
                // 기존 충돌 영역을 유지한 채 이미지만 그린다.
                float u = width / TILE_WORLD_WIDTH;
                batch.draw(texture, x, r.y, width, r.height,
                    0, 0, Math.max(1, Math.round(u * texture.getWidth())),
                    texture.getHeight(), false, false);
            }
        }
    }

    public void dispose() { texture.dispose(); }
}
