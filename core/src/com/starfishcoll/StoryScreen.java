package com.starfishcoll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Color;

/**
 * This will play the "movie" = a Scene = list of SceneSegments...
 * 20/01/2022
 */

public class StoryScreen extends BaseScreen
{
    Scene scene;
    BaseActor continueKey;

    @Override
    public void initialize()
    {
        // Initialize actors

        BaseActor background = new BaseActor(0,0, mainStage);
        background.loadTexture( "oceanside.png" );
        background.setSize(800,600);
        background.setOpacity(0);

        BaseActor.setWorldBounds(background);
        BaseActor turtle = new BaseActor(0,0, mainStage);
        turtle.loadTexture( "turtle-big.png" );
        turtle.setPosition( -turtle.getWidth(), 0 );

        DialogBox dialogBox = new DialogBox(0,0, uiStage);
        dialogBox.setDialogSize(600, 200);
        dialogBox.setBackgroundColor( new Color(0.6f, 0.6f, 0.8f, 1) );
        dialogBox.setFontScale(0.75f);
        dialogBox.setVisible(false);
        uiTable.add(dialogBox).expandX().expandY().bottom();

        continueKey = new BaseActor(0,0,uiStage);
        continueKey.loadTexture("key-C.png");
        continueKey.setSize(32,32);
        continueKey.setVisible(false);
        dialogBox.addActor(continueKey);
        continueKey.setPosition( dialogBox.getWidth() - continueKey.getWidth(), 0 );

        // Add scene segments to the list = create the movie...

        scene = new Scene();
        mainStage.addActor(scene);
        scene.addSegment( new SceneSegment( background, Actions.fadeIn(1) ));
        scene.addSegment( new SceneSegment( turtle, SceneActions.moveToScreenCenter(2) ));
        scene.addSegment( new SceneSegment( dialogBox, Actions.show() ));
        scene.addSegment( new SceneSegment( dialogBox,
                SceneActions.setText("I want to be the very best . . . Starfish Collector!" ) ));
        scene.addSegment( new SceneSegment( continueKey, Actions.show() ));
        scene.addSegment( new SceneSegment( background, SceneActions.pause() ));
        scene.addSegment( new SceneSegment( continueKey, Actions.hide() ));
        scene.addSegment( new SceneSegment( dialogBox,
                SceneActions.setText("I've got to collect them all!" ) ));
        scene.addSegment( new SceneSegment( continueKey, Actions.show() ));
        scene.addSegment( new SceneSegment( background, SceneActions.pause() ));
        scene.addSegment( new SceneSegment( continueKey, Actions.hide() ));
        scene.addSegment( new SceneSegment( dialogBox, Actions.hide() ) );
        scene.addSegment( new SceneSegment( turtle, SceneActions.moveToOutsideRight(2) ));
        scene.addSegment( new SceneSegment( background, Actions.fadeOut(2) ));
        scene.start();
    }

    @Override
    public void update(float dt)
    {
        if ( scene.isSceneFinished() )  BaseGame.setActiveScreen( new LevelScreen() );
    }

    @Override
    public boolean keyDown(int keyCode)         // "C" -> next segment
    {
        if ( keyCode == Keys.C && continueKey.isVisible() )   scene.loadNextSegment();
        return false;
    }
}