package io.jbnu.test;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
public class Player {
    private final Texture texture;
    private static final float WIDTH=65,HEIGHT=90,SPEED=350,GRAVITY=-1800,JUMP_FORCE=850;
    private float x=150,y=70,velocityY;
    private float previousFeetY=70;
    private boolean onGround=true,facingLeft;
    private Platform standingOn;
    public Player(){texture=new Texture(Gdx.files.internal("player/player1.png"));
        texture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);}
    public void update(float dt,Array<Platform> platforms,Array<MovingPlatform> movingPlatforms){
        if(standingOn instanceof MovingPlatform){x+=((MovingPlatform)standingOn).getDeltaX();y+=((MovingPlatform)standingOn).getDeltaY();}
        float direction=0;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))direction--;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))direction++;
        if(direction<0)facingLeft=true;else if(direction>0)facingLeft=false;
        float oldX=x;
        x+=direction*SPEED*dt;
        x=Math.max(0,Math.min(Stage.WIDTH-WIDTH,x));

        // 수평 벽 충돌과 수직 충돌을 별도로 처리한다.
        for(Platform p:platforms){resolveHorizontal(p,oldX,direction);}
        for(MovingPlatform p:movingPlatforms){resolveHorizontal(p,oldX,direction);}

        // [1] SPACE 키 입력 시 점프
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            velocityY = JUMP_FORCE;
            onGround = false;
            standingOn = null;
        }

    // [2] 충돌 판정을 위한 이전 위치 저장
        float previousY = y;
        previousFeetY = y;

    // [3] 중력 적용 및 최대 낙하 속도 제한
        velocityY += GRAVITY * dt;
        velocityY = Math.max(velocityY, -950);

    // [4] 수직 속도에 따른 플레이어 위치 갱신
        y += velocityY * dt;
        onGround = false;
        standingOn = null;
        // [5] 하강 중 발판 착지 판정
        if (velocityY <= 0) {
            for (Platform p : platforms) {
                if (land(p, previousY)) break;
            }

            if (!onGround)
                for (MovingPlatform p : movingPlatforms) {
                    if (land(p, previousY)) break;
                }

        // [6] 상승 중 천장 충돌 판정
        } else {
            for (Platform p : platforms)
                hitCeiling(p, previousY);

            for (MovingPlatform p : movingPlatforms)
                hitCeiling(p, previousY);
        }
    }
    private void resolveHorizontal(Platform p,float oldX,float direction){
        Rectangle b=p.getBounds();
        // 얇은 발판은 아래쪽과 옆쪽에서 통과할 수 있다.
        if(b.height<=25f)return;
        if(y+HEIGHT<=b.y+2||y>=b.y+b.height-2)return;
        if(direction>0&&oldX+WIDTH<=b.x&&x+WIDTH>b.x)x=b.x-WIDTH;
        if(direction<0&&oldX>=b.x+b.width&&x<b.x+b.width)x=b.x+b.width;
    }
    private boolean land(Platform p,float previousY){
        Rectangle b=p.getBounds();float top=b.y+b.height;
        if(x+WIDTH>b.x&&x<b.x+b.width&&previousY>=top-3&&y<=top){
            y=top;velocityY=0;onGround=true;standingOn=p;return true;
        }
        return false;
    }
    private void hitCeiling(Platform p,float previousY){
        Rectangle b=p.getBounds();
        // 얇은 공중 발판은 상승 점프를 막지 않는다.
        if(b.height<=25f)return;
        if(x+WIDTH>b.x&&x<b.x+b.width&&previousY+HEIGHT<=b.y&&y+HEIGHT>=b.y){y=b.y-HEIGHT;velocityY=0;}
    }
    public Rectangle getBounds(){return new Rectangle(x,y,WIDTH,HEIGHT);}
    public float getCenterX(){return x+WIDTH/2;}
    /** 몬스터 밟기는 하강 중 발이 몬스터 윗면을 통과할 때만 인정한다. */
    public boolean canStomp(Rectangle enemy){
        float top=enemy.y+enemy.height;
        return velocityY<0
            && previousFeetY>=top-8f
            && y<=top+8f
            && x+WIDTH>enemy.x+6f
            && x<enemy.x+enemy.width-6f;
    }
    public void bounceFromEnemy(float enemyTop){
        y=enemyTop+2f;
        velocityY=550f;
        onGround=false;
        standingOn=null;
    }
    public void resetPosition(){setPosition(150,70);facingLeft=false;}
    public void setPosition(float newX,float newY){x=newX;y=newY;velocityY=0;previousFeetY=newY;onGround=false;standingOn=null;}
    public void draw(SpriteBatch batch) {
        // 캐릭터 표시 크기: 기존 125의 80%

        final float visualHeight = 100f;

        // 이미지 원본 비율 유지
        final float visualWidth =
            visualHeight * texture.getWidth() / texture.getHeight();

        // 캐릭터를 충돌 영역 중앙에 배치
        final float visualX = x + (WIDTH - visualWidth) / 2f;

        // 좌우 방향에 따라 이미지 반전
        if (facingLeft) {
            batch.draw(
                texture,
                visualX + visualWidth,
                y,
                -visualWidth,
                visualHeight
            );
        } else {
            batch.draw(
                texture,
                visualX,
                y,
                visualWidth,
                visualHeight
            );
        }
    }
    public void dispose(){texture.dispose();}
}
