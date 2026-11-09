package io.jbnu.test;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/** 현재 스테이지를 관리하고 스테이지 전환 기능을 제공한다. */
public final class StageManager {
    private final Stage current = new Stage();
    public void setStage(int number) { current.setStage(number); }
    public int getStageNumber() { return current.getStageNumber(); }
    public boolean hasNextStage() { return getStageNumber() < Stage.TOTAL_STAGES; }
    public void nextStage() {
        if (!hasNextStage()) throw new IllegalStateException("Already at final stage");
        setStage(getStageNumber() + 1);
    }
    public void update(float dt) { current.update(dt); }
    public void draw(ShapeRenderer renderer) { current.draw(renderer); }
    public void draw(ShapeRenderer renderer, boolean drawFixedPlatforms) { current.draw(renderer, drawFixedPlatforms); }
    public Array<Platform> getPlatforms() { return current.getPlatforms(); }
    public Array<MovingPlatform> getMovingPlatforms() { return current.getMovingPlatforms(); }
    public Array<Enemy> getEnemies() { return current.getEnemies(); }
    public Array<FlyingEnemy> getFlyingEnemies() { return current.getFlyingEnemies(); }
    public Array<Trap> getTraps() { return current.getTraps(); }
    public Array<DynamicTrap> getDynamicTraps() { return current.getDynamicTraps(); }
    public Array<Checkpoint> getCheckpoints() { return current.getCheckpoints(); }
    public Rectangle getGoal() { return current.getGoal(); }
}
