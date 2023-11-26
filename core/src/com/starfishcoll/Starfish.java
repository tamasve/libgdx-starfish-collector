package com.starfishcoll;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Starfish extends BaseActor
{

    private boolean collected;      // to collect only once

    public Starfish(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("starfish.png");    // no image based animation

        // add a value-based animation: a slow rotation of 30 degrees every ONE second to draw the player's attention
        Action spin = Actions.rotateBy(30, 1);
        this.addAction( Actions.forever(spin) );

        // for collision detection
        setBoundaryPolygon(8);

        collected = false;
    }

    // methods for checking if Starfish is collected and to collect it with add. animation
    public boolean isCollected()
    {
        return collected;
    }

    public void collect()
    {
        collected = true;
        clearActions();                                    // clear all animations
        addAction( Actions.fadeOut(fadingTime) );              // fade it out during a central set time (to reach synchronization)
        addAction( Actions.after( Actions.removeActor() ) );    // then remove it from Stage
    }
}