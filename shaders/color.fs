#version 330 core

out vec4 FragColor;

in vec3 ltColor;
in vec3 ptColor;
in vec3 ptNorm;

const int numColors = 20;

void main()
{
    // variables that do nothing can be optimized out in GLSL by default
    FragColor = vec4( (floor(ltColor * numColors) / numColors ) * ptColor, 1.0);
}