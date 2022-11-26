package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;

public class LightingSimulator  extends ApplicationAdapter implements InputProcessor {
    private static final float SCALE = 1f;
    PolygonSpriteBatch sb;
    ShapeRenderer sr;
    TextureRegion img;
    Texture normal_cube;
    Texture img2;
    ShaderProgram lightShader;
    ShaderProgram basicShader;
    ShaderProgram meshShader;

    Vector2 mousePos;
    Mesh mesh;
    ArrayList<LightBlocker> walls;
    LightSource lightSource;

    @Override
    public void create() {
//        ShaderProgram.pedantic = false;
        sb = new PolygonSpriteBatch();

        lightShader = loadShader("red");
        basicShader = loadShader("basic");
        meshShader = loadShader("red","mesh");

        img = new TextureRegion(new Texture(Gdx.files.internal("grass.PNG")));

        img2 = new Texture(Gdx.files.internal("tree.png"));
        normal_cube = new Texture(Gdx.files.internal("normal_cube.png"));
        mousePos = new Vector2(0,0);

        VertexAttribute attribute = new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE);
        mesh = new Mesh(true, 4, 6, attribute, VertexAttribute.ColorUnpacked());
        mesh.setVertices(new float[] {
                -1f, -1f, 0.5f, 1, 1, 1,
                1f, -1f, 1, 0.5f, 1, 1,
                1f, 1f, 1, 1, 0.5f, 1,
                -1f, 1f, 0.5f, 1, 1, 1,
        });
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        walls = new ArrayList<>();
        walls.add(new Wall(new Vector2(0.1f*w, 0.4f*h), new Vector2(0.5f*w, 0.9f*h)));
        walls.add(new Wall(new Vector2(0.2f*w, 0.1f*h), new Vector2(0.1f*w, 0.9f*h)));
        walls.add(new Wall(new Vector2(0.4f*w, 0.4f*h),  new Vector2(0.9f*w, 0.1f*h)));
        walls.add(new Wall(new Vector2(0.3f*w, 0.5f*h),  new Vector2(0.7f*w, 0.5f*h)));

//        sr = new ShapeRenderer(1000, basicShader);
        sr = new ShapeRenderer();
//        sr.getTransformMatrix().setToScaling(SCALE,SCALE,1);
        sb.getTransformMatrix().setToScaling(SCALE,SCALE,1);

        lightSource = new LightSource(500, 500, 500);
//        l = new Ray(new Vector2(500,500),0);
    }

    private ShaderProgram loadShader(String name) {
        return loadShader(name, name);
    }
    private ShaderProgram loadShader(String vertName, String fragName) {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/" + vertName +".vsh"), Gdx.files.internal("shaders/"+fragName+".fsh"));
        if(shader.isCompiled()){
            System.out.println("Shader works!");

        } else {
            System.err.println(shader.getLog());
            System.exit(-1);
        }
        return shader;
    }

    void bindImages(){
//        img2 = lightSource.getLightingMask();
//        img2.bind(1);
//        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    void drawMasked(){
        /* Clear our depth buffer info from previous frame. */
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Enable depth writing. */
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        /* Disable RGBA color writing. */
        Gdx.gl.glColorMask(false, false, false, false);

        /* Render mask elements. */

        PolygonRegion pr = lightSource.getLightingMask().getRegion();
        sb.begin();
        Gdx.gl.glDepthMask(true);
        sb.setColor(1,1,1,1);
        sb.draw(pr, lightSource.getTopLeft().x, lightSource.getTopLeft().y);
        sb.end();

        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0,0,0,1);
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

//        lightSource.render(sr);


//        drawMasked();

        Gdx.gl.glDepthMask(false);

        bindLightingShader(mousePos, screenRes, lightColor, ambientLight, meshShader);
        mesh.render(lightShader, GL20.GL_TRIANGLES);

        bindLightingShader(mousePos, screenRes, lightColor, ambientLight, lightShader);
        sb.begin();
        sb.setShader(lightShader);
        sb.draw(img, 300,300, 200, 200);
        sb.end();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

        sr.setAutoShapeType(true);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(LightBlocker wall : walls){
            ((Wall)wall).render(sr);
        }

        sr.end();



    }

    private void bindLightingShader(float[] mousePos, float[] screenRes, float[] lightColor, float ambientLight, ShaderProgram lightShader) {
        lightShader.bind();
        lightShader.setUniform2fv("u_screenRes", screenRes, 0, 2);
        lightShader.setUniform2fv("u_mousePos", mousePos, 0, 2);
        lightShader.setUniform4fv("u_lightColor", lightColor, 0, 4);
        lightShader.setUniformf("u_ambientLight", ambientLight);
        lightShader.setUniformMatrix("u_projTrans", sb.getTransformMatrix());
        normal_cube.bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        lightShader.setUniformi("u_normalMap",1);
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
