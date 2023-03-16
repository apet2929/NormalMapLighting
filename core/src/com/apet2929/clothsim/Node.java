package com.apet2929.clothsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234L;

    public final float weight;
    public Vector2 position;
    public Vector2 velocity;
    private Vector2 force;
    public final float k; // spring constant
    private boolean fixed;

    public Node(float x, float y, float weight, float springConstant){
        this.position = new Vector2(x,y);
        this.weight = weight;
        this.k = springConstant;
        this.velocity = new Vector2(0,0);
        this.force = new Vector2(0,0);
        this.fixed = false;
    }

    public Node(float x, float y){
        this(x, y, 10f, 0.05f);
    }

    public Node(float x, float y, boolean fixed){
        this(x, y);
        this.fixed = fixed;
    }

    public void applyForce(Vector2 force){
        this.force.add(force);
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    public void toggleFixed() {
        this.fixed = !this.fixed;
    }

    public void update(float delta){
        if(fixed) {
            return;
        }

        position.add(velocity.x * delta, velocity.y * delta);
        velocity.add(force.x * delta / weight, force.y * delta / weight);
        velocity.scl(0.995f);

        force = new Vector2(0,0);
    }

    public void render(ShapeRenderer sr){
        sr.setColor(Color.BLUE);
        if(fixed) sr.setColor(Color.RED);
        sr.circle(this.position.x, this.position.y, 15);
    }


}
