package io.jbnu.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/** 세 스테이지의 전체 지형을 구성한다. */
public class Stage {
    public static final int TOTAL_STAGES = 3;
    public static final float WIDTH = 16000f;

    private final Array<Platform> platforms = new Array<>();
    private final Array<MovingPlatform> movingPlatforms = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<FlyingEnemy> flyingEnemies = new Array<>();
    private final Array<Trap> traps = new Array<>();
    private final Array<DynamicTrap> dynamicTraps = new Array<>();
    private final Array<Checkpoint> checkpoints = new Array<>();
    private final Rectangle goal = new Rectangle();
    private int stageNumber;

    public Stage() { setStage(1); }
    public void setStage(int n) {
        if (n < 1 || n > TOTAL_STAGES) throw new IllegalArgumentException("Invalid stage: " + n);
        stageNumber = n;
        platforms.clear(); movingPlatforms.clear(); enemies.clear(); flyingEnemies.clear();
        traps.clear(); dynamicTraps.clear(); checkpoints.clear();
        StageData.populate(this, n);

        dynamicTraps.clear();
    }

    public void update(float dt) {
        for (MovingPlatform p : movingPlatforms) p.update(dt);
        for (Enemy e : enemies) e.update(dt);
        for (FlyingEnemy e : flyingEnemies) e.update(dt);
        for (DynamicTrap t : dynamicTraps) t.update(dt);
    }
    public Array<Platform> getPlatforms() { return platforms; }
    public Array<MovingPlatform> getMovingPlatforms() { return movingPlatforms; }
    public Array<Enemy> getEnemies() { return enemies; }
    public Array<FlyingEnemy> getFlyingEnemies() { return flyingEnemies; }
    public Array<Trap> getTraps() { return traps; }
    public Array<DynamicTrap> getDynamicTraps() { return dynamicTraps; }
    public Array<Checkpoint> getCheckpoints() { return checkpoints; }
    public Rectangle getGoal() { return goal; }
    public int getStageNumber() { return stageNumber; }
    public void draw(ShapeRenderer sr) { draw(sr, true); }
    public void draw(ShapeRenderer sr, boolean drawFixedPlatforms) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (drawFixedPlatforms) for (Platform p : platforms) p.draw(sr);
        if (stageNumber == 1) for (MovingPlatform p : movingPlatforms) p.draw(sr);



        for (Checkpoint c : checkpoints) c.draw(sr);
        sr.setColor(Color.WHITE); sr.rect(goal.x, goal.y, 5, goal.height);
        sr.setColor(Color.RED);
        sr.triangle(goal.x + 5, goal.y + goal.height,
            goal.x + 58, goal.y + goal.height - 20,
            goal.x + 5, goal.y + goal.height - 40);
        sr.end();
    }
}
