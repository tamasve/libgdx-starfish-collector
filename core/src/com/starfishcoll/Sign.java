package com.starfishcoll;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Game signs: they will store text to be displayed by a DialogBox object
 */

public class Sign extends BaseActor
{
    // the text to be displayed
    private String text;
    // used to determine if sign text is currently being displayed
    private boolean viewing;

    public Sign(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("sign.png");

        text = " ";
        viewing = false;

        setBoundaryPolygon(8);
    }

    public void setText(String t)
    { text = t; }

    public String getText()
    { return text; }

    public void setViewing(boolean v)
    { viewing = v; }

    public boolean isViewing()
    { return viewing; }
}