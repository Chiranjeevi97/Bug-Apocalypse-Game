import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;

    public class Hero extends BaseActor
{
    private Shield shield;
    public int shieldPower;
    public LevelScreen won;
    private boolean win = false, fired = false;

    public Hero(float x, float y, Stage s)
    {
        super(x,y,s);

        loadTexture("assets/frog.png");
        setSize(75, 75);
        setBoundaryPolygon(8);
        setAcceleration(200);
        setMaxSpeed(150);
        setDeceleration(200);
        setRotation(0);
        
        //won = new LevelScreen();
        
        shield = new Shield(0,0, s);
        addActor(shield);
        shield.centerAtPosition( getWidth()/2, getHeight()/2 );
        shieldPower = 100;
    }

    public void shoot()
    {
        if ( getStage() == null)
            return;

        Bullet bullet = new Bullet(0,0, this.getStage());
        bullet.centerAtActor(this);
        bullet.setRotation(90);
    }

    public void shootArrow()
    {
        if ( getStage() == null)
            return;

        Arrow arrow = new Arrow(0,0, this.getStage());
        arrow.centerAtActor(this);
    }

    public void fireThrower()
    {
        if ( getStage() == null)
            return;
        
        Fire fire = new Fire(0,0, this.getStage());
        fire.topAtActor(this);
        fire.accelerateAtAngle(90);

    }
    
    public void warp()
    {
        if ( getStage() == null)
            return;

        Warp warp1 = new Warp(0,0, this.getStage());
        warp1.centerAtActor(this);
        setPosition(MathUtils.random(800),0);
        Warp warp2 = new Warp(0,0, this.getStage());
        warp2.centerAtActor(this);
    }

    public void act(float dt)
    {
        super.act( dt );
        
        //win = won.getProgress();
        
        if (Gdx.input.isKeyPressed(Keys.LEFT)) 
            accelerateAtAngle(180);
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            accelerateAtAngle(0);
        applyPhysics(dt);
        
        if(BugApocalypse.getLevel())
        {
            wrapAroundWorld();
        }
        else
        {   
            boundToWorld();
        }
        
        shield.setOpacity(shieldPower / 100f);
    }

}