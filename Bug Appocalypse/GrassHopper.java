import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GrassHopper extends BaseActor
{  
    long delayM;
    float delay;
    public GrassHopper(float x, float y, Stage s)
    {
        super(x,y,s);
        delayM = TimeUtils.nanoTime();
        
        if(BugApocalypse.getLevel()){
            loadTexture("assets/grasshopper2.png");
            setHeight(64);
            setWidth(75);
        }
        else{
            loadTexture("assets/grasshopper.png");
            setHeight(64);
            setWidth(64);
        }

        setBoundaryPolygon(8);
        setSpeed(100);
        setMaxSpeed(105);
        setDeceleration(0);
        setMotionAngle(MathUtils.random(230, 300));
    }

    public void act(float dt)
    {
        super.act(dt);
        applyPhysics(dt);
        delay = MathUtils.random(0.1f, 0.5f);
        if (((TimeUtils.nanoTime() - delayM)/1000000000 > 0.4)){
            setMotionAngle(MathUtils.random(240, 290));
            delayM = TimeUtils.nanoTime();
        }
    }

}
