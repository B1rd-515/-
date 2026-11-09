package io.jbnu.test;

/** 지형과 함정 배치만 정의하며 화면 출력과 게임 상태는 관리하지 않는다. */
public final class StageData {
    private static final float SECTION = 850f;
    private static final int SECTION_COUNT = 18;
    private StageData() { }

    private static void ground(Stage stage, float x, float width) {
        stage.getPlatforms().add(new Platform(x, 0, width, 70));
    }


    private static void spikeCarpet(Stage stage, float fromX, float toX) {
        final float spikeWidth = 40f;
        for (float x = fromX; x < toX; x += spikeWidth) {
            stage.getTraps().add(new Trap(x, 70, Math.min(spikeWidth, toX - x), 22));
        }
    }

    private static void ledge(Stage stage, float x, float y, float width) {
        stage.getPlatforms().add(new Platform(x, y, width, 25));
    }



    private static void verticalLedge(Stage stage, float x, float y, float width) {
        stage.getMovingPlatforms().add(new MovingPlatform(x, y, width, 25, 205, 74, true));
    }
    private static void horizontalLedge(Stage stage, float x, float y, float width) {


        int seed = Math.round(x / 5f);
        float phase = (Math.floorMod(seed * 7 + 3, 11)) / 10f;
        int direction = (Math.floorMod(seed, 2) == 0) ? 1 : -1;
        float speed = 64f + Math.floorMod(seed * 3, 5) * 8f;
        stage.getMovingPlatforms().add(new MovingPlatform(
            x, y, width, 25, 105, speed, false, phase, direction));
    }


    private static void populateIntroCourse(Stage stage) {
        for (int i = 0; i < SECTION_COUNT; i++) {
            float x = i * SECTION;


            float gap = 0f;
            if (i >= 2 && i < SECTION_COUNT - 1) {
                gap = (i % 4 == 0) ? 165f : (i % 3 == 0 ? 145f : 125f);
            }
            ground(stage, x, SECTION - gap);



            if (i == 0) {
                ledge(stage, x + 480, 180, 170);
            } else if (i == 1) {
                ledge(stage, x + 270, 185, 160);
                ledge(stage, x + 530, 220, 155);
            } else {
                if (i % 2 == 0) ledge(stage, x + 260, 185, 170);
                if (i % 3 == 1) ledge(stage, x + 485, 225, 175);
                if (i >= 10 && i % 4 == 3) ledge(stage, x + 310, 310, 165);
            }



            if (i >= 2 && i % 3 == 2) {
                stage.getTraps().add(new Trap(x + 465, 70, 42, 22));
            }
            if (i >= 7 && i % 4 == 1) {
                stage.getTraps().add(new Trap(x + 590, 70, 42, 22));
            }




            if (i == 1 || i == 3 || i == 5 || i == 7 || i == 9 ||
                i == 11 || i == 13 || i == 15 || i == 17) {
                float start = x + (i < 6 ? 300 : 260);
                float patrol = (i < 6 ? 115 : 145);
                float speed = (i < 6 ? 65 : i < 12 ? 80 : 95);
                stage.getEnemies().add(new Enemy(start, 70, 48, 45, patrol, speed));
            }

            if (i == 6 || i == 10 || i == 14) {
                stage.getEnemies().add(new Enemy(x + 380, 70, 48, 45, 90, 85));
            }
            if (i >= 10 && i % 4 == 2) {

                stage.getEnemies().add(new Enemy(x + 555, 70, 48, 45, 70, 70));
            }


            if (i == 4 || i == 8 || i == 12 || i == 16) {
                stage.getCheckpoints().add(new Checkpoint(x + 95, 70));
            }
        }

        ground(stage, SECTION_COUNT * SECTION, Stage.WIDTH - SECTION_COUNT * SECTION);
        stage.getGoal().set(Stage.WIDTH - 230, 70, 45, 145);
    }


    private static void populateSkyCourse(Stage stage) {
        ground(stage, 0, 1050);






        final float[] top = {145, 225, 335, 205, 315, 410, 275, 190, 310, 390};
        final int count = 34;
        for (int i = 0; i < count; i++) {
            float x = 1060 + i * 405f;
            float y = top[i % top.length] - 25f;
            boolean checkpoint = (i == 7 || i == 15 || i == 23 || i == 30);
            if (i == 3) {


            } else if (checkpoint || i % 6 == 5) {
                ledge(stage, x, y, 220);
            } else if (i % 6 == 0 || i % 6 == 3) {
                verticalLedge(stage, x, y - 65f, 220);
            } else {
                horizontalLedge(stage, x, y, 220);
            }
            if (checkpoint) stage.getCheckpoints().add(new Checkpoint(x + 65, top[i % top.length]));
            if (i >= 3 && i % 5 == 3 && !checkpoint) {
                stage.getFlyingEnemies().add(new FlyingEnemy(
                    x + 125, top[i % top.length] + 100, 44, 36, 90, 60, 20, 0.6f));
            }
        }
        ground(stage, 14920, Stage.WIDTH - 14920);
        stage.getGoal().set(Stage.WIDTH - 230, 70, 45, 145);
    }

