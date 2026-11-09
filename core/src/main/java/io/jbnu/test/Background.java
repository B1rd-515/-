package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Background {
    private static final float TILE = 1280f;
    private static final float HEIGHT = 720f;
    private static final float STAGE3_FACTORY_START = 1280f;
    private static final float STAGE3_FACTORY_END = 14720f;
    private static final float STAGE2_GATE_WIDTH = 1280f;
    private static final float STAGE2_FADE = 560f;

    private static final float TRANSITION_HALF_WIDTH = 720f;
    private Texture blackPixel;
    private final Texture[] images = new Texture[3];
    private Texture stage2Mountains, stage2Gate;
    private Texture stage3Station, stage3Factory;

    public Background() {
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.BLACK);
        pixel.fill();
        blackPixel = new Texture(pixel);
        pixel.dispose();
        for (int i = 0; i < images.length; i++) {
            String path = "background/stage" + (i + 1) + "_bg.png";
            images[i] = loadIfExists(path);
        }
        stage2Mountains = loadIfExists("background/stage2_mountains_seamless.png");
        stage2Gate = loadIfExists("background/stage2_gate_transition.png");
        stage3Station = loadIfExists("background/stage3_platform.png");
        stage3Factory = loadIfExists("background/stage3_factory.png");
    }

    private Texture loadIfExists(String path) {
        if (!Gdx.files.internal(path).exists()) return null;
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }

    public void draw(SpriteBatch batch, int stage) {
        if (stage < 1 || stage > images.length) return;
        if (stage == 3 && stage3Station != null && stage3Factory != null) {
            drawRegion(batch, stage3Station, 0f, TILE, false);
            for (float x = STAGE3_FACTORY_START; x < STAGE3_FACTORY_END; x += TILE)
                drawRegion(batch, stage3Factory, x, Math.min(TILE, STAGE3_FACTORY_END - x), false);
            drawRegion(batch, stage3Station, STAGE3_FACTORY_END,
                Math.min(TILE, Stage.WIDTH - STAGE3_FACTORY_END), true);


            drawBlackFade(batch, STAGE3_FACTORY_START);
            drawBlackFade(batch, STAGE3_FACTORY_END);
            return;
        }
        if (stage == 2 && stage2Mountains != null) {
            drawRepeated(batch, stage2Mountains);
            if (stage2Gate != null) drawFinalGate(batch);
            return;
        }
        if (images[stage - 1] != null) drawRepeated(batch, images[stage - 1]);
    }

    private void drawBlackFade(SpriteBatch batch, float boundaryX) {
        Color previous = new Color(batch.getColor());
        final float stripWidth = 8f;
        for (float x = boundaryX - TRANSITION_HALF_WIDTH;
             x < boundaryX + TRANSITION_HALF_WIDTH; x += stripWidth) {
            float center = x + stripWidth * 0.5f;
            float darkness = 1f - Math.abs(center - boundaryX) / TRANSITION_HALF_WIDTH;
            darkness = Math.max(0f, Math.min(1f, darkness));
            // 전환 구간의 밝기를 부드럽게 조절한다.
            darkness = darkness * darkness * (3f - 2f * darkness);
            batch.setColor(0f, 0f, 0f, previous.a * darkness);
            batch.draw(blackPixel, x, 0f, stripWidth, HEIGHT);
        }
        batch.setColor(previous);
    }

    private void drawRepeated(SpriteBatch batch, Texture texture) {
        for (float x = 0; x < Stage.WIDTH; x += TILE)
            drawRegion(batch, texture, x, Math.min(TILE, Stage.WIDTH - x), false);
    }

    private void drawRegion(SpriteBatch batch, Texture texture, float x, float width, boolean flipX) {
        if (width <= 0) return;
        int sourceWidth = Math.max(1, Math.round(texture.getWidth() * width / TILE));
        int sourceX = flipX ? texture.getWidth() - sourceWidth : 0;
        batch.draw(texture, x, 0, width, HEIGHT,
            sourceX, 0, sourceWidth, texture.getHeight(), flipX, false);
    }

    private void drawFinalGate(SpriteBatch batch) {
        float start = Math.max(0f, Stage.WIDTH - STAGE2_GATE_WIDTH);
        Color original = new Color(batch.getColor());
        int strips = 70;
        float sw = STAGE2_FADE / strips;
        for (int i = 0; i < strips; i++) {
            float x = start + i * sw;
            int srcX = Math.round(stage2Gate.getWidth() * i * sw / STAGE2_GATE_WIDTH);
            int srcNext = Math.round(stage2Gate.getWidth() * (i + 1) * sw / STAGE2_GATE_WIDTH);
            batch.setColor(original.r, original.g, original.b, original.a * (i + 0.5f) / strips);
            batch.draw(stage2Gate, x, 0, sw, HEIGHT, srcX, 0,
                Math.max(1, srcNext - srcX), stage2Gate.getHeight(), false, false);
        }
        batch.setColor(original);
        int srcX = Math.round(stage2Gate.getWidth() * STAGE2_FADE / STAGE2_GATE_WIDTH);
        batch.draw(stage2Gate, start + STAGE2_FADE, 0, STAGE2_GATE_WIDTH - STAGE2_FADE, HEIGHT,
            srcX, 0, stage2Gate.getWidth() - srcX, stage2Gate.getHeight(), false, false);
    }

    public void dispose() {
        if (blackPixel != null) blackPixel.dispose();
        for (Texture texture : images) if (texture != null) texture.dispose();
        if (stage2Mountains != null) stage2Mountains.dispose();
        if (stage2Gate != null) stage2Gate.dispose();
        if (stage3Station != null) stage3Station.dispose();
        if (stage3Factory != null) stage3Factory.dispose();
    }
}
