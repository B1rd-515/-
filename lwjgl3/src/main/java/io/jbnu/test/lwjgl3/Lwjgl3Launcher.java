
package io.jbnu.test.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.jbnu.test.Main;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(
            new Main(),
            getDefaultConfiguration()
        );
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {

        Lwjgl3ApplicationConfiguration configuration =
            new Lwjgl3ApplicationConfiguration();

        configuration.setTitle("Road to Stormwind - MiddleProject");

        // 게임 창 크기
        configuration.setWindowedMode(1280, 720);

        // 화면 주사율 설정
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        return configuration;
    }
}
