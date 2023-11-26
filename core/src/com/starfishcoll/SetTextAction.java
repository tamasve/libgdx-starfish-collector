package com.starfishcoll;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Extension of Action in order to be able to set text to a DialogBox as an Action (which is part of an animation)
 * 20/01/2022
 */

public class SetTextAction extends Action
{
    protected String textToDisplay;

    public SetTextAction(String t)
    {
        textToDisplay = t;
    }

    @Override
    public boolean act(float dt)
    {
        DialogBox db = (DialogBox) target;      // target is the ref. to Actor in Action
        db.setText( textToDisplay );
        return true;                    // flag to indicate if the Action is completed
    }
}