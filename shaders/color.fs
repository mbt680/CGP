#version 330 core

out vec4 FragColor;

in vec3 ptColor;
in vec3 ptNorm;

void main()
{
    // note that ptNorm intentionally contributes nothing here
    // variables that do nothing can be optimized out in GLSL by default
    FragColor = vec4(ptColor, 1.0) + vec4(dot(ptNorm, vec3(0.0, 0.0, 0.0)), 0.0, 0.0, 0.0);
}