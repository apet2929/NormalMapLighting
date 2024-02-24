package com.apet2929.clothsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ShortArray;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

public class LightMask {

    EarClippingTriangulator triangulator;
    private Rectangle lightingMaskRect;
    private PolygonSprite polygonSprite;
    private float radius;
    ArrayList<Ray> rays;
    private Vector2 pos;

    public LightMask(int numRays, float x, float y){
        rays = new ArrayList<>();
        pos = new Vector2(x,y);
        radius = 400;
        float incRot = 360.0f / numRays;
        for (int i = 0; i < numRays; i++) {
            rays.add(new Ray(new Vector2(pos.x, pos.y), incRot*i, radius));
        }

        triangulator = new EarClippingTriangulator();
    }

    public void drawMask(PolygonSpriteBatch sb){
        PolygonRegion pr = getLightingMask().getRegion();
        sb.begin();
        Gdx.gl.glDepthMask(true);
        sb.setColor(1,1,1,1);
        sb.draw(pr, getTopLeft().x, getTopLeft().y);
        sb.end();
    }

    public void update(ArrayList<LightBlocker> walls){
        for (Ray ray : rays) {
            ray.cast(walls);
        }
        this.polygonSprite = getLightingMask();
    }

    public void setRadius(float radius){
        this.radius = radius;
        for (Ray ray : rays) {
            ray.radius = radius;
        }
    }

    public void setPos(float x, float y){
        pos.set(x,y);
        for (Ray ray : rays) {
            ray.pos = new Vector2(pos.x, pos.y);
        }
    }

    public PolygonSprite getLightingMask(){
        float[] vertices = getLightingPolygon();

        ShortArray triangles = triangulator.computeTriangles(vertices);
        PolygonRegion reg = new PolygonRegion(new TextureRegion(getWhiteTexture()), vertices, triangles.toArray());
        PolygonSprite sprite = new PolygonSprite(reg);
        Vector2 c = this.getCenter();
        sprite.setOrigin(c.x, c.y);

        return sprite;
    }

    public Texture getLightingMask2(){
        float[] vertices = getLightingPolygon();
        Pixmap pixmap = new Pixmap((int) lightingMaskRect.width, (int) lightingMaskRect.height, RGBA8888);
        pixmap.setColor(1,1,1,1);
        short[] triangles = triangulator.computeTriangles(vertices).toArray();
        for (int i = 0; i < triangles.length-2; i+=3) {
            // vertex indexes
            int i1 = triangles[i];
            int i2 = triangles[i+1];
            int i3 = triangles[i+2];

            pixmap.fillTriangle((int) vertices[i1], (int) vertices[i1+1], (int) vertices[i2], (int) vertices[i2+1], (int) vertices[i3], (int) vertices[i3+1]);
        }

        return new Texture(pixmap);
    }

    public Vector2 getCenter(){
        return new Vector2(this.pos.x - this.lightingMaskRect.x, this.pos.y - this.lightingMaskRect.y);
    }

    public Vector2 getTopLeft(){
        return new Vector2(this.polygonSprite.getX(), this.polygonSprite.getY());
    }


    /**
     * @return a list of vertices of the lighting polygon in pixels
     */
    public float[] getLightingPolygon(){
        float[] vertices = new float[rays.size()*2];

        float min_x = Float.MAX_VALUE;
        float max_x = -Float.MAX_VALUE;
        float min_y = Float.MAX_VALUE;
        float max_y = -Float.MAX_VALUE;

        int rayIndex = 0;
        for (int i = 0; i < rays.size()*2; i+=2) {
            float x = rays.get(rayIndex).intersect.x;
            float y = rays.get(rayIndex).intersect.y;
            if(x < min_x) min_x = x;
            if(x > max_x) max_x = x;
            if(y < min_y) min_y = y;
            if(y > max_y) max_y = y;

            vertices[i] = x;
            vertices[i + 1] = y;
            rayIndex++;

        }
        lightingMaskRect = new Rectangle(min_x, min_y, max_x - min_x, max_y - min_y);

        return vertices;
    }

    public Texture getWhiteTexture(){
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1,1,1,1);
        pixmap.fill();
        return new Texture(pixmap);
    }
}
