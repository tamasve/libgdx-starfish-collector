/**
	Base project for a starfish collector turtle game from
	Java Game Development with LibGDX From Beginner to Professional  by Lee Stemkoski

	Version 3:
    starting creating a framework for games
    - instead of images every graphic object is now an Actor object
    - unique sprite classes inheriting from BaseActor extending Actor of libGDX, with full facility package
    - Stage objects to handle all Actor objects together
    - unique game class extending Screen: BaseScreen - stages, render -> abstract class
    After all these in this main class nothing else remains just the initialization of Actor objects, adding them to
    Stage objects, and the collision detection call...

 	Version 4:
 	- plus user interface with information texts
 	- buttons
 	- using Table for effective layout management
	28/12/2021 - last: 18/01/2022
 */

package com.starfishcoll;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;


public class LevelScreen extends BaseScreen
{
	private Turtle turtle;		// extended BaseActor
	private boolean win;
	private Label starfishLabel;
	private DialogBox dialogBox;


	@Override
	public void initialize ()		// called from create() in GameBeta
	{
		BaseActor ocean = new BaseActor(0,0, mainStage);	    // background lies here
		ocean.loadTexture ("water-border.jpg");		// BaseActor method
		ocean.setSize(1200,900);				// Actor method

		BaseActor.setWorldBounds(ocean);			// set the size of the game world (for the actors)

		turtle = new Turtle(20,20, mainStage);	// for starfish and turtle: everything is included in the constructor!

		new Starfish(400,400, mainStage);		// not saved in variables, mainStage stores them
		new Starfish(500,100, mainStage);
		new Starfish(100,450, mainStage);
		new Starfish(200,250, mainStage);

		new Rock(200,150, mainStage);
		new Rock(100,300, mainStage);
		new Rock(300,350, mainStage);
		new Rock(450,200, mainStage);

		win = false;

		// initialize label for ui text information
		starfishLabel = new Label("Starfish Left:", BaseGame.labelStyle);
		starfishLabel.setColor( Color.CYAN );
		// starfishLabel.setPosition( 20, 520 );
		// uiStage.addActor(starfishLabel);


		// initialize restart BUTTON

		// image -> style
		ButtonStyle buttonStyle = new ButtonStyle();		// style
		Texture buttonTex = new Texture( Gdx.files.internal("undo.png") );		// Texture type
		TextureRegion buttonRegion = new TextureRegion( buttonTex );		// TextureRegion type
		buttonStyle.up = new TextureRegionDrawable( buttonRegion );		// a TextureRegion that implements Drawable interface

		// button with style
		Button restartButton = new Button( buttonStyle );
		restartButton.setColor( Color.CYAN );
		// restartButton.setPosition(720,520);
		// uiStage.addActor(restartButton);			// handle as an Actor

		// set code for button with anonymous class of event listener
		restartButton.addListener(
				new EventListener() {
					@Override
					public boolean handle(Event e) {
						if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
							return false;
						StarfishGame.setActiveScreen(new LevelScreen());	// InputEvent + touchDown (= mouseclick) -> set LevelScreen as active
						return false;
					}
				}
		);

		// more effective layout management
		uiTable.pad(10);		// 10 pix free space on every side
		uiTable.add(starfishLabel).top();
		uiTable.add().expandX().expandY();		// an empty cell in the middle which pushes the widgets to the edges by using expands
		uiTable.add(restartButton).top();

		// Initialize game signs
		Sign sign1 = new Sign(20,400, mainStage);
		sign1.setText("West Starfish Bay");
		Sign sign2 = new Sign(600,300, mainStage);
		sign2.setText("East Starfish Bay");
		dialogBox = new DialogBox(0,0, uiStage);
		dialogBox.setBackgroundColor( Color.TAN );
		dialogBox.setFontColor( Color.BROWN );
		dialogBox.setDialogSize(600, 100);
		dialogBox.setFontScale(0.80f);
		dialogBox.alignCenter();
		dialogBox.setVisible(false);
		uiTable.row();
		uiTable.add(dialogBox).colspan(3);
	}

	@Override
	public void update (float dt)		// called from render() in BaseScreen
	{
		// show the actual number of starfishes left - .setScale can be used to change size here
		starfishLabel.setText("Starfish Left: " + BaseActor.count(mainStage, "Starfish"));

		// check that turtle does not overlap with any rock
		for ( BaseActor rockActor : BaseActor.getList(mainStage, "Rock") )
			turtle.preventOverlap(rockActor);

		// if Turtle gets a Starfish, start a special animation: it fades out in a whirl
		for (BaseActor starfishActor : BaseActor.getList(mainStage, "Starfish"))
		{
			Starfish starfish = (Starfish) starfishActor;        // cast in order to reach Starfish methods
			if (turtle.overlaps(starfish) && !starfish.isCollected())
			{
				starfish.collect();

				Whirlpool whirl = new Whirlpool(0, 0, mainStage);        // whirl effect upon Starfish
				whirl.centerAtActor(starfish);
				whirl.setOpacity(0.5f);
			}
		}

		// Check: turtle is near a sign?
		for ( BaseActor signActor : BaseActor.getList(mainStage, "Sign") )
		{
			Sign sign = (Sign) signActor;
			turtle.preventOverlap(sign);
			boolean nearby = turtle.isWithinDistance(4, sign);
			if ( nearby && !sign.isViewing() )
			{
				dialogBox.setText( sign.getText() );  // Sign-text -> Dialogbox-text, is visible, is viewing
				dialogBox.setVisible( true );
				sign.setViewing( true );
			}
			if (sign.isViewing() && !nearby)
			{
				dialogBox.setText( " " );		// already not nearby: opposite to the upper
				dialogBox.setVisible( false );
				sign.setViewing( false );
			}
		}


		// If every Starfish was collected then the "you win" subscript appears then fades in
		if ( BaseActor.count(mainStage, "Starfish") == 0 && !win )
		{
			win = true;
			BaseActor youWinMessage = new BaseActor(0, 0, uiStage);	// draw it on the stage with fix camera
			youWinMessage.loadTexture("you-win.png");
			youWinMessage.centerAtPosition(400, 300);
			youWinMessage.setOpacity(0);
			youWinMessage.addAction(Actions.delay(1));
			youWinMessage.addAction(Actions.after(Actions.fadeIn(youWinMessage.fadingTime)));
		}

	}
}