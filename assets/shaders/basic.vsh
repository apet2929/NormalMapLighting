attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;
uniform mat4 u_projTrans;

varying vec4 v_col;
void main() {
    gl_Position = a_position;
    v_col = a_color;
}