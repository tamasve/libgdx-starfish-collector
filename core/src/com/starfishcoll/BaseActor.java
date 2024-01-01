package com.starfishcoll;

import java.util.ArrayList;

import com.badlogic.gdx.math.*;                             // Vector2, Polygon, Rectangle, MathUtils...
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * Part of Game FW   implemented during creating Starfish Collector game
 *   Extended functionality of the LibGDX Actor class
 *
 *        Unique sprite classes inherit from this BaseActor class extending Actor of libGDX, with full facility package.
 *          Class features:
 *          - set size and position of actor and add it to main stage
 *          - create and control image based animations for actor
 *          - handle actors on a stage
 *          - handle movement and collision detection
 *          - align camera to actor movement
 *          - handle more actor together as a group
 *
 *          03/01/2022 - last: 20/01/2022
 */

public class BaseActor extends Group            // Group extends Actor...
{
    private static String packageName = "com.starfishcoll.";
    private static Rectangle worldBounds;       // the boundaries of the game world - static

    private Animation<TextureRegion> animation;     // animation with images in an array
    protected float elapsedTime;        // to control animation time
    protected float fadingTime;        // to synchronize animations
    private boolean animationPaused;

    private Vector2 velocityVec;        // movement vectors
    private Vector2 accelerationVec;
    private float maxSpeed;             // movable objects will set the following 3 values in their constructors
    private float acceleration;
    private float deceleration;

    private Polygon boundaryPolygon;       // boundary polygon for collision detection


    // CONSTRUCTOR
    // @param:  Position, Stage
    public BaseActor(float x, float y, Stage stage)
    {
        // call constructor of Actor/Group superclass
        super();
        // perform additional initialization tasks
        setPosition(x,y);       // Actor method
        stage.addActor(this);       // add itself to Stage

        animation = null;
        elapsedTime = 0;
        fadingTime = 4;
        animationPaused = false;

        velocityVec = new Vector2(0,0);
        accelerationVec = new Vector2(0,0);
        acceleration = 0;                           // movable objects will set these values in their constructors
        deceleration = 0;
        maxSpeed = 1000;
    }


    // HANDLING ACTORS AS A LIST
    // @param:  Stage, Classname
    // This method gives the list of actors of a given type on a given stage
    // Typical Actor method, but can not be linked to any Actor object -> it should be static
    public static ArrayList<BaseActor> getList(Stage stage, String className)
    {
        ArrayList<BaseActor> list = new ArrayList<BaseActor>();
        Class theClass = null;

        try
        { theClass = Class.forName(packageName + className); }    // create an object of the class of the given name (full name = with package name)
        catch (Exception error)
        { error.printStackTrace(); }

        for (Actor a : stage.getActors())
        {
            if ( theClass.isInstance( a ) )     // "a" is instance of theClass?
                list.add( (BaseActor) a );
        }
        return list;
    }

    // Count the number of instances of a given class on a given stage
    public static int count(Stage stage, String className)
    {
        return getList(stage, className).size();
    }



    // SETTING WORLD BOUNDS: either directly from numerical values or based on an actor - static

    public static void setWorldBounds(float width, float height)
    {
        worldBounds = new Rectangle( 0,0, width, height );
    }

    public static void setWorldBounds(BaseActor ba)
    {
        setWorldBounds( ba.getWidth(), ba.getHeight() );
    }

    // SceneSegment FW needs to get world bounds
    public static Rectangle getWorldBounds()
    {
        return worldBounds;
    }


    // KEEP ACTOR WITHIN WORLD BOUNDS - 2 versions

    // 'A' version: Check all 4 edges and adjust if necessary, uses worldBounds static Rectangle
    public void boundToWorld()
    {
        // check left edge
        if (getX() < 0)
            setX(0);
        // check right edge
        if (getX() + getWidth() > worldBounds.width)
            setX(worldBounds.width - getWidth());
        // check bottom edge
        if (getY() < 0)
            setY(0);
        // check top edge
        if (getY() + getHeight() > worldBounds.height)
            setY(worldBounds.height - getHeight());
    }

    // 'B' version: object goes out at one side -> comes in at opposite side...  (new function at SpaceRock game)
    public void wrapAroundWorld()
    {
        if (getX() + getWidth() < 0)
            setX( worldBounds.width );
        if (getX() > worldBounds.width)
            setX( -getWidth() );
        if (getY() + getHeight() < 0)
            setY( worldBounds.height );
        if (getY() > worldBounds.height)
            setY( -getHeight() );
    }


