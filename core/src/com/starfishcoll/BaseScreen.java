package com.starfishcoll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.InputProcessor;         // interface to handle discrete user input
import com.badlogic.gdx.InputMultiplexer;    // class to handle discrete user input: a central dispatcher (in BaseGame)
import com.badlogic.gdx.scenes.scene2d.ui.Table;   // for effective layout management - subclass of Actor


/**
 * Part of Game FW   implemented during creating Starfish Collector game
 Organizing every common method about graphic object creation and rendering: through a Stage object.
 This further version uses multiple screens which uses multiple stages.
 Here is the template for such a Game Screen as a controlling center.
 The version used in Space Rocks game implements InputProcessor as well in order to handle discrete user input
 Table: for effective layout management
 07/01/2022 - last: 18/01/2022
 */

public abstract class BaseScreen implements Screen, InputProcessor
{
    // stages are visible only in this package and subclasses
    protected Stage mainStage;      // game world stage (with aligned camera acc. to Turtle)
    protected Stage uiStage;        // user interface stage (with fix camera)
    protected Table uiTable;        // for effective layout management


    public BaseScreen()
    {
        mainStage = new Stage();
        uiStage = new Stage();

        uiTable = new Table();
        uiTable.setFillParent(true);    // in order to be fitted to Stage (which does not handle the size of its children)
        uiStage.addActor(uiTable);

        initialize();               // unique: Actors def. + adding to Stage object
    }

    public abstract void initialize();    // must be overridden in subclass!

    // RENDER
    @Override
    public void render(float dt)        // time elapsed since last rendering is here a received parameter
    {
        // act method
        mainStage.act(dt);      // user input query for every Actor of Stage, actions management
        uiStage.act(dt);
        // defined by user
        update(dt);             // unique: upon user input update every Actor
        // clear the screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // draw the graphics
        mainStage.draw();       // draw every Actor of Stage
        uiStage.draw();
    }

    public abstract void update(float dt);   // must be overridden in subclass!


    // SCREEN INTERFACE methods

    public void resize(int width, int height) { }
    public void pause() { }
    public void resume() { }
    public void dispose() { mainStage.dispose(); uiStage.dispose(); }

    // both of the Stage objects and the BaseScreen class itself should be added to the game’s central user input dispatcher:
    // InputMultiplexer when this screen is displayed...
    public void show()
    {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();     // returns the currently set InputProcessor (... in BaseGame)
        im.addProcessor(this);      // delegate it and the stages to the event controller - chain InputProcessors (an event will run through each of them in order...)
        im.addProcessor(uiStage);
        im.addProcessor(mainStage);
    }

    // ... and they should be removed when another screen is set...
    public void hide()
    {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.removeProcessor(this);
        im.removeProcessor(uiStage);
        im.removeProcessor(mainStage);
    }


    // INPUTPROCESSOR INTERFACE methods:
    // return values are false = here they do not handle any event
    // any methods that will be used will be overridden in the extension of the BaseScreen class
    public boolean keyDown(int keycode)    { return false; }
    public boolean keyUp(int keycode)    { return false; }
    public boolean keyTyped(char c)    { return false; }
    public boolean mouseMoved(int screenX, int screenY)    { return false; }
    public boolean scrolled(float amountX, float amountY)    { return false; }
    public boolean touchDown(int screenX, int screenY, int pointer, int button)    { return false; }
    public boolean touchDragged(int screenX, int screenY, int pointer)    { return false; }
    public boolean touchUp(int screenX, int screenY, int pointer, int button)    { return false; }
}