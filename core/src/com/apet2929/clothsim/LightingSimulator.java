package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class LightingSimulator  extends ApplicationAdapter implements InputProcessor {
    private static final float SCALE = 1f;
    SpriteBatch sb;
    ShapeRenderer sr;
    Texture img;
    ShaderProgram lightShader;
    ShaderProgram basicShader;

    Vector2 mousePos;
    Mesh mesh;
    Mesh lightingMesh;
    Wall[] walls;
    Ray ray;

    @Override
    public void create() {
        sb = new SpriteBatch();

        lightShader = loadShader("red");
        basicShader = loadShader("basic");

        img = new Texture(Gdx.files.internal("badlogic.jpg"));
        mousePos = new Vector2(0,0);
        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[] {
                -1f, -1f, 0, 1, 0,
                1f, -1f, 0, 1, 0,
                1f, 1f, 0, 1, 0,
                -1f, 1f, 0, 1, 0
        });
        lightingMesh = new Mesh(false, 1000, 1000, VertexAttribute.Position());

        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        walls = new Wall[]{
                new Wall(new Vector2(0.1f*w, 0.1f*h), new Vector2(0.1f*w, 0.9f*h)),
                new Wall(new Vector2(0.1f*w, 0.1f*h),  new Vector2(0.9f*w, 0.1f*h)),
                new Wall(new Vector2(0.3f*w, 0.5f*h),  new Vector2(0.7f*w, 0.5f*h))
        };
        sr = new ShapeRenderer(1000, basicShader);
//        sr.getTransformMatrix().setToScaling(SCALE,SCALE,1);
        sb.getTransformMatrix().setToScaling(SCALE,SCALE,1);

        ray = new Ray(new Vector2(500,500),0);
    }

    public Mesh updateLightingMesh(Vector2 mousePos){
        ArrayList<Float> vertices;
        return null;
    }

    private ShaderProgram loadShader(String name) {
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/" + name +".vsh"), Gdx.files.internal("shaders/"+name+".fsh"));
        if(shader.isCompiled()){
            System.out.println("Shader works!");

        } else {
            System.err.println(shader.getLog());
            System.exit(-1);
        }
        return shader;
    }

    @Override
    public void render() {
//        ScreenUtils.clear(1,1,1,0);
//        img.bind();
        lightShader.bind();
        float[] mousePos = new float[]{
                (float)Gdx.input.getX(),
                (float)Gdx.graphics.getHeight() - Gdx.input.getY()
        };
        float[] screenRes = new float[]{
                (float)Gdx.graphics.getWidth(),
                (float)Gdx.graphics.getHeight()
        };

        ray.pos.set(mousePos[0], mousePos[1]);
        ray.cast(walls);

        lightShader.setUniform2fv("u_screenRes", screenRes, 0, 2);
        lightShader.setUniform2fv("u_mousePos", mousePos, 0, 2);
        lightShader.setUniformMatrix("u_projTrans", sb.getTransformMatrix());

        mesh.render(lightShader, GL20.GL_TRIANGLES);
        basicShader.setUniformMatrix("u_projModelView", sb.getTransformMatrix());
        sr.setAutoShapeType(true);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(Wall wall : walls) wall.render(sr);
        ray.render(sr);

        sr.end();


    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePos.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