    // CAMERA alignment so that viewing area will be completely contained within the game world

    public void alignCamera()
    {
        Camera cam = this.getStage().getCamera();       // get camera of stage the actor is in
        Viewport v = this.getStage().getViewport();

        // center camera on actor: shift view with BaseActor
        cam.position.set( this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0 );

        // bound camera to layout: keep camera view within game world
        cam.position.x = MathUtils.clamp(cam.position.x,
                cam.viewportWidth/2, worldBounds.width - cam.viewportWidth/2);
        cam.position.y = MathUtils.clamp(cam.position.y,
                cam.viewportHeight/2, worldBounds.height - cam.viewportHeight/2);

        cam.update();
    }



    // ANIMATION HANDLING
    // The methods that load image data and use it to create Animation objects
    // No image handling separately, only animation (for image only: method version 3)
    // The common part for all 3 methods: setAnimation(...)

    // Version 1: load from separate files
    public Animation<TextureRegion> loadAnimationFromFiles( String[] fileNames, float frameDuration, boolean loop )
    {
        int fileCount = fileNames.length;
        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        for (int n = 0; n < fileCount; n++)     // load each image, set its filter, add to Array as a TextureRegion class type
        {
            String fileName = fileNames[n];
            Texture texture = new Texture( Gdx.files.internal( fileName ) );
            texture.setFilter( TextureFilter.Linear, TextureFilter.Linear );
            textureArray.add( new TextureRegion( texture ) );
        }

        // create animation with frame duration and image array params, then set play mode to loop or normal acc. to param
        Animation<TextureRegion> anim = new Animation<TextureRegion> (frameDuration, textureArray);

        if (loop)  anim.setPlayMode(Animation.PlayMode.LOOP);
        else       anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)  setAnimation(anim);

