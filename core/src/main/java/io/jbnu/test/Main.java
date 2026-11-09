package io.jbnu.test;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
public class Main extends ApplicationAdapter {
    private static final float WORLD_WIDTH=1280,WORLD_HEIGHT=720;
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private OrthographicCamera camera,uiCamera;
    private FitViewport viewport;
    private Player player;
    private StageManager stageManager;
    private Background background;
    private SnowTerrain snowTerrain;
    private FactoryTerrain factoryTerrain;
    private EnemyVisuals enemyVisuals;
    private SpikeVisuals spikeVisuals;
    private GameUI ui;
    private GameStateManager gameStates;
    private int stage=1;
    private boolean developerMode=false;
    private boolean showCoordinates=false, showCollisionBounds=false;
    private float respawnX=150,respawnY=70;
    private GameTimer timer;
    private GameAudio gameAudio;
    private Texture stormwindClearBackground;
    private static final int TRAM_REQUIRED_COINS = 10;
    private int savedCoins = 0;  // 이전 스테이지에서 확보한 코인
    private int stageCoins = 0;  // 현재 스테이지에서 획득한 코인
    // 부활 보호 시간은 게임 진행 중에만 감소한다.
    private static final float RESPAWN_PROTECTION_SECONDS = 1.2f;
    private float protectionRemaining = 0f;
    private float protectionBlinkElapsed = 0f;
    private boolean isProtected(){return protectionRemaining > 0f;}
    private void resetProtection(){protectionRemaining=0f;protectionBlinkElapsed=0f;}
    private void beginRespawnProtection(){protectionRemaining=RESPAWN_PROTECTION_SECONDS;protectionBlinkElapsed=0f;}

    @Override public void create(){
        batch=new SpriteBatch();shapes=new ShapeRenderer();

        camera=new OrthographicCamera();viewport=new FitViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);

        uiCamera=new OrthographicCamera();uiCamera.setToOrtho(false,WORLD_WIDTH,WORLD_HEIGHT);

        viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);

        player=new Player();
        stageManager=new StageManager();
        background=new Background();
        snowTerrain=new SnowTerrain();
        factoryTerrain=new FactoryTerrain();
        enemyVisuals=new EnemyVisuals();
        spikeVisuals=new SpikeVisuals();ui=new GameUI();
        timer=new GameTimer(Stage.TOTAL_STAGES);
        gameStates=new GameStateManager();
        gameAudio=new GameAudio();
        stormwindClearBackground=new Texture(Gdx.files.internal("backgrounds/stormwind_clear.png"));
        stormwindClearBackground.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        updateCamera(1);
    }
    private void startStage(int next){
        // 새 게임을 시작하면 누적 코인을 초기화한다.
        if (next == 1) savedCoins = 0;
        stageCoins = 0;
        stage=next;stageManager.setStage(stage);respawnX=150;respawnY=70;
        player.resetPosition();resetProtection();timer.startStage(stage);gameStates.startPlaying();
        gameAudio.playStage(stage);
        gameAudio.resumeBgm();
        updateCamera(1);
    }
    /** 현재 스테이지의 몬스터와 체크포인트를 초기화한다. */
    private void restartCurrentStage(){
        stageCoins = 0;
        startStage(stage);
    }
    // 지상 및 비행 몬스터를 최초 위치에 다시 생성한다.
    // 발판과 체크포인트 및 부활 위치는 유지한다.
    private void restoreEnemies(){
        Stage fresh = new Stage();
        fresh.setStage(stage);
        stageManager.getEnemies().clear();
        stageManager.getEnemies().addAll(fresh.getEnemies());
        stageManager.getFlyingEnemies().clear();
        stageManager.getFlyingEnemies().addAll(fresh.getFlyingEnemies());
    }
    private void die(String reason){
        if(isProtected() || !gameStates.die(1.5f))return;
        Gdx.app.log("GAME",reason);timer.recordDeath();stageCoins = 0;restoreEnemies();
    }
    private void dieFromFall(){
        // 부활 보호 중에도 추락은 사망으로 처리한다.
        resetProtection();
        die("FALL");
    }
    // 개발 모드에서 체크포인트로 이동하고 부활 위치를 갱신한다.
    private void jumpCheckpoint(boolean forward){
        float px=player.getBounds().x;
        Checkpoint selected=null;
        float best=forward?Float.MAX_VALUE:-Float.MAX_VALUE;
        for(Checkpoint c:stageManager.getCheckpoints()){
            float cx=c.getBounds().x;
            if(forward && cx>px+15f && cx<best){best=cx;selected=c;}
            if(!forward && cx<px-15f && cx>best){best=cx;selected=c;}
        }
        if(selected==null){Gdx.app.log("DEV", "No checkpoint in that direction");return;}
        selected.activate();
        respawnX=selected.getBounds().x+6f;
        respawnY=selected.getBounds().y;
        player.setPosition(respawnX,respawnY);
        beginRespawnProtection();
        updateCamera(1f);
    }
    private void drawCollisionDebug(){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.2f,1f,0.3f,1f);
        Rectangle b=player.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        shapes.setColor(0.3f,0.8f,1f,1f);
        for(Platform v:stageManager.getPlatforms()){
            b=v.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        }
        shapes.setColor(1f,0.85f,0.2f,1f);
        for(MovingPlatform v:stageManager.getMovingPlatforms()){
            b=v.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        }
        shapes.setColor(1f,0.3f,0.3f,1f);
        for(Enemy v:stageManager.getEnemies())if(v.isAlive()){
            b=v.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        }
        for(FlyingEnemy v:stageManager.getFlyingEnemies())if(v.isAlive()){
            b=v.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        }
        for(Trap v:stageManager.getTraps()){
            b=v.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        }
        for(DynamicTrap v:stageManager.getDynamicTraps()){
            b=v.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        }
        shapes.setColor(0.9f,0.3f,1f,1f);
        for(Checkpoint v:stageManager.getCheckpoints()){
            b=v.getBounds();shapes.rect(b.x,b.y,b.width,b.height);
        }
        shapes.end();
    }
    private void update(float dt){
        // 개발 모드 단축키는 시작 화면에서도 사용할 수 있다.
        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)){
            developerMode=!developerMode;
            Gdx.app.log("DEV", "Developer mode: " + developerMode);
        }
        if(developerMode){
            if(Gdx.input.isKeyJustPressed(Input.Keys.F2)){
                startStage(stage < Stage.TOTAL_STAGES ? stage+1 : 1);
                return;
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.F3)){
                startStage(stage > 1 ? stage-1 : Stage.TOTAL_STAGES);
                return;
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.F4)){
                restartCurrentStage();
                return;
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.F5))jumpCheckpoint(true);
            if(Gdx.input.isKeyJustPressed(Input.Keys.F6))jumpCheckpoint(false);
            if(Gdx.input.isKeyJustPressed(Input.Keys.F7))showCoordinates=!showCoordinates;
            if(Gdx.input.isKeyJustPressed(Input.Keys.F8))showCollisionBounds=!showCollisionBounds;
        }
        GameState state=gameStates.getState();
        if(state==GameState.READY){
            if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){timer.reset();startStage(1);}
            return;
        }
        // 일시정지
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            gameStates.togglePause();
            if (gameStates.getState() == GameState.PAUSED) gameAudio.pauseBgm();
            else if (gameStates.getState() == GameState.PLAYING) gameAudio.resumeBgm();
            return;
        }
        // 재시작
        if(state==GameState.PAUSED && Gdx.input.isKeyJustPressed(Input.Keys.R)){
            restartCurrentStage();
            return;
        }
        if(state==GameState.STAGE_CLEAR&&Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            savedCoins += stageCoins;
            startStage(stage+1);return;
        }
        if(state==GameState.TRAM_DENIED&&Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            timer.reset();
            startStage(1);
            return;
        }
        if(state==GameState.GAME_CLEAR&&Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            timer.reset();startStage(1);return;
        }
        if(state==GameState.DEAD){
            if(gameStates.updateDeath(dt)){player.setPosition(respawnX,respawnY);beginRespawnProtection();}
            return;
        }
        if(state!=GameState.PLAYING)return;
        if(protectionRemaining>0f){
            protectionRemaining=Math.max(0f,protectionRemaining-dt);
            protectionBlinkElapsed+=dt;
        }
        timer.update(dt);
        stageManager.update(dt);
        player.update(dt,stageManager.getPlatforms(),stageManager.getMovingPlatforms());
        Rectangle b=player.getBounds();
        // 한 프레임에서 사망 처리가 중복되지 않도록 상태를 확인한다.
        if(!isProtected()){
            for(Trap t:stageManager.getTraps()){
                if(b.overlaps(t.getBounds())){die("SPIKES");break;}
            }
        }
        if(gameStates.isPlaying()){
            for(Enemy e:stageManager.getEnemies()){
                if(!e.isAlive() || !b.overlaps(e.getBounds()))continue;
                if(player.canStomp(e.getBounds())){
                    e.defeat();
                    stageCoins++;
                    gameAudio.playCoin();
                    player.bounceFromEnemy(e.getBounds().y+e.getBounds().height);
                    b=player.getBounds();
                }else if(!isProtected()){
                    die("ENEMY");
                    break;
                }
            }
        }
        // 비행 몬스터에도 밟기 및 부활 보호 판정을 적용한다.
        if(gameStates.isPlaying()){
            for(FlyingEnemy e:stageManager.getFlyingEnemies()){
                if(!e.isAlive() || !b.overlaps(e.getBounds()))continue;
                if(player.canStomp(e.getBounds())){
                    e.defeat();
                    stageCoins++;
                    gameAudio.playCoin();
                    player.bounceFromEnemy(e.getBounds().y+e.getBounds().height);
                    b=player.getBounds();
                }else if(!isProtected()){
                    die("FLYING ENEMY");
                    break;
                }
            }
        }
        // 일시적인 충돌 보호 상태에서도 추락하면 사망한다.
        if(gameStates.isPlaying() && b.y+b.height<0)dieFromFall();
        if(gameStates.isPlaying()){
            for(Checkpoint c:stageManager.getCheckpoints()){
                if(b.overlaps(c.getBounds())&&!c.isActivated()){
                    c.activate();respawnX=c.getBounds().x+6;respawnY=c.getBounds().y;
                }
            }
            if (b.overlaps(stageManager.getGoal())) {
                timer.finishStage(stage);

                // [2] 2스테이지에서 코인 부족 여부 검사
                if (stage == 2 &&
                    savedCoins + stageCoins < TRAM_REQUIRED_COINS) {

                    // [3] 조건 미충족: 트램 탑승 거부
                    gameStates.denyTram();
                    gameAudio.pauseBgm();

                } else {
                    // [4] 조건 충족: 정상적인 스테이지 클리어
                    gameStates.finishStage(stage == Stage.TOTAL_STAGES);
                }

                // [5] 마지막 스테이지 완료 시 음악 종료
                if (stage == Stage.TOTAL_STAGES)
                    gameAudio.stopBgm();
            }
        }
        updateCamera(dt);
    }
    private void updateCamera(float dt){
        float target=MathUtils.clamp(player.getCenterX(),WORLD_WIDTH/2,Stage.WIDTH-WORLD_WIDTH/2);
        float alpha=Math.min(1,dt*5);
        camera.position.set(MathUtils.lerp(camera.position.x,target,alpha),WORLD_HEIGHT/2,0);
        camera.update();
    }
    @Override public void render(){
        float dt=Math.min(Gdx.graphics.getDeltaTime(),1f/30f);
        update(dt);
        ScreenUtils.clear(0.08f,0.11f,0.16f,1);
        viewport.apply();
        GameState state=gameStates.getState();
        if (state == GameState.GAME_CLEAR) {
            // 최종 클리어 시 스톰윈드 도착 이미지를 배경으로 표시한다.
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            batch.draw(stormwindClearBackground, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            batch.end();
        } else {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();background.draw(batch,stage);batch.end();
            shapes.setProjectionMatrix(camera.combined);stageManager.draw(shapes, false);
            batch.begin();snowTerrain.draw(batch,stageManager,stage);factoryTerrain.draw(batch,stageManager,stage);enemyVisuals.draw(batch,stageManager);spikeVisuals.draw(batch,stageManager);batch.end();
            if(developerMode && showCollisionBounds)drawCollisionDebug();
            batch.begin();
            // 부활 보호 중에만 플레이어를 점멸시킨다.
            boolean blinkOff=gameStates.isPlaying() && isProtected()
                && ((int)(protectionBlinkElapsed/0.10f)%2==1);
            if(!blinkOff)player.draw(batch);
            batch.end();
        }
        if(state==GameState.PAUSED || state==GameState.READY || state==GameState.STAGE_CLEAR || state==GameState.TRAM_DENIED || state==GameState.GAME_CLEAR || state==GameState.DEAD){
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapes.setProjectionMatrix(uiCamera.combined);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0.02f,0.04f,0.08f,state==GameState.READY?0.86f:(state==GameState.GAME_CLEAR?0.48f:0.72f));shapes.rect(0,0,WORLD_WIDTH,WORLD_HEIGHT);
            shapes.end();
        }
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();ui.draw(batch,stage,state,timer,savedCoins + stageCoins);ui.drawDeveloperMode(batch,developerMode);
        if(developerMode && showCoordinates){
            Rectangle debugPos=player.getBounds();
            ui.drawDebugCoordinates(batch,debugPos.x,debugPos.y,camera.position.x);
        }
        batch.end();
    }
    @Override public void resize(int width,int height){viewport.update(width,height,false);}
    @Override public void dispose(){batch.dispose();shapes.dispose();player.dispose();background.dispose();snowTerrain.dispose();factoryTerrain.dispose();enemyVisuals.dispose();
        spikeVisuals.dispose();ui.dispose();
        if (gameAudio != null) gameAudio.dispose();
        if (stormwindClearBackground != null) stormwindClearBackground.dispose();
    }
}
