package com.apet2929.clothsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.Pixmap.Format.Alpha;
import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

public class LightSource {
    private ArrayList<Ray> rays;
    private Vector2 pos;
    private Rectangle lightingMaskRect;
    private PolygonSprite polygonSprite;
    private PolygonSpriteBatch psb;
    EarClippingTriangulator triangulator;

    public LightSource(int numRays, float x, float y){
        rays = new ArrayList<>();
        pos = new Vector2(x,y);
        float incRot = 360.0f / numRays;
        for (int i = 0; i < numRays; i++) {
            rays.add(new Ray(pos, incRot*i));
        }
        psb = new PolygonSpriteBatch();
        triangulator = new EarClippingTriangulator();
    }

    public void setPos(float x, float y){
        pos.set(x,y);
        for (Ray ray : rays) {
            ray.pos = pos;
        }
    }

    public Vector2 getTopLeft(){
        return new Vector2(this.polygonSprite.getX(), this.polygonSprite.getY());
    }

    public Vector2 getCenter(){
        return new Vector2(this.pos.x - this.lightingMaskRect.x, this.pos.y - this.lightingMaskRect.y);
    }

    public void update(ArrayList<LightBlocker> walls){
        for (Ray ray : rays) {
            ray.cast(walls);
        }
        this.polygonSprite = getLightingMask();
    }

    public Texture getWhiteTexture(){
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1,1,1,1);
        pixmap.fill();
        return new Texture(pixmap);
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

    /**
     * @return a list of vertices of the lighting polygon in pixels
     */
    public float[] getLightingPolygon(){
        float[] vertices = new float[rays.size()*2];

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        float min_x = Float.MAX_VALUE;
        float max_x = -Float.MAX_VALUE;
        float min_y = Float.MAX_VALUE;
        float max_y = -Float.MAX_VALUE;

        int rayIndex = 0;
        for (int i = 0; i < rays.size()*2; i+=2) {
            float x = rays.get(rayIndex).intersect.x;
            float y = rays.get(rayIndex).intersect.y;
//            x /= w;
//            y /= h;
//            x -= 0.5f;
//            y -= 0.5f;
//            x *= 2;
//            y *= 2;
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

    public void render(ShapeRenderer sr){
        psb.begin();
        psb.setColor(1,1,1,0.2f);

        psb.draw(polygonSprite.getRegion(), polygonSprite.getX(), polygonSprite.getY());
        psb.end();


//        sr.begin(ShapeRenderer.ShapeType.Filled);
//        sr.setColor(Color.CYAN);
//        for (int i = 0; i < rays.size()-1; i+=2) {
//            sr.line(rays.get(i).intersect, rays.get(i+1).intersect);
//        }
//        sr.end();
    }
}
