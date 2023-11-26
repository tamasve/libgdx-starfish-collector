package com.starfishcoll;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Align;

/**
 * Extension of Actions class, for new action types as static methods:
 * - text to DialogBox
 * - pause
 * - screen alignment move actions  (using Actions' static methods)
 */

public class SceneActions extends Actions
{
    public static Action setText(String s)
    {
        return new SetTextAction(s);
    }

    public static Action pause()
    {
        return Actions.forever( Actions.delay(1) );
    }

    public static Action moveToScreenLeft(float duration)
    {
        return Actions.moveToAligned( 0, 0, Align.bottomLeft, duration );
    }

    public static Action moveToScreenRight(float duration)
    {
        return Actions.moveToAligned( BaseActor.getWorldBounds().width, 0,
                Align.bottomRight, duration );
    }

    public static Action moveToScreenCenter(float duration)
    {
        return Actions.moveToAligned( BaseActor.getWorldBounds().width / 2, 0,
                Align.bottom, duration);
    }

    public static Action moveToOutsideLeft(float duration)
    {
        return Actions.moveToAligned( 0, 0, Align.bottomRight, duration );
    }

    public static Action moveToOutsideRight(float duration)
    {
        return Actions.moveToAligned( BaseActor.getWorldBounds().width, 0,
                Align.bottomLeft, duration );
    }
}
