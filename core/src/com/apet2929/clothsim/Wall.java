package com.apet2929.clothsim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Wall implements LightBlocker{
    public final Vector2 start;
    public final Vector2 end;

    public Wall(Vector2 start, Vector2 end){
        this.start = start;
        this.end = end;
    }

    public void render(ShapeRenderer sr){
        sr.line(start, end);
    }

    @Override
    public Vector2 getStartPoint() {
        return start;
    }

    @Override
    public Vector2 getEndPoint() {
        return end;
    }
}
