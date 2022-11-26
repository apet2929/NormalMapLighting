//Sets the shader to be used in a GLES 2.0 environment. Vertex position attribute is called "a_position",
//the texture coordinates attribute is called "a_texCoord0", the color attribute is called "a_color".
//See ShaderProgram.POSITION_ATTRIBUTE, ShaderProgram.COLOR_ATTRIBUTE and ShaderProgram.TEXCOORD_ATTRIBUTE
//which gets "0" appended to indicate the use of the first texture unit. The combined transform and projection
//matrx is uploaded via a mat4 uniform called "u_projTrans". The texture sampler is passed via a uniform called "u_texture".
//Call this method with a null argument to use the default shader.
//This method will flush the batch before setting the new shader, you can call it in between begin() and end().

//uniform sampler2D textures[3]; // 0 => texture, 1 => normal map, 2 => light mask

uniform vec2 u_screenRes;
uniform vec2 u_mousePos;
uniform vec4 u_lightColor;
uniform float u_ambientLight;

varying vec4 v_color;

float calcLightIntensity(float dist){
    float intensity = 1 - tanh(dist);
//    return intensity * texture2D(lightMask, v_texCoord0);
    return intensity;
}

void main()
{
    vec2 diff = (u_mousePos - gl_FragCoord.xy)/u_screenRes;

    float dist = sqrt(diff.x*diff.x + diff.y*diff.y)*2;
    gl_FragColor = v_color * (calcLightIntensity(dist) * u_lightColor) + (u_ambientLight * u_lightColor);
//    gl_FragColor = v_color * tex1 * (calcLightIntensity(dist) * u_lightColor) + (u_ambientLight * u_lightColor);
}


