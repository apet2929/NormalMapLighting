package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;


public class OilSlick extends ApplicationAdapter implements InputProcessor {
    private static final float SCALE = 1f;
    private static final float[] resolution = new float[]{Simulator.WIDTH, Simulator.HEIGHT};

    SpriteBatch sb;
    ShaderProgram oilShader;

    final float indexOfRefraction = 1.5f;
    float maxDepth = 50000f;
    float minDepth = 20f;
    float depth = minDepth;
    float saturation = 0.8f;
    float intensity = 0.7f;
    float speed = 3f;

    int size = 100;
    float smoothness = 1f;

    Mesh depthMesh;


    @Override
    public void create() {
        depthMesh = getSquareMesh(size, size);

        oilShader = loadShader("oil");
        sb = new SpriteBatch();
        sb.setShader(oilShader);
        Gdx.input.setInputProcessor(this);
    }

    // triagulate mesh
    // for a mesh with a prime width, and a vertex index
    // index % width = column of vertex
    // index / width = row of vertex
    // so: for a quad starting at index i,
    // the indices are:
    // (i, i + width, i + width + 1, i + width + 1, i + 1, i)
    private Mesh getSquareMesh(int width, int height) {
        float h = height+1;
        float w = width+1;

        VertexAttribute pos = new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE);
        VertexAttribute d = new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 1, "a_depth");
        Mesh mesh = new Mesh(false, (int) (w * h), width * height * 6, pos, d);
        setDepthVertices(mesh, width, height);

        int indice = 0;
        short[] indices = new short[width * height * 6];
        for (int i = 0; i < height; i++) { //
            for(int j = 0; j < width; j++){
                float v = i * (width+1) + j;
                indices[indice++] = (short) (v);
                indices[indice++] = (short) (v + width + 1);
                indices[indice++] = (short) (v + width + 2);
//                System.out.println("triangle = " + (int)(v)+ "" + (int)(v+width+1)+""+(int)(v+width+2));
                indices[indice++] = (short) (v + width + 2);
                indices[indice++] = (short) (v + 1);
                indices[indice++] = (short) (v);
//                System.out.println("triangle = " + (int)(v+width+2) + "" + (int)(v+1)+""+(int)(v));
            }
        }

        mesh.setIndices(indices);
        return mesh;
    }

    private void setDepthVertices(Mesh mesh, int width, int height){
        float h = height+1;
        float w = width+1;

        float[] depths = getDepths(width, height);
//        float[] depths = {0f, 1f, 0f, 1f, 0f, 1f, 0f, 1f, 0f};
        System.out.println("depths.length = " + depths.length);
        System.out.println("depths = " + Arrays.toString(depths));
        float[] vertices = new float[(int) (w*h*3)]; // x, y, depth
        int vertice = 0;
        for (int i = 0; i < h; i++) {
            float y = i / h;
            y -= 0.5f;
            for (int j = 0; j < w; j++) {
                float x = j / w;
                x -= 0.5f;
                vertices[vertice++] = x;
                vertices[vertice++] = y;
                vertices[vertice++] = depths[(int) (i*w + j)];
            }
        }

        mesh.setVertices(vertices);
    }

    private float[] getDepths(int width, int height) {
        float[] depths = new float[(width+1)*(height+1)];
        int index = 0;
        long seed = (long)(Math.random()*999999999);
        for (int i = 100; i <= height+100; i++) {
            float y = (float) i / (height * smoothness);
            for (int j = 100; j <= width+100; j++) {
                float x = (float) j / (width * smoothness);
                float d = generateDepth(seed, x, y);
                depths[index++] = d;
            }
        }
        return depths;
    }

    private float generateDepth(long seed, float x, float y){
//        return (x/ (depth/10)) / (y);
        return atanDepth(x, y);
    }

    private static float atanDepth(float x, float y) {
        double cx = 4 * (x - 1.5);
        double cy = 2 * (y - 1.5);
        double theta = Math.atan2(cy, cx);
        double a = 1;
        double b = 1;
        return (float) (a * Math.exp(theta * b));
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
        depth += speed;
        if(depth > maxDepth) depth = minDepth;

        oilShader.bind();
        oilShader.setUniformf("u_indexOfRefraction", indexOfRefraction);
        oilShader.setUniformf("u_maxThickness", depth);
        oilShader.setUniformf("u_saturation", saturation);
        oilShader.setUniformf("u_intensity", intensity);
        oilShader.setUniformMatrix("u_projTrans", sb.getTransformMatrix());
        oilShader.setUniform2fv("u_resolution", OilSlick.resolution, 0, 2);

        depthMesh.render(oilShader, GL20.GL_TRIANGLES);


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
        if(keycode == Input.Keys.ENTER){
            System.out.println("Yee");
            depthMesh.unbind(oilShader);
            setDepthVertices(depthMesh, size, size);
            depthMesh.bind(oilShader);
            return true;
        }
        if(keycode == Input.Keys.SHIFT_RIGHT){
            System.out.println("Yee2");
            depth = minDepth;
            return true;
        }
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
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
