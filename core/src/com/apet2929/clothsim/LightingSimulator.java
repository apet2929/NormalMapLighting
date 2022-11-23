package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
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
    Texture img2;
    ShaderProgram lightShader;
    ShaderProgram basicShader;

    Vector2 mousePos;
    Mesh mesh;
    Mesh lightingMesh;
    ArrayList<LightBlocker> walls;
    LightSource lightSource;

    @Override
    public void create() {
//        ShaderProgram.pedantic = false;
        sb = new SpriteBatch();

        lightShader = loadShader("red");
        basicShader = loadShader("basic");

        img = new Texture(Gdx.files.internal("grass.PNG"));
        img2 = new Texture(Gdx.files.internal("tree.png"));
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
        walls = new ArrayList<>();
        walls.add(new Wall(new Vector2(0.1f*w, 0.1f*h), new Vector2(0.1f*w, 0.9f*h)));
        walls.add(new Wall(new Vector2(0.1f*w, 0.1f*h), new Vector2(0.1f*w, 0.9f*h)));
        walls.add(new Wall(new Vector2(0.1f*w, 0.1f*h),  new Vector2(0.9f*w, 0.1f*h)));
        walls.add(new Wall(new Vector2(0.3f*w, 0.5f*h),  new Vector2(0.7f*w, 0.5f*h)));

//        sr = new ShapeRenderer(1000, basicShader);
        sr = new ShapeRenderer();
//        sr.getTransformMatrix().setToScaling(SCALE,SCALE,1);
        sb.getTransformMatrix().setToScaling(SCALE,SCALE,1);

        lightSource = new LightSource(100, 500, 500);
//        l = new Ray(new Vector2(500,500),0);
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

    void bindImages(){
        img2 = lightSource.getLightingMask();
        img2.bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    @Override
    public void render() {
//        ScreenUtils.clear(1,1,1,0);
//        img.bind();

        float[] mousePos = new float[]{
                (float)Gdx.input.getX(),
                (float)Gdx.graphics.getHeight() - Gdx.input.getY()
        };
        float[] screenRes = new float[]{
                (float)Gdx.graphics.getWidth(),
                (float)Gdx.graphics.getHeight()
        };

        lightSource.setPos(mousePos[0], mousePos[1]);
        lightSource.update(walls);

        float[] lightColor = new float[]{0.5f, 0.5f, 0.5f, 1f};
        float ambientLight = 0.2f;

        lightShader.bind();
        lightShader.setUniformi("u_texture", 0);
        lightShader.setUniformi("u_tex2", 1);
        bindImages();
        lightShader.setUniform2fv("u_screenRes", screenRes, 0, 2);
        lightShader.setUniform2fv("u_mousePos", mousePos, 0, 2);
        lightShader.setUniform4fv("u_lightColor", lightColor, 0, 4);
        lightShader.setUniformf("u_ambientLight", ambientLight);
        lightShader.setUniformMatrix("u_projTrans", sb.getTransformMatrix());

        sb.begin();
        sb.setShader(lightShader);
        sb.draw(img, 0,0);
        sb.end();

        mesh.render(lightShader, GL20.GL_TRIANGLES);
        basicShader.setUniformMatrix("u_projModelView", sb.getTransformMatrix());
        sr.setAutoShapeType(true);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(LightBlocker wall : walls){
            ((Wall)wall).render(sr);
        }
//        sr.set(ShapeRenderer.ShapeType.Filled);
//        sr.polyline(lightSource.getLightingPolygon());
//        lightSource.getLightingPolygon().render(basicShader, GL20.GL_LINES);
//        lightSource.render(sr);
        sr.end();

        sr.setColor(Color.BLUE);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float[] polygon = new float[]{
                100, 100,
                200, 400,
                300, 100,
        };

        sr.polygon(polygon);
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
