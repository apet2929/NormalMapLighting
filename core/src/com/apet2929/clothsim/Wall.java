package com.apet2929.clothsim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Wall {
    public final Vector2 start;
    public final Vector2 end;

    public Wall(Vector2 start, Vector2 end){
        this.start = start;
        this.end = end;
    }

    public void render(ShapeRenderer sr){
        sr.line(start, end);
    }
}
