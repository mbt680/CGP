#version 330 core

out vec4 FragColor;

in vec3 ptColor;

void main()
{
    FragColor = vec4(ptColor, 1.0);
}