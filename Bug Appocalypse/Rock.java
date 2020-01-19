import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Rock extends BaseActor
{  
    long delayM;
    float delay;
    public Rock(float x, float y, Stage s)
    {
       super(x,y,s);
        
       loadTexture("assets/rock.png");

       float random = MathUtils.random(30);
       
       addAction( Actions.forever( Actions.rotateBy(30 + random, 1) ) );   
       
       setSpeed(50 + random);
       setMaxSpeed(50 + random);
       setDeceleration(0);
       
       setMotionAngle( MathUtils.random(230, 300) );
    }
    
    public void act(float dt)
    {
        super.act(dt);
        applyPhysics(dt);
        //wrapAroundWorld();
        delay = MathUtils.random(0.1f, 0.5f);
        if (((TimeUtils.nanoTime() - delayM)/1000000000 > 0.4)){
            setMotionAngle(MathUtils.random(240, 290));
            delayM = TimeUtils.nanoTime();
        }
    }
}