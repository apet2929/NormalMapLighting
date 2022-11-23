//Sets the shader to be used in a GLES 2.0 environment. Vertex position attribute is called "a_position",
//the texture coordinates attribute is called "a_texCoord0", the color attribute is called "a_color".
//See ShaderProgram.POSITION_ATTRIBUTE, ShaderProgram.COLOR_ATTRIBUTE and ShaderProgram.TEXCOORD_ATTRIBUTE
//which gets "0" appended to indicate the use of the first texture unit. The combined transform and projection
//matrx is uploaded via a mat4 uniform called "u_projTrans". The texture sampler is passed via a uniform called "u_texture".
//Call this method with a null argument to use the default shader.
//This method will flush the batch before setting the new shader, you can call it in between begin() and end().

attribute vec4 a_color;
attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoord0;

void main()
{
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projTrans * vec4(a_position.xy * 20, a_position.z,1.0);
}