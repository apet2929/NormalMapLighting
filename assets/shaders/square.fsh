#ifdef GL_ES
precision mediump float;
#endif

varying float v_depth;

uniform vec2 u_resolution;

void main() {
    gl_FragColor = vec4(v_depth);
}