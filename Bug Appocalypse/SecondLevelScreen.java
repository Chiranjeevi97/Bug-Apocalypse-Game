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

public class SecondLevelScreen extends BaseScreen
{
    private Hero heroActor;
    private Bug bugActor;
    private boolean gameOver, once = true;
    long lastbugDropTime;
    long lastgrasshopperDropTime, lastrockDropTime, lastfireTime;
    public int score = 0;
    public boolean win = false, fire = true; 
    public long starttime, duration;
    private Label scoreLabel;
    private Label timeLabel, arrowLabel;
    private Label bulletLabel;
    public int bullets = 25;
    public int arrows = 10, flames = 10;
    private float audioVolume;
    private Music instrumental;
    private Music lost, winmusic, laser, firemusic, hit;

    public void initialize() 
    { 
        starttime = System.nanoTime();
        BaseActor grass = new BaseActor(0,0, mainStage);
        grass.loadTexture( "assets/grass.png" );
        grass.setSize(800,500);
        BaseActor.setWorldBounds(grass);
        
        heroActor = new Hero(800 / 2 - 64 / 2,20, mainStage);
        
        scoreLabel = new Label("Score : " , BaseGame.labelStyle);
        scoreLabel.setColor( Color.YELLOW );
        scoreLabel.setPosition( 20, 520 );
        uiStage.addActor(scoreLabel);
        
        bulletLabel = new Label("Bullets : " , BaseGame.labelStyle);
        bulletLabel.setColor( Color.YELLOW );
        bulletLabel.setPosition( 520, 25 );
        uiStage.addActor(bulletLabel);
        
        timeLabel = new Label("TIME : " , BaseGame.labelStyle);
        timeLabel.setColor( Color.RED );
        timeLabel.setPosition( 550, 520 );
        uiStage.addActor(timeLabel);

        arrowLabel = new Label("ARROWS : " , BaseGame.labelStyle);
        arrowLabel.setColor( Color.RED );
        arrowLabel.setPosition( 50, 25 );
        uiStage.addActor(arrowLabel);

        ButtonStyle buttonStyle = new ButtonStyle();
        
        Texture buttonTex = new Texture( Gdx.files.internal("assets/undo.png") );
        NinePatch buttonPatch = new NinePatch(buttonTex, 24,24,24,24);
        buttonStyle.up = new NinePatchDrawable(buttonPatch);
        
        instrumental = Gdx.audio.newMusic(Gdx.files.internal("assets/secondmusic.mp3"));
        lost = Gdx.audio.newMusic(Gdx.files.internal("assets/lostmusic.wav"));
        winmusic = Gdx.audio.newMusic(Gdx.files.internal("assets/winmusic.mp3"));
        laser = Gdx.audio.newMusic(Gdx.files.internal("assets/laser.mp3"));
        firemusic = Gdx.audio.newMusic(Gdx.files.internal("assets/fire.mp3"));
        hit = Gdx.audio.newMusic(Gdx.files.internal("assets/hit.mp3"));
        
        audioVolume = 0.30f;
        instrumental.setVolume(audioVolume);
        instrumental.play();
        lost.setVolume(audioVolume);
        winmusic.setVolume(audioVolume);
        laser.setVolume(audioVolume);
        firemusic.setVolume(audioVolume);
        hit.setVolume(audioVolume);
        
        Button restartButton = new Button( buttonStyle );
        uiStage.addActor(restartButton);

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
        
        gameOver = false;
    }
    
    public void spawnBug()
    {
        bugActor = new Bug(MathUtils.random(0, 800 - 64),570, mainStage);
        lastbugDropTime = TimeUtils.nanoTime();
        lastrockDropTime = TimeUtils.nanoTime();
        bugActor.shootRock();
    }
    
    public void spawnGrassHopper()
    {
        new GrassHopper(MathUtils.random(0, 800 - 64),570, mainStage);
        lastgrasshopperDropTime = TimeUtils.nanoTime();
    }

    public boolean getProgress()
    {
        return win;
    }
    
    public void update(float dt)
    {
        scoreLabel.setText("SCORE : " + score);
        arrowLabel.setText("ARROWS : " + arrows);
        bulletLabel.setText("Bullets : " + bullets);
        if ((TimeUtils.nanoTime()/1000000000 - lastgrasshopperDropTime/1000000000 > 1) && !gameOver){
               spawnGrassHopper();
        }
        
        if ((TimeUtils.nanoTime()/1000000000 - lastbugDropTime/1000000000 > 3) && !gameOver){
                spawnBug();              
        } 
        
        if((TimeUtils.nanoTime()/1000000000 - lastfireTime/1000000000 >= 3)){
            fire = true;
        }
        
        if(duration == 30)
            duration = 30;
        else
            duration = System.nanoTime()/1000000000 - starttime/1000000000;
            
        timeLabel.setText("TIME : " + duration);
             
        if(duration == 30 && !win || bullets == 0 && !win)
        {
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

                    BaseActor messageLose = new BaseActor(0,0, uiStage);
                    messageLose.loadTexture("assets/message-lose.png");
                    messageLose.centerAtPosition(400,300);
                    messageLose.setOpacity(0);
                    messageLose.addAction( Actions.fadeIn(1) );
                    gameOver = true;
                    instrumental.stop();
                    if(once)
                    lost.play();
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
                    if(score >= 30)
                        score = 30;
                    else
                        score = score + 3;
                }
            }
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Fire") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    firemusic.stop();
                    if(score >= 30)
                        score = 30;
                    else
                        score = score + 3;
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
                    if(score >= 30)
                        score = 30;
                    else
                        score = score + 3;
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
                    if(score >= 30)
                        score = 30;
                    else
                        score = score + 2;
                }
            }
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Fire") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    firemusic.stop();
                    if(score >= 30)
                        score = 30;
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
                    if(score >= 30)
                        score = 30;
                    else
                        score = score + 2;
                }
            }
        }
        
        for ( BaseActor rockActor : BaseActor.getList(mainStage, "Rock") )
        {
            if (rockActor.overlaps(heroActor))
            {
                if (heroActor.shieldPower <= 0 && !win)
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(heroActor);
                    heroActor.remove();
                    heroActor.setPosition(-1000,-1000);

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
                    heroActor.shieldPower -= 20;
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
                    if(score >= 30)
                        score = 30;
                    else
                        score--;
                }
            }
            
            for ( BaseActor laserActor : BaseActor.getList(mainStage, "Fire") )
            {
                if (laserActor.overlaps(rockActor))
                {
                    Blast boom = new Blast(0,0, mainStage);
                    boom.centerAtActor(rockActor);
                    laserActor.remove();
                    rockActor.remove();
                    firemusic.stop();
                    if(score >= 30)
                        score = 30;
                    else
                        score--;
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
                    if(score >= 30)
                        score = 30;
                    else
                        score--;
                }
            }
        }
        
        if (!gameOver && score >= 30)
        {
            BaseActor messageWin = new BaseActor(0,0, uiStage);
            messageWin.loadTexture("assets/message-win.png");
            messageWin.centerAtPosition(400,300);
            messageWin.setOpacity(0);
            messageWin.addAction( Actions.fadeIn(1) );
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
        
        if ( keycode == Keys.X)
        {
            if(!gameOver)
                heroActor.warp();
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
        
        if ( keycode == Keys.A)
        {
            if(!gameOver && arrows > 0){
                heroActor.shootArrow();
                arrows--;
            }
            }
        
        return false;  
    }

}

