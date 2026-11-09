package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** 1스테이지 설원 지형을 그리며 충돌 영역은 유지한다. */
public final class SnowTerrain {
    private static final float GROUND_ART_HEIGHT = 93f;


    private static final float GROUND_VISUAL_RAISE = 14f;
    private static final float GROUND_TILE_WIDTH = 96f;
    private static final int CAP_SRC = 28;
    private static final int CENTER_SRC = 32;
    private static final float CAP_WORLD = 28f;
    private final Texture ground;
    private final Texture ledge;

    public SnowTerrain() {
        ground = load("terrain/snow_ground_seamless.png");
        ledge = load("terrain/snow_ledge_rounded.png");
    }

    private Texture load(String path) {
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return t;
    }

    public void draw(SpriteBatch batch, StageManager manager, int stage) {
        if (stage != 1 && stage != 2) return;
        for (Platform platform : manager.getPlatforms()) {
            Rectangle r = platform.getBounds();
            if (r.width <= 0 || r.height <= 0) continue;
            if (r.y <= 1f && r.height >= 50f) drawGround(batch, r);
            else drawLedge(batch, r);
        }
        // 이동 발판에도 설원 공중 발판 이미지를 사용한다.

        if (stage == 2) {
            for (MovingPlatform platform : manager.getMovingPlatforms()) {
                Rectangle r = platform.getBounds();
                if (r.width > 0f && r.height > 0f) drawLedge(batch, r);
            }
        }
    }


    private void drawGround(SpriteBatch batch, Rectangle r) {
        float top = r.y + r.height;
        float end = r.x + r.width;
        for (float x = r.x; x < end - 0.001f; x += GROUND_TILE_WIDTH) {
            float width = Math.min(GROUND_TILE_WIDTH, end - x);
            int sourceWidth = Math.max(1, Math.round(width / GROUND_TILE_WIDTH * ground.getWidth()));
            batch.draw(ground, x, top - GROUND_ART_HEIGHT + GROUND_VISUAL_RAISE,
                width, GROUND_ART_HEIGHT,
                0, 0, sourceWidth, ground.getHeight(), false, false);
        }
    }

    private void drawLedge(SpriteBatch batch, Rectangle r) {
        float artHeight = Math.max(44f, r.height);
        float y = r.y + r.height - artHeight;
        float cap = Math.min(CAP_WORLD, r.width * 0.5f);
        float centerWidth = Math.max(0f, r.width - 2f * cap);
        int texW = ledge.getWidth();
        int texH = ledge.getHeight();

        // 발판 양 끝은 한 번씩 그리고 가운데 부분만 반복한다.
        batch.draw(ledge, r.x, y, cap, artHeight,
            0, 0, CAP_SRC, texH, false, false);
        float middleStart = r.x + cap;
        float middleEnd = middleStart + centerWidth;
        int sourceX = texW / 2 - CENTER_SRC / 2;
        for (float x = middleStart; x < middleEnd - 0.001f; x += CENTER_SRC) {
            float width = Math.min(CENTER_SRC, middleEnd - x);
            int srcWidth = Math.max(1, Math.round(width));
            batch.draw(ledge, x, y, width, artHeight,
                sourceX, 0, srcWidth, texH, false, false);
        }
        batch.draw(ledge, r.x + r.width - cap, y, cap, artHeight,
            texW - CAP_SRC, 0, CAP_SRC, texH, false, false);
    }

    public void dispose() {
        ground.dispose();
        ledge.dispose();
    }
}
