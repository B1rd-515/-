package io.jbnu.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/** 게임 HUD와 중앙 안내 화면을 표시한다. */
public class GameUI {
    private final BitmapFont font = new BitmapFont(
        com.badlogic.gdx.Gdx.files.internal("fonts/korean_ui.fnt"),
        com.badlogic.gdx.Gdx.files.internal("fonts/korean_ui.png"), false);
    private static final float CENTER_X = 640f;

    private void line(SpriteBatch batch, String value, float y, float scale, Color color) {
        font.getData().setScale(scale * 0.38f);
        font.setColor(color);
        font.draw(batch, value, 0, y, 1280, Align.center, false);
    }

    public void draw(SpriteBatch batch, int stage, GameState state, GameTimer timer, int coins) {
        if (state == GameState.PLAYING || state == GameState.PAUSED || state == GameState.DEAD) {
            font.setColor(Color.WHITE);
            font.getData().setScale(0.52f);
            font.draw(batch, "스테이지 " + stage + " / 3", 28, 683);
            font.draw(batch, "전체 시간  " + GameTimer.format(timer.getTotalTime()), 28, 651);
            font.draw(batch, "스테이지  " + GameTimer.format(timer.getStageTime()), 28, 619);
            font.draw(batch, "사망 횟수  " + timer.getDeaths(), 28, 587);
            font.setColor(Color.GOLD);
            font.draw(batch, "코인  " + coins, 28, 555);
        }
        if (state == GameState.READY) {
            line(batch, "스톰윈드 가는길", 485, 3.0f, Color.WHITE);
            line(batch, "던 모로", 426, 1.8f, Color.LIGHT_GRAY);
            line(batch, "방향키 좌/우 : 이동     SPACE : 점프", 322, 1.6f, Color.WHITE);
            line(batch, "ESC : 일시정지     ENTER : 진행", 285, 1.6f, Color.WHITE);
            line(batch, "ENTER 키를 눌러 시작", 196, 2.3f, Color.GOLD);

        } else if (state == GameState.PAUSED) {
            line(batch, "일시정지", 435, 4.0f, Color.WHITE);
            line(batch, "ESC 키를 눌러 계속하기", 347, 1.8f, Color.GOLD);
            line(batch, "R 키를 눌러 스테이지 재시작", 300, 1.8f, Color.WHITE);

        } else if (state == GameState.DEAD) {
            line(batch, "사망했습니다", 434, 3.7f, Color.WHITE);
            line(batch, "체크포인트에서 부활 중...", 347, 1.5f, Color.LIGHT_GRAY);

        } else if (state == GameState.STAGE_CLEAR) {

            if (stage == 2) {
                line(batch, "트램 탑승 가능",
                    470, 3.5f, Color.WHITE);

                line(batch, "보유 코인  " + coins,
                    380, 1.8f, Color.GOLD);

                line(batch, "ENTER 키를 눌러 트램 탑승",
                    300, 1.8f, Color.GOLD);

            }
            else {
                line(batch, "스테이지 " + stage + " 클리어",
                    470, 3.5f, Color.WHITE);

                line(batch, "스테이지 시간  " +
                        GameTimer.format(timer.getStageTime()),
                    380, 1.8f, Color.LIGHT_GRAY);

                line(batch, "코인  " + coins,
                    248, 1.6f, Color.GOLD);

                line(batch, "ENTER 키를 눌러 계속하기",
                    300, 1.8f, Color.GOLD);
            }
        } else if (state == GameState.TRAM_DENIED) {
            line(batch, "트램을 이용하려면 10코인이 필요합니다.", 440, 2.1f, Color.WHITE);
            line(batch, "현재 코인  " + coins, 355, 1.8f, Color.GOLD);
            line(batch, "ENTER 키를 눌러 처음부터 다시 시작", 275, 1.7f, Color.GOLD);

        } else if (state == GameState.GAME_CLEAR) {
            line(batch, "코인  " + coins, 232, 1.5f, Color.GOLD);
            line(batch, "스톰윈드 도착", 540, 3.5f, Color.WHITE);
            line(batch, "전체 시간  " + GameTimer.format(timer.getTotalTime()), 464, 1.9f, Color.GOLD);
            for (int i = 1; i <= timer.getStageCount(); i++) {
                line(batch, "스테이지 " + i + "  " + GameTimer.format(timer.getRecordedStageTime(i)), 409 - (i - 1) * 38, 1.6f, Color.WHITE);
            }
            line(batch, "사망 횟수  " + timer.getDeaths(), 269, 1.8f, Color.WHITE);
            line(batch, "ENTER 키를 눌러 다시 시작", 199, 1.8f, Color.GOLD);
        }
        font.setColor(Color.WHITE);
        font.getData().setScale(0.38f);
    }

    public void drawDeveloperMode(SpriteBatch batch, boolean enabled) {
        if (!enabled) return;
        font.getData().setScale(0.43f);
        font.setColor(Color.GOLD);
        font.draw(batch, "개발 모드 | F2/F3 스테이지 | F4 재시작 | F5/F6 체크포인트 | F7 좌표 | F8 충돌영역 | F1 종료", 28, 510);
        font.setColor(Color.WHITE);
        font.getData().setScale(0.38f);
    }

    public void drawDebugCoordinates(SpriteBatch batch,float x,float y,float cameraX){
        font.getData().setScale(0.45f);
        font.setColor(Color.CYAN);
        font.draw(batch,"플레이어 X="+(int)x+" Y="+(int)y+" 카메라="+(int)cameraX,28,515);
        font.setColor(Color.WHITE);
        font.getData().setScale(0.38f);
    }
    public void dispose() { font.dispose(); }
}
