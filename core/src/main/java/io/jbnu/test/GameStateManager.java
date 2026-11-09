package io.jbnu.test;

/** Controls legal game-state transitions independently of rendering and level data. */
public final class GameStateManager {
    private GameState state = GameState.READY;
    private float deathTimer;

    public GameState getState() { return state; }
    public boolean isPlaying() { return state == GameState.PLAYING; }
    public void startPlaying() { state = GameState.PLAYING; deathTimer = 0f; }
    public void togglePause() {
        if (state == GameState.PLAYING) state = GameState.PAUSED;
        else if (state == GameState.PAUSED) state = GameState.PLAYING;
    }
    public boolean die(float seconds) {
        if (!isPlaying()) return false;
        state = GameState.DEAD;
        deathTimer = seconds;
        return true;
    }
    /** Returns true once when the respawn delay has finished. */
    public boolean updateDeath(float dt) {
        if (state != GameState.DEAD) return false;
        deathTimer -= dt;
        if (deathTimer > 0f) return false;
        startPlaying();
        return true;
    }
    // 트램 탑승 조건을 만족하지 못하면 재시작 대기 화면으로 전환한다.
    public void denyTram() {
        if (isPlaying()) state = GameState.TRAM_DENIED;
    }
    public void finishStage(boolean lastStage) {
        if (isPlaying()) state = lastStage ? GameState.GAME_CLEAR : GameState.STAGE_CLEAR;
    }
}
