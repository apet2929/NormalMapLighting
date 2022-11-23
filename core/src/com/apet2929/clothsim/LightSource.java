package com.apet2929.clothsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.Pixmap.Format.Alpha;

public class LightSource {
    private ArrayList<Ray> rays;
    private Vector2 pos;
    private Rectangle lightingMaskRect;

    public LightSource(int numRays, float x, float y){
        rays = new ArrayList<>();
        pos = new Vector2(x,y);
        for (int i = 0; i < numRays; i++) {
            rays.add(new Ray(pos, (float)(360*i)/numRays));
        }
    }

    public void setPos(float x, float y){
        pos.set(x,y);
        for (Ray ray : rays) {
            ray.pos = pos;
        }
    }

    public void update(ArrayList<LightBlocker> walls){
        for (Ray ray : rays) {
            ray.cast(walls);
        }
    }

    public Texture getLightingMask(){
        Pixmap pixmap = new Pixmap(160, 160, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(0,1,0,0);
        pixmap.fill();
        pixmap.setColor(1,1,1,1);
        pixmap.fillRectangle(20,20,100,100);
//        pixmap.setColor(1,0,0,1);
//        pixmap.fillRectangle(5,5,60,20);
//        float[] vertices = getLightingPolygon();
//        Pixmap pixmap = new Pixmap((int)(lightingMaskRect.width), (int)(lightingMaskRect.height), Alpha);
//        pixmap.setBlending(Pixmap.Blending.None);
//
//        int startX = (int) (pos.x - lightingMaskRect.x);
//        int startY = (int) (pos.y - lightingMaskRect.y);
//        pixmap.setColor(0,0,0,1);
//        for (int i = 0; i < vertices.length-1; i+=2) {
//            int endX = (int) (vertices[i] - lightingMaskRect.x);
//            int endY = (int) (vertices[i+1] - lightingMaskRect.y);
//            pixmap.drawLine(startX, startY, endX, endY);
//        }

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
        for (Ray ray : rays) {
            ray.render(sr);
        }
//        for (int i = 0; i < rays.size()-1; i+=2) {
//            sr.line(rays.get(i).intersect, rays.get(i+1).intersect);
//        }
    }
}
