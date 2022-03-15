#version 330 core

out vec4 FragColor;

in vec4 ltColor;
in vec3 ptColor;
in vec3 ptNorm;
flat in int levels;

void main()
{
    // variables that do nothing can be optimized out in GLSL by default
    FragColor =  (floor(ltColor * levels) / levels ) * vec4(ptColor, 1.0);
}