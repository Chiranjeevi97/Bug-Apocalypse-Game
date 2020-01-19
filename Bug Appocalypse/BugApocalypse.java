
/**
 * Write a description of class BugApocalypse here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class BugApocalypse extends BaseGame
{
    public static boolean secondlevel = false;
    
    public void create() 
    {        
        super.create();
        setActiveScreen( new MenuScreen() );
    }
    
    public static void setLevel(boolean sent)
    {
        secondlevel = sent;
    }
    
    public static boolean getLevel()
    {
        return secondlevel;
    }
    
}

    
