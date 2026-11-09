package io.jbnu.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/** 스테이지별 배경음악과 코인 획득 효과음을 관리한다. */
public class GameAudio {
    private final Music[] bgms = new Music[3];
    private Sound coinSound;
    private int currentStage = -1;
    private boolean paused;
    private float bgmVolume = 0.15f;
    private float sfxVolume = 0.8f;

    public GameAudio() {
        bgms[0] = loadMusic("audio/16. Dun Morogh.mp3");
        bgms[1] = loadMusic("audio/13. Ironforge.mp3");
        bgms[2] = loadMusic("audio/Ulduar_Tram_InMotion.ogg");
        if (Gdx.files.internal("audio/LootCoinSmall.ogg").exists()) {
            coinSound = Gdx.audio.newSound(Gdx.files.internal("audio/LootCoinSmall.ogg"));
        }
    }

    private Music loadMusic(String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("GameAudio", "Missing BGM: " + path);
            return null;
        }
        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setLooping(true);
        music.setVolume(bgmVolume);
        return music;
    }

    /** 스테이지 진입 시 호출하며 같은 스테이지의 중복 호출은 무시한다. */
    public void playStage(int stageNumber) {
        if (stageNumber < 1 || stageNumber > 3) return;
        if (currentStage == stageNumber) return;
        stopAll();
        currentStage = stageNumber;
        Music music = bgms[stageNumber - 1];
        if (music != null && !paused) music.play();
    }

    public void pauseBgm() {
        paused = true;
        Music music = currentMusic();
        if (music != null) music.pause();
    }

    public void resumeBgm() {
        paused = false;
        Music music = currentMusic();
        if (music != null) music.play();
    }

    public void stopBgm() {
        stopAll();
        currentStage = -1;
    }

    public void playCoin() {
        if (coinSound != null) coinSound.play(sfxVolume);
    }

    public void setBgmVolume(float volume) {
        bgmVolume = Math.max(0f, Math.min(1f, volume));
        for (Music music : bgms) if (music != null) music.setVolume(bgmVolume);
    }

    public void setSfxVolume(float volume) {
        sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    private Music currentMusic() {
        return currentStage >= 1 && currentStage <= 3 ? bgms[currentStage - 1] : null;
    }

    private void stopAll() {
        for (Music music : bgms) if (music != null) music.stop();
    }

    public void dispose() {
        for (Music music : bgms) if (music != null) music.dispose();
        if (coinSound != null) coinSound.dispose();
    }
}