    public static void populate(Stage stage, int n) {
        if (n == 1) {
            populateIntroCourse(stage);
            return;
        }
        if (n == 2) {
            populateSkyCourse(stage);
            return;
        }
        populateFinalCourse(stage);
    }


    private static void populateFinalCourse(Stage stage) {

        ground(stage, 0, 1050);
        ground(stage, 1200, 850);
        ground(stage, 2200, 800);
        ledge(stage, 910, 145, 190);
        stage.getMovingPlatforms().add(new MovingPlatform(2010, 135, 200, 25, 85, 50, true));
        stage.getEnemies().add(new Enemy(550, 70, 48, 45, 150, 80));
        stage.getEnemies().add(new Enemy(1560, 70, 48, 45, 160, 95));







        stage.getFlyingEnemies().add(new FlyingEnemy(
            2310, 310, 44, 36, 85, 35, 8, 0.45f));


        ground(stage, 3000, 810);
        ground(stage, 3960, 850);
        ground(stage, 4960, 1040);
        ledge(stage, 3710, 165, 205);
        stage.getMovingPlatforms().add(new MovingPlatform(4660, 160, 205, 25, 100, 55, true));



        stage.getFlyingEnemies().add(new FlyingEnemy(
            4980, 350, 44, 36, 95, 40, 8, 0.45f));

        stage.getTraps().add(new Trap(3370, 70, 42, 22));
        stage.getTraps().add(new Trap(4350, 70, 42, 22));
        stage.getEnemies().add(new Enemy(4080, 70, 48, 45, 140, 100));







        spikeCarpet(stage, 1800, 2050);
        spikeCarpet(stage, 2200, 2600);
        spikeCarpet(stage, 4600, 4810);
        spikeCarpet(stage, 4960, 5300);

        stage.getCheckpoints().add(new Checkpoint(6000, 70));


        ground(stage, 6000, 460);
        ground(stage, 8630, 370);
        float[] cx = {6380, 6660, 6940, 7220, 7500, 7780, 8060, 8340};
        float[] cy = {135, 245, 325, 195, 315, 260, 190, 125};

        for (int i = 0; i < cx.length; i++) {
            float y = cy[i] - 25f;
            if (i == 3 || i == 5) continue;
            if (i == 0 || i == 3 || i == 6)
                verticalLedge(stage, cx[i], y - 65f, 200);
            else horizontalLedge(stage, cx[i], y, 200);
        }
        stage.getDynamicTraps().add(new DynamicTrap(8810, 70, 42, 22, 3.2f, 1.5f, 0.4f));


        ground(stage, 9000, 450);
        final float[] skyTop={155,255,355,215,335,390,260,175,305,205};
        for (int i = 0; i < 10; i++) {
            float x = 9350 + i * 310f;
            float top = skyTop[i];
            if (i == 9) {

                continue;
            }
            if (i == 7) {


                stage.getFlyingEnemies().add(new FlyingEnemy(
                    x + 90, top + 110, 44, 36, 85, 60, 20, 0.65f));
                continue;
            }
            if (i == 5) {
                ledge(stage, x, top - 25, 215);
                stage.getCheckpoints().add(new Checkpoint(x + 65, top));
            } else if (i == 0 || i == 3 || i == 6 || i == 9) {
                verticalLedge(stage, x, top - 95, 215);
            } else {
                horizontalLedge(stage, x, top - 25, 215);
            }
            if (i == 2 || i == 8) stage.getFlyingEnemies().add(new FlyingEnemy(
                x + 90, top + 110, 44, 36, 85, 60, 20, 0.65f));
        }

        ground(stage, 12430, 470);


        ground(stage, 12900, 720);
        ground(stage, 13770, 700);
        ground(stage, 14620, Stage.WIDTH-14620);
        verticalLedge(stage, 13500, 140, 205);




        stage.getFlyingEnemies().add(new FlyingEnemy(
            13700, 414, 44, 36, 55, 32, 5, 0.4f));
        horizontalLedge(stage, 14340, 160, 205);
        stage.getTraps().add(new Trap(13180, 70, 42, 22));
        stage.getDynamicTraps().add(new DynamicTrap(14070, 70, 42, 22, 3.0f, 1.45f, 0.6f));
        stage.getEnemies().add(new Enemy(13310, 70, 48, 45, 115, 105));
        stage.getEnemies().add(new Enemy(14830, 70, 48, 45, 150, 120));
        stage.getFlyingEnemies().add(new FlyingEnemy(
            14300, 270, 44, 36, 105, 70, 25, 0.7f));
        stage.getTraps().add(new Trap(15180, 70, 42, 22));
        stage.getGoal().set(Stage.WIDTH-230, 70, 45, 145);
    }
}
