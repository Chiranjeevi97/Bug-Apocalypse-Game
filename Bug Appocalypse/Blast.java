import com.badlogic.gdx.scenes.scene2d.Stage;

public class Blast extends BaseActor
{
    public Blast(float x, float y, Stage s)
    {
       super(x,y,s);
        
       loadAnimationFromSheet("assets/Blast.png", 6, 6, 0.03f, false);
    }
    
    public void act(float dt)
    {
        super.act(dt);
        
        if ( isAnimationFinished() )
            remove();
    }
}