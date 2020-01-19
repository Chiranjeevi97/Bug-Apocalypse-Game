
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

public class LevelScreen extends BaseScreen
{
    private Hero heroActor;
    private GrassHopper bugActor;
    private boolean gameOver, once = true;
    long lastbugDropTime;
    long lastgrasshopperDropTime, lastrockDropTime, lastfireTime;
    public int score = 0;
    public boolean win = false, fire = true; 
    public long starttime, duration;
    private Label scoreLabel;
    private Label timeLabel;
    private Label bulletLabel;
    public int bullets = 300, flames = 10;
    private float audioVolume;
    private Music instrumental;
    private Music lost, winmusic, laser, firemusic, hit;

    public void initialize() 
    { 
        starttime = System.nanoTime();
        BaseActor grass = new BaseActor(0,0, mainStage);
        grass.loadTexture( "assets/grass1.png" );
        grass.setSize(800,600);
        BaseActor.setWorldBounds(grass);
        
        heroActor = new Hero(800 / 2 - 64 / 2,20, mainStage);
        
        scoreLabel = new Label("Score : " , BaseGame.labelStyle);
        scoreLabel.setColor( Color.YELLOW );
        scoreLabel.setPosition( 20, 520 );
        uiStage.addActor(scoreLabel);
        
        timeLabel = new Label("TIME : " , BaseGame.labelStyle);
        timeLabel.setColor( Color.RED );
        timeLabel.setPosition( 550, 520 );
        uiStage.addActor(timeLabel);
        
        instrumental = Gdx.audio.newMusic(Gdx.files.internal("assets/Crazy_Frog.mp3"));
        lost = Gdx.audio.newMusic(Gdx.files.internal("assets/lostmusic.wav"));
        laser = Gdx.audio.newMusic(Gdx.files.internal("assets/laser.mp3"));
        firemusic = Gdx.audio.newMusic(Gdx.files.internal("assets/fire.mp3"));
        winmusic = Gdx.audio.newMusic(Gdx.files.internal("assets/win1.mp3"));
        hit = Gdx.audio.newMusic(Gdx.files.internal("assets/hit.mp3"));
        
        audioVolume = 0.30f;
        instrumental.setVolume(audioVolume);
        instrumental.play();
        laser.setVolume(audioVolume);
        lost.setVolume(audioVolume);
        firemusic.setVolume(audioVolume);
        hit.setVolume(audioVolume);
        
        gameOver = false;
    }
    
    public void spawnBug()
    {
        new Bug(MathUtils.random(0, 800 - 64),570, mainStage);
        lastbugDropTime = TimeUtils.nanoTime();
    }
    
    public void spawnGrassHopper()
    {
        bugActor = new GrassHopper(MathUtils.random(0, 800 - 64),570, mainStage);
        lastgrasshopperDropTime = TimeUtils.nanoTime();
    }

    public boolean getProgress()
    {
        return win;
    }
    
