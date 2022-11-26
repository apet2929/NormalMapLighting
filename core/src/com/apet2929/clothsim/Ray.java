package com.apet2929.clothsim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Ray {
    public Vector2 pos;
    public Vector2 dir;
    public Vector2 intersect;
    public float radius;

    public Ray(Vector2 pos, double angleDeg, float radius){
        this.pos = pos;
        double angle = angleDeg * Math.PI/180;
        this.dir = new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
        this.radius = radius;
    }

    public void cast(ArrayList<LightBlocker> walls){
        Vector2 closestIntersect = new Vector2(this.pos).add(this.dir.x * radius, this.dir.y * radius);
        float closestDist = radius * radius;

        for (LightBlocker wall : walls) {
            Vector2 intersectionPoint = getIntersect(wall);
            float dist = this.pos.dst2(intersectionPoint);
            if(dist < closestDist){
                closestIntersect = intersectionPoint;
                closestDist = dist;

            }
        }

        this.intersect = closestIntersect;
    }

    public Vector2 getIntersect(LightBlocker wall){
        float x1 = wall.getStartPoint().x;
        float y1 = wall.getStartPoint().y;
        float x2 = wall.getEndPoint().x;
        float y2 = wall.getEndPoint().y;

        float x3 = this.pos.x;
        float y3 = this.pos.y;
        float x4 = this.pos.x + this.dir.x;
        float y4 = this.pos.y + this.dir.y;

        float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 -x4);
        if(den == 0) return new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);

        float t = ((x1-x3) * (y3-y4) - (y1-y3) * (x3-x4)) / den;
        float u = -((x1-x2) * (y1-y3) - (y1-y2)*(x1-x3)) / den;

        if (t > 0 && t < 1 && u > 0){
            return new Vector2(x1 + t *(x2-x1), (y1 + t * (y2 - y1)));
        }
        return new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public void render(ShapeRenderer sr){
        sr.line(pos, intersect);
    }
}
