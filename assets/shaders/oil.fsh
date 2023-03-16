#ifdef GL_ES
precision mediump float;
#endif

#define PI 3.1415926535897932384626433832795
#define MAX_WAVELENGTH = 740


varying float v_depth;

uniform mat4 u_projTrans;
uniform vec2 u_depthRange;
uniform float u_indexOfRefraction;
uniform float u_maxThickness;
uniform float u_saturation;
uniform float u_intensity;
uniform vec2 u_resolution;

vec3 wavelengthToHSV(float wavelength, float intensity){
    float maxRed = 740.0;
    float minViolet = 380.0;
    // set 740 => 2Pi
    // set minViolet => 0
    // lerp everything inbetween
    float normalized = 1.0 - ( (wavelength - minViolet) / (maxRed - minViolet) );
    return vec3(normalized, u_saturation, intensity);
}

float lerp(float value, float startValue, float endValue){
    return (endValue - value) + startValue;
}

vec3 hsv2RGB(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec4 wavelengthToRGB(float wavelength, float intensity) {
    // convert wavelength to radians
    // convert radians + intensity w (HSV assume saturation is 1)
    vec3 HSV = wavelengthToHSV(wavelength, intensity);
    // convert HSV to RGB
    return vec4(hsv2RGB(HSV), 1);
}

void main() {
    float thickness = v_depth * u_maxThickness;
    vec4 sum_color = vec4(0.0,0.0,0.0,1.0);

    for(float i = 340.0; i < 740.0; i+= 1.0){
        float lambda_n = i / u_indexOfRefraction;
        float phi_total = PI + (2.0*thickness)*(2.0*PI)/lambda_n;
        float intensity = u_intensity * pow(cos(phi_total/2.0),2.0);
        vec4 slick_color = wavelengthToRGB(lambda_n, intensity);
        sum_color += slick_color / 100.0;
//        sum_color += slick_color;
//        if(sum_color.x > 1) sum_color.x = 0;
//        if(sum_color.y > 1) sum_color.y = 0;
//        if(sum_color.z > 1) sum_color.z = 0;
    }

    gl_FragColor = sum_color;
//    gl_FragColor = vec4(v_depth);

}