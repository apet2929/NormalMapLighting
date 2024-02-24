package com.apet2929.clothsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.Pixmap.Format.Alpha;
import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

public class LightSource {
    private Vector3 pos;
    private float radius;
    private Color color;
    private float ambientLight;

    public LightSource(float x, float y){
        pos = new Vector3(x,y, 0.1f);
        radius = 400;
        color = Color.WHITE;
        ambientLight = 0.2f;
    }

    /**
     * @param r,g,b,a the value for the red, green, blue, and alpha channels in range [0,1]
     */
    public void setColor(float r, float g, float b, float a){
        color.set(r,g,b,a);
    }

//    TODO: Have ambient light color
    public static void bindShader(ShaderProgram shader, Batch batch, ArrayList<LightSource> lSources){
        float[] lightPos = new float[2 * lSources.size()];
        float[] lightColor = new float[4 * lSources.size()];

        float[] screenRes = new float[]{
                (float)Gdx.graphics.getWidth(),
                (float)Gdx.graphics.getHeight()
        };

        for (int i = 0; i < lSources.size(); i++) {
            lightPos[2*i] = lSources.get(i).pos.x;
            lightPos[2*i+1] = lSources.get(i).pos.y;
            lightColor[4*i] = lSources.get(i).color.r;
            lightColor[4*i+1] = lSources.get(i).color.g;
            lightColor[4*i+2] = lSources.get(i).color.b;
            lightColor[4*i+3] = lSources.get(i).color.a;
        }

        shader.bind();
        shader.setUniformMatrix("u_projTrans", batch.getTransformMatrix());
        shader.setUniform2fv("u_screenRes", screenRes, 0, 2);
        shader.setUniformi("u_numLights", lSources.size());
        shader.setUniform2fv("u_lightPos", lightPos, 0, 2*lSources.size());
        shader.setUniform4fv("u_lightColor", lightColor, 0, 4*lSources.size());
        shader.setUniformf("u_lightZ", lSources.get(0).pos.z); // setting the lightPos to be a Vec3 breaks opacity
        shader.setUniformf("u_ambientLight", lSources.get(0).ambientLight);
        shader.setUniformf("u_lightRadiusPixels", lSources.get(0).radius);
    }
    public void setAmbientLight(float ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setPos(float x, float y){
        pos.set(x,y, pos.z);
    }

    public Texture getWhiteTexture(){
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1,1,1,1);
        pixmap.fill();
        return new Texture(pixmap);
    }

    public float getRadius() {
        return radius;
    }
}
