import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Fire extends BaseActor
{  
    public Fire(float x, float y, Stage s)
    {
       super(x,y,s);
       
       String[] filenames = 
            {"assets/frame_1.png", "assets/frame_2.png", "assets/frame_3.png", 
                "assets/frame_4.png", "assets/frame_5.png", "assets/frame_6.png", 
                "assets/frame_7.png", "assets/frame_8.png", "assets/frame_9.png", 
                "assets/frame_10.png", "assets/frame_11.png", "assets/frame_12.png"};

       loadAnimationFromFiles(filenames, 0.1f, true);
       setSize(290, 290);
       setBoundaryPolygon(8);     
       
       addAction( Actions.delay(0.4f) );   
       addAction( Actions.after( Actions.fadeOut(0.2f) ) );   
       addAction( Actions.after( Actions.removeActor() ) );  
       
       setSpeed(400);
       setMaxSpeed(100);
       setDeceleration(400);
       setMotionAngle(90);
    }
    
    public void act(float dt)
    {
        super.act(dt);
        applyPhysics(dt);
        
        setAnimationPaused( !isMoving() );

        if ( getSpeed() > 0 )
            setRotation( getMotionAngle() );
    }  
}