package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class MeshTest extends ApplicationAdapter {

    private static final float SCALE = 1f;
    private static final float[] resolution = new float[]{Simulator.WIDTH, Simulator.HEIGHT};

    SpriteBatch sb;
    ShaderProgram oilShader;

    int size = 2;
    float smoothness = 1;
    Mesh depthMesh;


    @Override
    public void create() {
        depthMesh = getSquareMesh(100, 100);
        oilShader = loadShader("square");
        sb = new SpriteBatch();
        sb.setShader(oilShader);


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

        float[] depths = getDepths((int) w, (int) h);
        System.out.println("depths = " + depths.length);
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
                vertices[vertice++] = depths[i*width + j];
            }
        }
        VertexAttribute pos = new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE);
        VertexAttribute depth = new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 1, "a_depth");
        Mesh mesh = new Mesh(true, (int) (w * h), width * height * 6, pos, depth);
        mesh.setVertices(vertices);

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

    private float generateDepth(long seed, float x, float y){
        return (float) Math.random();
//        float d = OpenSimplex2S.noise2(seed, x, y) + 0.5f;
//        System.out.println("d = " + d);
//        return d;
    }

    private float[] getDepths(int width, int height) {
        float[] depths = new float[width*height];
        int index = 0;
        long seed = (long)(Math.random()*999999999);
        for (int i = 0; i < height; i++) {
            float x = (float) i / (height * smoothness);
            for (int j = 0; j < width; j++) {
                float y = (float) j / (width * smoothness);
                float depth = generateDepth(seed, x, y);
                depths[index++] = depth;
            }
        }
        return depths;
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
        oilShader.bind();
        oilShader.setUniformMatrix("u_projTrans", sb.getTransformMatrix());
        oilShader.setUniform2fv("u_resolution", resolution, 0, 2);
        depthMesh.render(oilShader, GL20.GL_TRIANGLES);

    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
