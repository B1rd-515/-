package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** 3스테이지의 지형만 그리며 물리 판정과 위치는 변경하지 않는다. */
public final class FactoryTerrain {
    private final Texture ground, ledge, moving;
    public FactoryTerrain() {
        ground = load("terrain/factory_ground_tile.png");
        ledge = load("terrain/factory_ledge.png");
        moving = load("terrain/factory_moving.png");
    }
    private Texture load(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }
    public void draw(SpriteBatch batch, StageManager manager, int stage) {
        if (stage != 3) return;
        for (Platform platform : manager.getPlatforms()) {
            Rectangle r = platform.getBounds();
            if (r.width <= 0 || r.height <= 0) continue;
            if (r.y <= 1f && r.height >= 50f) drawGround(batch, r);
            else drawLedge(batch, ledge, r);
        }
        for (MovingPlatform platform : manager.getMovingPlatforms()) {
            Rectangle r = platform.getBounds();
            if (r.width > 0 && r.height > 0) drawLedge(batch, moving, r);
        }
    }
    private void drawGround(SpriteBatch batch, Rectangle r) {
        float end = r.x + r.width;
        float tile = 96f;
        float height = Math.max(96f, r.height);
        for (float x = r.x; x < end - 0.001f; x += tile) {
            float width = Math.min(tile, end - x);
            int srcWidth = Math.max(1, Math.round(width / tile * ground.getWidth()));
            batch.draw(ground, x, r.y + r.height - height, width, height,
                0, 0, srcWidth, ground.getHeight(), false, false);
        }
    }
    private void drawLedge(SpriteBatch batch, Texture texture, Rectangle r) {
        float height = Math.max(44f, r.height);
        float y = r.y + r.height - height;
        float cap = Math.min(20f, r.width / 2f);
        int capSrc = 20;
        int texH = texture.getHeight();
        int texW = texture.getWidth();
        batch.draw(texture, r.x, y, cap, height, 0, 0, capSrc, texH, false, false);
        float start = r.x + cap, end = r.x + r.width - cap;
        for (float x = start; x < end - 0.001f; x += 32f) {
            float w = Math.min(32f, end - x);
            int srcW = Math.max(1, Math.round(w));
            batch.draw(texture, x, y, w, height, texW / 2 - 16, 0, srcW, texH, false, false);
        }
        batch.draw(texture, end, y, cap, height,
            texW - capSrc, 0, capSrc, texH, false, false);
    }
    public void dispose() { ground.dispose(); ledge.dispose(); moving.dispose(); }
}
