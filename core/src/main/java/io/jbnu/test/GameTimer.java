package io.jbnu.test;
public class GameTimer {
    private float totalTime;
    private float stageTime;
    private final float[] stageRecords;
    private int deaths;
    public GameTimer(int stageCount) { stageRecords = new float[stageCount]; }
    public void update(float delta) {
        if (delta > 0) { totalTime += delta; stageTime += delta; }
    }
    public void startStage(int stage) { stageTime = 0; }
    public void finishStage(int stage) {
        if (stage >= 1 && stage <= stageRecords.length) stageRecords[stage-1] = stageTime;
    }
    public void recordDeath() { deaths++; }
    public float getTotalTime() { return totalTime; }
    public float getStageTime() { return stageTime; }
    public int getDeaths() { return deaths; }
    public int getStageCount() { return stageRecords.length; }
    public float getRecordedStageTime(int stage) { return stageRecords[stage-1]; }
    public void reset() {
        totalTime = 0; stageTime = 0; deaths = 0;
        java.util.Arrays.fill(stageRecords, 0);
    }
    public static String format(float seconds) {
        int s = (int)seconds;
        return String.format(java.util.Locale.US, "%02d:%02d", s/60, s%60);
    }
}
