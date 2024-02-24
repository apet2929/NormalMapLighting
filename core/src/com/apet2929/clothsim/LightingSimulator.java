package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class LightingSimulator  extends ApplicationAdapter implements InputProcessor {
    private static final float SCALE = 1f;
    PolygonSpriteBatch sb;
    ShapeRenderer sr;
    TextureWithNormal cube;
    TextureWithNormal triangle;
    TextureWithNormal bird;
    ShaderProgram lightShader;
    ShaderProgram basicShader;
    ShaderProgram meshShader;

    Vector2 mousePos;
    Mesh mesh;
    ArrayList<LightBlocker> walls;
    ArrayList<LightSource> lightSources;

    @Override
    public void create() {
//        ShaderProgram.pedantic = false;
        sb = new PolygonSpriteBatch();

        lightShader = loadShader("red");
        basicShader = loadShader("basic");
        meshShader = loadShader("red","mesh");

        triangle = new TextureWithNormal("triangle.png", "normal_triangle.png", 10);
        cube = new TextureWithNormal("grass.PNG", "normal_cube.png", 10);
        bird = new TextureWithNormal("coco.png", "coco_normal.png", 10);
        mousePos = new Vector2(0,0);

        VertexAttribute attribute = new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE);
        mesh = new Mesh(true, 4, 6, attribute, VertexAttribute.ColorUnpacked());
        float mc = 0.1f;
        mesh.setVertices(new float[] {
                -1f, -1f, mc,mc,mc, 1,
                1f, -1f, mc,mc,mc, 1,
                1f, 1f, mc,mc,mc, 1,
                -1f, 1f, mc,mc,mc, 1,
        });

        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        walls = new ArrayList<>();
        walls.add(new Wall(new Vector2(0.1f*w, 0.4f*h), new Vector2(0.5f*w, 0.9f*h)));
        walls.add(new Wall(new Vector2(0.2f*w, 0.1f*h), new Vector2(0.1f*w, 0.9f*h)));
        walls.add(new Wall(new Vector2(0.3f*w, 0.5f*h),  new Vector2(0.7f*w, 0.5f*h)));

        sr = new ShapeRenderer();
        sb.getTransformMatrix().setToScaling(SCALE,SCALE,1);

        lightSources = new ArrayList<>();

//        TODO: Figure out why having 2 light sources disables mesh rendering
        LightSource lightSource2 = new LightSource(500, 180, 464);
        lightSource2.setColor(1f,0f,0f,1f);
        lightSources.add(lightSource2);

        LightSource lightSource = new LightSource(500, 500, 500);
        lightSource.setColor(0.7f,0.7f,0.7f,1f);
        lightSources.add(lightSource);
//        180, 464


        computeNormal(255,137,128);
        computeNormal(255/2,255/2,250);
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

    void enableLightMask(){
        /* Clear our depth buffer info from previous frame. */
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Enable depth writing. */
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        /* Disable RGBA color writing. */
        Gdx.gl.glColorMask(false, false, false, false);

        /* Render mask elements. */
        lightSources.forEach(s -> {s.drawMask(sb);});

        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0,0,0,1);

        // define uniform data to be given to light shader
        lightSources.get(0).setPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        lightSources.forEach(lightSource -> lightSource.update(walls));

        triangle.rotation += 1;

        /* Draw background */
        LightSource.bindShader(meshShader, sb, lightSources);
//        lightSource.bindShader(meshShader, sb);
        mesh.render(meshShader, GL20.GL_TRIANGLES);

//        enableLightMask();
        /* Draw stuff you want to light mask to affect */

        LightSource.bindShader(lightShader, sb, lightSources);
        sb.begin();
        sb.setShader(lightShader);
        cube.render(sb, lightShader, 200, 200);
        triangle.render(sb, lightShader, 100,100);
        bird.render(sb, lightShader, 400,300);
        sb.end();


        disableMask(); // disables light mask
        /* Draw foreground objects unaffected by light mask */
        drawWalls();

    }

    private void drawWalls() {
        sr.setAutoShapeType(true);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(LightBlocker wall : walls){
            ((Wall)wall).render(sr);
        }

        sr.end();
    }

    private static void disableMask() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    void computeNormal(int r, int g, int b) {
        float x = r / 255.0f;
        float y = g / 255.0f;
        x -= 0.5f;
        y -= 0.5f;
        x *= 2;
        y *= 2;
        System.out.println("x,y = {" + x + ", " + y + "}");
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
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
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
