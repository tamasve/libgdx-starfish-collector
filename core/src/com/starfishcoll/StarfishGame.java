package com.starfishcoll;

public class StarfishGame extends BaseGame
{
    public void create()
    {
        super.create();
        setActiveScreen( new MenuScreen() );    // create starting screen and set it as active
    }
}
