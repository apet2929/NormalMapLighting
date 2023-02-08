package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Arrays;

public class OilSlick extends ApplicationAdapter {
    private static final float SCALE = 1f;
    SpriteBatch sb;
    ShapeRenderer sr;
    ShaderProgram oilShader;

    Texture noiseTexture;
    final float indexOfRefraction = 1.2f;
    float maxDepth = 200f;
    float saturation = 0.8f;
    float intensity = 0.4f;
    float speed = 2.5f;


    @Override
    public void create() {
        noiseTexture = new Texture("drawing4.png");


        oilShader = loadShader("oil");
        sb = new SpriteBatch();
        sb.setShader(oilShader);
    }

    private ShaderProgram loadShader(String name) {
        return loadShader(name, name);
    }
    private ShaderProgram loadShader(String vertName, String fragName) {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/" + vertName + ".vsh"), Gdx.files.internal("shaders/" + fragName + ".fsh"));
        if (shader.isCompiled()) {
            System.out.println("Shader works!");

        } else {
            System.err.println(shader.getLog());
            System.exit(-1);
        }
        return shader;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0,0,0,1);
        sb.setShader(oilShader);
        maxDepth += speed;
        if(maxDepth > 800f) maxDepth = 300f;
        sb.begin();
//        oilShader.bind();
        sb.setShader(oilShader);
        oilShader.setUniformf("u_indexOfRefraction", indexOfRefraction);
        oilShader.setUniformf("u_maxThickness", maxDepth);
        oilShader.setUniformf("u_saturation", saturation);
        oilShader.setUniformf("u_intensity", intensity);
        sb.draw(noiseTexture,0,0,noiseTexture.getWidth(),noiseTexture.getHeight());
        sb.end();

    }

    @Override
    public void dispose() {
        super.dispose();
    }


}
