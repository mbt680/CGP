#version 330 core
layout (location = 0) in vec3 aPos; // position variable w attribute position 0

uniform mat4 viewMatrix;
uniform vec3 ourColor; // specify a color output to the fragment shader

out vec3 ptColor;

void main()
{
    gl_Position = viewMatrix * vec4(aPos, 1.0); // convert aPos to homogoneous coordinates
    ptColor = vec3(ourColor); // set the output color to the input color
}
