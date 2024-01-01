package com.starfishcoll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;

/**
 * Starting screen of game
 * after pressing "S" it creates the game screen and sets it as active
 *
 * Version 2: start and quit buttons
 *
 * 07/01/2022 - last: 15/01/2022
 */

public class MenuScreen extends BaseScreen
{

    @Override
    public void initialize()
    {
        BaseActor ocean = new BaseActor(0,0, mainStage);	    // background lies here
        ocean.loadTexture ("water-border.jpg");		// BaseActor method
        ocean.setSize(1200,900);				// Actor method

        Camera cam = mainStage.getCamera();                 // set camera view at center of ocean
        cam.position.set( ocean.getX() + ocean.getWidth()/2, ocean.getY() + ocean.getHeight()/2, 0 );

        BaseActor title = new BaseActor(0,0, mainStage);        // titles, centered at ocean
        title.loadTexture( "starfish-collector.png" );
        // title.centerAtActor(ocean);
        // title.moveBy(0,100);


        /*BaseActor start = new BaseActor(0,0, mainStage);
        start.loadTexture( "message-start.png" );
        start.centerAtActor(ocean);
        start.moveBy(0,-100);*/

        // buttons: start and exit
        TextButton startButton = new TextButton( "Start", BaseGame.textButtonStyle );
        // startButton.setPosition(150,150);
        // uiStage.addActor(startButton);
        startButton.addListener(
                new EventListener() {
                    @Override
                    public boolean handle(Event e) {
                        if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                            return false;
                        StarfishGame.setActiveScreen(new StoryScreen());
                        return false;
                    }
                }
        );

        TextButton quitButton = new TextButton( "Quit", BaseGame.textButtonStyle );
        // quitButton.setPosition(500,150);
        // uiStage.addActor(quitButton);
        quitButton.addListener(
                new EventListener() {
                    @Override
                    public boolean handle(Event e) {
                        if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                            return false;
                        Gdx.app.exit();
                        return false;
                    }
                }
        );

        // Table positioning
        uiTable.add(title).colspan(2);
        uiTable.row();
        uiTable.add(startButton);
        uiTable.add(quitButton);
    }

    @Override
    public void update(float dt)        // 1 task only: if "S" is pressed create LevelScreen and set it as active
    {
        /*if (Gdx.input.isKeyPressed(Keys.S))   StarfishGame.setActiveScreen( new LevelScreen() );*/
    }

    @Override
    public boolean keyDown(int keyCode)         // keyboard versions for the 2 buttons
    {
        if (Gdx.input.isKeyPressed(Keys.ENTER))
            StarfishGame.setActiveScreen( new LevelScreen() );
        if (Gdx.input.isKeyPressed(Keys.ESCAPE))
            Gdx.app.exit();
        return false;
    }

}