attribute float a_depth;
attribute vec4 a_position;

uniform mat4 u_projTrans;

varying float v_depth;


void main() {
    v_depth = a_depth;
    gl_Position = u_projTrans * a_position;
}