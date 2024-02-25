//Sets the shader to be used in a GLES 2.0 environment. Vertex position attribute is called "a_position",
//the texture coordinates attribute is called "a_texCoord0", the color attribute is called "a_color".
//See ShaderProgram.POSITION_ATTRIBUTE, ShaderProgram.COLOR_ATTRIBUTE and ShaderProgram.TEXCOORD_ATTRIBUTE
//which gets "0" appended to indicate the use of the first texture unit. The combined transform and projection
//matrx is uploaded via a mat4 uniform called "u_projTrans". The texture sampler is passed via a uniform called "u_texture".
//Call this method with a null argument to use the default shader.
//This method will flush the batch before setting the new shader, you can call it in between begin() and end().

//uniform sampler2D textures[3]; // 0 => texture, 1 => normal map, 2 => light mask

#define MAX_LIGHTS 12

uniform vec2 u_screenRes;
uniform vec2 u_lightPos[MAX_LIGHTS];
uniform float u_lightZ[MAX_LIGHTS];
uniform vec4 u_lightColor[MAX_LIGHTS];
uniform float u_lightRadiusPixels[MAX_LIGHTS];
uniform float u_brightness[MAX_LIGHTS];
uniform float u_ambientLight;
uniform vec4 u_ambientLightColor;
uniform float u_rotation;
uniform int u_numLights;


varying vec4 v_color;

float calcLightIntensity(vec2 dist, float radius){
    float distPixels = length(vec2(dist.x * u_screenRes.x, dist.y * u_screenRes.y));
    float percentRadius = distPixels / radius;
    return 1 - percentRadius;
}

void main()
{
    vec4 accu_light = vec4(0);
    for(int i = 0; i < u_numLights; i++){
        vec2 diff = (u_lightPos[i] - gl_FragCoord.xy)/u_screenRes;
        float lightIntensity = calcLightIntensity(diff, u_lightRadiusPixels[i]);
        if(lightIntensity < 0) lightIntensity = 0;
        accu_light += u_brightness[i] * lightIntensity;
    }

    gl_FragColor = (v_color * accu_light) + (u_ambientLight * u_ambientLightColor);
    //gl_FragColor = u_ambientLight * u_ambientLightColor;
}


