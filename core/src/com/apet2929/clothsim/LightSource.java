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

public class LightSource {
    private ArrayList<Ray> rays;
    private Vector2 pos;
    private Rectangle lightingMaskRect;
    private PolygonSprite polygonSprite;
    private PolygonSpriteBatch psb;

    public LightSource(int numRays, float x, float y){
        rays = new ArrayList<>();
        pos = new Vector2(x,y);
        float incRot = 360.0f / numRays;
        for (int i = 0; i < numRays; i++) {
            rays.add(new Ray(pos, incRot*i));
        }
        psb = new PolygonSpriteBatch();
    }

    public void setPos(float x, float y){
        pos.set(x,y);
        for (Ray ray : rays) {
            ray.pos = pos;
        }
    }

    public Vector2 getTopLeft(){
        return new Vector2(this.lightingMaskRect.x, this.lightingMaskRect.y);
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

    private Texture getWhiteTexture(){
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1,1,1,1);
        pixmap.fill();
        return new Texture(pixmap);
    }

    public PolygonSprite getLightingMask(){
        float[] vertices = getLightingPolygon();
        vertices = new float[]{
                200, 200,
                300, 500,
                700,500,
                400, 200,
                600, 100,
                300, 50
        };
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        ShortArray triangles = triangulator.computeTriangles(vertices);
        PolygonRegion reg = new PolygonRegion(new TextureRegion(getWhiteTexture()), vertices, triangles.toArray());
        PolygonSprite sprite = new PolygonSprite(reg);
        Vector2 c = this.getCenter();
        sprite.setOrigin(c.x, c.y);
        return sprite;
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
        psb.setColor(1,1,1,1);
        psb.draw(polygonSprite.getRegion(), polygonSprite.getX(), polygonSprite.getY());
        psb.end();



        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.CYAN);
        for (int i = 0; i < rays.size()-1; i+=2) {
            sr.line(rays.get(i).intersect, rays.get(i+1).intersect);
        }
        sr.end();
    }
}
