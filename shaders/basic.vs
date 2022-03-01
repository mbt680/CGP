#version 330 core
layout (location = 0) in vec3 aPos; // position variable w attribute position 0

uniform mat4 viewMatrix;

void main()
{
    gl_Position = vec4(aPos, 1.0); // convert aPos to homogoneous coordinates
    //gl_Position = viewMatrix * vec4(aPos, 1.0); // multiply by camera view and convert aPos to homogoneous coordinates
}