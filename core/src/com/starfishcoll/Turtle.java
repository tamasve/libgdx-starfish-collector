package com.starfishcoll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Turtle extends BaseActor
{
    public Turtle(float x, float y, Stage s)
    {
        super(x,y,s);

        // animation: load images, set animation
        String[] filenames =
                {"turtle-1.png", "turtle-2.png", "turtle-3.png", "turtle-4.png", "turtle-5.png", "turtle-6.png"};
        loadAnimationFromFiles(filenames, 0.1f, true);

        // movement base values
        setAcceleration(400);
        setDeceleration(400);
        setMaxSpeed(100);

        // for collision detection
        setBoundaryPolygon(8);
    }

    @Override
    public void act(float dt)       // override ActorBeta's action method but of course run the original at first...
    {
        super.act(dt);

        if (Gdx.input.isKeyPressed(Keys.LEFT))  accelerateAtAngle(180);     // key press -> set acc. angle
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) accelerateAtAngle(0);
        if (Gdx.input.isKeyPressed(Keys.UP))    accelerateAtAngle(90);
        if (Gdx.input.isKeyPressed(Keys.DOWN))  accelerateAtAngle(270);

        applyPhysics(dt);       // acc. -> velocity -> movement

        setAnimationPaused( !isMoving() );      // animation only when moving

        if ( getSpeed() > 0 )  setRotation( getMotionAngle() );     // if it moves set image angle acc. to movement's angle

        boundToWorld();         // keep Turtle within the edges of game world

        alignCamera();          // shift view as the Turtle moves but within game world bounds
    }
}