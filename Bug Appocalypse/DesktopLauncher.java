import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopLauncher extends BaseGame
{
    public static void main (String[] args)
    {
        // To start a LibGDX program, this method:
        // (1) creates an instance of the game
        // (2) creates a new application with game instance and window settings as argument
        Game myGame = new BugApocalypse  ();
        LwjglApplication launcher = new LwjglApplication( myGame, "Bug Apocalypse", 800, 600 );
    }
}