        return anim;        // return animation object for ?
    }

    // Version 2: load images from a spritesheet - TextureRegion.split() method will cut it to images
    // additional params: number of rows and cols of images on sheet
    public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration, boolean loop)
    {
        Texture texture = new Texture(Gdx.files.internal(fileName), true);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        int frameWidth = texture.getWidth() / cols;         // set width and height of 1 image
        int frameHeight = texture.getHeight() / rows;

        // split() method cuts sheet into separate images, puts into a 2dim array
        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<TextureRegion>();     // put images from 2dim array into Array
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                textureArray.add( temp[r][c] );

        // create animation with frame duration and image array params, then set play mode to loop or normal acc. to param
        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        if (loop)  anim.setPlayMode(Animation.PlayMode.LOOP);
        else       anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)  setAnimation(anim);

        return anim;
    }

    // Version 3 for convenience: a special method for game objects that actually do not require an animation, because they have only 1 image
    public Animation<TextureRegion> loadTexture(String fileName)
    {
        String[] fileNames = new String[1];         // array with 1 image and then call anim method version 1
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    // to check if the animation is finished: it calls Animation class' method with the same name
    public boolean isAnimationFinished()
    {
        return animation.isAnimationFinished(elapsedTime);
    }

    // set animation (private field) + actor size and center acc. to 1st image
    public void setAnimation( Animation<TextureRegion> anim )
    {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);   // width and height of the actor will be set to the width and height of the first image of the animation
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize(w,h);
        setOrigin(w/2,h/2);     // set center acc. to size
    }

    public void setAnimationPaused(boolean pause)
    {
        animationPaused = pause;
    }

    // in order to set transparency
    public void setOpacity(float opacity)
    {
        this.getColor().a = opacity;
    }



    // MOVEMENT MANAGEMENT
    // hit moving key -> accelerate,  release it -> decelerate
    // velocity is in pixels/sec

    public void setSpeed(float speed)
    {
        // if length is zero set motion angle acc. to acceleration angle
        if (velocityVec.len() == 0)
        {
            velocityVec.set(speed, 0);
            setMotionAngle( accelerationVec.angleDeg() );
        }
        else
            velocityVec.setLength(speed);
    }

    public float getSpeed()
    {
        return velocityVec.len();
    }

    public void setMotionAngle(float angle)
    {
        velocityVec.setAngleDeg(angle);
    }

    public float getMotionAngle()
    {
        return velocityVec.angleDeg();
    }

    public boolean isMoving()
    {
        return (getSpeed() > 0);
    }

    public void setAcceleration(float acc)
    {
        acceleration = acc;
    }

    public void accelerateAtAngle(float angle)      // set angle of acc. in degrees (called by act(dt) upon key press)
    {
        accelerationVec.add( new Vector2(acceleration, 0).setAngleDeg(angle) );     // add - this makes it possible to give 2 directions parallel
    }

    public void accelerateForward()    // accelerates an object in the direction it is currently facing
    {
        accelerateAtAngle( getRotation() );
    }

    public void setMaxSpeed(float ms)
    {
        maxSpeed = ms;
    }

    public void setDeceleration(float dec)
    {
        deceleration = dec;
    }


    // THE MAIN MOVEMENT HANDLE METHOD, called by Actor.act()
    // acceleration -> velocity -> movement  - dt = time elapsed since last rendering
    public void applyPhysics(float dt)
    {
        // apply acceleration to velocity
        velocityVec.add( accelerationVec.x * dt, accelerationVec.y * dt );
        float speed = getSpeed();

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0)  speed -= deceleration * dt;

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, maxSpeed);
        setSpeed(speed);    // update velocity

        // apply velocity -> move this
        moveBy( velocityVec.x * dt, velocityVec.y * dt );       // Actor method

        // reset acceleration (accelerate only while key is pressed)
        accelerationVec.set(0,0);
    }


    // COLLISION DETECTION

    // Create a polygon with vertices being points of a boundary ellipsis
    public void setBoundaryPolygon(int numSides)
    {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = new float[2*numSides];
        for (int i = 0; i < numSides; i++)
        {
            float angle = i * 6.28f / numSides;
            // x-coordinate
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2;
            // y-coordinate
            vertices[2*i+1] = h/2 * MathUtils.sin(angle) + h/2;
        }
        boundaryPolygon = new Polygon(vertices);        // create Polygon from array
    }

    // give boundary polygon but after adjusting it according to the Actor object’s current parameters
    public Polygon getBoundaryPolygon()
    {
        boundaryPolygon.setPosition( getX(), getY() );
        boundaryPolygon.setOrigin( getOriginX(), getOriginY() );
        boundaryPolygon.setRotation ( getRotation() );
        boundaryPolygon.setScale( getScaleX(), getScaleY() );
        return boundaryPolygon;
    }

    // check overlapping - first only with rectangle to spare time
    public boolean overlaps(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();
        // initial test to improve performance
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return false;
        return Intersector.overlapConvexPolygons( poly1, poly2 );   // static method for checking overlap between polygons
    }

    // Special method in order to avoid overlap with solid obstacles:
    // it calculates the minimal distance the character needs to be moved so that there will be no overlap
    // then moves character with this vector
    // return value: null if no overlap, otherwise the normal vector of direction
    public Vector2 preventOverlap(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return null;

        MinimumTranslationVector mtv = new MinimumTranslationVector();
        // mtv = data output: the vector of minimal movement - .normal is a normal vector, .depth is the distance
        boolean polygonOverlap = Intersector.overlapConvexPolygons( poly1, poly2, mtv );

        if ( !polygonOverlap )  return null;

        // The main point: if there is overlap it moves character by the min. vector
        this.moveBy( mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth );

        return mtv.normal;                  // returns the direction (normal vector)
    }

    // To test if other actor is within a given distance: by scaling the boundaryPolygon
    public boolean isWithinDistance(float distance, BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        float scaleX = (this.getWidth() + 2 * distance) / this.getWidth();
        float scaleY = (this.getHeight() + 2 * distance) / this.getHeight();
        poly1.setScale(scaleX, scaleY);
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return false;
        return Intersector.overlapConvexPolygons( poly1, poly2 );
    }

    // 2 methods for centering BaseActor - 1st - with concrete coords, 2nd - acc. to other BaseActor
    public void centerAtPosition(float x, float y)
    {
        setPosition( x - getWidth()/2 , y - getHeight()/2 );
    }

    public void centerAtActor(BaseActor other)
    {
        centerAtPosition( other.getX() + other.getWidth()/2 , other.getY() + other.getHeight()/2 );
    }


    // OVERRIDDEN ACTOR METHODS: act, draw

    @Override
    public void act(float dt)
    {
        super.act(dt);      // this is important! - Actor.act() manages the value based actions

        if (!animationPaused) elapsedTime += dt;    // if there is an animation increment elapsed time
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        // apply color tint effect
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);
        if ( animation != null && isVisible() )
            batch.draw( animation.getKeyFrame(elapsedTime), getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation() );

        // so that the actors attached to the group render after (and therefore, appear on top of) the image
        // corresponding to the group object itself - this command is now the last to do:
        super.draw( batch, parentAlpha );
    }

}