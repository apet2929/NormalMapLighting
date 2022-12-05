package com.apet2929.clothsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;

public class TextureWithNormal {

    private Texture texture;
    private Texture normal;
    private float width, height;
    public float rotation;
    public TextureWithNormal(String name, String normalName, float scale){
        texture = new Texture(Gdx.files.internal(name));
        normal = new Texture(Gdx.files.internal(normalName));
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
        this.rotation = 0;
    }

    void rotateNormal(){
        // turn normal into mesh?
        // but once its a mesh how do i bind it to the shader?
        // how can I get the normal texture to be rotated along with
        // omg it was never the normal's issue.
        // the problem is...what is the problem?
        // the mouse position is in pixels. the gl_FragCoord is something like pixels...
        // does the gl_FragCoord change when the image is rotated? it should, right?
        // fragCoord  is always the same relative to the screen no matter how the image is rotated
        // a point on the image need not have the same fragCoord every frame as its rotating
        // which is fine, which means I can use the fragCoord as a representation of the pixel location.
        VertexAttribute attribute = new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE);
        Mesh mesh = new Mesh(true, 4, 6, attribute, VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[] {
                -1f, -1f, 0, 0,
                1f, -1f, 16, 0,
                1f, 1f, 16, 16,
                -1f, 1f, 0, 16
        });
    }

    public void render(Batch sb, ShaderProgram shader, int x, int y){
        normal.bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        shader.setUniformi("u_normalMap",1);
        shader.setUniformf("u_rotation", (float) (rotation * Math.PI/180f));
        shader.setUniform2fv("u_textureResolution", new float[]{16,16}, 0, 2);
        sb.draw(texture, x, y, texture.getWidth()/2, texture.getHeight()/2,texture.getWidth(),texture.getHeight(),
                width / texture.getWidth(),height / texture.getHeight(),rotation,0,0,texture.getWidth(),texture.getHeight(),false,false);
        sb.flush();
    }
}
