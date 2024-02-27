//Sets the shader to be used in a GLES 2.0 environment. Vertex position attribute is called "a_position",
//the texture coordinates attribute is called "a_texCoord0", the color attribute is called "a_color".
//See ShaderProgram.POSITION_ATTRIBUTE, ShaderProgram.COLOR_ATTRIBUTE and ShaderProgram.TEXCOORD_ATTRIBUTE
//which gets "0" appended to indicate the use of the first texture unit. The combined transform and projection
//matrx is uploaded via a mat4 uniform called "u_projTrans". The texture sampler is passed via a uniform called "u_texture".
//Call this method with a null argument to use the default shader.
//This method will flush the batch before setting the new shader, you can call it in between begin() and end().

//uniform sampler2D textures[3]; // 0 => texture, 1 => normal map, 2 => light mask

#define MAX_LIGHTS 12

uniform sampler2D u_normalMap;
uniform sampler2D u_texture;
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

varying vec2 v_texCoord0;
varying vec4 v_color;

float calcLightIntensity(vec3 dist, float radius){
    float distPixels = length(vec3(dist.x * u_screenRes.x, dist.y * u_screenRes.y, dist.z));
    float percentRadius = distPixels / radius;
    return 1 - percentRadius;
}

vec3 get_normal(){
    vec4 n = texture2D(u_normalMap, v_texCoord0);
    vec3 normal = vec3(2 * (n.x - 0.5), 2 * (n.y - 0.5), 2 * (n.z - 0.5));
    float x = normal.x * cos(u_rotation) - normal.y * sin(u_rotation);
    float y = normal.x * sin(u_rotation) + normal.y * cos(u_rotation);
    return vec3(x,y, normal.z);
}

vec4 calcLight(vec4 tex){
    vec3 normal = get_normal();
    vec4 light = vec4(0,0,0,1);
    for(int i = 0; i < u_numLights; i++){
        vec3 pos = vec3(u_lightPos[i], u_lightZ[i]);
        vec3 fragCoord = vec3(gl_FragCoord.xy, 0);
        vec3 diff = (pos - fragCoord)/vec3(u_screenRes, 1);
        float normalFalloff = clamp(dot(normalize(normal), normalize(diff)), 0.0, 1.0);
        float lightIntensity = calcLightIntensity(diff, u_lightRadiusPixels[i]);
        if(lightIntensity < 0) lightIntensity = 0;
        light += u_brightness[i] * normalFalloff * tex  * lightIntensity;
    }
    light.a = min(light.a, tex.a);
    return light;
}

void main()
{
    vec4 tex1 = texture2D(u_texture, v_texCoord0);
    gl_FragColor = calcLight(tex1) + (tex1 * u_ambientLight * u_ambientLightColor);
}