    public void update(float dt)
    {
        scoreLabel.setText("SCORE : " + score);
        
        if ((TimeUtils.nanoTime() - lastbugDropTime > 1000000000) && !gameOver){
               spawnBug();
        }
          
        if((TimeUtils.nanoTime()/1000000000 - lastfireTime/1000000000 >= 3)){
            fire = true;
        }
        
        if ((TimeUtils.nanoTime()/1000000000 - lastgrasshopperDropTime/1000000000 > 3) && !gameOver){
               spawnGrassHopper();
        } 
        
        if(duration == 25)
            duration = 25;
        else
            duration = System.nanoTime()/1000000000 - starttime/1000000000;
        
        timeLabel.setText("TIME : " + duration);
           
        if(duration == 25 && !win || bullets == 0 && !win)
        {
            ButtonStyle buttonStyle = new ButtonStyle();

            Texture buttonTex = new Texture( Gdx.files.internal("assets/undo.png") );
            NinePatch buttonPatch = new NinePatch(buttonTex,24,24,24,24);
            buttonStyle.up = new NinePatchDrawable(buttonPatch);
            Button restartButton = new Button( buttonStyle );
            uiStage.addActor(restartButton);
            restartButton.setPosition(350, 190);
            restartButton.addListener(
            (Event e) ->
            {
                if (!isTouchDownEvent(e) )
                    return false;
                else{
                    BugApocalypse.setActiveScreen( new LevelScreen() );
                    BugApocalypse.setLevel(false);
                }
                return true;
            }
            );
               BaseActor messageLose = new BaseActor(0,0, uiStage);
               messageLose.loadTexture("assets/message-lose.png");
               messageLose.centerAtPosition(400,300);
               messageLose.setOpacity(0);
               messageLose.addAction( Actions.fadeIn(1) );
               gameOver = true;
               instrumental.stop();
               if(once){
                   lost.play();
                   once = false;
                }
        }
            
        for ( BaseActor rockActor : BaseActor.getList(mainStage, "Bug") )
        {
            if (rockActor.overlaps(heroActor))
            {
                if (heroActor.shieldPower <= 0 && !win)
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(heroActor);
                    heroActor.remove();
                    heroActor.setPosition(-1000,-1000);
                    
                    ButtonStyle buttonStyle = new ButtonStyle();
        
                    Texture buttonTex = new Texture( Gdx.files.internal("assets/undo.png") );
                    NinePatch buttonPatch = new NinePatch(buttonTex,24,24,24,24);
                    buttonStyle.up = new NinePatchDrawable(buttonPatch);
                    Button restartButton = new Button( buttonStyle );
                    uiStage.addActor(restartButton);
                    restartButton.setPosition(350, 190);
                    restartButton.addListener(
                    (Event e) -> 
                    { 
                        if (!isTouchDownEvent(e) )
                            return false;
                        
                        BugApocalypse.setActiveScreen( new LevelScreen() );
                        BugApocalypse.setLevel(false);
                        return true;
                    }
                    );
                    BaseActor messageLose = new BaseActor(0,0, uiStage);
                    messageLose.loadTexture("assets/message-lose.png");
                    messageLose.centerAtPosition(400,300);
                    messageLose.setOpacity(0);
                    messageLose.addAction( Actions.fadeIn(1) );
                    gameOver = true;
                    instrumental.stop();
                    if(once){
                        lost.play();
                        once = false;
                    }
                }
                
                else
                {
                    heroActor.shieldPower -= 34;
                    hit.play();
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    rockActor.remove();
                }

            }
            
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Bullet") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    if(score >= 20)
                        score = 20;
                    else
                        score++;
                }
            }
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Fire") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Actions.delay(1);
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    firemusic.stop();
                    if(score >= 20)
                        score = 20;
                    else
                        score = score + 1;
                }
            }
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Arrow") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    if(score >= 20)
                        score = 20;
                    else
                        score = score + 1;
                }
            }
        }
        
        for ( BaseActor rockActor : BaseActor.getList(mainStage, "GrassHopper") )
        {
            if (rockActor.overlaps(heroActor))
            {
                if (heroActor.shieldPower <= 0 && !win)
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(heroActor);
                    heroActor.remove();
                    heroActor.setPosition(-1000,-1000);
                    ButtonStyle buttonStyle = new ButtonStyle();
        
                    Texture buttonTex = new Texture( Gdx.files.internal("assets/undo.png") );
                    NinePatch buttonPatch = new NinePatch(buttonTex,24,24,24,24);
                    buttonStyle.up = new NinePatchDrawable(buttonPatch);
                    Button restartButton = new Button( buttonStyle );
                    uiStage.addActor(restartButton);
                    restartButton.setPosition(350, 190);
                    restartButton.addListener(
                    (Event e) -> 
                    { 
                        if (!isTouchDownEvent(e) )
                            return false;
                        else{
                            BugApocalypse.setActiveScreen( new LevelScreen() );
                            BugApocalypse.setLevel(false);
                        }
                        return true;
                    }
                    );
                    
                    BaseActor messageLose = new BaseActor(0,0, uiStage);
                    messageLose.loadTexture("assets/message-lose.png");
                    messageLose.centerAtPosition(400,300);
                    messageLose.setOpacity(0);
                    messageLose.addAction( Actions.fadeIn(1) );
                    gameOver = true;
                    instrumental.stop();
                    if(once){
                        lost.play();
                        once = false;
                    }
                }
                
                else
                {
                    heroActor.shieldPower -= 50;
                    hit.play();
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    rockActor.remove();
                }

            }

            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Bullet") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    if(score >= 20)
                        score = 20;
                    else
                        score = score + 2;
                }
            }
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Arrow") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    if(score >= 20)
                        score = 20;
                    else
                        score = score + 2;
                }
            }
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Fire") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Actions.delay(1);
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    firemusic.stop();
                    if(score >= 20)
                        score = 20;
                    else
                        score = score + 2;
                }
            }
        }
        
        if (!gameOver && score >= 20)
        {
            ButtonStyle buttonStyle = new ButtonStyle();
        
            Texture buttonTex = new Texture( Gdx.files.internal("assets/nextlevelbutton.png") );
            NinePatch buttonPatch = new NinePatch(buttonTex,24,24,24,24);
            buttonStyle.up = new NinePatchDrawable(buttonPatch);
            Button restartButton = new Button( buttonStyle );
            uiStage.addActor(restartButton);
            restartButton.setPosition(250, 240);
            restartButton.addListener(
            (Event e) -> 
            { 
                if (!isTouchDownEvent(e) )
                    return false;
                else{
                    BugApocalypse.setActiveScreen( new SecondLevelScreen() );
                    BugApocalypse.setLevel(true);
                }
                return true;
            }
            );
            gameOver = true;
            win = true;
            instrumental.stop();
            winmusic.play();
        }
        
    }

    // override default InputProcessor methods
    public boolean keyDown(int keycode)
    {
        if ( keycode == Keys.SPACE )
        {
            if(bullets > 0 && !gameOver)
            {   
                heroActor.shoot();
                laser.play();
                bullets--;
            }
        }

        if ( keycode == Keys.F)
        {
            if(!gameOver && flames > 0 && fire){
                heroActor.fireThrower();
                firemusic.play();
                lastfireTime = TimeUtils.nanoTime();
                flames--;
                fire = false;
            }
        }
        return false;  
    }

}

