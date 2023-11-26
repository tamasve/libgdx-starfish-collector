package com.starfishcoll;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;    // class to handle discrete user input: a central dispatcher
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/*  For Freetype - plus dependencies should be written into build.gradle:
    Core Dependency:   compile "com.badlogicgames.gdx:gdx-freetype:$gdxVersion"
    Desktop Dependency:  compile "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop"
    Then refresh under Gradle...
 */

/**
 * Part of Game FW   implemented during creating Starfish Collector game
 * The BaseGame class is mainly responsible for storing a static reference to the Game object initialized by the
 * Launcher class so that the Screen-derived classes can easily access and switch the currently active screen.
 * Another task is handling discrete user input -
 * for this set the InputMultiplexer as main InputProcessor, to which BaseScreen will add the stages.
 * 07/01/2022 - last: 15/01/2022
 */

public abstract class BaseGame extends Game
{
    private static BaseGame game;       // a reference to itself

    public static LabelStyle labelStyle;    // a central definition of bitmap font style

    public static TextButtonStyle textButtonStyle;      // a central style definition for text buttons


    public BaseGame()
    {
        game = this;
    }

    public void create()
    {
        // prepare for multiple classes/stages to receive discrete input
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor( im );


        // initialize label style
        labelStyle = new LabelStyle();
        labelStyle.font = new BitmapFont();
        // png + fnt version
        labelStyle.font = new BitmapFont( Gdx.files.internal("cooper.fnt") );

        /*// freetype version
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans.ttf"));
        FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = 48;
        fontParameters.color = Color.WHITE;
        fontParameters.borderWidth = 2;
        fontParameters.borderColor = Color.BLACK;
        fontParameters.borderStraight = true;
        fontParameters.minFilter = TextureFilter.Linear;
        fontParameters.magFilter = TextureFilter.Linear;
        labelStyle.font = fontGenerator.generateFont(fontParameters);*/

        // initialize text button style
        textButtonStyle = new TextButtonStyle();
        Texture buttonTex = new Texture( Gdx.files.internal("button.png") );
        NinePatch buttonPatch = new NinePatch(buttonTex, 24,24,24,24);  // image with borders for fine alingment
        textButtonStyle.up = new NinePatchDrawable( buttonPatch );      // a NinePatch that implements Drawable
        textButtonStyle.font = labelStyle.font;
        textButtonStyle.fontColor = Color.GRAY;
    }

    public static void setActiveScreen(BaseScreen s)
    {
        game.setScreen(s);      // create a reference to the currently active screen
    }
}
