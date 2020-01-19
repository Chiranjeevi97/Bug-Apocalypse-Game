import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.MathUtils;

public class Bug extends BaseActor
{  
    public Bug(float x, float y, Stage s)
    {
       super(x,y,s);
       
       if(BugApocalypse.getLevel()){
           loadTexture("assets/bug2.png");
           setHeight(75);
           setWidth(64);
       }
       else{
           loadTexture("assets/bug.png");
           setHeight(64);
           setWidth(64);
       }

       
       setBoundaryPolygon(8);
       setSpeed(100);
       setMaxSpeed(100);
       setDeceleration(0);
       setMotionAngle(270);
    }
    
    public void act(float dt)
    {
        super.act(dt);
        applyPhysics(dt);
        //wrapAroundWorld();
    }
    
    public void shootRock()
    {
        if ( getStage() == null)
            return;

        Rock rock = new Rock(0,0, this.getStage());
        rock.centerAtActor(this);
        rock.setRotation(this.getRotation() );
    }
}