package com.starfishcoll;

import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.ArrayList;

/**
 * The manager of scene segments: a list of actions of actors ~ a movie
 * The engine is the act() method...
 * 20/01/2022
 */

public class Scene extends Actor
{
    private ArrayList<SceneSegment> segmentList;
    private int index;

    public Scene()
    {
        super();
        segmentList = new ArrayList<SceneSegment>();
        index = -1;
    }

    public void addSegment(SceneSegment segment)
    {
        segmentList.add(segment);
    }

    public void clearSegments()
    {
        segmentList.clear();
    }

    public void start()
    {
        index = 0;
        segmentList.get(index).start();
    }

    public boolean isSegmentFinished()
    {
        return segmentList.get(index).isFinished();
    }

    public boolean isLastSegment()
    {
        return (index >= segmentList.size() - 1);
    }

    public void loadNextSegment()
    {
        if ( isLastSegment() )
            return;
        segmentList.get(index).finish();
        index++;
        segmentList.get(index).start();
    }

    public boolean isSceneFinished()
    {
        return ( isLastSegment() && isSegmentFinished() );
    }

    @Override
    public void act(float dt)           // switch to the next after current is finished
    {
        if ( isSegmentFinished() && !isLastSegment() )    loadNextSegment();
    }
}