package com.starfishcoll;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Whirlpool extends BaseActor
{
    public Whirlpool(float x, float y, Stage s)
    {
        super(x,y,s);
        loadAnimationFromSheet("whirlpool.png", 2, 5, 0.1f, true);
    }

    // checks if the animation is finished playing and, if so, calls the remove method to remove it from its stage
    @Override
    public void act(float dt)
    {
        super.act(dt);
        if ( elapsedTime > fadingTime)    // remove it after a central set time (to reach synchronization)
            remove();
    }
}